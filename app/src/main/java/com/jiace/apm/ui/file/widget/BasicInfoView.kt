package com.jiace.apm.ui.file.widget

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.jiace.apm.R
import com.jiace.apm.core.dataStruct.Doc
import kotlinx.android.synthetic.main.basic_info_log_view_layout.view.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2021/8/16.
3 * Description: 原始参数和测试日志TreeView
4 *
5 */
class BasicInfoView(context: Context): LinearLayout(context) {

    private val bootView = View.inflate(context, R.layout.basic_info_log_view_layout,null)

    init {
        addView(bootView)
    }

    fun setBasicInfoAndLog(summaryItems: ArrayList<Doc.SummaryItem>) {
        bootView.basicInfoTreeView.setElementData(summaryItems)
    }
}