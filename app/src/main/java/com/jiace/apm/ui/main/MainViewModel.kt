package com.jiace.apm.ui.main

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableInt
import com.jiace.apm.Application
import com.jiace.apm.MyActivityManager
import com.jiace.apm.R
import com.jiace.apm.base.BaseViewModel
import com.jiace.apm.core.AlarmInfo
import com.jiace.apm.core.ParamHelper
import com.jiace.apm.core.dataStruct.Record
import com.jiace.apm.core.event.OnRecordChange
import com.jiace.apm.core.event.TestStatusChanged
import com.jiace.apm.core.operation.ResumeMonitorOperation
import com.jiace.apm.core.operation.StartNewMonitorOperation
import com.jiace.apm.core.operation.StopMonitorOperation
import com.jiace.apm.core.operation.SuspendOperation
import com.jiace.apm.core.service.MainDevice
import com.jiace.apm.core.service.MonitorDevice
import com.jiace.apm.core.service.ServiceHelper
import com.jiace.apm.ui.file.FileListActivity
import com.jiace.apm.ui.main.dialog.MenuOperatingDialog
import com.jiace.apm.ui.param.ParamActivity
import com.jiace.apm.until.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/4/29.
3 * Description:
4 *
5 */
class MainViewModel: BaseViewModel() {

    /** 上一次收到报警信息的时间 */
    private var mLastAlarmTime = 0L

    /** 上一次收到的报警信息 */
    private var mLastAlarm = ""

    /** 上一次收到提示信息的时间 */
    private var mLastTipTime = 0L

    /** 上一次收到的提示信息 */
    private var mLastTip = ""

    private  var mFreshMessageCount = 0L

    private var mMessageJob : Job? = null

    /** 与采集仪通讯控制 */
    private var mMonitorDevice: MonitorDevice? = null
    /** 监测过程的控制 */
    private var mMainDevice: MainDevice? = null

    /** 记录采样数据 */
    val mIsNeedUpdateCurve = ObservableInt(0)

    /** 监测参数 */
    val mMonitorParam = MonitorParam()
    /** 工程信息 */
    val mProjectInfo = ProjectInfo()

