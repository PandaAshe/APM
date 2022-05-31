package com.jiace.apm.ui.file

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.RadioGroup
import com.jiace.apm.R
import com.jiace.apm.base.BaseActivity
import com.jiace.apm.common.dialog.DoubleDatePickerDialog
import com.jiace.apm.until.Utils
import com.jiace.apm.until.showCenterToast
import com.jiace.apm.widget.RefreshListView
import kotlinx.android.synthetic.main.layout_file_list.*
import kotlinx.android.synthetic.main.layout_file_list.view.*
import kotlinx.android.synthetic.main.search_last_layout.*
import kotlinx.android.synthetic.main.search_last_layout.view.*
import kotlinx.android.synthetic.main.search_name_layout.*
import kotlinx.android.synthetic.main.search_name_layout.view.*
import kotlinx.android.synthetic.main.search_time_layout.*
import kotlinx.android.synthetic.main.search_time_layout.view.*
import java.util.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/30.
3 * Description:
4 *
5 */
class FileListActivity: BaseActivity(),RadioGroup.OnCheckedChangeListener, TextWatcher, AdapterView.OnItemClickListener,
    RefreshListView.RefreshListViewListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var mFileController: FileController
    private lateinit var mFileListAdapter: FileListAdapter
    private var queryMode = QUERY_COUNT
    var mQueryTerm = ""
    var mQueryCount = 30
    var mQueryStartTime: Date
    var mQueryEndTime: Date

    companion object {
        const val QUERY_NAME = 1
        const val QUERY_NUM = 2
        const val QUERY_PLIE_NO = 3
        const val QUERY_TIME = 4
        const val QUERY_COUNT = 5
    }

    init {
        val calendar = Calendar.getInstance()
        mQueryEndTime = calendar.time
        calendar.add(Calendar.MONTH,-1)
        mQueryStartTime = calendar.time
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_file_list)
        mFileController = FileController(this,this)
        initViews()
        loadData()
    }

    // 加载数据
    private fun loadData() {
        mFileController.queryRecentData(false,mQueryCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        mFileController.onDestroy()
    }

    // <editor-fold desc="初始化UI及注册相应事件">
    private fun initViews() {
        search_Group.setOnCheckedChangeListener(this)
        lastGroup.setOnCheckedChangeListener(this)
        search_InputTv.addTextChangedListener(this)
        allPickCheckBox.setOnCheckedChangeListener(this)

        mFileListAdapter = FileListAdapter(this,mFileController.mFileList)
        fileListView.adapter = mFileListAdapter
        fileListView.setPullLoadEnable(true)

        mFileListAdapter.callback = { position, isUpload ->
            mFileController.onChangeServer(position, isUpload)
        }

        mFileListAdapter.modifyProjectCallback = { position ->
            mFileController.onModifyProject(position)
        }

        fileListView.setOnItemClickListener { _, _, position, _ ->
            if (mFileListAdapter.mPickMode == FileListAdapter.PickMode.Single) {
                mFileController.onFileChoose(position - 1)
            } else {
                mFileListAdapter.checkItem(position - 1)
            }
        }

        fileListView.setOnItemLongClickListener { _, _, position, _ ->
            if (mFileListAdapter.mPickMode == FileListAdapter.PickMode.Single) {
                fileListView.setPullLoadEnable(false)
                fileListView.setPullRefreshEnable(false)
                mFileListAdapter.mPickMode = FileListAdapter.PickMode.Multiple
                mFileListAdapter.checkItem(position -1)

                allPickCheckBox.visibility = View.VISIBLE
                cancelPickTv.visibility = View.VISIBLE
            }
            return@setOnItemLongClickListener true
        }

        fileListView.setOnRefreshListViewListener(this)

        navigationTv.setOnClickListener {
            finish()
        }

       onChangeOriginClick.setOnClickListener {
            if (mFileListAdapter.mPickMode == FileListAdapter.PickMode.Multiple) {
                showCenterToast(this,"选择模式下不能切换")
                return@setOnClickListener
            }
            mFileController.onChangeOrigin(it)
        }
        unDoIcon.setOnClickListener {
            val checkedList = mFileListAdapter.getCheckedItems()
            if (checkedList.size == 0) {
                showCenterToast(this,"未选择任何一条数据")
                return@setOnClickListener
            }
            mFileController.onUndoFile(checkedList)
        }

       onDeleteClick.setOnClickListener {
            val checkedList = mFileListAdapter.getCheckedItems()
            if (checkedList.size == 0) {
                showCenterToast(this,"未选择任何一条数据")
                return@setOnClickListener
            }
            mFileController.onFileDelete(checkedList)
        }

        shareIcon.setOnClickListener {
            val checkedList = mFileListAdapter.getCheckedItems()
            if (checkedList.size == 0) {
                showCenterToast(this,"未选择任何一条数据")
                return@setOnClickListener
            }
            mFileController.onShareFile(checkedList)
        }

        cancelPickTv.setOnClickListener {
            onCancelPickClick()
        }

        queryTime.setOnClickListener {
            val dialog = DoubleDatePickerDialog(this)

            dialog.setEndDate(mQueryEndTime.time)
            dialog.setStartDate(mQueryStartTime.time)
            dialog.setOnDateSetListener{ start: Long, end: Long ->
                var startTime = start
                var endTime = end
                if (startTime > endTime) {
                    startTime = end
                    endTime = start
                }
                queryTime.text = "%s~%s".format(
                    Utils.formatDate(Date(startTime)),
                    Utils.formatDate(
                    Date(endTime)
                ))
                mQueryStartTime = Date(startTime)
                mQueryEndTime = Date(endTime)
                mFileController.queryFileByStartTime(false,mQueryStartTime,mQueryEndTime)
            }
            dialog.show()
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.lastData -> {
                search_last_layout.visibility = View.VISIBLE
                search_time_layout.visibility = View.GONE
                search_name_layout.visibility = View.GONE
                queryMode = QUERY_COUNT
            }

            R.id.time -> {
                search_last_layout.visibility = View.GONE
                search_time_layout.visibility = View.VISIBLE
                search_name_layout.visibility = View.GONE
                queryMode = QUERY_TIME
            }

            R.id.projectName -> {
                search_last_layout.visibility = View.GONE
                search_time_layout.visibility = View.GONE
                search_name_layout.visibility = View.VISIBLE
                search_InputTv.setText("")
                queryMode = QUERY_NAME
                search_title.text = "工程名称"
            }

            R.id.num -> {
                search_last_layout.visibility = View.GONE
                search_time_layout.visibility = View.GONE
                search_name_layout.visibility = View.VISIBLE
                search_InputTv.setText("")
                queryMode = QUERY_NUM
                search_title.text = "流水号"
            }

            R.id.pileNo -> {
                search_last_layout.visibility = View.GONE
                search_time_layout.visibility = View.GONE
                search_name_layout.visibility = View.VISIBLE
                search_InputTv.setText("")
                queryMode = QUERY_PLIE_NO
                search_title.text = "桩号"
            }

            R.id.last30 -> {
                mQueryCount = 30
                mFileController.queryRecentData(false,mQueryCount)
            }

            R.id.last60 -> {
                mQueryCount = 60
                mFileController.queryRecentData(false,mQueryCount)
            }

            R.id.last100 -> {
                mQueryCount = 100
                mFileController.queryRecentData(false,mQueryCount)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s == null || TextUtils.isEmpty(s))
            return
        mQueryTerm = s.toString()
        when (queryMode) {
            QUERY_NUM -> {
                mFileController.queryFileByNo(false,mQueryTerm)
            }

            QUERY_NAME -> {
                mFileController.queryFileByProjectName(false,mQueryTerm)
            }

            QUERY_PLIE_NO -> {
                mFileController.queryFileByPileNo(false,mQueryTerm)
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (mFileListAdapter.mPickMode != FileListAdapter.PickMode.Multiple) {
            return
        }
        if (isChecked) {
            mFileListAdapter.checkAll()
        } else {
            mFileListAdapter.clearChecks()
        }
    }
    // </editor-fold>

    // <editor-fold desc="操作事件">

    fun onCancelPickClick() {
        if (mFileListAdapter.mPickMode == FileListAdapter.PickMode.Multiple) {
            mFileListAdapter.clearChecks()
            mFileListAdapter.mPickMode = FileListAdapter.PickMode.Single
            fileListView.setPullLoadEnable(true)
            fileListView.setPullRefreshEnable(true)
            allPickCheckBox.visibility = View.GONE
            cancelPickTv.visibility = View.GONE
            allPickCheckBox.isChecked = false
        }
    }

    /** 获取条件查询模式 */
    fun getQueryMode(): Int {
        return queryMode
    }

    /** 刷新文件列表 */
    fun notifyDataSetChanged() {
        mFileListAdapter.notifyDataChange()
    }

    /** 下拉刷新 */
    override fun onRefresh() {
        mFileController.onRefresh()
    }

    /** 上拉加载 */
    override fun onLoadMore() {
        mFileController.onLoadMore()
    }

    /** ListView 停止刷新 */
    fun stopFreshData() {
        fileListView.stopRefresh()
        fileListView.setRefreshTime(Utils.formatDateTime(Date()))
    }

    /** ListView 停止加载更多 */
    fun stopLoadingMore(isHashData: Boolean) {
        val hashData = if (isHashData) { 1 } else { 0 }
        fileListView.stopLoadMore(hashData)
    }

    /**切换文件类型时，自动变更查询模式为按数量查询 */
    fun autoChangeQueryMode() {
        queryMode = QUERY_COUNT
        search_Group.check(R.id.lastData)
    }

    /** 更新页面名称 */
    fun updatePageName(name: String,isOriginMode: Boolean) {
        onChangeOriginClick.text = name
        if (isOriginMode) {
            shareIcon.visibility = View.VISIBLE
            unDoIcon.visibility = View.GONE
            mFileListAdapter.isCanUploadFile(true)
            mFileListAdapter.mIsOriginData = true
        } else {
            shareIcon.visibility = View.GONE
            unDoIcon.visibility = View.VISIBLE
            mFileListAdapter.isCanUploadFile(false)
            mFileListAdapter.mIsOriginData = false
        }
    }

    override fun onBackPressed() {
        finish()
    }
}