package com.jiace.apm.core.dataStruct

import com.jiace.apm.R
import com.jiace.apm.until.buildGson
import com.jiace.apm.until.getString


/**
 * @author: yw
 * @date: 2021-05-24
 * @description:外设基本信息
 */
class DeviceGather: DataStructBase {

    companion object {

        /** 采集仪 */
        const val SampleMachine = 0

        /** 扭矩传感器 */
        const val TorsionSensor = 1

        /** 倾角传感器 */
        const val AngleOfDipSensor = 2

        /** 激光测距仪 */
        const val Displacement = 3

        /** 蓝牙模块编号 */
        const val Bluetooth = 4

        /**
         * 获取设备名称
         * @param type Int 设备类型
         * @return String 设备名称
         */
        fun getDeviceName(type: Int) = run {
            when(type) {
                SampleMachine -> getString(R.string.sample_machine_no)
                TorsionSensor -> getString(R.string.torsion_sensor)
                AngleOfDipSensor -> getString(R.string.angle_sensor)
                Displacement -> getString(R.string.displacement_sensor)
                Bluetooth -> getString(R.string.bluetooth)
                else -> getString(R.string.unknown_device).format(type)
            }
        }

        /**
         * 从json字符串生成
         * @param json String json字符串
         * @return (com.jiace.jyh.core.dataStruct.DeviceGather..com.jiace.jyh.core.dataStruct.DeviceGather?)
         */
        fun fromJSon(json: String) = buildGson().fromJson(json, DeviceGather::class.java)
    }


    constructor(): super()
    constructor(type: Int): super() {
        Type = type
        No = "2001"
        when (Type) {

            Displacement -> {
                FullScall = 100000
            }

            TorsionSensor -> {
                FullScall = 5000000
                LoadingRatio = 1000000
            }
        }
    }

    /** 类型 */
    var Type = SampleMachine

    /** 编号 */
    var No = ""

    /** 满量程，位移单位为um，压力单位为N, 油压为kPa */
    var FullScall = 0

    /** 荷重传感器标定系数(uV/V) */
    var LoadingRatio = 0

    /**
     *  数据类型
     */
    override val dataType: Int
        get() = DataStructBase.DeviceGather

    /**
     * 荷重传感器根据电压(0.1uV)计算荷载(N)
     * @param uV Int 电压(0.1uV)
     * @return Int 荷载(N)
     */
    fun calcLoadingByVoltage(uV: Int) = run{
        if(ErrorHelper.isErrorValue(uV)) uV
        else if(FullScall>0 && LoadingRatio > 0) (uV/10.0/LoadingRatio*FullScall).toInt()
        else ErrorHelper.Error_UnCalc

    }

    /**
     * 荷重传感器根据荷载(N)计算电压值(0.1uV)
     * @param loading Int 荷载(N)
     * @return Int 电压(0.1uV)
     */
    fun calcVoltageByLoading(loading: Int) = run {
        if(ErrorHelper.isErrorValue(loading)) loading
        else if (FullScall>0 && LoadingRatio > 0) (loading / FullScall.toDouble() * LoadingRatio * 10).toInt()
        else ErrorHelper.Error_UnCalc
    }

    override fun isParamCorrect(): Boolean {
        return when (Type) {
            TorsionSensor, Displacement, AngleOfDipSensor -> {
                No.isNotEmpty() && FullScall > 0
            }

            SampleMachine -> {
                No.isNotEmpty()
            }

            else -> {
                No.isNotEmpty() && FullScall > 0 && LoadingRatio > 0
            }
        }

    }
}