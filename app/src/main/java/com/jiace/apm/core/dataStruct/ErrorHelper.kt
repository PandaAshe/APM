package com.jiace.apm.core.dataStruct

/**
 * @author: yw
 * @date: 2021-05-24
 * @description: 出错信息
 */
object ErrorHelper {

    // <editor-fold> ErrorCode
    /** 通道未用 */
    const val Error_UnUsed = 0x7FFFFFFF

    /** 传感器或GPS模块无信号 */
    const val Error_NoSignal = 0x7FFFFFFE

    /** 压力变送器短路 */
    const val Error_Short = 0x7FFFFFFD

    /** 传感器信号出错 */
    const val Error_Error = 0x7FFFFFFC

    /** 未设置传感器编号 */
    const val Error_NoNumber = 0x7FFFFFFB

    /** 未找到对应的率定表 */
    const val Error_NoCorrectTable = 0x7FFFFFFA

    /** 率定表数据错误 */
    const val Error_CorrectTableError = 0x7FFFFFF9

    /** 无法计算 */
    const val Error_UnCalc = 0x7FFFFFF8

    /** AD模块故障 */
    const val Error_ADError = 0x7FFFFFF7

    /** 位移盒未接 */
    const val Error_BoxUnconnected = 0x7FFFFFF6

    /** 蓝牙接收模块故障 */
    const val Error_BluetoothReceiverFault = 0x7FFFFFF5

    /** 荷重传感器未和蓝牙发射模块连接 */
    const val Error_UnConnectedWithBluetooth = 0x7FFFFFF4

    /** 型号不匹配 */
    const val Error_Mismatch = 0x7FFFFFF3

    /** 荷重盒未接 */
    const val Error_LoadSensorBoxUnconnected = 0x7FFFFFF2

    /** 未知错误 */
    const val Error_Unknown = 0x7FFFFF00

    /** GPS定位信息无效 */
    const val Error_GpsInvalid = 0

    // </editor-fold>

    /**
     * 数据是否有误
     * @param value Int 需检测的数据
     * @return Boolean
     */
    fun isErrorValue(value: Int) = (value >= Error_Unknown)

    /**
     * 获取错误信息
     * @param errorCode Int 错误码
     * @return String 文字说明
     */
    fun getErrorString(errorCode: Int) = when(errorCode) {
        Error_UnUsed -> "未用"
        Error_NoSignal -> "未接"
        Error_Short -> "短路"
        Error_Error -> "出错"
        Error_NoNumber -> "无编号"
        Error_NoCorrectTable -> "率空"
        Error_CorrectTableError -> "率错"
        Error_UnCalc -> "无法计算"
        Error_ADError -> "AD故障"
        Error_BoxUnconnected -> "位移盒未接"
        Error_BluetoothReceiverFault -> "蓝牙故障"
        Error_UnConnectedWithBluetooth -> "未接"
        Error_Mismatch -> "类型错误"
        Error_LoadSensorBoxUnconnected -> "荷重盒未接"
        else -> "未知错误(${errorCode})"
    }

    /**
     * 判断Gps定位是否有效
     * @param gpsLongitude Int 0.01“
     * @param gpsLatitude Int 0.01”
     * @return Boolean
     */
    fun isGpsValid(gpsLongitude:Int, gpsLatitude:Int):Boolean {
        val error = arrayOf(0, Error_NoSignal)
        if(gpsLongitude in error ||
                gpsLatitude in error) {
            return false
        }
        return true
    }
}