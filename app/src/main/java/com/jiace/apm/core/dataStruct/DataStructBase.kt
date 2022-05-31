package com.jiace.apm.core.dataStruct

import com.jiace.apm.until.buildGson


/**
 * @author: yw
 * @date: 2021/05/24
 * @description: 数据接口
 */
abstract class DataStructBase {

    companion object {

        /** 工程参数 */
        const val ProjectParamCode = 1

        /** 施工及设计参数 */
        const val BuildParamCode = 2

        /** 监测参数 */
        const val MonitorParamCode = 3

        /** 传感器编号 */
        const val SensorParamCode = 4

        /** 设备基本信息 */
        const val DeviceGather = 3

        /** 控制参数 */
        const val ControlParam = 4

        /** 压力参数 */
        const val PressParam = 5

        /** 位移参数 */
        const val SensorParam = 6

        /** 采样数据 */
        const val RecordCode = 7

        /** 最近一次的有效GPS定位信息 */
        const val LastValidGps = 8

    }

    /**
     *  数据类型
     */
    abstract val dataType: Int


    /**
     * 将数据转换为JSon字符串
     * @return String
     */
    override fun toString(): String {
        return buildGson().toJson(this)
    }


    /**
     * 验证参数是否正确
     * @return 返回参数是否正确 boolean
     * */
    abstract fun isParamCorrect(): Boolean
}