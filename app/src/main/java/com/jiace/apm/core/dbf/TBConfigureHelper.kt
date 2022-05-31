package com.jiace.apm.core.dbf

import com.jiace.apm.until.AESUtils
import com.jiace.apm.until.Utils
import java.lang.Exception
import java.util.*

/**
 * @author: yw
 * @date: 2021-05-28
 * @description: 对Device数据库中的Configure表进行操作,为避免破解,key取MD5(key),value取AES(value)
 */
object TBConfigureHelper {
    /** 表名 */
    const val TableName = "Configure"

    /** AES密码 */
    private val PassWord = "JiaCe"

    /**
     * 获取上下文
     * @return Context?
     */
    private fun getContext() = DBManager.getContext()

    /**
     * 获取Device数据库
     * @return SQLiteDatabase?
     */
    private fun getDatabase() = DBManager.getDeviceDatabase()

    /**
     * 查询参数
     * @param key String
     * @param defaultValue String
     * @return String
     */
    fun queryConfigure(key: String, defaultValue: String):String {
         val md5 = Utils.getMD5(key)
        getDatabase()?.let {
            val  sql = "SELECT Value FROM ${TableName} WHERE [Key]=?"
            it.rawQuery(sql, arrayOf(md5)).use {
                if(it.moveToFirst()) {
                    try {
                        return AESUtils.decode(PassWord, it.getString(it.getColumnIndex("Value")))
                    }catch (e:Exception) {
                        e.printStackTrace()
                        return defaultValue
                    }
                }
            }
        }
        return defaultValue
    }

    /**
     * 更新或加入参数
     * @param key String
     * @param value String
     */
    fun updateConfigure(key: String, value: String) {
        val md5 = Utils.getMD5(key)
        val aes = AESUtils.encode(PassWord, value)
        getDatabase()?.let {
            var sql = "SELECT Value FROM ${TableName} WHERE [Key]=?"
            it.rawQuery(sql, arrayOf(md5)).use { cursor ->
                if(cursor.moveToFirst()) {
                    sql = "UPDATE ${TableName} SET Value=?, UpdateTime=? WHERE Key = ?"
                } else {
                    sql = "INSERT INTO ${TableName}( Value, UpdateTime, Key) VALUES(?, ?, ?)"
                }
                it.execSQL(sql, arrayOf(aes, Utils.formatDateTime(Date()), md5))
            }
        }
    }
}