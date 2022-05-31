package com.jiace.apm.core.dbf

import android.annotation.SuppressLint
import com.jiace.apm.core.dataStruct.*
import com.jiace.apm.until.Utils
import java.util.*
import kotlin.collections.HashMap

/**
 * @author: yw
 * @date: 2021-05-28
 * @description: 对Device数据库中的VirtualDevice表进行操作
 */
object TBVirtualDeviceHelper {

    /** 列类型 */
    const val Id = 1
    const val SampleMachineId = 2
    const val BasicInfoId = 3
    const val BuildParam = 4
    const val MonitorParam = 5
    const val SensorParam = 6
    const val UpdateTime = 7
    const val IsMonitor = 8
    const val ProjectParam = 9
    const val SampleMachineVersion = 11
    const val LastValidGps = 13

    /** 列名 */
    private val ColumnNames = mapOf<Int, String>(
        Pair(Id, "Id"),
        Pair(SampleMachineId, "SampleMachineId"),
        Pair(BasicInfoId, "BasicInfoId"),
        Pair(BuildParam, "BuildParam"),
        Pair(MonitorParam, "MonitorParam"),
        Pair(SensorParam, "SensorParam"),
        Pair(UpdateTime, "UpdateTime"),
        Pair(IsMonitor, "IsMonitor"),
        Pair(ProjectParam, "ProjectParam"),
        Pair(ProjectParam, "ProjectParam"),
        Pair(SampleMachineVersion, "SampleMachineVersion"),
        Pair(LastValidGps, "LastValidGps")
    )

    /** 表名 */
    const val TableName = "VirtualDevice"

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
     * 更新参数 重载
     * @param paramType Int 参数类型
     * @param any Any 参数
     * */
    fun updateParam(paramType: Int, any:Any) {
        updateParam(1,paramType, any)
    }

    /**
     * 更新参数
     * @param id Int 前端机序号(1,2, ... N, N为允许连接的前端机个数)
     * @param paramType Int 参数类型
     * @param any Any 参数
     */
    fun updateParam(id: Int = 1, paramType: Int, any:Any) {
            if (ColumnNames.containsKey(paramType)) {
                val name = ColumnNames[paramType]
                val sql = "UPDATE $TableName SET ${name}=?, UpdateTime=? WHERE  Id=${id}"
                var value:Any? = null
                when(paramType) {
                    Id -> value = (any as Int)
                    SampleMachineId -> value = (any as String)
                    BasicInfoId -> value = (any as Long)
                    BuildParam -> value = (any as BuildParam).toString()
                    ProjectParam -> value = (any as ProjectParam).toString()
                    SensorParam -> value = (any as SensorParam).toString()
                    MonitorParam -> value = (any as MonitorParam).toString()
                    UpdateTime -> value =  Utils.formatDateTime (any as Date)
                    IsMonitor -> value = (any as Int)
                    SampleMachineVersion -> value = (any as Int)
                    LastValidGps -> value = (any as LastValidGps).toString()
                }
                if(value != null) {
                    getDatabase()?.execSQL(sql, arrayOf<Any>(value, Utils.formatDateTime(Date())))
                }
            }
    }


    /**
     * 查询所有参数,如果没有,就插入默认值,并返回
     * @param id Int 前端机序号(1,2, ... N, N为允许连接的前端机个数)
     * @return HashMap<Int, Any>
     */
    @SuppressLint("Range")
    fun queryAllParam(id:Int = 1):HashMap<Int, Any> {
        val map = HashMap<Int, Any>()
        getDatabase()?.let { db->
            var endDeviceId = "88888"
            var basicInfoId = 0L
            var buildParam = BuildParam()
            var sensorParam = SensorParam()
            var monitorParam = MonitorParam()
            var isMonitor = 0
            var projectParam = ProjectParam()
            var endDeviceSoftVersion = 0
            var lastValidGps = LastValidGps()

            var sql = "SELECT * FROM $TableName WHERE Id=${id}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    endDeviceId = cursor.getString(cursor.getColumnIndex("SampleMachineId"))
                    basicInfoId = cursor.getLong(cursor.getColumnIndex("BasicInfoId"))
                    buildParam = com.jiace.apm.core.dataStruct.BuildParam.fromJson(cursor.getString(cursor.getColumnIndex("BuildParam")))
                    sensorParam = com.jiace.apm.core.dataStruct.SensorParam.fromJson(cursor.getString(cursor.getColumnIndex("SensorParam")))
                    monitorParam = com.jiace.apm.core.dataStruct.MonitorParam.fromJson(cursor.getString(cursor.getColumnIndex("MonitorParam")))
                    isMonitor = cursor.getInt(cursor.getColumnIndex("IsMonitor"))
                    projectParam = com.jiace.apm.core.dataStruct.ProjectParam.fromJSon(cursor.getString(cursor.getColumnIndex("ProjectParam")))
                    endDeviceSoftVersion = cursor.getInt(cursor.getColumnIndex("SampleMachineVersion"))
                    lastValidGps = com.jiace.apm.core.dataStruct.LastValidGps.fromJSon(cursor.getString(cursor.getColumnIndex("LastValidGps")))
                } else {
                    sql = "INSERT INTO ${TableName}(ID, SampleMachineId, BasicInfoId, BuildParam, MonitorParam, SensorParam, UpdateTime, IsMonitor, ProjectParam, SampleMachineVersion, LastValidGps) " +
                            "Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                    val values = arrayOf<Any>(
                            id,
                            endDeviceId,
                            basicInfoId,
                            buildParam.toString(),
                            monitorParam.toString(),
                            sensorParam.toString(),
                            Utils.formatDateTime(Date()),
                            isMonitor,
                            projectParam.toString(),
                            endDeviceSoftVersion,
                            lastValidGps.toString()
                    )
                    db.execSQL(sql, values)
                }
                map[Id] = id
                map[SampleMachineId] = endDeviceId
                map[BasicInfoId] = basicInfoId
                map[BuildParam] = buildParam
                map[MonitorParam] = monitorParam
                map[SensorParam] = sensorParam
                map[IsMonitor] = isMonitor
                map[ProjectParam] = projectParam
                map[SampleMachineVersion] = endDeviceSoftVersion
                map[LastValidGps] = lastValidGps
            }
        }
        return map
    }
}