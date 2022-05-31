package com.jiace.apm.core

import com.jiace.apm.core.dataStruct.*
import com.jiace.apm.core.service.ServiceHelper
import com.jiace.apm.until.toIntValue

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/9.
3 * Description: 参数拷贝用于参数配置UI
4 *
5 */
object ParamCloneHelper {

    enum class Mode {
        Project,
        BuildParam,
        DesignBuildParam,
        MonitorParam,
        DesignMonitorParam,
        SensorParam,
        MainParam
    }

    lateinit var mProjectParam: ProjectParam
    lateinit var mBuildParam: BuildParam
    lateinit var mMonitorParam: MonitorParam
    lateinit var mSensorParam: SensorParam

    var mMachineId = ConfigureHelper.MachineId

    fun init() {
        mProjectParam = ParamHelper.mProjectParam.clone()
        mBuildParam = ParamHelper.mBuildParam.clone()
        mMonitorParam = ParamHelper.mMonitorParam.clone()
        mSensorParam = ParamHelper.mSensorParam.clone()

    }

    /**
     *  对一些参数处理后显示给用户
     *  @param param 待处理的参数
     *  @param paramMode 参数类别
     *  @return string 参数文本值
     * */
    fun showUserInterfaceText(param: Param, paramMode: Mode) = when (paramMode) {

        Mode.DesignMonitorParam -> {
            when (param.paramId) {
                MonitorParam.Id_AngleOfDipMax -> {
                    "%.1f".format(  param.paramValue.toIntValue() / 10f)
                }
                MonitorParam.Id_FootageMin,
                MonitorParam.Id_FootageMax -> {
                    param.paramValue.toString()
                }
                MonitorParam.Id_TorsionMin,
                MonitorParam.Id_TorsionMax -> {
                    "%.1f".format(  param.paramValue.toIntValue() / 1000f)
                }
                else ->  param.paramValue.toString()
            }
        }

        Mode.MonitorParam -> {
            when (param.paramId) {

                MonitorParam.Id_MonitorType -> {
                    MonitorParam.getMonitorTypeText(param.paramValue.toIntValue())
                }

                else -> param.paramValue.toString()
            }
        }

        Mode.BuildParam -> {
            when (param.paramId) {
                BuildParam.Id_AnchorDiameter -> {
                    "%.1f".format(  param.paramValue.toIntValue() / 1000f)
                }
                else ->  param.paramValue.toString()
            }

        }

        Mode.DesignBuildParam -> {
            when (param.paramId) {
                BuildParam.Id_DesignDepth,
                BuildParam.Id_DesignOutcrop -> {
                    "%.1f".format(  param.paramValue.toIntValue() / 1000f)
                }

                BuildParam.Id_DesignAngleOfDip,
                BuildParam.Id_DesignDirection -> {
                    "%.1f".format(  param.paramValue.toIntValue() / 60f)
                }
                else ->  param.paramValue.toString()
            }
        }


        else -> {
            param.paramValue.toString()
        }
    }

    /**
     * 获取哪些参数可以在试验过程中可以编辑
     *
     * @param paramId 参数id
     * @return boolean true 可以在试验中编辑 false 则否
     * */
    fun isEditable(paramMode: Mode,paramId: Int): Boolean {
        if (ServiceHelper.isTesting()) {
            return when (paramMode) {
                Mode.DesignMonitorParam -> {
                    true
                }
                else -> false
            }
        }
        return true
    }

    /**
     *  处理用户输入的参数
     *  @param param 待处理的参数
     *  @param paramMode 参数类别
     *  @return string 参数文本值
     * */
    fun handlerUserInputText(text: String,param: Param,paramMode: Mode) = when (paramMode) {

        Mode.DesignBuildParam -> {
            when (param.paramId) {
                BuildParam.Id_DesignOutcrop,
                BuildParam.Id_DesignDepth, -> {
                    (text.toFloat() * 1000).toInt()
                }
                BuildParam.Id_DesignAngleOfDip,
                BuildParam.Id_DesignDirection -> {
                    (text.toFloat() * 60).toInt()
                }
                else -> {
                    text
                }
            }
        }

        Mode.BuildParam -> {
            when (param.paramId) {
                BuildParam.Id_AnchorDiameter -> {
                    (text.toFloat() * 1000).toInt()
                }

                else -> {
                    text
                }
            }
        }

        Mode.MonitorParam -> {
            when (param.paramId) {
                MonitorParam.Id_RecordInterval -> {
                    text.toInt()
                }

                else -> {
                    text
                }
            }
        }

        Mode.DesignMonitorParam -> {
            when (param.paramId) {
                MonitorParam.Id_FootageMin,
                MonitorParam.Id_FootageMax -> {
                    text.toFloat().toInt()
                }
                MonitorParam.Id_TorsionMin,
                MonitorParam.Id_TorsionMax -> {
                    (text.toFloat() * 1000).toInt()
                }
                MonitorParam.Id_AngleOfDipMax -> {
                    (text.toFloat() * 10).toInt()
                }
                else -> {
                    text
                }
            }
        }

        else -> {
            text
        }
    }

    /** 检查参数 */
    fun checkParam() {
        mProjectParam.isParamCorrect()
        mMonitorParam.isParamCorrect()
        mBuildParam.isParamCorrect()
        mSensorParam.isParamCorrect()
    }

}