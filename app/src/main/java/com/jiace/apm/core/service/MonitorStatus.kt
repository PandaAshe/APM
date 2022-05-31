package com.jiace.apm.core.service

import com.jiace.apm.core.ParamHelper
import com.jiace.apm.core.dataStruct.ErrorHelper

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/11.
3 * Description:
4 *
5 */
class MonitorStatus {

    /** 激光测距异常代码 */
    var mDisplacementErrorCode = ErrorHelper.Error_Unknown

    /** 位移值 */
    var mDisplacementValue = ErrorHelper.Error_Unknown

    /** 电压值(0.1uV) */
    var mRealVoltage = 0

    /** 电压值的错误码 */
    var mRealVoltageErrorCode = ErrorHelper.Error_Unknown

    /** 扭矩错误代码 */
    var mTorsionErrorCode = ErrorHelper.Error_Unknown

    /** 倾角错误代码 */
    var mAngleErrorCode = ErrorHelper.Error_Unknown

    /** 实测扭矩值(N.m) */
    var mTorsionValue = 0

    /** 倾斜角(‘’) */
    var mAngleOfDip = 0

    /** 圈数 */
    var turns = 0

    /** 进尺(um) */
    var footage = 0

    /** 距离下次记录数据的时间(s) */
    var leftTime = 0

    /** 距离下次记录的距离cm */
    var leftDistance = 0

    /** 最后一次收到采集仪数据的时间 */
    var mLastTime = 0L

    /** 扭矩传感器信号强度 */
    var mRssi = 0

    /** 扭矩传感器电压 */
    var mBatteryVoltage = 0

    init {
        init()
    }


    fun init() {
        mLastTime = 0

    }

    /** 从采集仪主机中转换数据  */
    fun convertFromSampleMachine(data: SensorData) {
        if (ErrorHelper.isErrorValue(data.torque)) {
            mRealVoltageErrorCode = data.torque
            mTorsionErrorCode = data.torque
        } else {
            mRealVoltage = data.torque
            mTorsionValue = ParamHelper.mSensorParam.TorsionDevice.calcLoadingByVoltage(mRealVoltage)
        }

        if (ErrorHelper.isErrorValue(data.distance)) {
            mDisplacementErrorCode = data.distance
        } else {
            mDisplacementValue = data.distance
        }

        if (ErrorHelper.isErrorValue(data.angleZ)) {
            mAngleErrorCode = data.angleZ
        } else {
            mAngleOfDip = data.angleZ
        }

        footage = data.footage
        turns = data.cycles

        mRssi = data.torsionSensor.rssi

        mBatteryVoltage = data.torsionSensor.battery
    }


    /** 当前的试验状态 */
    enum class State {
        Monitoring,Suspend,IDLE
    }
}