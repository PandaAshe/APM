package com.jiace.apm.core.dataStruct

import android.annotation.SuppressLint
import android.database.Cursor
import com.jiace.apm.databinding.Keyed
import com.jiace.apm.until.Utils
import com.jiace.apm.until.buildGson
import java.util.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/9.
3 * Description:
4 *
5 */
class Record: Keyed<String>,DataStructBase() {

    companion object {

        /** 正常记录 */
        const val Normal = 0

        /** 提前记录 */
        const val SampleEarly = 1



        fun fromJson(json: String) = buildGson().fromJson(json, Record::class.java)

        /**
         * 从数据库中生成
         * @param cursor Cursor
         * @return JyRecord
         */
        @SuppressLint("Range")
        fun fromCursor(cursor: Cursor) = run{
            val record = Record()
            record.apply {
                Id = cursor.getInt(cursor.getColumnIndex("Id"))
                BasicInfoId = cursor.getLong(cursor.getColumnIndex("BasicInfoId"))
                GUID = cursor.getString(cursor.getColumnIndex("GUID"))
                SampleTime = Utils.getDateTimeFormat().parse(cursor.getString(cursor.getColumnIndex("SampleTime")))!!
                CreateTime = Utils.getDateTimeFormat().parse(cursor.getString(cursor.getColumnIndex("CreateTime")))!!
                RecordCount = cursor.getInt(cursor.getColumnIndex("RecordCount"))
                TorsionSensorVoltage = cursor.getInt(cursor.getColumnIndex("TorsionSensorVoltage"))
                Torsion = cursor.getInt(cursor.getColumnIndex("Torsion"))
                AngleOfDip = cursor.getInt(cursor.getColumnIndex("AngleOfDip"))
                Footage = cursor.getInt(cursor.getColumnIndex("Footage"))
                Depth = cursor.getInt(cursor.getColumnIndex("Depth"))
                Truns = cursor.getInt(cursor.getColumnIndex("Truns"))
            }
            record
        }
    }

    /** 表中的Id */
    var Id = 0

    /** 数据库中的ID */
    var BasicInfoId = 0L

    /** 数据库中的UUID */
    var GUID = UUID.randomUUID().toString()

    /** 数据类型 */
    var RecordType = Normal

    /** 采样时间 */
    var SampleTime = Date()

    /** 创建时间 */
    var CreateTime = Date()

    /** 记录次数 */
    var RecordCount = 0

    /** 实测扭矩电压值 (0.1uV) */
    var TorsionSensorVoltage = 0

    /** 安装扭矩（N·M） */
    var Torsion = 0

    /** 角度（分） */
    var AngleOfDip = 0

    /** 进尺(mm) */
    var Footage = 0

    /** 深度(mm) */
    var Depth = 0

    /** 圈数 */
    var Truns = 0



    override val dataType: Int
        get() = RecordCode

    override fun isParamCorrect(): Boolean {
        return true
    }

    fun clone(): Record {
        return fromJson(this.toString())
    }

    override fun hashCode(): Int {
        var result = Id
        result = 31 * result + BasicInfoId.hashCode()
        result = 31 * result + GUID.hashCode()
        result = 31 * result + SampleTime.hashCode()
        result = 31 * result + CreateTime.hashCode()
        result = 31 * result + RecordCount
        result = 31 * result + TorsionSensorVoltage
        result = 31 * result + Torsion
        result = 31 * result + AngleOfDip
        result = 31 * result + Footage
        result = 31 * result + Depth
        result = 31 * result + Truns
        return result
    }

    fun copyFrom(other: Record) {
        if(!(this === other)) {
            Id = other.Id
            BasicInfoId = other.BasicInfoId
            GUID = other.GUID
            SampleTime.time = other.SampleTime.time
            RecordCount = other.RecordCount
            TorsionSensorVoltage = other.TorsionSensorVoltage
            Torsion = other.Torsion
            AngleOfDip = other.AngleOfDip
            Footage = other.Footage
            Depth = other.Depth
            Truns = other.Truns
            CreateTime = other.CreateTime
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (Id != other.Id) return false
        if (BasicInfoId != other.BasicInfoId) return false
        if (GUID != other.GUID) return false
        if (RecordType != other.RecordType) return false
        if (SampleTime != other.SampleTime) return false
        if (CreateTime != other.CreateTime) return false
        if (RecordCount != other.RecordCount) return false
        if (TorsionSensorVoltage != other.TorsionSensorVoltage) return false
        if (Torsion != other.Torsion) return false
        if (AngleOfDip != other.AngleOfDip) return false
        if (Footage != other.Footage) return false
        if (Depth != other.Depth) return false
        if (Truns != other.Truns) return false

        return true
    }

    override val key: String
        get() = GUID
}