    init {
        register(this)

        mMainDevice = MainDevice()
        mMainDevice?.start()

        mMonitorDevice = MonitorDevice(Application.get())
        mMonitorDevice?.start()

        // 定时刷新信息
        mMessageJob = applicationScope.launch(Dispatchers.Main) {
            val waitTime = 500L
            while (true) {
                try {
                    refreshMessage()
                    delay(waitTime)
                } catch (e: CancellationException) {
                    return@launch
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mMonitorDevice?.stop()
        mMainDevice?.stop()
        mMessageJob?.cancel()
        mMessageJob = null
        unRegister(this)
    }

    /** 菜单点击事件 */
    fun onMenuClick(v: View) {
        //MenuOperatingDialog(MyActivityManager.getCurrentActivity()!!).apply {
        //    mOnMenuOperatingListener = mOnMenuOperatingChoose
        //    show()
        //}

        Intent(Application.get(),FileListActivity::class.java).let {
            Application.get().startActivity(it)
        }

    }

    /** 配置点击事件 */
    fun onSettingClick(v: View) {
        Intent(Application.get(),ParamActivity::class.java).let {
            Application.get().startActivity(it)
        }
    }

    /** 刷新信息显示,每隔500ms调用一次 */
    private fun refreshMessage() {
        // 交替显示
        if(mFreshMessageCount % 2 == 0L) {
            // 显示报警信息
            // 判断报警信息是否过期
            if(System.currentTimeMillis() - mLastAlarmTime > 5000) {
                // 如果已经过期,再判断提示信息是否过期
                mMonitorParam.messageText = if(System.currentTimeMillis() - mLastTipTime > 1000) {
                    ""
                } else {
                    mMonitorParam.isErrorMessage.set(false)
                    mLastTip
                }
            } else {
                // 报警信息未过期,继续显示报警信息
                mMonitorParam.messageText = mLastAlarm
                mMonitorParam.isErrorMessage.set(true)
            }
        } else {
            // 判断提示信息是否过期
            if(System.currentTimeMillis() - mLastTipTime > 1000) {
                // 如果已经过期,再判断报警信息是否过期
                if(System.currentTimeMillis() - mLastAlarmTime > 5000) {
                    mMonitorParam.messageText = ""
                } else {
                    mMonitorParam.messageText = mLastAlarm
                    mMonitorParam.isErrorMessage.set(true)
                }
            } else {
                mMonitorParam.isErrorMessage.set(false)
                mMonitorParam.messageText = mLastTip
            }
        }
        mFreshMessageCount ++
    }

    private fun updateTip(tip:String) {
        mLastTipTime = System.currentTimeMillis()
        mLastTip = tip
    }

    private fun updateAlarm(alarm: AlarmInfo.AlarmItem) {
        mLastAlarmTime = System.currentTimeMillis()
        mLastAlarm = alarm.mAlarm
    }

    /** 收到提示信息 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveTip(tip: String) {
        updateTip(tip)
    }

    /** 收到报警信息 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveAlarm(alarm: AlarmInfo.AlarmItem) {
      updateAlarm(alarm)
    }

    /** 刷新事件 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateRecord(record: Record) {
        mMonitorParam.apply {
            designAngle =  ParamHelper.mBuildParam.DesignAngleOfDip / 60f
            designDirection = ParamHelper.mBuildParam.DesignDirection / 60f
            designDepth = ParamHelper.mBuildParam.DesignDepth / 1000
            turns = record.Truns
        }

        mMonitorParam.record = record.clone()

        if (ServiceHelper.mVirtualDeviceService?.isMonitoring() == true) {
            ServiceHelper.mVirtualDeviceService?.getDoc()?.let {
                val buildTime = (it.mSourceData.last().SampleTime.time - it.mSourceData.first().SampleTime.time) / 1000
                mMonitorParam.buildTime = formatSecondToTime(buildTime.toInt())
            }
            return
        }
    }

    /** 数据发生了变化 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordDataChange(event: OnRecordChange) {
        if (ServiceHelper.mVirtualDeviceService?.isMonitoring() == true) {
            val doc = ServiceHelper.mVirtualDeviceService?.getDoc()
            if (doc != null) {
                // 刷新曲线
                mIsNeedUpdateCurve.set((0..11).random())
            }
            return
        }
    }

    /** 监测状态发生了变化 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTestStatusChange(e: TestStatusChanged) {
        if (ServiceHelper.mVirtualDeviceService?.isMonitoring() == true) {
            mProjectInfo.projectName = ParamHelper.mProjectParam.ProjectName
            mProjectInfo.pileNo = ParamHelper.mProjectParam.PileNo
            mProjectInfo.designDepth = ParamHelper.mBuildParam.DesignDepth
        } else {
            mProjectInfo.projectName = ""
            mProjectInfo.pileNo = ""
            mProjectInfo.designDepth = 0
            mProjectInfo.tallNo = ""
        }
    }


    /** 菜单操作 */
    private val mOnMenuOperatingChoose = object : MenuOperatingDialog.OnMenuOperatingListener {
        override fun onMenuOperatingListener(operatingId: Int) {
            when (operatingId) {

                MenuOperatingDialog.OPERATING_SUSPEND_TESTING -> {
                    SuspendOperation(MyActivityManager.getCurrentActivity()!!).doWork()
                }

                MenuOperatingDialog.OPERATING_RESUME_TESTING -> {
                    ResumeMonitorOperation(MyActivityManager.getCurrentActivity()!!).doWork()
                }

                MenuOperatingDialog.OPERATING_START -> {
                    StartNewMonitorOperation(MyActivityManager.getCurrentActivity()!!).doWork()
                }

                MenuOperatingDialog.OPERATING_STOP -> {
                    StopMonitorOperation(MyActivityManager.getCurrentActivity()!!).doWork()
                }
            }
        }
    }
}