package com.jiace.apm.ui.main.dialog

import android.content.Context
import android.view.View
import com.jiace.apm.R
import com.jiace.apm.base.HideBarDialog
import com.jiace.apm.core.ParamHelper
import com.jiace.apm.core.service.ServiceHelper
import com.jiace.apm.until.applicationScope
import kotlinx.android.synthetic.main.monitor_sensor_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/31.
3 * Description:
4 *
5 */
class MonitorSensorDialog(context: Context): HideBarDialog(context) {

    private val contentView = View.inflate(context, R.layout.monitor_sensor_layout,null)
    private var mIsWorking = true

    init {
        setOnDismissListener {
            clear()
        }

        contentView.torsionNo.text = ParamHelper.mSensorParam.TorsionSensorId
        contentView.bluetoothNo.text = ParamHelper.mSensorParam.TorsionBluetoothNo
        contentView.displacementNo.text = ParamHelper.mSensorParam.DisplacementSensorId
        contentView.angleNo.text = ParamHelper.mSensorParam.AngleOfDipSensorId
        setView(contentView)


        flow {
            while (mIsWorking) {
                kotlinx.coroutines.delay(1000L)
                emit(true)
            }
        }.flowOn(Dispatchers.IO).onEach {
            onMonitorRecord()
        }.launchIn(applicationScope)
    }

    private fun onMonitorRecord() {
        ServiceHelper.mVirtualDeviceService?.let {
            it.mRealTimeData.apply {
                contentView.voltageValue.text = "%.1f".format(mMonitorStatus.mRealVoltage / 1000f)
                contentView.torsionValue.text = "%.1f".format(mMonitorStatus.mTorsionValue / 1000f)
                contentView.rssiValue.text = "%d".format(mMonitorStatus.mRssi)
                contentView.torsionValue.text = "%d".format(mMonitorStatus.mBatteryVoltage)
                contentView.angleY.text = "%.1f".format(mMonitorStatus.mAngleOfDip / 10f)
                contentView.displacementValue.text = "%d".format(mMonitorStatus.mDisplacementValue)
            }
        }
    }

    private fun clear() {
        mIsWorking = false
    }

}