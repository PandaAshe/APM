package com.jiace.apm.core

import com.jiace.apm.core.dataStruct.*
import com.jiace.apm.core.dbf.TBDeviceGatherHelper
import com.jiace.apm.core.dbf.TBVirtualDeviceHelper

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/9.
3 * Description: 管理内存中的参数
4 *
5 */
object ParamHelper {

    lateinit var mProjectParam: ProjectParam
    lateinit var mBuildParam: BuildParam
    lateinit var mMonitorParam: MonitorParam
    lateinit var mSensorParam: SensorParam

    lateinit var mEndDeviceId: String

    lateinit var mMachineId: String

    var mCheckPosition = 0

    /** 上一次是否正在试验中 */
    var mLastIsTesting = false

    /** 上一次试验的基本信息ID */
    var mLastBasicInfoId = 0L

    /** 报警提示信息 */
    var mAlarmInfo:AlarmInfo? = null

    fun initParam() {
        val paramMap = TBVirtualDeviceHelper.queryAllParam()
        mProjectParam = paramMap[TBVirtualDeviceHelper.ProjectParam] as ProjectParam
        mBuildParam = paramMap[TBVirtualDeviceHelper.BuildParam] as BuildParam
        mMonitorParam = paramMap[TBVirtualDeviceHelper.MonitorParam] as MonitorParam
        mSensorParam = paramMap[TBVirtualDeviceHelper.SensorParam] as SensorParam

        mEndDeviceId =  paramMap[TBVirtualDeviceHelper.SampleMachineId] as String

        mLastBasicInfoId = paramMap[TBVirtualDeviceHelper.BasicInfoId] as Long

        // 上一次是否正在进行试验
        mLastIsTesting = paramMap[TBVirtualDeviceHelper.IsMonitor] as Int == 1

        mMachineId = ConfigureHelper.MachineId

    }


    /**
     *  更新参数
     * */
    fun updateParams() {
        setParam(ParamCloneHelper.mProjectParam)
        setParam(ParamCloneHelper.mBuildParam)
        setParam(ParamCloneHelper.mMonitorParam)
        setParam(ParamCloneHelper.mSensorParam)
    }

    /**
     * 保存新的工程参数
     * @param newParam ProjectParam
     */
    fun setParam(newParam:ProjectParam) {
        if(newParam != mProjectParam) {
            mProjectParam = newParam.clone()
            TBVirtualDeviceHelper.updateParam(TBVirtualDeviceHelper.ProjectParam, mProjectParam)

           // // 如果正在试验时,更新基本信息
           // ServiceHelper.mVirtualDeviceService?.let {
           //     if(it.isTesting()) {
           //         TBBasicInfoHelper.updateProjectParam(it.getBasicInfoId(), mProjectParam)
           //     }
           // }
        }
    }

    /**
     * 保存施工参数
     *
     * */
    fun setParam(newParam: BuildParam) {
        if (newParam != mBuildParam) {
            mBuildParam = newParam.clone()
            TBVirtualDeviceHelper.updateParam(TBVirtualDeviceHelper.BuildParam, mBuildParam)
        }
    }

    /**
     *  保存传感器
     * */
    fun setParam(newParam: SensorParam) {
        if (newParam != mSensorParam) {
            TBDeviceGatherHelper.queryDeviceTable(DeviceGather.TorsionSensor,newParam.TorsionSensorId).let {
                if (it != null) {
                    newParam.TorsionDevice = it
                }
            }
            mSensorParam = newParam.clone()
            TBVirtualDeviceHelper.updateParam(TBVirtualDeviceHelper.SensorParam, mSensorParam)
        }
    }

    /**
     *  保存监测参数
     * */
    fun setParam(newParam: MonitorParam) {
        if (newParam != mMonitorParam) {
            mMonitorParam = newParam.clone()
            TBVirtualDeviceHelper.updateParam(TBVirtualDeviceHelper.MonitorParam, mMonitorParam)
        }
    }
}