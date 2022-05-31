package com.jiace.apm.core.service

import com.jiace.apm.core.ParamHelper
import com.jiace.apm.core.dataStruct.ErrorHelper

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/11.
3 * Description: 实时数据
4 *
5 */
class RealTimeData {

    /** 激光测距的初始值(um) */
    var mDisplacementBeginValue = 0

    /** 激光测距的测试值(um)(未测试时,为绝对位移,正在测试时,为相对位移  */
    private var mSValue = 0

    /** 激光测距的绝对值(cm) */
    var mSChannel = 0

    /** 电压值(0.1uV) */
    var mRealVoltage = 0

    /** 实测扭矩值(N.m) */
    var mTorsionValue = 0

    /** 倾斜角(‘’) */
    var mAngleOfDip = 0

    /** 圈数 */
    var mTurns = 0

    /** 进尺(um) */
    var mFootage = 0

    /** 位移初始值是否有效 */
    var mIsBeginValueValid = false

    /** 实时数据 */
    val mMonitorStatus = MonitorStatus()

    init {
        mDisplacementBeginValue = 0
        mSValue = ErrorHelper.Error_Unknown
        mSChannel = ErrorHelper.Error_Unknown
        mIsBeginValueValid = false

        mRealVoltage = ErrorHelper.Error_Unknown

        mTorsionValue = ErrorHelper.Error_Unknown

        mAngleOfDip = ErrorHelper.Error_Unknown
    }

    /** 计算实时传感器数据  */
    fun calcValue(isMonitoring: Boolean,canUpdateDisplacement: Boolean) {

        // 判断是否为错误值
        if (ErrorHelper.isErrorValue(mMonitorStatus.mDisplacementValue)) {
            mSChannel = mMonitorStatus.mDisplacementErrorCode
        } else {
            mSChannel = mMonitorStatus.mDisplacementValue
        }

        if (ErrorHelper.isErrorValue(mMonitorStatus.mRealVoltage)) {
            mRealVoltage = mMonitorStatus.mTorsionErrorCode
            mTorsionValue = mMonitorStatus.mTorsionErrorCode
        } else {
            mRealVoltage = mMonitorStatus.mRealVoltage
            mTorsionValue = ParamHelper.mSensorParam.TorsionDevice.calcLoadingByVoltage(mRealVoltage)
        }

        if (ErrorHelper.isErrorValue(mMonitorStatus.mAngleOfDip)) {
            mAngleOfDip = mMonitorStatus.mAngleErrorCode
        } else {
            mAngleOfDip = mMonitorStatus.mAngleOfDip
        }

        mTurns = mMonitorStatus.turns
        mFootage = mMonitorStatus.footage

        if (canUpdateDisplacement) {
            if (ErrorHelper.isErrorValue(mSChannel)) {
                mSValue = mSChannel
            } else {
                /*if (mSChannel >= 0) {
                    mSChannel = (mSChannel + 5) / 10 * 10
                } else {
                    mSChannel = (mSChannel - 5) / 10 * 10
                }*/

            }

            if (isMonitoring) {
                if (mIsBeginValueValid) {
                    // 判断方向
                    mSValue = (mSChannel - mDisplacementBeginValue) * -1
                }
            }
        }

    }
}