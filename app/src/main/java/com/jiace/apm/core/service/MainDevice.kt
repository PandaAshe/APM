package com.jiace.apm.core.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.jiace.apm.Application

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/13.
3 * Description:
4 *
5 */
class MainDevice {

    private var mMainDeviceService: MainDeviceService? = null

    private val mMainDeviceServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mMainDeviceService = (service as MainDeviceService.MainDeviceBinder).getService()
            if (mMainDeviceService == null) {
                return
            }
            mMainDeviceService?.start()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mMainDeviceService?.stop()
            mMainDeviceService = null
        }
    }


    fun start() {
        val intent = Intent(Application.get(),MainDeviceService::class.java)
        Application.get().bindService(intent,mMainDeviceServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun stop() {
        Application.get().unbindService(mMainDeviceServiceConnection)
        mMainDeviceService?.stop()
        mMainDeviceService = null
    }

}