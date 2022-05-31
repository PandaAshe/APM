package com.jiace.apm.core.dbf

import android.annotation.SuppressLint
import android.net.Uri
import com.google.gson.Gson
import com.jiace.apm.core.dataStruct.Record
import com.jiace.apm.until.Utils
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author: yw
 * @date: 2021/5/29
 * @description:对Data数据库中的DetailsData表进行操作
 */
object TBDetailsDataHelper {

    /** 表名 */
    const val TableName = "DetailsData"

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

    fun isDetailsDataExist(basicinfoId: Long):Boolean {
        var exist =false
        getDatabase()?.let { db->
            val sql = "SELECT Id FROM ${TableName} WHERE BasicInfoId=${basicinfoId}"
            db.rawQuery(sql, null).use{ cursor ->
                if(cursor.moveToFirst()) {
                    exist = true
                }
            }
        }
        return  exist
    }


    /**
     * 查找所有的测试数据
     * @param basicinfoId Long
     * @return ArrayList<JyRecord> 按插入的先后顺序排列
     */
    fun queryDetailsDatas(basicinfoId: Long) = run{
        val datas = ArrayList<Record>()
        getDatabase()?.let { db->
            val sql = "SELECT * FROM ${TableName} WHERE BasicInfoId=${basicinfoId} ORDER BY Id"
            db.rawQuery(sql, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val record = Record.fromCursor(cursor)
                        datas.add(record)
                    } while (cursor.moveToNext())
                }
            }
        }
        datas
    }

    /**
     * 查找数据
     * @param basicinfoId Long
     * @param id Int
     * @return JyRecord?
     */
    @SuppressLint("Range")
    fun queryDetailsData(basicinfoId: Long, id: Int, action:((Date) ->Unit)? = null):Record? {
        var record: Record? = null
        getDatabase()?.let { db ->
            val sql = "SELECT * FROM ${TableName} WHERE BasicInfoId=${basicinfoId} AND Id=${id}"
            db.rawQuery(sql, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    record = Record.fromCursor(cursor)
                    val t = Utils.getDateTimeFormat().parse(cursor.getString(cursor.getColumnIndex("CreateTime")))
                    action?.invoke(t)
                }
            }
        }
        return record
    }

    /**
     * 插入数据
     * @param record JyRecord
     */
    fun insertDetailsData( record: Record) {
        getDatabase()?.let { db->
            val sql = "INSERT INTO ${TableName}(" +
                    "BasicInfoId, " +
                    "GUID, " +
                    "SampleTime, " +
                    "CreateTime, " +
                    "RecordCount, " +
                    "TorsionSensorVoltage, " +
                    "Torsion, " +
                    "AngleOfDip, " +
                    "Footage, " +
                    "Depth, " +
                    "Truns) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            val values = arrayOf<Any> (
                record.BasicInfoId,
                record.GUID,
                Utils.formatDateTime(record.SampleTime),
                Utils.formatDateTime(record.CreateTime),
                record.RecordCount,
                record.TorsionSensorVoltage,
                record.Torsion,
                record.AngleOfDip,
                record.Footage,
                record.Depth,
                record.Truns)
            db.execSQL(sql, values)

            // 获取刚刚插入数据的Id
            db.rawQuery("SELECT last_insert_rowid() FROM ${TableName}", null).use { cursor ->
                if(cursor.moveToFirst()) {
                    record.Id = cursor.getInt(0)
                }
            }
        }
        // 更新记录总数
        TBBasicInfoHelper.updateRecordCount(record.BasicInfoId)
    }

    /**
     * 更新数据
     * @param basicinfoId Long
     * @param id Int
     * @param record JyRecord
     */
    fun updateDetailsData(basicinfoId: Long, id: Int, record: Record) {
        getDatabase()?.let { db->
            val sql = "UPDATE ${TableName} SET GUID=?," +
                    "SampleTime=?, " +
                    "CreateTime=?, " +
                    "RecordCount=?, " +
                    "TorsionSensorVoltage=?, " +
                    "Torsion=?, " +
                    "AngleOfDip=?, " +
                    "Footage=?, " +
                    "Depth=?, " +
                    "Truns=? " +
                    "WHERE BasicInfoId=${basicinfoId} AND Id=${id}"
            val values = arrayOf<Any> (
                record.GUID,
                Utils.formatDateTime(record.SampleTime),
                Utils.formatDateTime(record.CreateTime),
                record.RecordCount,
                record.TorsionSensorVoltage,
                record.Torsion,
                record.AngleOfDip,
                record.Footage,
                record.Depth,
                record.Truns)
            db.execSQL(sql, values)
        }
    }

    /**
     * 找出比当前Id大的下一条数据
     * @param basicinfoId Long
     * @param currentId Int
     * @return JyRecord?
     */
    fun queryNextDetailsData(basicinfoId: Long, currentId: Int):Record? {
        var record:Record? = null
        getDatabase()?.let { db->
            val sql = "SELECT * FROM ${TableName} WHERE BasicInfoId=${basicinfoId} AND Id>${currentId} ORDER BY Id LIMIT 1"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    record = Record.fromCursor(cursor)
                }
            }
        }
        return record
    }

    /**
     * 获取最后一次读数
     * @param basicinfoId Long
     * @return JyRecord?
     */
    fun queryLastDetailsData(basicinfoId: Long) = run{
        var record:Record? = null
        getDatabase()?.let { db->
            val sql = "SELECT * FROM ${TableName} WHERE BasicInfoId=${basicinfoId} ORDER BY Id DESC LIMIT 1"
            db.rawQuery(sql, null).use { cursor->
                if(cursor.moveToFirst()) {
                    record = Record.fromCursor(cursor)
                }
            }
        }
        record
    }

    /**
     * 查询此数据在该测试中所处的序号(从0开始)
     * @param basicinfoId Long
     * @param id Int
     * @return Int?
     */
    fun queryIndex(basicinfoId: Long, id: Int) = run{
        var index = 0
        getDatabase()?.let { db->
            val sql = "SELECT COUNT(Id) FROM ${TableName} WHERE BasicInfoId=${basicinfoId} AND Id<${id}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    index = cursor.getInt(0)
                }
            }
            index
        }
    }

    /**
     * 更新基本信息号
     * @param oldBasicInfoId Long
     * @param newBasicInfoId Long
     */
    fun updateBasicInfoId(oldBasicInfoId:Long, newBasicInfoId:Long) {
        getDatabase()?.let { db->
            val sql = "UPDATE ${TableName} SET BasicInfoId=${newBasicInfoId} WHERE BasicInfoId=${oldBasicInfoId}"
            db.execSQL(sql)
        }
    }

    /**
     * 更新GUID
     * @param basicinfoId Long
     * @param guid String
     * @param id Int
     */
    fun updateGUID(basicinfoId: Long, id:Int, guid:String) {
        getDatabase()?.let { db->
            val sql ="UPDATE ${TableName} SET GUID=${guid} WHERE BasicInfoId=${basicinfoId} AND Id=${id}"
            db.execSQL(sql)
        }
    }

    /**
     * 更新所有数据的GUID
     * @param basicinfoId Long
     */
    fun updateGUID(basicinfoId: Long) {
        val list = ArrayList<Int>()
        getDatabase()?.let { db->
            var sql = "SELECT Id FROM ${TableName} WHERE BasicInfoId=${basicinfoId} ORDER BY Id"
            db.rawQuery(sql, null).use { cursor ->
                while (cursor.moveToNext()) {
                    list.add(cursor.getInt(0))
                }
            }

            db.beginTransaction()
            try {
                sql = "UPDATE ${TableName} SET GUID=? WHERE BasicInfoId=${basicinfoId} And Id=?"
                val statement = db.compileStatement(sql)
                list.forEach {
                    statement.bindString(1, UUID.randomUUID().toString())
                    statement.bindLong(2, it.toLong())
                    statement.executeUpdateDelete()
                }
                db.setTransactionSuccessful()
            }finally {
                db.endTransaction()
            }
        }
    }

    fun getRecordCountUnderTheId(basicinfoId: Long, id:Int):Int {
        var ret = 0
        getDatabase()?.let { db ->
            val sql = "SELECT COUNT(Id) FROM ${TableName} WHERE BasicInfoId=${basicinfoId} AND Id<=${id}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    ret = cursor.getInt(0)
                }
            }
        }
        return ret
    }

    /** 获取数据个数 */
    fun getCount(basicinfoId: Long):Int {
        var ret = 0
        getDatabase()?.let { db ->
            val sql = "SELECT COUNT(Id) FROM ${TableName} WHERE BasicInfoId=${basicinfoId}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    ret = cursor.getInt(0)
                }
            }
        }
        return ret
    }

    /** 获取最大的数据Id */
    fun getMaxDetailsDataId(basicInfoId: Long): Int {
        var ret = 0
        getDatabase()?.let { db ->
            val sql = "SELECT MAX(Id) FROM ${TableName} WHERE BasicInfoId=${basicInfoId}"
            db.rawQuery(sql, null)?.use { cursor ->
                if(cursor.moveToFirst()) {
                    ret = cursor.getInt(0)
                }
            }
        }
        return  ret
    }

    /** 删除数据 */
    fun delete(basicInfoId: Long) {
        getDatabase()?.delete(TableName,"BasicInfoId = ?", arrayOf("$basicInfoId"))
    }

    /**
     * 获取某一级的初始采样时间
     * @param basicInfoId Long
     * @param grade Int
     * @return Date?
     */
    fun getOneGradeStartTime(basicInfoId: Long, grade: Int):Date? {
        getDatabase()?.let { db->
            val sql = "SELECT SampleTime FROM ${TableName} WHERE BasicInfoId=${basicInfoId} AND GradeIndex=${grade} AND SampleCount=1"
            db.rawQuery(sql, null)?.use { cursor ->
                if(cursor.moveToFirst()){
                    return try {
                        Utils.getDateTimeFormat().parse(cursor.getString(0))
                    } catch (e: Exception) {
                        Utils.parseGreenDate(cursor.getString(0))
                    }
                }
            }
        }
        return null
    }

    /**
     * 更改原始记录表
     * @param newData JyRecord
     */
    fun modifyData(newData:Record) {
        // 只保存最原始的数据
        getDatabase()?.let { db ->
            var sql = "SELECT COUNT(*) AS COUNT FROM SourceData WHERE GUID='${newData.GUID}'"
            var isExist = false
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    val count = cursor.getInt(0)
                    if(count > 0) {
                        isExist = true
                    }
                }
            }
            if(!isExist) {
                sql = "INSERT INTO SourceData SELECT * FROM DetailsData WHERE  GUID='${newData.GUID}'"
                db.execSQL(sql)
            }
        }

        // 更新数据
        updateDetailsData(newData.BasicInfoId, newData.Id, newData)
    }
}