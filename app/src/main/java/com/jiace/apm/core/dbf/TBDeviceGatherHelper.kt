package com.jiace.apm.core.dbf

import android.annotation.SuppressLint
import com.jiace.apm.core.dataStruct.DeviceGather
import kotlin.collections.ArrayList

/**
 * @author: yw
 * @date: 2021-05-28
 * @description: 对Device数据库中的DeviceGather表进行操作
 */
object TBDeviceGatherHelper {

    /** 表名 */
    const val TableName = "DeviceGather"

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
     * 根据类型和编号查询设备信息
     * @param type Int 设备类型
     * @param no String 编号
     * @return DeviceGather?
     */
    @SuppressLint("Range")
    fun queryDeviceTable(type: Int, no: String) = run {
        var deviceGather: DeviceGather? = null
        getDatabase()?.let { db ->
            val sql = "SELECT * FROM ${TableName} WHERE Type=? AND ([No]=?)"
            db.rawQuery(sql, arrayOf(type.toString(), no)).use { cursor ->
                if(cursor.moveToFirst()) {
                    deviceGather = DeviceGather()
                    deviceGather!!.let { dg ->
                        dg.No = cursor.getString(cursor.getColumnIndex("No"))
                        dg.Type = cursor.getInt(cursor.getColumnIndex("Type"))
                        dg.FullScall = cursor.getInt(cursor.getColumnIndex("FullScale"))
                        if(!cursor.isNull(cursor.getColumnIndex("LoadingRatio"))) {
                            dg.LoadingRatio = cursor.getInt(cursor.getColumnIndex("LoadingRatio"))
                        }
                    }
                }
            }
        }
        deviceGather
    }

    /**
     * 查询同一类型的设备
     * @param type Int 设备类型
     * @return ArrayList<DeviceGather>
     */
    @SuppressLint("Range")
    fun queryDeviceTables(type: Int) = run {
        val list = ArrayList<DeviceGather>()
        getDatabase()?.let { db ->
            val sql = "SELECT * FROM ${TableName} WHERE Type=? ORDER BY [No]"
            db.rawQuery(sql, arrayOf(type.toString())).use { cursor ->
                while(cursor.moveToNext()) {
                    val dg = DeviceGather()
                    dg.No = cursor.getString(cursor.getColumnIndex("No"))
                    dg.Type = cursor.getInt(cursor.getColumnIndex("Type"))
                    dg.FullScall = cursor.getInt(cursor.getColumnIndex("FullScale"))
                    if (!cursor.isNull(cursor.getColumnIndex("LoadingRatio"))) {
                        dg.LoadingRatio = cursor.getInt(cursor.getColumnIndex("LoadingRatio"))
                    }
                    list.add(dg)
                }
            }
        }
        list
    }

    /**
     * 更新或插入设备信息
     * @param deviceGather DeviceGather 设备
     */
    fun updateDeviceTable(deviceGather: DeviceGather) {
        getDatabase()?.let { db ->
            var sql = "SELECT * FROM ${TableName} WHERE Type=? AND ([No] = ?)"
            db.rawQuery(sql, arrayOf(deviceGather.Type.toString(), deviceGather.No)).use { cursor ->
                if(cursor.moveToFirst()) {
                    sql = "UPDATE ${TableName} SET FullScale=? "

                    // 荷重传感器时还需要更新灵敏度系数
                    if (deviceGather.Type == DeviceGather.TorsionSensor) {
                        sql += ", LoadingRatio=? WHERE Type=? AND ([No] = ?)"
                        db.execSQL(sql, arrayOf<Any>(deviceGather.FullScall, deviceGather.LoadingRatio, deviceGather.Type, deviceGather.No))
                    } else {
                        sql += "WHERE Type=? AND ([No]=?)"
                        db.execSQL(sql, arrayOf(deviceGather.FullScall, deviceGather.Type, deviceGather.No))
                    }
                } else {
                    if(deviceGather.Type == DeviceGather.TorsionSensor) {
                        sql = "INSERT INTO ${TableName}([No], Type, FullScale, LoadingRatio) VALUES(?, ?, ?, ?)"
                        db.execSQL(sql, arrayOf<Any>(deviceGather.No, deviceGather.Type, deviceGather.FullScall, deviceGather.LoadingRatio))
                    } else {
                        sql = "INSERT INTO ${TableName}([No], Type, FullScale) VALUES(?, ?, ?)"
                        db.execSQL(sql, arrayOf<Any>(deviceGather.No, deviceGather.Type, deviceGather.FullScall))
                    }
                }
            }
        }
    }

    /**
     * 删除设备信息
     * @param type Int 设备类型
     * @param no String 设备名称
     */
    fun deleteDeviceTable(type:Int, no: String) {
        getDatabase()?.let { db ->
            val sql = "DELETE FROM ${TableName} WHERE Type=? AND ([No]=?)"
            db.execSQL(sql, arrayOf<Any>(type, no))
        }
    }
}