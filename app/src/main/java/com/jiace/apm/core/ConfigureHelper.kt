package com.jiace.apm.core

import com.jiace.apm.core.dbf.TBConfigureHelper

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/9.
3 * Description:
4 *
5 */
object ConfigureHelper {

    const val Id_MachineId = 0
    const val Id_Version = 1


    /** 主机编号(默认为 000000-0000H,可据此判断是否需要重新设置主机编号) */
    var MachineId = ""
        set(value) {
            if(field != value) {
                TBConfigureHelper.updateConfigure("MachineId", value)
                field = value
            }
        }

    /** 主机时间与系统时间的差值(ms) */
    var DetalTime = 0L
        set(value) {
            if(field != value) {
                TBConfigureHelper.updateConfigure("DeltaTime", value.toString())
                field = value
            }
        }

    init {

        // 主机时间与系统时间的差值
        DetalTime = TBConfigureHelper.queryConfigure("DeltaTime", "0").toLong()

        // 主机编号
        MachineId = TBConfigureHelper.queryConfigure("MachineId", "000000-0000H")
    }

    /**
     * 保存或更新主机编号
     * */
    fun saveMachineId(machineId: String) {
        TBConfigureHelper.updateConfigure("MachineId", machineId)
    }

}