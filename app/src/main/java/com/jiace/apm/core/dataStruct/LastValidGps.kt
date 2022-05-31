package com.jiace.apm.core.dataStruct

import com.jiace.apm.until.buildGson
import java.util.*

/**
 * @author: yw
 * @date: 2021-05-27
 * @description:最近有效的GPS信息
 */
class LastValidGps:DataStructBase() {

    companion object {

        /**
         * 从JSon字符串生成
         * @param json String JSon字符串
         * @return (com.jiace.jyh.core.dataStruct.LastValidGps..com.jiace.jyh.core.dataStruct.LastValidGps?)
         */
        fun fromJSon(json:String) = buildGson().fromJson(json, com.jiace.apm.core.dataStruct.LastValidGps::class.java)
    }

    /** 是否有效 */
    var IsValid = false

    /** 最近有效的GPS时间 */
    var LastValidTime = Date()

    /** 经度，为0时表示无效, 以 0.01" 为单位，负值表示西经 */
    var Longitude = 0

    /** 纬度，为0时表示无效, 以 0.01" 为单位，负值表示南纬 */
    var Latitude = 0

    /**
     *  数据类型
     */
    override val dataType: Int
        get() = DataStructBase.LastValidGps

    override fun isParamCorrect(): Boolean {
        return true
    }
}