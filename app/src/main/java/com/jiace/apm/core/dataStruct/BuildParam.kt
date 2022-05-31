package com.jiace.apm.core.dataStruct

import com.jiace.apm.R
import com.jiace.apm.until.TEXT_ERROR
import com.jiace.apm.until.buildGson
import com.jiace.apm.until.checkTextIsValid
import com.jiace.apm.until.getString

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/9.
3 * Description:
4 *
5 */
class BuildParam: DataStructBase() {

    companion object {

        const val Id_MachineType = 0
        const val Id_MachineNo = 1
        const val Id_AnchorDiameter = 2
        const val Id_AnchorPlateNo = 3
        const val Id_AnchorPlateCount = 4

        const val Id_DesignDepth = 0
        const val Id_DesignOutcrop = 1
        const val Id_DesignAngleOfDip = 2
        const val Id_DesignDirection = 3
        const val Id_λB = 4

        fun fromJson(json: String) =  buildGson().fromJson(json, BuildParam::class.java)
    }

    /**
     * 机械型号
     * */
    var MachineType = ""

    /**
     * 机械编号
     * */
    var MachineNo = ""

    /**
     * 锚杆直径(um)
     * */
    var AnchorDiameter = 100000

    /**
     * 锚盘编号
     * */
    var AnchorPlateNo = ""

    /**
     * 锚盘数量
     * */
    var AnchorPlateCount = 1

    /**
     * 设计埋深（um）
     * */
    var DesignDepth = 100000

    /**
     * 设计露头（um）
     * */
    var DesignOutcrop = 100000

    /**
     * 设计倾角(分)
     * */
    var DesignAngleOfDip = 180

    /**
     * 设计方位角（分）
     * */
    var DesignDirection = 0

    /**
     * 土体高度影响系数λB
     *
     * */
    var λB = 0f

    override val dataType: Int
        get() = BuildParamCode

    override fun isParamCorrect(): Boolean {
        if (!checkTextIsValid(MachineType)) {
            throw Exception(getString(R.string.error_machine_type).format(TEXT_ERROR))
        }

        if (!checkTextIsValid(MachineNo)) {
            throw Exception(getString(R.string.error_machine_no).format(TEXT_ERROR))
        }

        if (!checkTextIsValid(AnchorPlateNo)) {
            throw Exception(getString(R.string.error_plate_no).format(TEXT_ERROR))
        }

        if (AnchorDiameter < 0) {
            throw Exception(getString(R.string.error_anchor_diameter))
        }

        if (DesignDepth < 0) {
            throw Exception(getString(R.string.error_design_depth))
        }

        if (DesignOutcrop < 0) {
            throw Exception(getString(R.string.error_design_outcrop))
        }

        if (DesignDirection < 0) {
            throw Exception(getString(R.string.error_design_direction))
        }
        return true
    }


    fun clone(): BuildParam {
        return fromJson(this.toString())
    }
}