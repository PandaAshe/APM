package com.jiace.apm.ui.file

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewpager.widget.PagerAdapter
import com.jiace.apm.R
import com.jiace.apm.base.HideBarDialog
import com.jiace.apm.core.dataStruct.Doc
import com.jiace.apm.until.applicationScope
import com.jiace.apm.ui.file.widget.BasicInfoView
import com.jiace.apm.ui.file.widget.MonitorView
import com.jiace.apm.ui.file.widget.SummaryCurveView
import com.jiace.apm.ui.file.widget.SummaryTableView
import kotlinx.android.synthetic.main.dialog_single_button_layout.view.*
import kotlinx.android.synthetic.main.file_preview_dialog.*
import kotlinx.android.synthetic.main.file_preview_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlin.collections.ArrayList

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2021/8/11.
3 * Description: 文件阅览
4 *
5 */
class FilePreviewDialog(context: Context,val basicInfoId: Long): HideBarDialog(context),
    DialogInterface.OnDismissListener {

    private val bootView = View.inflate(context, R.layout.file_preview_dialog,null)
    /** 曲线数据表格 */
    private val mTableCurveViews = ArrayList<View>()

    init {
        bootView.errorText.setOnClickListener {
            bootView.processLayout.visibility = View.VISIBLE
            bootView.errorText.visibility = View.GONE
            bootView.contentLayout.visibility = View.GONE
            loadData()
        }
        bootView.commit.setOnClickListener { dismiss() }
        setOnDismissListener(this)
        setView(bootView)
        loadData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置对话框全屏显示
        window?.let { w ->
            // 把DecorView的默认padding取消，同时DecorView的默认大小也会取消
            w.decorView.setPadding(0, 0, 0, 0)
            val layoutParam = w.attributes

            // 宽度
            layoutParam.width = WindowManager.LayoutParams.MATCH_PARENT

            // 高度
            layoutParam.height = WindowManager.LayoutParams.MATCH_PARENT

            // 给DecorView设置背景色，很重要，不然会导致Dialog内容显示不全，有一部分内容会充当padding
            w.decorView.setBackgroundColor(Color.TRANSPARENT)
        }
    }




    private fun loadData() {
        flow {
            val doc = Doc(basicInfoId)
            emit(doc)
        }.flowOn(Dispatchers.IO).catch {
            bootView.processLayout.visibility = View.GONE
            bootView.errorText.visibility = View.VISIBLE
            bootView.contentLayout.visibility = View.GONE
            bootView.errorText.text = "加载数据异常"
        }.onEach { doc ->
            bootView.processLayout.visibility = View.GONE
            bootView.errorText.visibility = View.GONE
            bootView.contentLayout.visibility = View.VISIBLE
            initView(doc)
        }.launchIn(applicationScope)
    }

    private fun initView(doc: Doc) {
        bootView.tabLayout.setupWithViewPager(curveTableViewPager)
        mTableCurveViews.add(BasicInfoView(context))
        val titles = context.resources.getStringArray(R.array.file_preview_titles)

        mTableCurveViews.apply {
            add(SummaryTableView(context))
            add(SummaryCurveView(context))
            add(MonitorView(context))
        }

        curveTableViewPager.offscreenPageLimit = 1
        bootView.curveTableViewPager.adapter = CurveTableAdapter(mTableCurveViews,titles)
        // 绘制表格和曲线
        updateBasicInfoLog(doc)
        updateCurve(doc)
        updateSummaryTableAndCurve(doc)
    }

    private fun updateBasicInfoLog(doc: Doc) {
        (mTableCurveViews[BasicInfo] as BasicInfoView).setBasicInfoAndLog(doc.getBasicInfoSummary())
    }

    private fun updateSummaryTableAndCurve(doc: Doc) {
        flow {
            emit(doc.getSummaryTable())
        }.flowOn(Dispatchers.IO).onEach {
            (mTableCurveViews[SummaryTable] as SummaryTableView).updateTable(doc.getSummaryTable())
            (mTableCurveViews[SummaryCurve] as SummaryCurveView).updateCurve(doc.getSummaryTable())
        }.launchIn(applicationScope)
    }

    private fun updateCurve(doc: Doc) {
        (mTableCurveViews[MonitorCurve] as MonitorView).setSourceData(doc.mSourceData)
    }

    private class CurveTableAdapter(val views: ArrayList<View>,val titles: Array<String>): PagerAdapter() {

        override fun getCount(): Int {
            return views.size
        }

        override fun isViewFromObject(p0: View, p1: Any): Boolean {
            return p0 == p1
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = views[position]
            if (view.parent != null) {
                val parent = view.parent as ViewGroup
                parent.removeAllViews()
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(views[position])
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }
    }


    companion object {
        private const val BasicInfo = 0
        private const val SummaryTable = 1
        private const val SummaryCurve = 2
        private const val MonitorCurve = 3
    }

    override fun onDismiss(dialog: DialogInterface?) {
        (mTableCurveViews[MonitorCurve] as MonitorView).onClear()
    }
}