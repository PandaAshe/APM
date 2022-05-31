package com.jiace.apm.core.dbf

import android.annotation.SuppressLint
import android.content.ContentValues
import com.jiace.apm.core.HostTime
import com.jiace.apm.core.dataStruct.BasicInfo
import com.jiace.apm.core.dataStruct.SensorParam
import com.jiace.apm.until.Utils
import java.util.*

/**
 * @author: yw
 * @date: 2021/5/29
 * @description: 对BasicInfo表的操作
 */
object TBBasicInfoHelper {

    /** 表名 */
    const val TableName = "BasicInfo"

    /**
     * 获取上下文
     * @return Context?
     */
    private fun getContext() = DBManager.getContext()

    /**
     * 获取Data数据库
     * @return SQLiteDatabase?
     */
    private fun getDatabase() = DBManager.getDataDatabase()

    /**
     * 插入数据
     * @param basicInfo BasicInfo
     */
    fun insertBasicInfo(basicInfo: BasicInfo) {
        getDatabase()?.let { db ->
            val sql = "INSERT INTO ${TableName}(" +
                    "BasicInfoId," +
                    "BaseInfoId," +
                    "MachineId," +
                    "SampleMachineId," +
                    "ProjectName," +
                    "SerialNo," +
                    "PileNo," +
                    "BuildPosition," +
                    "StartTime," +
                    "RecordCount," +
                    "SourceParam," +
                    "ProjectParam," +
                    "BuildParam, " +
                    "MonitorParam," +
                    "SensorParam," +
                    "GpsLongitude," +
                    "GpsLatitude," +
                    "CreateTime," +
                    "UpdateTime," +
                    "IsMonitor) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

            val value = arrayOf<Any>(
                basicInfo.mBasicInfoId,
                basicInfo.mBaseInfoId,
                basicInfo.mMachineId,
                basicInfo.mSampleMachineId,
                basicInfo.mProjectName,
                basicInfo.mSerialNo,
                basicInfo.mPileNo,
                basicInfo.mBuildPosition,
                Utils.formatDateTime(basicInfo.mStartTime),
                basicInfo.mRecordCount,
                basicInfo.mSourceParam,
                basicInfo.mProjectParam.toString(),
                basicInfo.mBuildParam.toString(),
                basicInfo.mMonitorParam.toString(),
                basicInfo.mSensorParam.toString(),
                basicInfo.mGpsLongitude,
                basicInfo.mGpsLatitude,
                Utils.formatDateTime(basicInfo.mCreateTime),
                Utils.formatDateTime(basicInfo.mUpdateTime),
                basicInfo.mIsMonitor
            )
            db.execSQL(sql, value)
        }

        // 保存初始参数
        //TBRealTimeParamHelper.insertRealTimeParam(basicInfo.mBasicInfoId, basicInfo.mPressParam)
    }

