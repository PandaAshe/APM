package com.jiace.apm.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.jiace.apm.R
import com.jiace.apm.base.ThemeChangeActivity
import com.jiace.apm.common.dialog.SureAlertDialog
import com.jiace.apm.core.HostTime
import com.jiace.apm.core.ParamHelper
import com.jiace.apm.core.dbf.DBManager
import com.jiace.apm.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_init.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.system.exitProcess

class InitActivity : ThemeChangeActivity() {

    private var mAppSettingForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            PERMISSIONS.map {
                if (ContextCompat.checkSelfPermission(this,it) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS,PERMISSION_REQUEST_CODE)
                    return@registerForActivityResult
                }
            }
            initApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        PERMISSIONS.map {
            if (ContextCompat.checkSelfPermission(this@InitActivity,it) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@InitActivity, PERMISSIONS,PERMISSION_REQUEST_CODE)
                return
            }
        }
        processBar.updateProcess()
        initApp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
                    SureAlertDialog(this).apply {
                        setMessage(getString(R.string.dialog_permission_error))
                        setPositionButton(getString(R.string.config_permission)) { dialog ->
                            dialog.dismiss()
                            startAppSetting()
                        }
                        setNegativeButton(getString(R.string.dialog_exit)) { dialog ->
                            dialog.dismiss()
                            android.os.Process.killProcess(android.os.Process.myPid())
                            exitProcess(1)
                        }
                        show()
                    }
                }
                else {
                    initApp()
                }
            }
            else ->
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun startAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("$PACKAGE_URL_SCHEME$packageName")
        mAppSettingForResult.launch(intent)
    }

    private fun initApp() {
        flow {
            DBManager.init(this@InitActivity)
            // 主机时间初始化,因为需要读取数据库,需放在数据库的初始化之后
            HostTime.init()
            ParamHelper.initParam()
            emit(0)
        }.flowOn(Dispatchers.IO).onEach {
            startActivity(Intent(this, MainActivity::class.java))

        }.launchIn(lifecycleScope)

    }

    companion object {
        const val PACKAGE_URL_SCHEME = "package:"
        const val PERMISSION_REQUEST_CODE = 1

        private val PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA)
    }
}