package com.jiace.apm.ui.file.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.jiace.apm.R
import com.jiace.apm.core.dataStruct.Record
import com.jiace.apm.until.getColor
import kotlinx.android.synthetic.main.summary_table_item_layout.view.*
import kotlinx.android.synthetic.main.summary_table_layout.view.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/31.
3 * Description:
4 *
5 */
class SummaryTableView: LinearLayout {

    fun updateTable(summaryTable: ArrayList<Record>) {
        mGradeStructs.clear()
        mGradeStructs.addAll(summaryTable)
        mSummaryDataAdapter.notifyDataSetChanged()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    private val contentView = View.inflate(context, R.layout.summary_table_layout,null)

    private val mGradeStructs = ArrayList<Record>()

    private var mSummaryDataAdapter: SummaryDataAdapter

    init {
        addView(contentView)
        mSummaryDataAdapter = SummaryDataAdapter()
        summaryLv.adapter = mSummaryDataAdapter



    }

    private inner class SummaryDataAdapter: BaseAdapter() {
        override fun getCount(): Int {
            return mGradeStructs.size
        }

        override fun getItem(position: Int): Any {
            return mGradeStructs[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = convertView ?: View.inflate(context,R.layout.summary_table_item_layout,null)
            val gradeStruct = mGradeStructs[position]
            if (position % 2 == 0) {
                rowView.summaryContentView.setBackgroundColor(getColor(R.color.white))
            } else {
                rowView.summaryContentView.setBackgroundColor(getColor(R.color.gray_95))
            }

            rowView.no.text = "%d".format(position + 1)

            rowView.depth.text = ("%.1f").format(gradeStruct.Depth / 100f)
            rowView.torsion.text = ("%.1f").format(gradeStruct.Torsion / 1000f)
            rowView.angle.text = ("%.1f").format(gradeStruct.AngleOfDip / 10f)
            rowView.footage.text = ("%df").format(gradeStruct.Footage * 10)
            return rowView
        }
    }
}