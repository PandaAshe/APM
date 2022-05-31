package com.jiace.apm.core.dataStruct

import com.jiace.apm.R
import com.jiace.apm.until.TEXT_ERROR
import com.jiace.apm.until.buildGson
import com.jiace.apm.until.checkTextIsValid
import com.jiace.apm.until.getString

/**
 * @author: yw
 * @date: 2021/05/24
 * @description: 工程参数
 */
class ProjectParam : DataStructBase() {

    companion object {

        /**
         * 从json字符串生成
         * @param json String json字符串
         * @return (com.jiace.jyh.core.dataStruct.ProjectParm..com.jiace.jyh.core.dataStruct.ProjectParm?)
         */
        fun fromJSon(string: String) = buildGson().fromJson(string, ProjectParam::class.java)


        const val Id_ProjectName = 0
        const val Id_PileNo = 1
        const val Id_SerialNo = 2
        const val Id_TallNo = 3
        const val Id_BaseNo = 4
        const val Id_BaseAnchorNo = 5
        const val Id_BuildPosition = 6
    }

    /**
     *  工程名称
     */
    var ProjectName = ""

    /**
     *  桩号
     */
    var PileNo = ""

    /**
     *  测试流水号
     */
    var SerialNo = ""

    /**
     *  基础编号
     */
    var BaseNo = ""

    /**
     * 基锚编号
     * */
    var BaseAnchorNo = ""

    /**
     * 施工部位
     * */
    var BuildPosition = ""

    /**
     * 塔位编号
     * */
    var TallNo = ""

    /**
     *  自动上传
     */
    var AutoUpload = 0


    /**
     *  当GPS定位无效时,是否使用最近一次有效的GPS定位信息
     */
    var UseLastValidGps = 0

    /**
     *  是否启用GPS定位
     */
    var UseGps = 1


    /**
     *  数据类型
     */
    override val dataType: Int
        get() = DataStructBase.ProjectParamCode

    override fun isParamCorrect(): Boolean {
        if (!checkTextIsValid(ProjectName)) {
            throw Exception(getString(R.string.error_project_name).format(TEXT_ERROR))
        }
        if (!checkTextIsValid(PileNo)) {

            throw Exception(getString(R.string.error_pile_no).format(TEXT_ERROR))
        }
        if (!checkTextIsValid(SerialNo)) {
            throw Exception(getString(R.string.error_serial_no).format(TEXT_ERROR))
        }
        if (!checkTextIsValid(BaseNo)) {
            throw Exception(getString(R.string.error_base_no).format(TEXT_ERROR))
        }
        if (!checkTextIsValid(BaseAnchorNo)) {
            throw Exception(getString(R.string.error_base_anchor_no).format(TEXT_ERROR))
        }
        if (!checkTextIsValid(TallNo)) {
            throw Exception(getString(R.string.error_tall_no).format(TEXT_ERROR))
        }
        if (!checkTextIsValid(BuildPosition)) {
            throw Exception(getString(R.string.error_build_position).format(TEXT_ERROR))
        }
        return true
    }

    override fun hashCode(): Int {
        var result = ProjectName.hashCode()
        result = 31 * result + PileNo.hashCode()
        result = 31 * result + SerialNo.hashCode()
        result = 31 * result + BaseNo.hashCode()
        result = 31 * result + AutoUpload
        result = 31 * result + BaseAnchorNo.hashCode()
        result = 31 * result + UseLastValidGps
        result = 31 * result + UseGps
        result = 31 * result + TallNo.hashCode()
        result = 31 * result + BuildPosition.hashCode()
        return result
    }

    /**
     * 复制对象
     * @return ProjectParam
     */
    fun clone():ProjectParam {
        return fromJSon(toString())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectParam

        if (ProjectName != other.ProjectName) return false
        if (PileNo != other.PileNo) return false
        if (!SerialNo.contentEquals(other.SerialNo)) return false
        if (BaseNo != other.BaseNo) return false
        if (AutoUpload != other.AutoUpload) return false
        if (BaseAnchorNo != other.BaseAnchorNo) return false
        if (UseLastValidGps != other.UseLastValidGps) return false
        if (UseGps != other.UseGps) return false
        if (BuildPosition != other.BuildPosition) return false
        if (TallNo != other.TallNo) return false

        return true
    }
}