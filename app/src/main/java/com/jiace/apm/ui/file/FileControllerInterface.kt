package com.jiace.apm.ui.file

import android.view.View
import java.util.*

interface FileControllerInterface {

    /** 下拉刷新 */
    fun onRefresh()

    /** 上拉加载 */
    fun onLoadMore()

    /** 文件选择 */
    fun onFileChoose(position: Int)

    /**文件删除 */
    fun onFileDelete(checkedList: ArrayList<Int>)

    /** 数据上传 */
    fun onFileUpload()

    /** 导出文件 */
    fun onFileExport(checkedList: ArrayList<Int>)

    /** 通过蓝牙、wifi等方式分享文件 */
    fun onShareFile(checkedList: ArrayList<Int>)

    /**
     * 按工程名称查询
     * @param queryTerm 查询条件
     * */
    fun queryFileByProjectName(isLoadMoreData: Boolean = false,queryTerm: String)

    /**
     * 按桩号查询
     * @param queryTerm 查询条件
     * */
    fun queryFileByPileNo(isLoadMoreData: Boolean = false,queryTerm: String)

    /**
     * 按流水号查询文件
     * @param queryTerm 查询条件
     * */
    fun queryFileByNo(isLoadMoreData: Boolean = false,queryTerm: String)

    /**
     * 按测试时间查询文件
     * @param startTime 开始日期
     * @param endTime 结束日期
     *
     * */
    fun queryFileByStartTime(isLoadMoreData: Boolean = false,startTime: Date,endTime: Date)

    /** 按最近数量查询 */
    fun queryRecentData(isLoadMoreData: Boolean = false,count: Int)

    /** 恢复数据 */
    fun onUndoFile(checkedList: ArrayList<Int>)

    /** 更改数据源 */
    fun onChangeOrigin(view: View)

    /** 恢复试验 */
    fun onRecoverTesting(checkedIndex: Int)

    /** 手动数据上传 */
    fun onChangeServer(position: Int,isUpload: Int)

    /** 修正工程信息 */
    fun onModifyProject(position: Int)
}