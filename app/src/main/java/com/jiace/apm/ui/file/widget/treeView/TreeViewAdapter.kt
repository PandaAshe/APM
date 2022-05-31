package com.jiace.apm.ui.file.widget.treeView

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.jiace.apm.R
import com.jiace.apm.core.dataStruct.Doc
import kotlinx.android.synthetic.main.treeview_item_layout.view.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2021/8/13.
3 * Description: TreeView适配器
4 *
5 */
class TreeViewAdapter(val context: Context): BaseAdapter() {

    private val indentionBase = 40

    private val elementData = ArrayList<Doc.SummaryItem>()
    private val elements = ArrayList<Doc.SummaryItem>()

    fun setElements(summaryItems: ArrayList<Doc.SummaryItem>) {
        elements.clear()
        elements.addAll(summaryItems)
        notifyDataSetChanged()
    }

    fun getElements() = elements

    fun getElementsData() = elementData

    override fun getCount(): Int {
        return elements.size
    }

    override fun getItem(position: Int): Any {
        return elements[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val bootView = convertView ?: View.inflate(context, R.layout.treeview_item_layout,null)
        elements[position].let {
            val level = it.level
            bootView.contentText.setPadding(indentionBase * level,0,0,0)
            bootView.contentText.text = it.param
        }
        return bootView
    }
}