    /**
     * 基本信息是否存在
     * @param basicInfoId Long
     * @return Boolean
     */
    fun isBasicInfoExist(basicInfoId: Long): Boolean {
        var ret =false
        getDatabase()?.let { db ->
            val sql = "SELECT BasicInfoId FROM ${TableName} WHERE BasicInfoId=${basicInfoId} "
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    ret = true
                }
            }
        }
        return ret
    }

    /**
     * 删除基本信息
     * @param basicInfoId Long
     *
     * */
    fun deleteBasicInfo(basicInfoId: Long) {
        getDatabase()?.delete(TableName,"BasicInfoId = ?", arrayOf("$basicInfoId"))
    }

    /**
     * 更新工程信息
     * @param basicInfoId Long
     * @param projectParam ProjectParm
     */
    //fun updateProjectParam(basicInfoId: Long, projectParam: ProjectParam) {
    //    if(isBasicInfoExist(basicInfoId)) {
    //        getDatabase()?.let { db ->
    //            var sql = "UPDATE ${TableName} SET ProjectName=?," +
    //                    "SerialNo=?," +
    //                    "PileNo=?," +
    //                    "WorkId=?," +
    //                    "AutoUpload=?," +
    //                    "ProjectParam=?," +
    //                    "UpdateTime=? " +
    //                    "WHERE BasicInfoId=${basicInfoId}"
    //            val value = arrayOf<Any>(
    //                projectParam.ProjectName,
    //                projectParam.SerialNo[0],
    //                projectParam.PileNo,
    //                projectParam.WorkId,
    //                projectParam.AutoUpload,
    //                projectParam.toString(),
    //                Utils.formatDateTime(HostTime.getHostTime())
    //            )
    //            db.execSQL(sql, value)
    //        }
    //    }
    //}

    /**
     * 更新测试规范
     * @param basicInfoId Long
     * @param standardParam StandardParam
     */
    //fun updateStandardParam(basicInfoId: Long, standardParam: StandardParam) {
    //    if(isBasicInfoExist(basicInfoId)) {
    //        getDatabase()?.let { db ->
    //            val sql = "UPDATE ${TableName} SET " +
    //                    "TestType=?," +
    //                    "MaxLoad=?," +
    //                    "StandardParam=?," +
    //                    "UpdateTime=? " +
    //                    "WHERE BasicInfoId=${basicInfoId}"
    //            val value = arrayOf<Any>(
    //                StandardDeclare.getStandardName(standardParam.TestMethod),
    //                standardParam.MaxLoad,
    //                standardParam.toString(),
    //                Utils.formatDateTime(HostTime.getHostTime())
    //            )
    //            db.execSQL(sql, value)
    //        }
    //    }
    //}

    /**
     * 更新位移参数
     * @param basicInfoId Long
     * @param displacementParam DisplacementParam
     */
    fun updateDisplacementParam(basicInfoId: Long, displacementParam: SensorParam) {
        if(isBasicInfoExist(basicInfoId)) {
            getDatabase()?.let { db ->
                val sql = "UPDATE ${TableName} SET " +
                        "DisplacementParam=?," +
                        "UpdateTime=? " +
                        "WHERE BasicInfoId=${basicInfoId}"
                val value = arrayOf<Any>(
                    displacementParam.toString(),
                    Utils.formatDateTime(HostTime.getHostTime())
                )
                db.execSQL(sql, value)
            }
        }
    }

    /**
     * 更新压力参数
     * @param basicInfoId Long
     * @param pressParam PressParam
     */
    /*fun updatePressParam(basicInfoId: Long, pressParam: PressParam) {
        if(isBasicInfoExist(basicInfoId)) {
            getDatabase()?.let { db ->
                val sql = "UPDATE ${TableName} SET " +
                        "PressParam=?, " +
                        "UpdateTime=? " +
                        "WHERE BasicInfoId=${basicInfoId}"
                val value = arrayOf<Any>(
                    pressParam.toString(),
                    Utils.formatDateTime(HostTime.getHostTime())
                )
                db.execSQL(sql, value)
            }
        }
    }

    /**
     * 更新控制参数
     * @param basicInfoId Long
     * @param controlParam ControlParam
     */
    fun updateControlParam(basicInfoId: Long, controlParam: ControlParam) {
        if(isBasicInfoExist(basicInfoId)) {
            getDatabase()?.let { db ->
                val sql = "UPDATE ${TableName} SET " +
                        "ControlParam=?, " +
                        "UpdateTime=? " +
                        "WHERE BasicInfoId=${basicInfoId}"
                val value = arrayOf<Any>(
                    controlParam.toString(),
                    Utils.formatDateTime(HostTime.getHostTime())
                )
                db.execSQL(sql, value)
            }
        }
    }

    /**
     * 更新GPS定位信息
     * @param basicInfoId Long
     * @param GpsLongitude Int 经度，为0时表示无效, 以 0.01" 为单位，负值表示西经
     * @param GpsLatitude Int 纬度，为0时表示无效, 以 0.01" 为单位，负值表示南纬
     */
    fun updateGps(basicInfoId: Long, GpsLongitude: Int, GpsLatitude: Int) {
        if(isBasicInfoExist(basicInfoId)) {
            if(!ErrorHelper.isErrorValue(GpsLongitude) && !ErrorHelper.isErrorValue(GpsLatitude)) {
                getDatabase()?.let { db ->
                    val sql = "UPDATE ${TableName} SET " +
                            "GpsLongitude=${GpsLongitude}, " +
                            "GpsLatitude=${GpsLatitude}, " +
                            "UpdateTime=? " +
                            "WHERE BasicInfoId=${basicInfoId}"
                    val value = arrayOf<Any>(Utils.formatDateTime(HostTime.getHostTime()))
                    db.execSQL(sql, value)
                }
            }
        }
    }*/

    /**
     * 查询基本信息
     * @param basicInfoId Long
     * @return BasicInfo?
     */
    fun queryBasicInfo(basicInfoId: Long):BasicInfo? {
        var basicInfo:BasicInfo? = null
        getDatabase()?.let { db ->
            val sql = "SELECT * FROM ${TableName} WHERE BasicInfoId=${basicInfoId}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    basicInfo = BasicInfo.fromCursor(cursor)
                }
            }
        }
        return basicInfo
    }

    /**
     * 获取采样总数
     * @param basicInfoId Long 唯一基本信息号
     * @return Int?
     */
    @SuppressLint("Range")
    fun queryRecordCount(basicInfoId: Long):Int? {
        var recordCount:Int? = null
        getDatabase()?.let { db ->
            val sql = "SELECT RecordCount FROM ${TableName} WHERE BasicInfoId=${basicInfoId}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    recordCount = cursor.getInt(cursor.getColumnIndex("RecordCount"))
                }
            }
        }
        return recordCount
    }

    /**
     * 更新测试中的最大采样Id,从DetailsData中获取最大Id
     * @param basicInfoId Long
     */
    /*fun updateMaxDetailsDataId(basicInfoId: Long) {
        if (isBasicInfoExist(basicInfoId)) {
            getDatabase()?.let { db ->
                val sql = "UPDATE ${TableName} SET MaxDetailsDataId=(SELECT MAX(Id) FROM ${TBDetailsDataHelper.TableName} WHERE BasicInfoId=${basicInfoId}) WHERE BasicInfoId=${basicInfoId}"
                db.execSQL(sql)
            }
        }
    }*/

    /**
     * 更新数据总数
     * @param basicInfoId Long
     */
    fun updateRecordCount(basicInfoId: Long) {
        if(isBasicInfoExist(basicInfoId)) {
            getDatabase()?.let { db->
                val sql = "UPDATE ${TableName} SET RecordCount=(SELECT COUNT(Id) FROM ${TBDetailsDataHelper.TableName} WHERE BasicInfoId=${basicInfoId}) WHERE BasicInfoId=${basicInfoId}"
                db.execSQL(sql)
            }
        }
    }

    /**
     * 根据查询条件计算结果总数
     * @param queryTerm String 查询条件
     * @return Int
     */
    fun queryBasicInfoCountByTerm(queryTerm: String): Int {
        var count = 0
        val tableName = TableName
        val queryCount = "SELECT COUNT(*) AS RowCount FROM ${tableName} WHERE " + queryTerm
        getDatabase()?.let { db->
            db.rawQuery(queryCount, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    count = cursor.getInt(0)
                }
            }
        }
        return count
    }


    /**
     * 根据工程名称获取结果总数
     * @param projectName String 工程名称
     * @return Int
     */
    fun queryBasicInfoCountByProjectName(projectName: String):Int {
        val queryTerm = makeQueryTermByProjectName(projectName)
        return queryBasicInfoCountByTerm(queryTerm)
    }

    /**
     * 根据桩号获取结果总数
     * @param pileNo String 桩号
     * @return Int
     */
    fun queryBasicInfoCountByPileNo(pileNo:String):Int {
        val queryTerm = makeQueryTermByPileNo(pileNo)
        return queryBasicInfoCountByTerm(queryTerm)
    }

    /**
     * 根据流水号获取结果总数
     * @param serialNo String 流水号
     */
    fun queryBasicInfoCountBySerialNo(serialNo: String):Int {
        val queryTerm = makeQueryTermBySerialNo(serialNo)
        return queryBasicInfoCountByTerm(queryTerm)
    }

    /**
     * 根据测试时间查询
     * @param start Date 开始日期
     * @param end Date 结束日期
     * @return Int
     */
    fun queryBasicInfoCountByTestTime(start: Date, end: Date):Int {
        val queryTerm = makeQueryTermByTestTime(start, end)
        return queryBasicInfoCountByTerm(queryTerm)
    }



    /**
     * 所有数据总数
     * @return Int
     */
    fun queryBasicInfoCount():Int {
        val queryTerm = "TRUE"
        return queryBasicInfoCountByTerm(queryTerm)
    }

    /**
     * 根据工程名称构造查询条件
     * @param projectName String 工程名称
     * @return String
     */
    private fun makeQueryTermByProjectName(projectName: String): String {
        return "ProjectName LIKE('%${projectName}%')"
    }

    /**
     * 根据桩号构造查询条件
     * @param pileNo String 桩号
     * @return String
     */
    private fun makeQueryTermByPileNo(pileNo: String):String {
        return "PileNo LIKE('%${pileNo}%')"
    }

    /**
     * 根据流水号构造查询条件
     * @param serialNo String 流水号
     * @return String
     */
    private fun makeQueryTermBySerialNo(serialNo: String):String {
        return "SerialNo LIKE('%${serialNo}%')"
    }

    /**
     * 根据测试日期构造查询条件
     * @param start Date 开始日期
     * @param end Date 结束日期
     * @return String
     */
    private fun makeQueryTermByTestTime(start: Date, end: Date):String {
        return "StartTime<=DateTime('${Utils.formatDate(end)} 23:59:59') AND StartTime>=DateTime('${Utils.formatDate(start)} 00:00:00')"
    }

    /**
     * 根据查询条件按页返回查询结果
     * @param queryTerm String
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    @SuppressLint("Range")
    private fun queryBasicInfoByQueryTerm(queryTerm: String, pageNo: Int, PageRange: Int) = run{
        val list = ArrayList<HashMap<String, Any>>()
        getDatabase()?.let { db->
            val tableName = TableName
            val queryCount = "SELECT COUNT(*) AS RowCount FROM ${tableName} WHERE " + queryTerm
            var temp = "SELECT BasicInfoId FROM ${tableName} WHERE " + queryTerm +
                    " ORDER BY BasicInfoId DESC LIMIT ${(pageNo-1)*PageRange}, ${PageRange} "
            var sql = "SELECT GROUP_CONCAT(BasicInfoId) FROM (${temp}) GROUP BY BasicInfoId "
            temp = sql
            val field = " " +
                    "BasicInfoId, " +
                    "MachineId, " +
                    "ProjectName, " +
                    "SerialNo, " +
                    "PileNo, " +
                    "StartTime, " +
                    "RecordCount, " +
                    "EndTime"

            var str1 = ""
            var str2 = ""

            sql ="SELECT b.*, ${str1} FROM ( " +
                    "SELECT ${field} FROM ${tableName} WHERE BasicInfoId IN (${temp}) " +
                    ")AS b ${str2} ORDER BY b.BasicInfoId DESC"
            db.rawQuery(sql, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val map = HashMap<String, Any>()
                    map.apply {
                        put("BasicInfoId", cursor.getLong(cursor.getColumnIndex("BasicInfoId")))
                        put("MachineId", cursor.getString(cursor.getColumnIndex("MachineId")))
                        put("ProjectName", cursor.getString(cursor.getColumnIndex("ProjectName")))
                        put("SerialNo", cursor.getString(cursor.getColumnIndex("SerialNo")))
                        put("PileNo", cursor.getString(cursor.getColumnIndex("PileNo")))
                        put("EndTime", cursor.getInt(cursor.getColumnIndex("EndTime")))
                        put("StartTime", cursor.getString(cursor.getColumnIndex("StartTime")))
                        put("RecordCount", cursor.getInt(cursor.getColumnIndex("RecordCount")))
                    }
                    list.add(map)
                }
            }
        }
        list
    }

    /**
     * 根据工程名称查询
     * @param projectName String 工程名称
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    fun queryBasicInfoByProjectName(projectName: String, pageNo: Int, PageRange: Int) = run{
        val queryTerm = makeQueryTermByProjectName(projectName)
        queryBasicInfoByQueryTerm(queryTerm, pageNo, PageRange)
    }




    /**
     * 根据桩号查询
     * @param pileNo String 桩号
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    fun queryBasicInfoByPileNo(pileNo: String, pageNo: Int, PageRange: Int) = run{
        val queryTerm = makeQueryTermByPileNo(pileNo)
        queryBasicInfoByQueryTerm(queryTerm, pageNo, PageRange)
    }

    /**
     * 根据流水号查询
     * @param serialNo String 流水号
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    fun queryBasicInfoBySerialNo(serialNo: String, pageNo: Int, PageRange: Int) = run{
        val queryTerm = makeQueryTermBySerialNo(serialNo)
        queryBasicInfoByQueryTerm(queryTerm, pageNo, PageRange)
    }

    /**
     *  根据测试日期查询
     * @param start Date 开始日期
     * @param end Date 结束日期
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    fun queryBasicInfoByTestTime(start: Date, end: Date, pageNo: Int, PageRange: Int) = run{
        val queryTerm = makeQueryTermByTestTime(start, end)
        queryBasicInfoByQueryTerm(queryTerm, pageNo, PageRange)
    }

    /**
     * 查询所有数据
     * @param pageNo Int 页码(1,2,3...)
     * @param PageRange Int 每页数据个数
     * @return ArrayList<HashMap<String, Any>>
     */
    fun queryBasicInfoByRecentCount(pageNo: Int, PageRange: Int) = run{
        val queryTerm = "1=1"
        queryBasicInfoByQueryTerm(queryTerm, pageNo, PageRange)
    }

    /**
     * 更新基本信息号
     * @param oldBasicInfoId Long
     * @param newBasicInfoId Long
     */
    fun updateBasicInfoId(oldBasicInfoId:Long, newBasicInfoId:Long) {
        getDatabase()?.let { db->
            val sql = "UPDATE ${TableName} SET BasicInfoId=${newBasicInfoId} WHERE BasicInfoId=${oldBasicInfoId}"
            db.execSQL(sql)
        }
    }

    /**
     * 更新BaseInfoId
     * @param basicinfoId Long
     * @param baseInfoId String
     */
    fun updateBaseInfoId(basicinfoId: Long,  baseInfoId:String) {
        getDatabase()?.let { db->
            val sql ="UPDATE ${TableName} SET BaseInfoId='${baseInfoId}' WHERE BasicInfoId=${basicinfoId}"
            db.execSQL(sql)
        }
    }

    /**
     * 更新是否正在试验标志
     * @param basicInfoId Long
     * @param isTesting Boolean
     */
    fun updateTestingMark(basicInfoId: Long, isTesting:Boolean) {
        getDatabase()?.let { db->
            val sql = if(isTesting) {
                "UPDATE $TableName SET IsMonitor=1 WHERE BasicInfoId=${basicInfoId}"
            } else {
                "UPDATE $TableName SET IsMonitor=0 WHERE BasicInfoId=${basicInfoId}"
            }
            val sql1 = "UPDATE $TableName SET IsMonitor = 0 WHERE BasicInfoId!=${basicInfoId}"
            db.execSQL(sql1)
            db.execSQL(sql)

        }
        // 如果是正在进行的试验,将试验结束标志置为未发送

    }

    /**
     * 判断数据是否需要上传
     * @param basicInfoId Long
     * @return Boolean
     */
    fun isNeedUpload(basicInfoId: Long):Boolean {
        var ret = false
        getDatabase()?.let { db ->
            val sql = "SELECT COUNT(*) FROM ${TableName} WHERE BasicInfoId=${basicInfoId} AND AutoUpload=1"
            db.rawQuery(sql, null).use {
                if(it.moveToFirst()) {
                    if(it.getInt(0) > 0) {
                        ret = true
                    }
                }
            }
        }
        return ret
    }

    /** 获取基本信息创建时间 */
    @SuppressLint("Range")
    fun getBasicInfoTime(basicInfoId: Long, action:(startTime:Date, createTime:Date) ->Unit):Boolean {
        var ret = false
        getDatabase()?.let { db ->
            val sql = "SELECT StartTime, CreateTime FROM ${TableName} WHERE BasicInfoId=${basicInfoId}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    val startTime = Utils.getDateTimeFormat().parse(cursor.getString(cursor.getColumnIndex("StartTime")))
                    val createTime = Utils.getDateTimeFormat().parse(cursor.getString(cursor.getColumnIndex("CreateTime")))
                    action.invoke(startTime, createTime)
                    ret = true
                }
            }
        }
        return ret
    }

    /** 获取原始参数 */
    fun querySourceParam(basicInfoId: Long):String? {
        var xml:String? = null
        getDatabase()?.let { db ->
            val sql = "SELECT SourceParam FROM ${TableName} WHERE BasicInfoId=${basicInfoId}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    xml = cursor.getString(0)
                }
            }
        }
        return xml
    }

    /** 是否正在试验 */
    fun isTesting(basicInfoId: Long):Boolean {
        var ret = false
        getDatabase()?.let { db ->
            val sql = "SELECT IsTesting FROM ${TableName} WHERE BasicInfoId=${basicInfoId}"
            db.rawQuery(sql, null).use { cursor ->
                if(cursor.moveToFirst()) {
                    ret = cursor.getInt(0) == 1
                }
            }
        }
        return ret
    }

    /** 更新上传状态 */
    fun updateUploadState(basicInfoId: Long, upload: Int) {
        val cv = ContentValues().apply {
            put("AutoUpload",upload)
        }
        getDatabase()?.update(TableName,cv,"BasicInfoId = ?", arrayOf("$basicInfoId"))
    }



    /**
     * 更新SourceParam中的工程信息
     * @param xml String
     * @param projectParam ProjectParam
     * @return String
     */
   /* fun modifySourceParam(xml:String, projectParam:ProjectParam):String {
        var desc = xml

        // 直接查找替换
        val list = listOf(
            "检测流水号",
            "工程名称",
            "桩号",
            "检测人员上岗证号"
        )
        list.forEachIndexed { index, one ->
            val start = "<${one}>"
            val end = "</${one}>"
            var startPos = desc.indexOf(start)
            val endPos = desc.indexOf(end)
            if(startPos != -1 && endPos != -1) {
                startPos += start.length
                val newValue =when(index) {
                    0 -> projectParam.SerialNo.first()
                    1 -> projectParam.ProjectName
                    2 -> projectParam.PileNo
                    3 -> projectParam.WorkId
                    else -> ""
                }
                desc = desc.replaceRange(startPos, endPos, newValue)
            }
        }*/

