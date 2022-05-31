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
class SensorParam: DataStructBase() {


    companion object {
        const val Id_SampleMachineId = 0
        const val Id_AngleOfDipSensorId = 1
        const val Id_TorsionSensorId = 2
        const val Id_TorsionBluetoothNo = 3
        const val Id_DisplacementSensorId = 4

        fun fromJson(string: String) = buildGson().fromJson(string,com.jiace.apm.core.dataStruct.SensorParam::class.java)
    }

    /**
     * 采集仪主机编号
     * */
    var SampleMachineId = getString(R.string.default_sensor_no)

    /**
     * 扭矩传感器编号
     * */
    var TorsionSensorId = getString(R.string.default_sensor_no)

    /**
     * 倾角传感器编号
     * */
    var AngleOfDipSensorId = getString(R.string.default_sensor_no)

    /**
     * 激光测距传感器编号
     * */
    var DisplacementSensorId = getString(R.string.default_sensor_no)


    /** 扭矩传感器的蓝牙编号 */
    var TorsionBluetoothNo= getString(R.string.default_sensor_no)

    /** 扭矩传感器的参数 */
    var TorsionDevice = DeviceGather()

    /**
     * 荷重传感器参数是否设置有效
     * @return Boolean
     */
    fun isLoadSensorValid(): Boolean {
        return TorsionDevice.No == TorsionSensorId && TorsionDevice.FullScall != 0 && TorsionDevice.LoadingRatio != 0
    }

    /**
     * 获取荷重传感器的灵敏度系数
     * @return ArrayList<Double>
     */
    fun getLoadSensorRatio() = run{
        TorsionDevice.LoadingRatio/1000.0
    }

    /**
     * 获取荷重传感器的量程
     * @return ArrayList<Double>
     */
    fun getLoadSensorRange() = run{
        TorsionDevice.FullScall/1000.0
    }


    override val dataType: Int
        get() = SensorParamCode

    override fun isParamCorrect(): Boolean {

        return true
    }

    fun clone() = fromJson(this.toString())
}