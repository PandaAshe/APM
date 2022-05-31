package com.jiace.apm.core.dataStruct

import com.jiace.apm.R
import com.jiace.apm.core.dbf.TBBasicInfoHelper
import com.jiace.apm.core.dbf.TBDetailsDataHelper
import com.jiace.apm.databinding.ObservableKeyedArrayList
import com.jiace.apm.until.d
import com.jiace.apm.until.getString
import org.json.JSONObject
import java.io.Closeable
import java.lang.Exception
import kotlin.system.measureTimeMillis

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/11.
3 * Description:
4 *
5 */
class Doc: Closeable {

    /** 基本信息 */
    var mBasicInfo = BasicInfo()

    /** 原始数据 */
    val mSourceData = ObservableKeyedArrayList<String,Record>()


    constructor(info: BasicInfo) {
        // 保存到数据库
        this.mBasicInfo = info
        saveDataToDbf(this.mBasicInfo)
    }

    constructor(basicInfoId: Long) {
        if (basicInfoId > 0) {
            measureTimeMillis {
                loadDataFromDbf(basicInfoId)
            }.let {
                d(TAG, "loadDataFromDbf coast: ${it} ms")
            }
        } else {
            throw Exception("BasicInfoId 不能小于0!")
        }
    }


    /** 从数据库中取出数据 */
    private fun loadDataFromDbf(basicInfoId: Long) {
        TBBasicInfoHelper.queryBasicInfo(basicInfoId)?.let {
            mBasicInfo = it
        }
        // mBasicInfo.mRecordCount = 0
        TBDetailsDataHelper.queryDetailsDatas(basicInfoId).let {
            it.forEach { currentData ->
                addOneData(currentData)
            }
        }
    }

    /**
     *  保存到数据库
     * */
    private fun saveDataToDbf(info: BasicInfo) {
        TBBasicInfoHelper.insertBasicInfo(info)
    }


    /** 获取最后一个数据 */
    fun getLastRecord(): Record {
        var lastRecord = Record()
        if(mSourceData.size > 0) {
            lastRecord = mSourceData.last()
        }
        return lastRecord
    }

    /**添加一个数据 */
    fun addOneData(record: Record): Record {
        val data = record.clone()
        val lastRecord = getLastRecord()

        if (ErrorHelper.isErrorValue(record.Depth)) {
            data.Depth = getLastValidDistance()
        }

        if (ErrorHelper.isErrorValue(record.Torsion)) {
            data.Torsion = lastRecord.Torsion
        }

        if (ErrorHelper.isErrorValue(record.TorsionSensorVoltage)) {
            data.TorsionSensorVoltage = lastRecord.TorsionSensorVoltage
        }

        if (ErrorHelper.isErrorValue(record.Footage)) {
            data.Footage = lastRecord.Footage
        }

        if (ErrorHelper.isErrorValue(record.AngleOfDip)) {
            data.AngleOfDip = lastRecord.AngleOfDip
        }

        mSourceData.add(data)
        return data
    }


    /**
     * 获取最后一次有效的通道位移
     * @return Int 位移(um)
     */
    private fun getLastValidDistance():Int {
        var value: Int
        getLastRecord().let {
            value = it.Depth
        }
        return value
    }

    override fun close() {

    }

    /** 导出到excel */
    fun saveToExcel(): ByteArray {
        return byteArrayOf()
    }

