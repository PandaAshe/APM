package com.jiace.apm.core.dbf

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * @author: yw
 * @date: 2021-05-27
 * @description: Data数据库,用以存储试验数据
 */
class DBDataHelper(
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
     * @see
     */
    override fun onCreate(db: SQLiteDatabase?) {

        // 试验基本信息
        val createBasicInfo = "" +
                "CREATE TABLE BasicInfo (\n" +
                "    BasicInfoId       INTEGER   PRIMARY KEY AUTOINCREMENT,\n" +
                "    BaseInfoId        TEXT,\n" +
                "    MachineId         TEXT      NOT NULL,\n" +
                "    SampleMachineId   TEXT      NOT NULL,\n" +
                "    ProjectName       TEXT      NOT NULL,\n" +
                "    SerialNo          TEXT      NOT NULL,\n" +
                "    PileNo            TEXT      NOT NULL,\n" +
                "    BuildPosition     TEXT NOT NULL,\n" +
                "    StartTime         TIMESTAMP NOT NULL,\n" +
                "    EndTime         TIMESTAMP NOT NULL,\n" +
                "    RecordCount       INTEGER   NOT NULL,\n" +
                "    SourceParam       TEXT NOT NULL,\n" +
                "    ProjectParam      TEXT      NOT NULL,\n" +
                "    BuildParam        TEXT      NOT NULL,\n" +
                "    MonitorParam      TEXT      NOT NULL,\n" +
                "    SensorParam       TEXT      NOT NULL,\n" +
                "    GpsLongitude      INTEGER   NOT NULL,\n" +
                "    GpsLatitude       INTEGER   NOT NULL,\n" +
                "    CreateTime        TIMESTAMP NOT NULL,\n" +
                "    UpdateTime        TIMESTAMP NOT NULL,\n" +
                "    IsMonitor         INTEGER   DEFAULT 0\n" +
                ");"

        // 详测数据
        val createDetailsData = "" +
                "CREATE TABLE DetailsData (\n" +
                "    Id             INTEGER   PRIMARY KEY AUTOINCREMENT,\n" +
                "    BasicInfoId    INTEGER   NOT NULL,\n" +
                "    GUID    TEXT   NOT NULL,\n" +
                "    SampleTime     TIMESTAMP NOT NULL,\n" +
                "    CreateTime     TIMESTAMP NOT NULL,\n" +
                "    RecordCount     INTEGER   NOT NULL,\n" +
                "    TorsionSensorVoltage          INTEGER   NOT NULL,\n" +
                "    Torsion    INTEGER   NOT NULL,\n" +
                "    AngleOfDip      INTEGER   NOT NULL,\n" +
                "    Footage        INTEGER   NOT NULL,\n" +
                "    Depth    INTEGER   NOT NULL,\n" +
                "    Truns      INTEGER\n" +
                ");"

        // 试验过程中的参数变动
        val createRealTimeParam ="" +
                "CREATE TABLE RealTimeParam (\n" +
                "    Id          INTEGER   PRIMARY KEY AUTOINCREMENT,\n" +
                "    BasicInfoId INTEGER   NOT NULL,\n" +
                "    ParamType   INTEGER   NOT NULL,\n" +
                "    Param       TEXT,\n" +
                "    CreateTime  TIMESTAMP NOT NULL\n" +
                ");"

        // 暂时保存于回收站中的数据
        val createRecycle =  "" +
                "CREATE TABLE Recycle (\n" +
                "    BasicInfoId       INTEGER   PRIMARY KEY AUTOINCREMENT,\n" +
                "    BaseInfoId        TEXT,\n" +
                "    MachineId         TEXT      NOT NULL,\n" +
                "    SampleMachineId   TEXT      NOT NULL,\n" +
                "    ProjectName       TEXT      NOT NULL,\n" +
                "    SerialNo          TEXT      NOT NULL,\n" +
                "    PileNo            TEXT      NOT NULL,\n" +
                "    BuildPosition     TEXT NOT NULL,\n" +
                "    StartTime         TIMESTAMP NOT NULL,\n" +
                "    EndTime         TIMESTAMP NOT NULL,\n" +
                "    RecordCount       INTEGER   NOT NULL,\n" +
                "    SourceParam       TEXT NOT NULL,\n" +
                "    ProjectParam      TEXT      NOT NULL,\n" +
                "    BuildParam     TEXT      NOT NULL,\n" +
                "    MonitorParam  TEXT      NOT NULL,\n" +
                "    SensorParam        TEXT      NOT NULL,\n" +
                "    GpsLongitude      INTEGER   NOT NULL,\n" +
                "    GpsLatitude       INTEGER   NOT NULL,\n" +
                "    CreateTime        TIMESTAMP NOT NULL,\n" +
                "    UpdateTime        TIMESTAMP NOT NULL,\n" +
                "    IsMonitor         INTEGER   DEFAULT 0\n" +
                ");"

        val createBaseValue = "" +
                "CREATE TABLE BaseValue (\n" +
                "    Id          INTEGER   PRIMARY KEY AUTOINCREMENT,\n" +
                "    BasicInfoId INTEGER   NOT NULL,\n" +
                "    S          INTEGER,\n" +
                "    UpdateTime  TIMESTAMP NOT NULL\n" +
                ");"

        db?.let {
            it.execSQL(createBasicInfo)
            it.execSQL(createDetailsData)
            it.execSQL(createRealTimeParam)
            it.execSQL(createRecycle)
            it.execSQL(createBaseValue)
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}