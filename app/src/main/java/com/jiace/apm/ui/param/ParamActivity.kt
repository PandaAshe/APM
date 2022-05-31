package com.jiace.apm.ui.param

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import com.jiace.apm.Application
import com.jiace.apm.R
import com.jiace.apm.base.BaseActivity
import com.jiace.apm.common.dialog.EditTextDialog
import com.jiace.apm.core.ConfigureHelper
import com.jiace.apm.core.ParamCloneHelper
import com.jiace.apm.core.ParamHelper
import com.jiace.apm.core.dataStruct.*
import com.jiace.apm.core.dbf.TBDeviceGatherHelper
import com.jiace.apm.until.ChoosePopupWindow
import com.jiace.apm.until.applicationScope
import com.jiace.apm.until.showCenterToast
import com.jiace.apm.until.toIntValue
import kotlinx.android.synthetic.main.activity_params.*
import kotlinx.android.synthetic.main.activity_params.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/9.
3 * Description:
4 *
5 */
class ParamActivity: BaseActivity() {

    private lateinit var mSideBarAdapter: SideBarAdapter

    private lateinit var mProjectParamAdapter: ParamAdapter
    private lateinit var mBuilderParamAdapter: ParamAdapter
    private lateinit var mDesignParamAdapter: ParamAdapter
    private lateinit var mMonitorParamAdapter: ParamAdapter
    private lateinit var mMonitorWarringAdapter: ParamAdapter
    private lateinit var mSensorParamAdapter: ParamAdapter
    private lateinit var mMainParamAdapter: ParamAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ParamCloneHelper.init()
        setContentView(R.layout.activity_params)
        initViews()

    }

    // 初始化view
    private fun initViews() {
        mSideBarAdapter = SideBarAdapter(this)
        sideBarList.adapter = mSideBarAdapter

        sideBarList.setOnItemClickListener { _, _, position, _ ->
            mSideBarAdapter.checkPosition = position
            ParamHelper.mCheckPosition = position
            checkParamPage(mSideBarAdapter.checkPosition)
        }

        mSideBarAdapter.checkPosition = ParamHelper.mCheckPosition
        mSideBarAdapter.notifyDataSetChanged()
        checkParamPage(ParamHelper.mCheckPosition)

        mProjectParamAdapter = ParamAdapter(this,ParamCloneHelper.Mode.Project)
        gridViewProject.adapter = mProjectParamAdapter
        gridViewProject.onItemClickListener = mProjectParamItemClickListener

        mBuilderParamAdapter = ParamAdapter(this,ParamCloneHelper.Mode.BuildParam)
        gridViewBuildParam.adapter = mBuilderParamAdapter
        gridViewBuildParam.onItemClickListener = mBuildParamItemClickListener

        mDesignParamAdapter = ParamAdapter(this,ParamCloneHelper.Mode.DesignBuildParam)
        gridViewBuildDesignParam.adapter = mDesignParamAdapter
        gridViewBuildDesignParam.onItemClickListener = mDesignBuildParamItemClickListener

        mMonitorParamAdapter = ParamAdapter(this,ParamCloneHelper.Mode.MonitorParam)
        gridViewStandardMonitorType.adapter = mMonitorParamAdapter
        gridViewStandardMonitorType.onItemClickListener = mMonitorParamItemClickListener

        mMonitorWarringAdapter = ParamAdapter(this,ParamCloneHelper.Mode.DesignMonitorParam)
        gridViewMonitorParam.adapter = mMonitorWarringAdapter
        gridViewMonitorParam.onItemClickListener = mMonitorWarringParamItemClickListener

        mSensorParamAdapter = ParamAdapter(this,ParamCloneHelper.Mode.SensorParam)
        gridViewSensorParam.adapter = mSensorParamAdapter
        gridViewSensorParam.onItemClickListener = mSensorParamItemClickListener

        mMainParamAdapter = ParamAdapter(this,ParamCloneHelper.Mode.MainParam)
        gridViewMainDevice.adapter = mMainParamAdapter
        gridViewMainDevice.onItemClickListener = mMainDeviceParamItemClickListener

    }

    /**  */
    fun onClickBack(view: View) {
        checkParam()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        checkParam()
    }

    private fun checkParam() {
        flow{
            ParamCloneHelper.checkParam()
            emit(true)
        }.flowOn(Dispatchers.IO).catch { e ->
            showCenterToast(Application.get(),e.message.toString())
        }.onEach {
            ParamHelper.updateParams()
        }.launchIn(applicationScope)
    }



    private fun checkParamPage(checkPosition: Int) {

        when (checkPosition) {

            SideBarAdapter.Project -> {
                projectParamLayout.visibility = View.VISIBLE
                builderLayout.visibility = View.GONE
                monitorParamLayout.visibility = View.GONE
                sensorParamLayout.visibility = View.GONE
                deviceLayout.visibility = View.GONE
            }

            SideBarAdapter.Build -> {
                projectParamLayout.visibility = View.GONE
                builderLayout.visibility = View.VISIBLE
                monitorParamLayout.visibility = View.GONE
                sensorParamLayout.visibility = View.GONE
                deviceLayout.visibility = View.GONE
            }

            SideBarAdapter.Monitor -> {
                projectParamLayout.visibility = View.GONE
                builderLayout.visibility = View.GONE
                monitorParamLayout.visibility = View.VISIBLE
                sensorParamLayout.visibility = View.GONE
                deviceLayout.visibility = View.GONE
            }

            SideBarAdapter.Sensor -> {
                projectParamLayout.visibility = View.GONE
                builderLayout.visibility = View.GONE
                monitorParamLayout.visibility = View.GONE
                sensorParamLayout.visibility = View.VISIBLE
                deviceLayout.visibility = View.GONE
            }

            SideBarAdapter.DeviceGather -> {
                projectParamLayout.visibility = View.GONE
                builderLayout.visibility = View.GONE
                monitorParamLayout.visibility = View.GONE
                sensorParamLayout.visibility = View.GONE
                deviceLayout.visibility = View.VISIBLE
            }

        }
    }

    /** 工程信息 */
    private val mProjectParamItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
        if (view == null)
            return@OnItemClickListener
        if (view.tag == null)
            return@OnItemClickListener
        val param = view.tag as Param

        if (!param.isEditable)
            return@OnItemClickListener

        if (!ParamCloneHelper.isEditable(ParamCloneHelper.Mode.Project,param.paramId))
            return@OnItemClickListener

        showInputDialog(InputType.TYPE_CLASS_TEXT, ParamCloneHelper.Mode.Project, param)
    }

    /** 施工参数 */
    private val mBuildParamItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
        if (view == null)
            return@OnItemClickListener
        if (view.tag == null)
            return@OnItemClickListener
        val param = view.tag as Param

        if (!param.isEditable)
            return@OnItemClickListener

        if (!ParamCloneHelper.isEditable(ParamCloneHelper.Mode.BuildParam,param.paramId))
            return@OnItemClickListener

        when (param.paramId) {
            BuildParam.Id_MachineNo,BuildParam.Id_MachineType,BuildParam.Id_AnchorPlateNo -> {
                showInputDialog(InputType.TYPE_CLASS_TEXT, ParamCloneHelper.Mode.BuildParam, param)
            }
            else -> {
                showInputDialog(InputType.TYPE_CLASS_NUMBER, ParamCloneHelper.Mode.BuildParam, param)
            }
        }


    }

    /** 设计施工参数 */
    private val mDesignBuildParamItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
        if (view == null)
            return@OnItemClickListener
        if (view.tag == null)
            return@OnItemClickListener
        val param = view.tag as Param

        if (!param.isEditable)
            return@OnItemClickListener

        if (!ParamCloneHelper.isEditable(ParamCloneHelper.Mode.DesignBuildParam,param.paramId))
            return@OnItemClickListener
        showInputDialog(InputType.TYPE_NUMBER_FLAG_DECIMAL, ParamCloneHelper.Mode.DesignBuildParam, param)
    }

    /** 监测参数 */
    private val mMonitorParamItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
        if (view == null)
            return@OnItemClickListener
        if (view.tag == null)
            return@OnItemClickListener
        val param = view.tag as Param

        if (!param.isEditable)
            return@OnItemClickListener

        if (!ParamCloneHelper.isEditable(ParamCloneHelper.Mode.MonitorParam,param.paramId)) {
            return@OnItemClickListener
        }

        when (param.paramId) {
            MonitorParam.Id_MonitorType -> {
                ChoosePopupWindow(this,param.paramValue.toIntValue(),MonitorParam.MonitorTypeText) {
                    param.paramValue = it
                    freshParam(param,ParamCloneHelper.Mode.MonitorParam)
                }.showAsDropDown(view)
            }

            MonitorParam.Id_RecordInterval -> {
                showInputDialog(InputType.TYPE_CLASS_NUMBER, ParamCloneHelper.Mode.MonitorParam, param)
            }
        }

    }

    /** 设计监测参数 */
    private val mMonitorWarringParamItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
        if (view == null)
            return@OnItemClickListener
        if (view.tag == null)
            return@OnItemClickListener
        val param = view.tag as Param

        if (!param.isEditable)
            return@OnItemClickListener

        when (param.paramId) {
            MonitorParam.Id_AngleOfDipMax,
            MonitorParam.Id_TorsionMin,
            MonitorParam.Id_TorsionMax -> {
                showInputDialog(InputType.TYPE_NUMBER_FLAG_DECIMAL, ParamCloneHelper.Mode.DesignMonitorParam, param)
            }

            else -> {
                showInputDialog(InputType.TYPE_CLASS_NUMBER, ParamCloneHelper.Mode.DesignMonitorParam, param)
            }
        }

    }

    /** 传感器 */
    private val mSensorParamItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
        if (view == null)
            return@OnItemClickListener
        if (view.tag == null)
            return@OnItemClickListener
        val param = view.tag as Param

        if (!param.isEditable)
            return@OnItemClickListener


        val chooseDeviceType = when (param.paramId) {
            SensorParam.Id_TorsionSensorId -> {
                DeviceGather.TorsionSensor
            }
            SensorParam.Id_AngleOfDipSensorId -> {
                DeviceGather.AngleOfDipSensor
            }
            SensorParam.Id_SampleMachineId -> {
                DeviceGather.SampleMachine
            }

            SensorParam.Id_DisplacementSensorId -> {
                DeviceGather.Displacement
            }
            else -> DeviceGather.SampleMachine
        }

        val items = TBDeviceGatherHelper.queryDeviceTables(chooseDeviceType).flatMap { listOf(it.No) }.toTypedArray()


        val index = items.indexOfFirst { it == param.paramValue }

        ChoosePopupWindow(this,index,items) { which ->
            when (param.paramId) {
                SensorParam.Id_TorsionSensorId -> {
                    ParamCloneHelper.mSensorParam.TorsionSensorId = items[which]
                }

                SensorParam.Id_AngleOfDipSensorId -> {
                    ParamCloneHelper.mSensorParam.AngleOfDipSensorId = items[which]
                }

                SensorParam.Id_SampleMachineId -> {
                    ParamCloneHelper.mSensorParam.SampleMachineId = items[which]
                }

                SensorParam.Id_DisplacementSensorId -> {
                    ParamCloneHelper.mSensorParam.DisplacementSensorId = items[which]
                }
            }
            mSensorParamAdapter.updateParam()
        }.showAsDropDown(view)


    }

    /** 主机 */
    private val mMainDeviceParamItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->

        if (view == null)
            return@OnItemClickListener
        if (view.tag == null)
            return@OnItemClickListener
        val param = view.tag as Param

        if (!param.isEditable)
            return@OnItemClickListener
        if (param.paramId == ConfigureHelper.Id_MachineId) {
            // todo d
        }
    }

    private fun showInputDialog(inputType: Int, paramType: ParamCloneHelper.Mode, param: Param) {
        val oldValue = ParamCloneHelper.showUserInterfaceText(param,paramType)
        val editTextDialog = EditTextDialog(this)
        editTextDialog.apply {
            setText(oldValue)
            setEditType(inputType)
            setTitle("请输入${param.paramTitle}")
            setCancelable(false)
            setOnPositionListener {
                if (it.isEmpty()) {
                    showCenterToast(context, "参数不能为空")
                    return@setOnPositionListener
                }
                try {
                    param.paramValue = ParamCloneHelper.handlerUserInputText(it,param,paramType)
                } catch (e: Exception) {
                    showCenterToast(context,"参数输入错误")
                    return@setOnPositionListener
                }
                if (freshParam(param, paramType)) {
                    dismiss()
                }
                return@setOnPositionListener
            }
            show()
        }
    }

    private fun freshParam(param: Param, paramType: ParamCloneHelper.Mode): Boolean {
        when (paramType) {
            ParamCloneHelper.Mode.Project -> {
                when (param.paramId) {
                    ProjectParam.Id_TallNo -> {
                        ParamCloneHelper.mProjectParam.TallNo = param.paramValue.toString()
                    }
                    ProjectParam.Id_SerialNo -> {
                        ParamCloneHelper.mProjectParam.SerialNo = param.paramValue.toString()
                    }
                    ProjectParam.Id_BaseAnchorNo -> {
                        ParamCloneHelper.mProjectParam.BaseAnchorNo = param.paramValue.toString()
                    }
                    ProjectParam.Id_BaseNo -> {
                        ParamCloneHelper.mProjectParam.BaseNo = param.paramValue.toString()
                    }
                    ProjectParam.Id_BuildPosition -> {
                        ParamCloneHelper.mProjectParam.BuildPosition = param.paramValue.toString()
                    }
                    ProjectParam.Id_ProjectName -> {
                        ParamCloneHelper.mProjectParam.ProjectName = param.paramValue.toString()
                    }
                    ProjectParam.Id_PileNo -> {
                        ParamCloneHelper.mProjectParam.PileNo = param.paramValue.toString()
                    }
                }
                mProjectParamAdapter.updateParam()
                return try {
                    ParamCloneHelper.mProjectParam.isParamCorrect()
                    true
                } catch (e: Exception) {
                    showCenterToast(this,e.message.toString())
                    false
                }
            }

            ParamCloneHelper.Mode.BuildParam -> {
                when (param.paramId) {
                    BuildParam.Id_MachineType -> {
                        ParamCloneHelper.mBuildParam.MachineType = param.paramValue.toString()
                    }

                    BuildParam.Id_MachineNo -> {
                        ParamCloneHelper.mBuildParam.MachineNo = param.paramValue.toString()
                    }

                    BuildParam.Id_AnchorDiameter -> {
                        ParamCloneHelper.mBuildParam.AnchorDiameter = param.paramValue.toIntValue()
                    }

                    BuildParam.Id_AnchorPlateNo -> {
                        ParamCloneHelper.mBuildParam.AnchorPlateNo = param.paramValue.toString()
                    }

                    BuildParam.Id_AnchorPlateCount -> {
                        ParamCloneHelper.mBuildParam.AnchorPlateCount = param.paramValue.toIntValue()
                    }
                }

                mBuilderParamAdapter.updateParam()
                return try {
                    ParamCloneHelper.mBuildParam.isParamCorrect()
                    true
                } catch (e: Exception) {
                    showCenterToast(this,e.message.toString())
                    false
                }

            }


            ParamCloneHelper.Mode.DesignBuildParam -> {
                when (param.paramId) {

                    BuildParam.Id_λB -> {
                        ParamCloneHelper.mBuildParam.λB = param.paramValue.toString().toFloat()
                    }

                    BuildParam.Id_DesignDirection -> {
                        ParamCloneHelper.mBuildParam.DesignDirection = param.paramValue.toIntValue()
                    }

                    BuildParam.Id_DesignAngleOfDip -> {
                        ParamCloneHelper.mBuildParam.DesignAngleOfDip = param.paramValue.toIntValue()
                    }

                    BuildParam.Id_DesignOutcrop -> {
                        ParamCloneHelper.mBuildParam.DesignOutcrop = param.paramValue.toIntValue()
                    }

                    BuildParam.Id_DesignDepth -> {
                        ParamCloneHelper.mBuildParam.DesignDepth = param.paramValue.toIntValue()
                    }
                }

                mDesignParamAdapter.updateParam()

                return try {
                    ParamCloneHelper.mBuildParam.isParamCorrect()
                    true
                } catch (e: Exception) {
                    showCenterToast(this,e.message.toString())
                    false
                }



            }

            ParamCloneHelper.Mode.MonitorParam -> {
                when (param.paramId) {
                    MonitorParam.Id_MonitorType -> {
                        ParamCloneHelper.mMonitorParam.MonitorType = param.paramValue.toIntValue()
                    }

                    MonitorParam.Id_RecordInterval -> {
                        ParamCloneHelper.mMonitorParam.RecordInterval = param.paramValue.toIntValue()
                    }
                }

                mMonitorParamAdapter.updateParam()

                return try {
                    ParamCloneHelper.mMonitorParam.isParamCorrect()
                    true
                } catch (e: Exception) {
                    showCenterToast(this,e.message.toString())
                    false
                }

            }

            ParamCloneHelper.Mode.DesignMonitorParam -> {
                when (param.paramId) {
                    MonitorParam.Id_FootageMax -> {
                        ParamCloneHelper.mMonitorParam.FootageMax = param.paramValue.toIntValue()
                    }

                    MonitorParam.Id_FootageMin -> {
                        ParamCloneHelper.mMonitorParam.FootageMin = param.paramValue.toIntValue()
                    }

                    MonitorParam.Id_TorsionMin -> {
                        ParamCloneHelper.mMonitorParam.TorsionMin = param.paramValue.toIntValue()
                    }

                    MonitorParam.Id_TorsionMax -> {
                        ParamCloneHelper.mMonitorParam.TorsionMax = param.paramValue.toIntValue()
                    }

                    MonitorParam.Id_AngleOfDipMax -> {
                        ParamCloneHelper.mMonitorParam.AngleOfDipMax = param.paramValue.toIntValue()
                    }
                }

                mMonitorWarringAdapter.updateParam()

                return try {
                    ParamCloneHelper.mMonitorParam.isParamCorrect()
                    true
                } catch (e: Exception) {
                    showCenterToast(this,e.message.toString())
                    false
                }
            }


            ParamCloneHelper.Mode.SensorParam -> {
                when (param.paramId) {
                    SensorParam.Id_DisplacementSensorId -> {
                        ParamCloneHelper.mSensorParam.DisplacementSensorId = param.paramValue.toString()
                    }

                    SensorParam.Id_SampleMachineId -> {
                        ParamCloneHelper.mSensorParam.SampleMachineId = param.paramValue.toString()
                    }

                    SensorParam.Id_TorsionSensorId -> {
                        ParamCloneHelper.mSensorParam.TorsionSensorId = param.paramValue.toString()
                    }

                    SensorParam.Id_AngleOfDipSensorId -> {
                        ParamCloneHelper.mSensorParam.AngleOfDipSensorId = param.paramValue.toString()
                    }
                }

                mSensorParamAdapter.updateParam()

                return try {
                    ParamCloneHelper.mSensorParam.isParamCorrect()
                    true
                } catch (e: Exception) {
                    showCenterToast(this,e.message.toString())
                    false
                }
            }
            else ->  return true
        }
        return true
    }

}