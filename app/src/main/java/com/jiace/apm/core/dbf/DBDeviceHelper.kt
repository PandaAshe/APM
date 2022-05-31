package com.jiace.apm.core.dbf

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * @author: yw
 * @date: 2021-05-27
 * @description: Device数据库,用以存储配置信息,率定表,设备信息,服务器信息及当前参数
 */
class DBDeviceHelper(
    context: Context?,
    name: String?
) : SQLiteOpenHelper(context, name, null, DATABASE_VERSION) {

    companion object {
        /** 数据库版本 */
        const val DATABASE_VERSION = 1
    }

    /**
     * 创建数据表
     * @param db SQLiteDatabase
     */
    override fun onCreate(db: SQLiteDatabase?) {
        val createConfigure = "" +
                "CREATE TABLE Configure (\n" +
                "    Id    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    [Key] TEXT,\n" +
                "    Value TEXT,\n" +
                "    UpdateTime TEXT NOT NULL" +
                ");"

        val createDeviceGather = "" +
                "CREATE TABLE DeviceGather (\n" +
                "    [No]         TEXT    NOT NULL,\n" +
                "    Type         INTEGER NOT NULL,\n" +
                "    FullScale    INTEGER NOT NULL,\n" +
                "    LoadingRatio INTEGET\n" +
                ");"

        val createVirtualDevice = "" +
                "CREATE TABLE VirtualDevice (\n" +
                "    Id                   INTEGER   NOT NULL,\n" +
                "    SampleMachineId          TEXT   NOT NULL,\n" +
                "    BasicInfoId          INTEGER   NOT NULL,\n" +
                "    BuildParam        TEXT      NOT NULL,\n" +
                "    MonitorParam    TEXT      NOT NULL,\n" +
                "    SensorParam    TEXT      NOT NULL,\n" +
                "    UpdateTime           TIMESTAMP NOT NULL,\n" +
                "    IsMonitor      INTEGER   NOT NULL DEFAULT 0,\n" +
                "    ProjectParam         TEXT      NOT NULL,\n" +
                "    SampleMachineVersion INTEGER   NOT NULL\n" +
                "                                   DEFAULT 0,\n" +
                "    LastValidGps         TEXT NOT NULL\n" +
                ");"
        db?.let {
            it.execSQL(createConfigure)
            it.execSQL(createDeviceGather)
            it.execSQL(createVirtualDevice)
        }
    }

    /**
     * 数据库升级
     * @param p0 SQLiteDatabase
     * @param p1 Int
     * @param p2 Int
     */
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}