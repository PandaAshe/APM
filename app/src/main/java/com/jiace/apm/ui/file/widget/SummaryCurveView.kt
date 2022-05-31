package com.jiace.apm.ui.file.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.jiace.apm.Application
import com.jiace.apm.R
import com.jiace.apm.core.dataStruct.Record
import com.jiace.apm.until.DisplayUtil
import com.jiace.apm.until.dip2px
import com.jiace.apm.until.getColor
import kotlin.math.abs
import kotlin.math.max

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/31.
3 * Description:
4 *
5 */
class SummaryCurveView: View {

    companion object {
        /** start padding */
        private const val HORIZONTAL_PADDING = 60f

        /** end padding */
        private const val END_HORIZONTAL_PADDING = 20f

        /** top padding */
        private const val TOP_PADDING = 10f
        /** bottom padding */
        private const val BOTTOM_PADDING = 24f

        /** 坐标轴与文本间隔 */
        private const val TEXT_PADDING = 8f


        /** 背景表格的列数 */
        private const val COLUMN_COUNT = 10

    }

    /** 表格画笔 */
    private val mTablePaint = Paint()


    /** 标题画笔 */
    private val mAixPaint = Paint()

    /** 曲线画笔 */
    private val mFootageCurvePaint = Paint()

    /** 曲线画笔 */
    private val mTorsionCurvePaint = Paint()

    /** 曲线画笔 */
    private val mAngleCurvePaint = Paint()

    /** row count */
    private var mRowCount = 10

    /** 文本rect */
    private val mTextRect = Rect()

    /** 表格RECT */
    private val mTableRect = RectF()

    /** 表格宽 */
    private var mTableWith = 0f

    /** 表格高 */
    private var mTableHeight = 0f

    /** 单元表格宽 */
    private var mCellWith = 0f

    /** 单元表格高 */
    private var mCellHeight = 0f

