package com.jiace.apm.ui.param

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat.getColor
import com.jiace.apm.R
import com.jiace.apm.core.ConfigureHelper
import com.jiace.apm.core.ParamCloneHelper
import com.jiace.apm.core.dataStruct.*
import com.jiace.apm.until.getString
import kotlinx.android.synthetic.main.param_layout.view.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/10.
3 * Description:
4 *
5 */
class ParamAdapter(val context: Context, private val mode: ParamCloneHelper.Mode): BaseAdapter() {


    private val mParams = LinkedHashMap<Int, Param>()

    init {
        init()
    }

    private fun init() {
        mParams.clear()
        mParams.apply {
            when (mode) {
                ParamCloneHelper.Mode.Project -> {
                    put(ProjectParam.Id_ProjectName,Param(getString(R.string.param_project_name), ParamCloneHelper.mProjectParam.ProjectName, ProjectParam.Id_ProjectName,true))
                    put(ProjectParam.Id_PileNo,Param(getString(R.string.param_pile_no), ParamCloneHelper.mProjectParam.PileNo, ProjectParam.Id_PileNo,true))
                    put(ProjectParam.Id_SerialNo,Param(getString(R.string.param_serial_no), ParamCloneHelper.mProjectParam.SerialNo, ProjectParam.Id_SerialNo,true))
                    put(ProjectParam.Id_BaseNo,Param(getString(R.string.param_base_no), ParamCloneHelper.mProjectParam.BaseNo, ProjectParam.Id_BaseNo,true))
                    put(ProjectParam.Id_BaseAnchorNo,Param(getString(R.string.param_base_anchor_no), ParamCloneHelper.mProjectParam.BaseAnchorNo, ProjectParam.Id_BaseAnchorNo,true))
                    put(ProjectParam.Id_TallNo,Param(getString(R.string.param_tall_no), ParamCloneHelper.mProjectParam.TallNo, ProjectParam.Id_TallNo,true))
                    put(ProjectParam.Id_BuildPosition,Param(getString(R.string.param_build_position), ParamCloneHelper.mProjectParam.BuildPosition, ProjectParam.Id_BuildPosition,true))
                }

                ParamCloneHelper.Mode.BuildParam -> {
                    put(BuildParam.Id_MachineType,Param(getString(R.string.param_machine_type), ParamCloneHelper.mBuildParam.MachineType, BuildParam.Id_MachineType,true))
                    put(BuildParam.Id_MachineNo,Param(getString(R.string.param_machine_no), ParamCloneHelper.mBuildParam.MachineNo, BuildParam.Id_MachineNo,true))
                    put(BuildParam.Id_AnchorDiameter,Param(getString(R.string.param_anchor_diameter), ParamCloneHelper.mBuildParam.AnchorDiameter, BuildParam.Id_AnchorDiameter,true))
                    put(BuildParam.Id_AnchorPlateNo,Param(getString(R.string.param_anchor_plate_no), ParamCloneHelper.mBuildParam.AnchorPlateNo, BuildParam.Id_AnchorPlateNo,true))
                    put(BuildParam.Id_AnchorPlateCount,Param(getString(R.string.param_plate_count), ParamCloneHelper.mBuildParam.AnchorPlateCount, BuildParam.Id_AnchorPlateCount,true))
                }

                ParamCloneHelper.Mode.DesignBuildParam -> {
                    put(BuildParam.Id_DesignDepth,Param(getString(R.string.param_design_depth), ParamCloneHelper.mBuildParam.DesignDepth, BuildParam.Id_DesignDepth,true))
                    put(BuildParam.Id_DesignOutcrop,Param(getString(R.string.param_design_outcrop), ParamCloneHelper.mBuildParam.DesignOutcrop, BuildParam.Id_DesignOutcrop,true))
                    put(BuildParam.Id_DesignAngleOfDip,Param(getString(R.string.param_design_angle), ParamCloneHelper.mBuildParam.DesignAngleOfDip, BuildParam.Id_DesignAngleOfDip,true))
                    put(BuildParam.Id_DesignDirection,Param(getString(R.string.param_design_direction), ParamCloneHelper.mBuildParam.DesignDirection, BuildParam.Id_DesignDirection,true))
                    put(BuildParam.Id_λB,Param(getString(R.string.param_λB), ParamCloneHelper.mBuildParam.λB, BuildParam.Id_λB,true))
                }

                ParamCloneHelper.Mode.MonitorParam -> {
                    put(MonitorParam.Id_MonitorType,Param(getString(R.string.param_record_type), ParamCloneHelper.mMonitorParam.MonitorType, MonitorParam.Id_MonitorType,true))

                    if (ParamCloneHelper.mMonitorParam.MonitorType == MonitorParam.MonitorType_Time) {
                        put(MonitorParam.Id_RecordInterval,Param(getString(R.string.param_record_time_interval), ParamCloneHelper.mMonitorParam.RecordInterval, MonitorParam.Id_RecordInterval,true))
                    } else {
                        put(MonitorParam.Id_RecordInterval,Param(getString(R.string.param_record_depth_interval), ParamCloneHelper.mMonitorParam.RecordInterval, MonitorParam.Id_RecordInterval,true))
                    }
                }

                ParamCloneHelper.Mode.DesignMonitorParam -> {
                    put(MonitorParam.Id_TorsionMax,Param(getString(R.string.param_torsion_max), ParamCloneHelper.mMonitorParam.TorsionMax, MonitorParam.Id_TorsionMax,true))
                    put(MonitorParam.Id_TorsionMin,Param(getString(R.string.param_torsion_min), ParamCloneHelper.mMonitorParam.TorsionMin, MonitorParam.Id_RecordInterval,true))
                    put(MonitorParam.Id_FootageMax,Param(getString(R.string.param_footage_max), ParamCloneHelper.mMonitorParam.FootageMax, MonitorParam.Id_FootageMax,true))
                    put(MonitorParam.Id_FootageMin,Param(getString(R.string.param_footage_min), ParamCloneHelper.mMonitorParam.FootageMin, MonitorParam.Id_FootageMin,true))
                    put(MonitorParam.Id_AngleOfDipMax,Param(getString(R.string.param_angle_max), ParamCloneHelper.mMonitorParam.AngleOfDipMax, MonitorParam.Id_AngleOfDipMax,true))
                }

                ParamCloneHelper.Mode.SensorParam -> {
                    put(SensorParam.Id_SampleMachineId,Param(getString(R.string.param_sample_machine_no), ParamCloneHelper.mSensorParam.SampleMachineId, SensorParam.Id_SampleMachineId,true))
                    put(SensorParam.Id_AngleOfDipSensorId,Param(getString(R.string.param_angle_sensor_no), ParamCloneHelper.mSensorParam.AngleOfDipSensorId, SensorParam.Id_AngleOfDipSensorId,true))
                    put(SensorParam.Id_TorsionSensorId,Param(getString(R.string.param_torsion_sensor_no), ParamCloneHelper.mSensorParam.TorsionSensorId, SensorParam.Id_TorsionSensorId,true))
                    put(SensorParam.Id_TorsionBluetoothNo,Param(getString(R.string.param_torsion_bluetooth_no), ParamCloneHelper.mSensorParam.TorsionBluetoothNo, SensorParam.Id_TorsionBluetoothNo,true))
                    put(SensorParam.Id_DisplacementSensorId,Param(getString(R.string.param_displacement_no), ParamCloneHelper.mSensorParam.DisplacementSensorId, SensorParam.Id_DisplacementSensorId,true))
                }

                ParamCloneHelper.Mode.MainParam -> {
                    put(ConfigureHelper.Id_MachineId,Param(getString(R.string.param_machine_no), ConfigureHelper.MachineId, ConfigureHelper.Id_MachineId,true))
                    put(ConfigureHelper.Id_Version,Param(getString(R.string.param_version_name), "1.0", ConfigureHelper.Id_Version,true))
                }
            }
        }
    }

