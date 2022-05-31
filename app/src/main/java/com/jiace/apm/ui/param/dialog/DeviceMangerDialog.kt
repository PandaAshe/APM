package com.jiace.apm.ui.param.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.jiace.apm.R
import com.jiace.apm.base.HideBarDialog
import com.jiace.apm.core.dataStruct.DeviceGather
import com.jiace.apm.until.checkTextIsValid
import com.jiace.apm.until.showCenterToast
import kotlinx.android.synthetic.main.device_manger_alert_dialog_layout.view.*
import kotlinx.android.synthetic.main.dialog_button_layout.view.*
import kotlinx.android.synthetic.main.edit_text_alert_dialog_layout.view.*
import kotlinx.android.synthetic.main.edit_text_alert_dialog_layout.view.title
import java.util.*

/**
2 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司
3 * FileName: DeviceMangerDialog
4 * Author: Mrw
5 * Date: 2021/6/10 14:12
6 * Description: 录入和更新设备对话框
7 * History:
10 */
class DeviceMangerDialog(context: Context,append: Boolean,deviceGather: DeviceGather): HideBarDialog(context) {

    private val contentView = View.inflate(context, R.layout.device_manger_alert_dialog_layout, null)

    private var cancelAction: (() -> Unit)? = null
    private var positionAction: ((newDeviceGather: DeviceGather,isSensorAndRouter: Boolean) -> Unit)? = null

    init {
        contentView.oK.setTextColor(Color.RED)
        contentView.oK.setOnClickListener {
            if (append) {
                try {
                    contentView.deviceNum.text.toString().trim().let {
                        if (!checkTextIsValid(it)) {
                            showCenterToast(context,"设备编号不能包含非法字符")
                            return@setOnClickListener
                        }
                        deviceGather.No = it.uppercase(Locale.ROOT)
                    }
                    deviceGather.FullScall = (contentView.fullScall.text.toString().trim().toFloat() * 1000).toInt()

                    when (deviceGather.Type) {
                        DeviceGather.TorsionSensor -> {
                            deviceGather.LoadingRatio =  (contentView.loadingRatio.text.toString().trim().toFloat() * 1000).toInt()
                        }
                    }
                    if (deviceGather.isParamCorrect()) {
                        if (deviceGather.Type == DeviceGather.Displacement) {
                            positionAction?.invoke(deviceGather,contentView.sensorAndRouterCheck.isChecked)
                        } else {
                            positionAction?.invoke(deviceGather,false)
                        }

                        dismiss()
                    } else {
                        showCenterToast(context,"录入参数异常")
                        return@setOnClickListener
                    }
                } catch (e: Exception) {
                    showCenterToast(context,"录入参数异常")
                    return@setOnClickListener
                }
            }
            dismiss()
        }

        contentView.cancel.setOnClickListener {
            dismiss()
            cancelAction?.invoke()
        }

        if (append) {
            contentView.title.text = "新增设备"
        } else {
            contentView.title.text = "查看设备"
        }

        contentView.deviceNum.setText(deviceGather.No)

        contentView.deviceNum.isEnabled = append
        contentView.fullScall.isEnabled = append
        contentView.loadingRatio.isEnabled = append

        if (!append) {
            contentView.deviceNum.clearFocus()
            contentView.fullScall.clearFocus()
            contentView.loadingRatio.clearFocus()

            contentView.deviceNum.setSelection(0)
            contentView.fullScall.isSelected = false
            contentView.loadingRatio.isSelected = false

            contentView.cancel.requestFocus()
        }

        when (deviceGather.Type) {
            DeviceGather.TorsionSensor -> {
                contentView.otherDeviceParamLayout.visibility = View.VISIBLE
                contentView.loadingRatioTitle.text = "灵敏度((uV/V))"
                contentView.fullScallTitle.text = "量程(kN)"
                try {
                    contentView.loadingRatio.setText(("%.1f").format(deviceGather.LoadingRatio / 1000f))
                    contentView.fullScall.setText(("%.1f").format(deviceGather.FullScall / 1000f))
                } catch (e: Exception) {

                }
            }

            DeviceGather.Displacement -> {
                contentView.otherDeviceParamLayout.visibility = View.GONE
                contentView.fullScallTitle.text = "量程(mm)"
                if (deviceGather.FullScall == 0) {
                    contentView.fullScall.setText("100")
                } else {
                    contentView.fullScall.setText(("%d").format(deviceGather.FullScall / 1000))
                }

                if (deviceGather.Type == DeviceGather.Displacement) {
                    contentView.sensorAndRouterLayout.visibility = View.VISIBLE
                }

            }
        }
        super.setView(contentView)
    }

    fun setTitle(title: String) {
        contentView.title.text = title
    }

    fun setOnPositionListener(action: (newDeviceGather: DeviceGather,isSensorAndRouter: Boolean) -> Unit) {
        this.positionAction = action
    }

    fun setOnCancelListener(action: () -> Unit) {
        this.cancelAction = action
    }
}