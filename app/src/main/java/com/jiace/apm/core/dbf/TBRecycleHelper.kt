package com.jiace.apm.core.dbf

import android.annotation.SuppressLint
import com.jiace.apm.core.dataStruct.BasicInfo
import com.jiace.apm.until.Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @author: yw
 * @date: 2021/5/30
 * @description: 数据回收站
 */
object TBRecycleHelper {

    /** 表名 */
    const val TableName = "Recycle"

    /**
     * 获取上下文
     * @return Context?
     */
    private fun getContext() = DBManager.getContext()

    /**
     * 获取Data数据库
     * @return SQLiteDatabase?
     */
    private fun getDatabase() = DBManager.getDataDatabase()


    /**
     * 插入数据
     * @param basicInfo BasicInfo
     */
    fun insertBasicInfo(basicInfo: BasicInfo) {
        getDatabase()?.let { db ->
            val sql = "INSERT INTO ${TBBasicInfoHelper.TableName}(" +
                    "BasicInfoId," +
                    "BaseInfoId," +
                    "MachineId," +
                    "SampleMachineId," +
                    "ProjectName," +
                    "SerialNo," +
                    "PileNo," +
                    "BuildPosition," +
                    "StartTime," +
                    "EndTime,"+
                    "RecordCount," +
                    "SourceParam," +
                    "ProjectParam," +
                    "BuildParam, " +
                    "MonitorParam," +
                    "SensorParam," +
                    "GpsLongitude," +
                    "GpsLatitude," +
                    "CreateTime," +
                    "UpdateTime," +
                    "IsMonitor) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

            val value = arrayOf<Any>(
                basicInfo.mBasicInfoId,
                basicInfo.mBaseInfoId,
                basicInfo.mMachineId,
                basicInfo.mSampleMachineId,
                basicInfo.mProjectName,
                basicInfo.mSerialNo,
                basicInfo.mPileNo,
                basicInfo.mBuildPosition,
                Utils.formatDateTime(basicInfo.mStartTime),
                basicInfo.mRecordCount,
                basicInfo.mSourceParam,
                basicInfo.mProjectParam.toString(),
                basicInfo.mBuildParam.toString(),
                basicInfo.mMonitorParam.toString(),
                basicInfo.mSensorParam.toString(),
                basicInfo.mGpsLongitude,
                basicInfo.mGpsLatitude,
                Utils.formatDateTime(basicInfo.mCreateTime),
                Utils.formatDateTime(basicInfo.mUpdateTime),
                basicInfo.mIsMonitor
            )
            db.execSQL(sql, value)
        }
    }