    fun updateParam() {
        init()
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mParams.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val bootView = convertView ?: View.inflate(context, R.layout.param_layout,null)

        if (isShowOneLineParam(mParams.keys.toTypedArray()[position])) {
            bootView.tag = mParams[mParams.keys.toTypedArray()[position]]
            bootView.twoLineLayout.visibility = View.GONE
            bootView.oneLineLayout.visibility = View.VISIBLE

            bootView.paramTitle.text = mParams[mParams.keys.toTypedArray()[position]]!!.paramTitle

            ParamCloneHelper.showUserInterfaceText(mParams[mParams.keys.toTypedArray()[position]]!!,mode).let {
                bootView.paramText.text = it
            }

            if (mParams[mParams.keys.toTypedArray()[position]]!!.isEditable) {
                bootView.tag = mParams[mParams.keys.toTypedArray()[position]]
                bootView.paramText.setTextColor(getColor(context,R.color.teal_200))

                if (ParamCloneHelper.isEditable(mode,mParams.keys.toTypedArray()[position])) {
                    bootView.paramText.setTextColor(getColor(context,R.color.teal_200))
                } else {
                    bootView.paramText.setTextColor(getColor(context,R.color.gray_60))
                }
            } else {
                bootView.tag = null
                bootView.paramText.setTextColor(getColor(context,R.color.gray_60))
            }
        }


        return bootView
    }

    /**
     * 判断是否为单行的参数显示
     * */
    private fun isShowOneLineParam(paramId: Int) = true

}