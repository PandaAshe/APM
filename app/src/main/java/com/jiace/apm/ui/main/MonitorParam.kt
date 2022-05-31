package com.jiace.apm.ui.main

import androidx.databinding.*
import com.jiace.apm.BR
import com.jiace.apm.core.dataStruct.Record

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/6.
3 * Description:
4 *
5 */
class MonitorParam: BaseObservable() {

    /** 圈数 */
    @get:Bindable
    var turns =  0
        set(value) {
            field = value
            notifyPropertyChanged(BR.turns)
        }

    /** 施工时间 */
    @get:Bindable
    var buildTime = "未开始"
        set(value) {
            field = value
            notifyPropertyChanged(BR.buildTime)
        }

    /** 设计方位 */
    @get:Bindable
    var designAngle = 0f
        set(value) {
            field = value
            notifyPropertyChanged(BR.designAngle)
        }

    /** 设计埋深 */
    @get:Bindable
    var designDepth  = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.designDepth)
        }



    /** 设计方位 */
    @get:Bindable
    var designDirection = 0f
        set(value) {
            field = value
            notifyPropertyChanged(BR.designDirection)
        }


    /** 报警信息 */
    @get: Bindable
    var messageText = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.messageText)
        }

    var isErrorMessage = ObservableBoolean(false)

    @get:Bindable
    var record = Record()
        set(value) {
            field = value
            notifyPropertyChanged(BR._all)
        }
}