package com.jiace.apm.core.dbf

import android.annotation.SuppressLint
import com.jiace.apm.core.HostTime
import com.jiace.apm.until.Utils

/**
 * @author: yw
 * @date: 2021/5/28
 * @description: 基准位移
 */
object TBBaseValueHelper {

    /** 表名 */
    const val TableName = "BaseValue"

    /**
     * 获取Data数据库
     * @return SQLiteDatabase?
     */
    private fun getDatabase() = DBManager.getDataDatabase()

    /**
     * 获取上下文
     * @return Context?
     */
    private fun getContext() = DBManager.getContext()

    /**
     * 更新或插入基准位移
     * @param basicInfoId Long 基本信息号
     * @param values IntArray 位移基准值
     */
    fun updateBaseValue(basicInfoId:Long, values:Int) {
        getDatabase()?.let { db ->
            var sql = "SELECT Id FROM $TableName WHERE BasicInfoId = $basicInfoId"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    sql = "UPDATE $TableName SET S =?, UpdateTime=? WHERE BasicInfoId=${basicInfoId}"
                    db.execSQL(sql, arrayOf<Any>(values, Utils.formatDateTime(HostTime.getHostTime())))
                } else {
                    sql = "INSERT INTO ${TableName}(" +
                            "BasicInfoId," +
                            "S," +
                            "UpdateTime" +
                            ") VALUES (" +
                            "?,?,?)"
                    db.execSQL(sql, arrayOf<Any>(
                            basicInfoId,
                            values,
                            Utils.formatDateTime(HostTime.getHostTime()),
                    ))
                }
            }
        }
    }


    /**
     * 删除基准位移
     * @param basicInfoId Long 基本信息号
     */
    fun deleteBaseValue(basicInfoId: Long) {
        getDatabase()?.let { db ->
            val sql = "DELETE FROM $TableName WHERE BasicInfoId=${basicInfoId}"
            db.execSQL(sql)
        }
    }

    /**
     * 查询基准位移
     * @param basicInfoId Long 基本信息号
     * @return IntArray?
     */
    @SuppressLint("Range")
    fun queryBaseValue(basicInfoId: Long) = run{
        var values:Int = 0
        getDatabase()?.let { db ->
            val sql= "SELECT * FROM $TableName WHERE BasicInfoId=${basicInfoId}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    values = cursor.getInt(cursor.getColumnIndex("S"))
                }
            }
        }
        values
    }

    /**
     * 更新基本信息号
     * @param oldBasicInfoId Long
     * @param newBasicInfoId Long
     */
    fun updateBasicInfoId(oldBasicInfoId:Long, newBasicInfoId:Long) {
        getDatabase()?.let { db->
            val sql = "UPDATE $TableName SET BasicInfoId=${newBasicInfoId} WHERE BasicInfoId=${oldBasicInfoId}"
            db.execSQL(sql)
        }
    }
}