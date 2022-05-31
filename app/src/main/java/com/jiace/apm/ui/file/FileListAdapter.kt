package com.jiace.apm.ui.file

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.jiace.apm.R
import com.jiace.apm.until.getColor
import kotlinx.android.synthetic.main.file_table_layout.view.*
import java.util.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2021/8/9.
3 * Description: 文件列表Adapter
4 *
5 */
class FileListAdapter(val context: Context, private val mFileList: ArrayList<HashMap<String, Any>>): BaseAdapter() {

    private val mCheckedList = ArrayList<Boolean>()

    private var mIsCanUploadFile = true

    var callback: ((position: Int,isUpload: Int) -> Unit)? = null

    var modifyProjectCallback: ((position: Int) -> Unit)? = null

    var mPickMode = PickMode.Single
        set(value) {
            mIsCanUploadFile = value != PickMode.Multiple
            field = value

        }

    var mIsOriginData = true

    enum class PickMode {
        Single,Multiple
    }

    init {
        mCheckedList.clear()
        mFileList.map { mCheckedList.add(false) }
    }

    /** 多选选择 */
    fun checkItem(position: Int) {
        if (mCheckedList.size == 0 || position < 0  || position > mCheckedList.size) {
            return
        }
        mCheckedList[position] = !mCheckedList[position]
        notifyDataSetChanged()
    }

    /** 清除选择 */
    fun clearChecks() {
        mCheckedList.mapIndexed { index, _ ->
            mCheckedList[index] = false
        }
        notifyDataSetChanged()
    }

    /** 全选 */
    fun checkAll() {
        mCheckedList.mapIndexed { index, _ ->
            mCheckedList[index] = true
        }
        notifyDataSetChanged()
    }

    /** 更新列表 */
    fun notifyDataChange() {
        mCheckedList.clear()
        mFileList.map { mCheckedList.add(false) }
        notifyDataSetChanged()
    }


    /** 获取被选中的条目 */
    fun getCheckedItems(): ArrayList<Int> {
        val checkedList = ArrayList<Int>()
        mCheckedList.mapIndexed { index, b ->
            if (b) {
                checkedList.add(index)
            }
        }
        return checkedList
    }

    fun isCanUploadFile(isCanUploadFile: Boolean) {
        mIsCanUploadFile = isCanUploadFile
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mFileList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val bootView = convertView ?: View.inflate(context, R.layout.file_table_layout,null)
        bootView.apply {
            no.text = "%d".format(position + 1)
            projectName.text = mFileList[position]["ProjectName"].toString().run {
                if (length > 26) {
                    "${this.substring(0,26)}..."
                } else {
                    this
                }
            }
            serialNo.text = mFileList[position]["SerialNo"].toString()
            pileNo.text = mFileList[position]["PileNo"].toString()
            startTime.text = mFileList[position]["StartTime"].toString()
            recordTime.text = mFileList[position]["RecordCount"].toString()
            endTime.text = mFileList[position]["EndTime"].toString()
            if (mCheckedList[position]) {
                fileContent.setBackgroundColor(getColor(R.color.gray_85))
            } else {
                fileContent.setBackgroundColor(Color.WHITE)
            }
        }
        return bootView
    }
}