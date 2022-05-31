package com.jiace.apm.ui.main

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import com.jiace.apm.BR
import com.jiace.apm.core.ParamHelper
import com.jiace.apm.core.dataStruct.MonitorParam
import com.jiace.apm.core.service.ServiceHelper

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/7.
3 * Description:
4 *
5 */
class ProjectInfo: BaseObservable() {

    /** 工程名称 */
    @get:Bindable
    var projectName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.projectName)
        }

    /** 编号 */
    @get:Bindable
    var pileNo = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.pileNo)
        }

    /** 塔位编号  */
    @get:Bindable
    var tallNo = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.tallNo)
        }

    /** 设计埋深(mm) */
    @get:Bindable
    var designDepth = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.designDepth)
        }

    /** 是否是以时间来记录数据 */
    var isRecordByTime = ObservableBoolean(true)

    init {
        projectName = ""
        pileNo = ""
        designDepth = 0
        tallNo = ""
        isRecordByTime.set(ParamHelper.mMonitorParam.MonitorType == MonitorParam.MonitorType_Time)
    }

    /** 获取倒计时的标题 */
    fun getCountDownTitle(): String {
        return if (ParamHelper.mMonitorParam.MonitorType == MonitorParam.MonitorType_Time) {
            "倒计时(s)"
        } else {
            "剩余记录深度(cm)"
        }
    }
}