package com.jiace.apm.core.dataStruct

import android.annotation.SuppressLint
import android.database.Cursor
import com.jiace.apm.until.Utils
import org.json.JSONObject
import java.util.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/9.
3 * Description:
4 *
5 */
class BasicInfo {

    companion object {

        @SuppressLint("Range")
        fun fromCursor(cursor: Cursor): BasicInfo {
            val basicInfo = BasicInfo()
            basicInfo.apply {
                mBasicInfoId = cursor.getLong(cursor.getColumnIndex("BasicInfoId"))
                mBaseInfoId = cursor.getString(cursor.getColumnIndex("BaseInfoId"))
                mMachineId = cursor.getString(cursor.getColumnIndex("MachineId"))
                mSampleMachineId = cursor.getString(cursor.getColumnIndex("SampleMachineId"))
                mProjectName = cursor.getString(cursor.getColumnIndex("ProjectName"))
                mSerialNo = cursor.getString(cursor.getColumnIndex("SerialNo"))
                mPileNo = cursor.getString(cursor.getColumnIndex("PileNo"))
                mBuildPosition = cursor.getString(cursor.getColumnIndex("BuildPosition"))
                mStartTime = Utils.getDateTimeFormat().parse(cursor.getString(cursor.getColumnIndex("StartTime")))!!

                mRecordCount = cursor.getInt(cursor.getColumnIndex("RecordCount"))

                mProjectParam = ProjectParam.fromJSon(cursor.getString(cursor.getColumnIndex("ProjectParam")))
                mBuildParam  = BuildParam.fromJson(cursor.getString(cursor.getColumnIndex("BuildParam")))
                mMonitorParam = MonitorParam.fromJson(cursor.getString(cursor.getColumnIndex("MonitorParam")))

                mGpsLongitude = cursor.getInt(cursor.getColumnIndex("GpsLongitude"))
                mGpsLatitude = cursor.getInt(cursor.getColumnIndex("GpsLatitude"))
                mCreateTime = Utils.getDateTimeFormat().parse(cursor.getString(cursor.getColumnIndex("CreateTime")))!!
                mIsMonitor = cursor.getInt(cursor.getColumnIndex("IsTesting"))
            }

            return BasicInfo()
        }
    }

    /** 从2000-01-01 00:00:00 以来的秒数 */
    var mBasicInfoId = (Date().time - Utils.getDateTimeFormat().parse("2000-01-01 00:00:00").time) / 1000

    var mBaseInfoId =  UUID.randomUUID().toString()

    var mMachineId = ""

    var mSampleMachineId = ""

    var mProjectName = ""

    var mPileNo = ""

    var mSerialNo = ""

    var mBuildPosition = ""

    var mStartTime = Date()

    var mRecordCount = 0

    var mProjectParam = ProjectParam()
        set(value) {
            mProjectName = value.ProjectName
            mSerialNo = value.SerialNo
            mPileNo = value.PileNo
            mBuildPosition = value.BuildPosition
            field = value
        }

    var mBuildParam = BuildParam()
        set(value) {
            field = value
        }

    var mMonitorParam = MonitorParam()

    var mSensorParam = SensorParam()

    /** 经度，为0时表示无效, 以 0.01" 为单位，负值表示西经 */
    var mGpsLongitude = 0

    /** 纬度，为0时表示无效, 以 0.01" 为单位，负值表示南纬 */
    var mGpsLatitude = 0

    /** 使用真实的系统时间 */
    var mCreateTime = Date()

    /**  更新时间 */
    var mUpdateTime = Date()

    /** 1 正在监测中 0 待机中 */
    var mIsMonitor = 1

    var mSourceParam:String = ""

    /** 转换原始参数JSON */
    fun updateSourceParam() {
        val sourceJSON = JSONObject()
        sourceJSON.put("ProjectParam",mProjectParam.toString())
        sourceJSON.put("BuildParam",mBuildParam.toString())
        sourceJSON.put("SensorParam",mSensorParam.toString())
        sourceJSON.put("MonitorParam",mMonitorParam.toString())
        mSourceParam = sourceJSON.toString()
    }

}