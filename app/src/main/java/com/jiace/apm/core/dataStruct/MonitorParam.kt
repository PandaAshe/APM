package com.jiace.apm.core.dataStruct

import com.jiace.apm.R
import com.jiace.apm.until.buildGson
import com.jiace.apm.until.getString

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/9.
3 * Description:
4 *
5 */
class MonitorParam: DataStructBase() {

    companion object {

        const val Id_MonitorType = 0
        const val Id_RecordInterval = 1


        const val Id_TorsionMax = 0
        const val Id_TorsionMin = 1
        const val Id_FootageMax = 2
        const val Id_FootageMin = 3
        const val Id_AngleOfDipMax = 4


        const val MonitorType_Depth = 0
        const val MonitorType_Time = 1

        val MonitorTypeText = arrayOf(getString(R.string.type_depth),getString(R.string.type_time))

        fun getMonitorTypeText(type: Int) = kotlin.run {
            when (type) {
                MonitorType_Depth -> MonitorTypeText[MonitorType_Depth]
                MonitorType_Time -> MonitorTypeText[MonitorType_Time]
                else -> "$type"
            }
        }

        fun fromJson(json: String) =  buildGson().fromJson(json, MonitorParam::class.java)
    }

    /** 数据采集方式 */
    var MonitorType = MonitorType_Depth

    /**
     *  数据记录间隔(cm)
     *  时间单位（s）
     * */
    var RecordInterval = 50

    /**
     * 扭矩上限值（N·m）
     * */
    var TorsionMax = 10000

    /**
     * 扭矩下限值（N·m）
     * */
    var TorsionMin = 10000

    /**
     * 进尺上限值（cm）
     * */
    var FootageMax = 100000

    /**
     * 进尺下限值（cm）
     * */
    var FootageMin = 10000

    /**
     * 最大运行倾角（0.1°）
     * */
    var AngleOfDipMax = 30

    override val dataType: Int
        get() = MonitorParamCode

    override fun isParamCorrect(): Boolean {
        if (RecordInterval <= 0) {
            when (MonitorType) {
                MonitorType_Time -> {
                    throw Exception(getString(R.string.error_record_interval))
                }

                MonitorType_Depth -> {
                    throw Exception(getString(R.string.error_record_interval_depth))
                }
            }
        }

        if (TorsionMax <= 0) {
            throw Exception(getString(R.string.error_torsion_max))
        }

        if (TorsionMin <= 0) {
            throw Exception(getString(R.string.error_torsion_min))
        }

        if (FootageMax <= 0) {
            throw Exception(getString(R.string.error_footage_max))
        }

        if (FootageMin <= 0) {
            throw Exception(getString(R.string.error_footage_min))
        }

        if (AngleOfDipMax <= 0) {
            throw Exception(getString(R.string.error_max_angle))
        }
        return true
    }

    fun clone() = fromJson(this.toString())
}