    /**
     * 查询基本信息
     * @param basicInfoId Long
     * @return BasicInfo?
     */
    fun queryBasicInfo(basicInfoId: Long):BasicInfo? {
        var basicInfo:BasicInfo? = null
        getDatabase()?.let { db ->
            val sql = "SELECT * FROM ${TableName} WHERE BasicInfoId=${basicInfoId}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    basicInfo = BasicInfo.fromCursor(cursor)
                }
            }
        }
        return basicInfo
    }


    /**
     * 基本信息是否存在
     * @param basicInfoId Long
     * @return Boolean
     */
    fun isBasicInfoExist(basicInfoId: Long): Boolean {
        var ret =false
        getDatabase()?.let { db ->
            val sql = "SELECT BasicInfoId FROM $TableName WHERE BasicInfoId=${basicInfoId} "
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    ret = true
                }
            }
        }
        return ret
    }

    /**
     * 删除基本信息
     * @param basicInfoId Long
     *
     * */
    fun deleteBasicInfo(basicInfoId: Long) {
        getDatabase()?.delete(TableName,"BasicInfoId = ?", arrayOf("$basicInfoId"))
    }

    /**
     * 删除基本信息和测试数据及日志
     * @param basicInfoId Long
     *
     * */
    fun deleteBasicInfoAndDetailsData(basicInfoId: Long) {
        getDatabase()?.delete(TableName,"BasicInfoId = ?", arrayOf("$basicInfoId"))
        TBDetailsDataHelper.delete(basicInfoId)
    }


    /**
     * 根据查询条件计算结果总数
     * @param queryTerm String 查询条件
     * @return Int
     */
    fun queryRecycleCountByTerm(queryTerm: String): Int {
        var count = 0
        val tableName = TableName
        val queryCount = "SELECT COUNT(*) AS RowCount FROM ${tableName} WHERE " + queryTerm
        getDatabase()?.let { db->
            db.rawQuery(queryCount, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    count = cursor.getInt(0)
                }
            }
        }
        return count
    }


    /**
     * 根据工程名称获取结果总数
     * @param projectName String 工程名称
     * @return Int
     */
    fun queryRecycleCountByProjectName(projectName: String):Int {
        val queryTerm = makeQueryTermByProjectName(projectName)
        return queryRecycleCountByTerm(queryTerm)
    }

    /**
     * 根据桩号获取结果总数
     * @param pileNo String 桩号
     * @return Int
     */
    fun queryRecycleCountByPileNo(pileNo:String):Int {
        val queryTerm = makeQueryTermByPileNo(pileNo)
        return queryRecycleCountByTerm(queryTerm)
    }

    /**
     * 根据流水号获取结果总数
     * @param serialNo String 流水号
     */
    fun queryRecycleCountBySerialNo(serialNo: String) {
        val queryTerm = makeQueryTermBySerialNo(serialNo)
        return queryRecycleCountBySerialNo(queryTerm)
    }

    /**
     * 根据测试时间查询
     * @param start Date 开始日期
     * @param end Date 结束日期
     * @return Int
     */
    fun queryRecycleCountByTestTime(start: Date, end: Date):Int {
        val queryTerm = makeQueryTermByTestTime(start, end)
        return queryRecycleCountByTerm(queryTerm)
    }

    /**
     * 所有数据总数
     * @return Int
     */
    fun queryRecycleCount():Int {
        val queryTerm = "1 = 1"
        return queryRecycleCountByTerm(queryTerm)
    }

    /**
     * 根据工程名称构造查询条件
     * @param projectName String 工程名称
     * @return String
     */
    private fun makeQueryTermByProjectName(projectName: String): String {
        return "ProjectName LIKE('%${projectName}%')"
    }

    /**
     * 根据桩号构造查询条件
     * @param pileNo String 桩号
     * @return String
     */
    private fun makeQueryTermByPileNo(pileNo: String):String {
        return "PileNo LIKE('%${pileNo}%')"
    }

    /**
     * 根据流水号构造查询条件
     * @param serialNo String 流水号
     * @return String
     */
    private fun makeQueryTermBySerialNo(serialNo: String):String {
        return "SerialNo LIKE('%${serialNo}%')"
    }

    /**
     * 根据测试日期构造查询条件
     * @param start Date 开始日期
     * @param end Date 结束日期
     * @return String
     */
    private fun makeQueryTermByTestTime(start: Date, end: Date):String {
        return "StartTime<=DateTime('${Utils.formatDate(end)} 23:59:59') AND StartTime>=DateTime('${Utils.formatDate(start)} 00:00:00')"
    }

    /**
     * 根据查询条件按页返回查询结果
     * @param queryTerm String
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    @SuppressLint("Range")
    private fun queryRecycleByQueryTerm(queryTerm: String, pageNo: Int, PageRange: Int) = run{
        val list = java.util.ArrayList<java.util.HashMap<String, Any>>()
        TBRecycleHelper.getDatabase()?.let { db->
            val tableName = TBBasicInfoHelper.TableName
            val queryCount = "SELECT COUNT(*) AS RowCount FROM ${tableName} WHERE " + queryTerm
            var temp = "SELECT BasicInfoId FROM ${tableName} WHERE " + queryTerm +
                    " ORDER BY BasicInfoId DESC LIMIT ${(pageNo-1)*PageRange}, ${PageRange} "
            var sql = "SELECT GROUP_CONCAT(BasicInfoId) FROM (${temp}) GROUP BY BasicInfoId "
            temp = sql
            val field = " " +
                    "BasicInfoId, " +
                    "MachineId, " +
                    "ProjectName, " +
                    "SerialNo, " +
                    "EndTime," +
                    "PileNo, " +
                    "StartTime, " +
                    "RecordCount, " +
                    "EndTime"

            var str1 = ""
            var str2 = ""

            sql ="SELECT b.*, ${str1} FROM ( " +
                    "SELECT ${field} FROM ${tableName} WHERE BasicInfoId IN (${temp}) " +
                    ")AS b ${str2} ORDER BY b.BasicInfoId DESC"
            db.rawQuery(sql, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val map = java.util.HashMap<String, Any>()
                    map.apply {
                        put("BasicInfoId", cursor.getLong(cursor.getColumnIndex("BasicInfoId")))
                        put("MachineId", cursor.getString(cursor.getColumnIndex("MachineId")))
                        put("ProjectName", cursor.getString(cursor.getColumnIndex("ProjectName")))
                        put("SerialNo", cursor.getString(cursor.getColumnIndex("SerialNo")))
                        put("PileNo", cursor.getString(cursor.getColumnIndex("PileNo")))
                        put("EndTime", cursor.getInt(cursor.getColumnIndex("EndTime")))
                        put("StartTime", cursor.getString(cursor.getColumnIndex("StartTime")))
                        put("RecordCount", cursor.getInt(cursor.getColumnIndex("RecordCount")))
                    }
                    list.add(map)
                }
            }
        }
        list
    }

    /**
     * 根据工程名称查询
     * @param projectName String 工程名称
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    fun queryRecycleByProjectName(projectName: String, pageNo: Int, PageRange: Int) = run{
        val queryTerm = makeQueryTermByProjectName(projectName)
        queryRecycleByQueryTerm(queryTerm, pageNo, PageRange)
    }

    /**
     * 根据桩号查询
     * @param pileNo String 桩号
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    fun queryRecycleByPileNo(pileNo: String, pageNo: Int, PageRange: Int) = run{
        val queryTerm = makeQueryTermByPileNo(pileNo)
        queryRecycleByQueryTerm(queryTerm, pageNo, PageRange)
    }

    /**
     * 根据流水号查询
     * @param serialNo String 流水号
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    fun queryRecycleBySerialNo(serialNo: String, pageNo: Int, PageRange: Int) = run{
        val queryTerm = makeQueryTermBySerialNo(serialNo)
        queryRecycleByQueryTerm(queryTerm, pageNo, PageRange)
    }

    /**
     *  根据测试日期查询
     * @param start Date 开始日期
     * @param end Date 结束日期
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    fun queryRecycleByTestTime(start: Date, end: Date, pageNo: Int, PageRange: Int) = run{
        val queryTerm = makeQueryTermByTestTime(start, end)
        queryRecycleByQueryTerm(queryTerm, pageNo, PageRange)
    }

    /**
     * 查询所有数据
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    fun queryRecycle(pageNo: Int, PageRange: Int) = run{
        val queryTerm = "1=1"
        queryRecycleByQueryTerm(queryTerm, pageNo, PageRange)
    }
}