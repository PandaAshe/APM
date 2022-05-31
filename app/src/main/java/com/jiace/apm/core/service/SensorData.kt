package com.jiace.apm.core.service

import com.jiace.apm.core.ParamHelper
import com.jiace.apm.until.Utils
import com.jiace.apm.until.testCycles
import com.jiace.apm.until.testDepth
import okhttp3.internal.and

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/13.
3 * Description:
4 *
5 */
class SensorData {

    companion object {
        const val WorkingStatus_IDLE = 0x00.toByte()
        const val WorkingStatus_MEASULE = 0x01.toByte()
        const val WorkingStatus_PAUSE = 0x02.toByte()
    }

    /** 数据编号 */
    var number = 0

    /** 距离 cm */
    var distance = 0

    /** 剩余位移 */
    var laveDistance = 0

    /** 圈数 */
    var cycles = 0

    /** 进尺 cm */
    var footage = 0

    /** 角度X  0.1°*/
    var angeleX = 0

    /** 角度Y  0.1°*/
    var angleY = 0

    /** 角度Z  0.1°*/
    var angleZ = 0

    /** 扭矩 电压值（mV/V） */
    var torque = 0

    /** 电量0.1% */
    var batteryValue = 1000

    /** 当前运行状态 */
    var workingStatus = WorkingStatus_IDLE

    /** 扭矩传感器 */
    val torsionSensor = TorsionSensor()

    fun convertFromBytes(data: ByteArray) {
        var ret = false
        if (data.size >= 35) {
            // 运行状态
            workingStatus = data[0]
            // 剩余位移
            laveDistance = Utils.bytesToUnsignedShort(data,1)
            // 当前位移
            distance = Utils.bytesToUnsignedShort(data,3)

            // 圈数
            cycles = Utils.bytesToUnsignedShort(data,5)

            // 进尺
            footage = Utils.bytesToUnsignedShort(data,7)

            // X
            angeleX = Utils.bytesToUnsignedShort(data,9) - 900
            // Y
            angleY = Utils.bytesToUnsignedShort(data,11) - 900
            // Z
            angleZ = Utils.bytesToUnsignedShort(data,13) - 900

            //电量
            batteryValue = Utils.bytesToUnsignedShort(data,15)

            val sensorDataArray = ByteArray(18)
            System.arraycopy(data,17,sensorDataArray,0,18)

            torsionSensor.convertFromBytes(sensorDataArray)

            torque = torsionSensor.sensorValue
        }
    }


    /** 转换成数据帧 */
    fun toBytes(): ByteArray {
        val data = ByteArray(35)
        data[0] = WorkingStatus_IDLE.toByte()
        // 剩余数据量
        Utils.shortToBytes(0).copyInto(data,1,0,2)
        // 距离
        if (testDepth > 10000) {
            testDepth = 10
        }
        testDepth+= (0..5).random()
        Utils.shortToBytes(testDepth.toShort()).copyInto(data,3,0,2)

        testCycles += 1
        if (testCycles > 500) {
            testCycles = 1
        }

        Utils.shortToBytes(testCycles.toShort()).copyInto(data,5,0,2)

        Utils.shortToBytes((100..450).random().toShort()).copyInto(data,7,0,2)

        Utils.shortToBytes(0).copyInto(data,9,0,2)
        Utils.shortToBytes(0).copyInto(data,11,0,2)
        Utils.shortToBytes((810..980).random().toShort()).copyInto(data,13,0,2)

        Utils.shortToBytes(66).copyInto(data,15,0,2)

        // 蓝牙数据
        data[17] = 66 and 0xFF

        data[18] = 4 and 0xFF

        Utils.intToBytes((1203..6021).random()).copyInto(data,19,0,4)

        Utils.shortToBytes((1..4).random().toShort()).copyInto(data,23,0,2)

        data[25] = 23 and 0xFF

        Utils.stringToBytes(ParamHelper.mSensorParam.TorsionBluetoothNo,9).copyInto(data,26,0,9)

        return data
    }
}

/** 扭矩传感器 */
class TorsionSensor {

    /**信号强度*/
    var rssi = 0

    /** 传感器类型 */
    var sensorType = 4

    /** 读数值 */
    var sensorValue = 0

    /** 电池电压 */
    var battery = 0

    /** 温度 */
    var temperature = 0

    /** 电压 */
    var sensorId = ""

    fun convertFromBytes(data: ByteArray) {
        rssi = data[0].toInt()

        sensorType = data[1].toInt()

        sensorValue = Utils.bytesToInt(data,2)

        battery = Utils.bytesToUnsignedShort(data,6)

        temperature = data[8].toInt()

        sensorId = Utils.bytesToString(data,9)

    }
}