    /** 数据源 */
    private val mSourceData = ArrayList<Record>()

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    init {

        mTablePaint.apply {
            strokeWidth = 1f
            color = ContextCompat.getColor(context, R.color.gray_30)
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        mAixPaint.apply {
            textSize = DisplayUtil.sp2px(context,14f)
            color = ContextCompat.getColor(context, R.color.white)
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        mFootageCurvePaint.apply {
            strokeWidth = 2f.dip2px(Application.get())
            color = ContextCompat.getColor(context, R.color.curve_color)
            style = Paint.Style.STROKE
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        mTorsionCurvePaint.apply {
            strokeWidth = 2f.dip2px(Application.get())
            color = ContextCompat.getColor(context, R.color.footage_curve_colo)
            style = Paint.Style.STROKE
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        mAngleCurvePaint.apply {
            strokeWidth = 2f.dip2px(Application.get())
            color = ContextCompat.getColor(context, R.color.angle_curve_colo)
            style = Paint.Style.STROKE
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mTableWith = width - HORIZONTAL_PADDING - END_HORIZONTAL_PADDING
        mTableHeight = (height - TOP_PADDING - BOTTOM_PADDING)

        mCellWith = mTableWith / COLUMN_COUNT
        mCellHeight = mTableHeight / mRowCount
    }

    override fun onDraw(canvas: Canvas) {
        drawBackgroundTable(canvas)
        calc()
        calePix()
        drawAxis(canvas)
        drawCurve(canvas)
    }

    /** 绘制背景 */
    private fun drawBackgroundTable(canvas: Canvas) {
        canvas.translate(HORIZONTAL_PADDING, TOP_PADDING)
        canvas.save()
        mTableRect.apply {
            top = 0f
            left = 0f
            right = mTableHeight
            bottom = mTableHeight
        }

        canvas.drawRect(mTableRect,mTablePaint)

        for (i in 1 .. mRowCount) {
            canvas.drawLine(0f,mCellHeight * i, TEXT_PADDING,mCellHeight * i,mTablePaint)
        }

        //for (i in 1 .. COLUMN_COUNT) {
        //    canvas.drawLine(mCellWith * i,0f,mCellWith * i,mTableHeight,mTablePaint)
        //}
        canvas.restore()
    }

    private fun drawAxis(canvas: Canvas) {
        // 绘制深度值
        for (i in 0 .. mRowCount) {
            val depthAxisText = ("%.1f").format((mRowCount * i) / 100f )
            mAixPaint.getTextBounds(depthAxisText,0,depthAxisText.length,mTextRect)
            canvas.drawText(depthAxisText,-TEXT_PADDING,mCellHeight * i + mTextRect.height() / 2,mAixPaint)
        }

        // 标记X轴坐标值
        val minFootageText = ("%.1f").format(mFootageMinValue)
        val maxFootageText = ("%.1f (cm)").format(mFootageMaxValue)

        mAixPaint.color = getColor(R.color.footage_curve_colo)

        mAixPaint.getTextBounds(minFootageText,0,minFootageText.length,mTextRect)
        canvas.drawText(minFootageText,0f,-TEXT_PADDING,mAixPaint)

        mAixPaint.getTextBounds(maxFootageText,0,maxFootageText.length,mTextRect)
        canvas.drawText(maxFootageText,mTableWith,-TEXT_PADDING,mAixPaint)


        val minAngleText = ("%.1f").format(mAngleMinValue / 10f)
        val maxAngleText = ("%.1f (°)").format(mAngleMaxValue / 10f)
        mAixPaint.color = getColor(R.color.angle_curve_colo)

        mAixPaint.getTextBounds(minAngleText,0,minAngleText.length,mTextRect)
        canvas.drawText(maxAngleText,0f, mTableHeight - TEXT_PADDING,mAixPaint)

        mAixPaint.getTextBounds(maxFootageText,0,maxFootageText.length,mTextRect)
        canvas.drawText(maxFootageText,mTableWith,mTableHeight - TEXT_PADDING,mAixPaint)

        val minTorsionText = ("%.1f").format(mTorsionMinValue / 1000f)
        val maxTorsionText = ("%.1f (kN·m)").format(mTorsionMaxValue / 1000f)

        mAixPaint.color = getColor(R.color.torsion_curve_colo)
        mAixPaint.getTextBounds(minTorsionText,0,minTorsionText.length,mTextRect)
        canvas.drawText(minFootageText,0f,mTableHeight + TEXT_PADDING + mTextRect.height(),mAixPaint)

        mAixPaint.getTextBounds(maxTorsionText,0,maxTorsionText.length,mTextRect)
        canvas.drawText(maxFootageText,mTableWith,mTableHeight + TEXT_PADDING + mTextRect.height(),mAixPaint)
    }

    /** 进尺曲线 */
    private fun drawCurve(canvas: Canvas) {

        val footagePath = Path()
        val torsionPath = Path()
        val anglePath = Path()

        mSourceData.mapIndexed { index, record ->
            if (index == 0) {
                footagePath.moveTo((record.Footage - mFootageMinValue) * mFootagePix,0f)
                torsionPath.moveTo((record.Torsion - mTorsionMinValue) * mTorsionPix,0f)
                anglePath.moveTo((record.AngleOfDip - mAngleMinValue) * mAnglePix,0f)
            }
            footagePath.lineTo((record.Footage - mFootageMinValue) * mFootagePix,record.Depth * mDepthPix)
            torsionPath.lineTo((record.Torsion - mTorsionMinValue) * mTorsionPix,record.Depth * mDepthPix)
            anglePath.lineTo((record.AngleOfDip - mAngleMinValue) * mAnglePix,record.Depth * mDepthPix)
        }
        canvas.drawPath(footagePath,mFootageCurvePaint)
        canvas.drawPath(torsionPath,mTorsionCurvePaint)
        canvas.drawPath(anglePath,mAngleCurvePaint)
    }

    /** 计算大小值 */
    private fun calc() {

        if (mSourceData.isEmpty()) {
            mFootageMaxValue = 300
            mFootageMinValue = 100
            mTorsionMaxValue = 200000
            mTorsionMinValue = 10000
            mAngleMaxValue = 60
            mAngleMinValue = -60
        } else {
            var maxValue = mSourceData.maxOfOrNull { it.Footage }!!
            var minValue = mSourceData.minOfOrNull { it.Footage }!!

            mFootageMaxValue = (maxValue / 50 + 1) * 50
            mFootageMinValue = when (minValue) {
                in 0..50 -> {
                    0
                }
                else -> {
                    (minValue / 50 - 1) * 50
                }
            }

            maxValue = mSourceData.maxOfOrNull { it.Torsion }!!
            minValue = mSourceData.minOfOrNull { it.Torsion }!!

            mTorsionMaxValue = (maxValue / 1000 + 1) * 1000
            mTorsionMinValue = when (minValue) {
                in 0..1000 -> {
                    0
                }
                else -> {
                    (minValue / 1000 - 1) * 1000
                }
            }

            maxValue = mSourceData.maxOfOrNull { it.AngleOfDip }!!
            minValue = mSourceData.minOfOrNull { it.AngleOfDip }!!

            val absMaxValue = max(abs(maxValue), abs(minValue))

            mAngleMaxValue = (absMaxValue / 10 + 1) * 10
            mAngleMinValue = -mAngleMaxValue
        }
    }

    private var mFootageMaxValue = 300
    private var mFootageMinValue = 100

    private var mTorsionMaxValue = 200000
    private var mTorsionMinValue = 10000

    private var mAngleMaxValue = 60
    private var mAngleMinValue = -60


    private var mFootagePix = 1f
    private var mTorsionPix = 1f
    private var mAnglePix = 1f
    private var mDepthPix = 1f

    /** 单位像素计算 */
    private fun calePix() {
        mFootagePix = mTableWith / (mFootageMaxValue - mFootageMinValue)
        mTorsionPix = mTableWith / (mTorsionMaxValue - mTorsionMinValue)
        mAnglePix = mTableWith / (mAngleMaxValue - mAngleMinValue)
        mDepthPix = mTableHeight / (mRowCount * 50)
    }

    /** 刷新曲线 */
    fun updateCurve(data: ArrayList<Record>) {
        mSourceData.clear()
        mSourceData.addAll(data)
        val tempMaxDepth = mSourceData.maxByOrNull { it.Depth }?.Depth ?: 99
        mRowCount = if (tempMaxDepth % 50 == 0) {
            tempMaxDepth / 50
        } else {
            tempMaxDepth / 50 + 1
        }
        invalidate()
    }

}