package com.jiace.apm.ui.file.widget.treeView

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.ListView
import com.jiace.apm.core.dataStruct.Doc

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2021/8/13.
3 * Description: TreeView
4 *
5 */
class TreeView: LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    private var mTreeViewAdapter: TreeViewAdapter

    init {
        val ls = ListView(context)
        ls.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        addView(ls)
        mTreeViewAdapter = TreeViewAdapter(context)
        ls.adapter = mTreeViewAdapter
        // ls.onItemClickListener = TreeViewItemClickListener(mTreeViewAdapter)
    }

    fun setElementData(summaryItems: ArrayList<Doc.SummaryItem>) {
        mTreeViewAdapter.setElements(summaryItems)
    }
}