/*
            try {
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()

                val inputSource = InputSource(StringReader(xml))

                // 此处无法正确解析，Element的Name不能为中文
                val doc = builder.parse(inputSource)
                val root = doc.documentElement
                if(root.hasChildNodes()) {

                    val list = listOf(
                        "检测流水号",
                        "工程名称",
                        "桩号",
                        "检测人员上岗证号"
                    )
                    list.forEachIndexed{index, str ->
                        // 获取节点
                        val nodelist = root.getElementsByTagName(str)
                        if(nodelist.length > 0) {
                            val node = nodelist.item(0)
                            if (node.nodeType == Element.ELEMENT_NODE) {
                                val el = node as Element
                                val oldNode = el.firstChild
                                el.firstChild.nodeValue = when(index) {
                                    0 -> projectParam.SerialNo.first()
                                    1 -> projectParam.ProjectName
                                    2 -> projectParam.PileNo
                                    3 -> projectParam.WorkId
                                    else -> ""
                                }
                                val newNode = oldNode.cloneNode(true)
                                el.replaceChild(newNode, oldNode)
                            }
                        }
                    }


                            // 写入字符串
                            val transFactory = TransformerFactory.newInstance()
                            val transformer = transFactory.newTransformer()
                            // 设置各种输入属性
                            transformer.setOutputProperty("encoding", "UTF-8")
                            val source = DOMSource(doc)
                            val result = StreamResult()
                            val output = ByteArrayOutputStream()
                            result.outputStream = output
                            try {
                                transformer.transform(source, result)
                            }catch (e: TransformerException) {
                                e.printStackTrace()
                            }finally {
                                try {
                                    output.flush()
                                    output.close()
                                }catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            desc = output.toString()
                        }
            }catch (e: Exception) {
                e.printStackTrace()
            }finally {
                return desc
            }


        return  desc
    }
    */
    /**
     * 更新表中的SourceParam
     * @param basicInfoId Long
     * @param xml String
     */
   /* fun updateSourceParam(basicInfoId: Long, xml:String) {
        if(isBasicInfoExist(basicInfoId)) {
            getDatabase()?.let { db ->
                val sql = "UPDATE ${TableName} SET " +
                        "SourceParam=?, " +
                        "UpdateTime=? " +
                        "WHERE BasicInfoId=${basicInfoId}"
                val value = arrayOf<Any>(
                    xml,
                    Utils.formatDateTime(HostTime.getHostTime())
                )
                db.execSQL(sql, value)
            }
        }
    }*/
}