    /** 生存参数信息摘要 */
    fun getBasicInfoSummary(): ArrayList<SummaryItem> {

        val summaryList = ArrayList<SummaryItem>()
        summaryList.let {
            val sourceParam = JSONObject(mBasicInfo.mSourceParam)
            it.add(SummaryItem(0,"基本信息"))
            val basicParam = ProjectParam.fromJSon(sourceParam.getJSONObject("ProjectParam").toString())
            basicParam.apply {
                it.add(SummaryItem(1,"${getString(R.string.param_project_name)}: $ProjectName"))
                it.add(SummaryItem(1,"${getString(R.string.param_pile_no)}: $PileNo"))
                it.add(SummaryItem(1,"${getString(R.string.param_serial_no)}: $SerialNo"))
                it.add(SummaryItem(1,"${getString(R.string.param_base_no)}: $BaseNo"))
                it.add(SummaryItem(1,"${getString(R.string.param_base_anchor_no)}: $BaseAnchorNo"))
                it.add(SummaryItem(1,"${getString(R.string.param_tall_no)}: $TallNo"))
                it.add(SummaryItem(1,"${getString(R.string.param_build_position)}: $BuildPosition"))
            }

            val buildParam = BuildParam.fromJson(sourceParam.getJSONObject("BuildParam").toString())

            it.add(SummaryItem(0,"施工参数"))
            buildParam.apply {
                it.add(SummaryItem(1,"${getString(R.string.param_machine_type)}: $MachineType"))
                it.add(SummaryItem(1,"${getString(R.string.param_machine_no)}: $MachineNo"))
                it.add(SummaryItem(1,"${getString(R.string.param_anchor_diameter)}: ${AnchorDiameter / 1000f}"))
                it.add(SummaryItem(1,"${getString(R.string.param_anchor_plate_no)}: $AnchorPlateNo"))
                it.add(SummaryItem(1,"${getString(R.string.param_plate_count)}: $AnchorPlateCount"))
            }

            it.add(SummaryItem(0,"设计参数"))
            buildParam.apply {
                it.add(SummaryItem(1,"${getString(R.string.param_design_depth)}: $DesignDepth"))
                it.add(SummaryItem(1,"${getString(R.string.param_design_outcrop)}: $DesignOutcrop"))
                it.add(SummaryItem(1,"${getString(R.string.param_design_angle)}: ${DesignAngleOfDip / 10f}"))
                it.add(SummaryItem(1,"${getString(R.string.param_design_direction)}: $DesignDirection"))
                it.add(SummaryItem(1,"${getString(R.string.param_λB)}: $λB"))
            }

            val monitorParam = MonitorParam.fromJson(sourceParam.getJSONObject("MonitorParam").toString())
            it.add(SummaryItem(0,"监测参数"))
            monitorParam.apply {
                it.add(SummaryItem(1,"${getString(R.string.param_torsion_max)}: ${TorsionMax / 1000f}"))
                it.add(SummaryItem(1,"${getString(R.string.param_torsion_min)}: ${TorsionMin / 1000f}"))
                it.add(SummaryItem(1,"${getString(R.string.param_footage_max)}: $FootageMax"))
                it.add(SummaryItem(1,"${getString(R.string.param_footage_min)}: $FootageMin"))
                it.add(SummaryItem(1,"${getString(R.string.param_angle_max)}: ${AngleOfDipMax / 10f}"))
            }

            val machineParam = SensorParam.fromJson(sourceParam.getJSONObject("SensorParam").toString())

            it.add(SummaryItem(0,"设备信息"))
            machineParam.apply {
                it.add(SummaryItem(1,"${getString(R.string.param_sample_machine_no)}: $SampleMachineId"))
                it.add(SummaryItem(1,"${getString(R.string.param_angle_sensor_no)}: $AngleOfDipSensorId"))
                it.add(SummaryItem(1,"${getString(R.string.param_torsion_sensor_no)}: $TorsionSensorId"))
                it.add(SummaryItem(1,"${getString(R.string.param_torsion_bluetooth_no)}: $TorsionBluetoothNo"))
                it.add(SummaryItem(1,"${getString(R.string.param_displacement_no)}: $DisplacementSensorId"))
            }
        }
        return summaryList
    }


    /** 获取汇总表 */
    fun getSummaryTable(): ArrayList<Record> {
        val tempList = ArrayList<Record>()
        // 最大深度
        val maxDepth = mSourceData.maxByOrNull { it.Depth }?.Depth ?: 100
        val tempDepth = (maxDepth / 50 + 1) * 50
        for (i in 1 .. tempDepth / 50) {
            tempList.add(mSourceData.last { it.Depth == i * 50 })
        }
        return tempList
    }



    companion object {
        private const val TAG = "doc"
    }

    class SummaryItem(val level: Int,val param: String)

}