package com.jiace.apm.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.jiace.apm.Application
import com.jiace.apm.R
import com.jiace.apm.core.ParamHelper
import com.jiace.apm.core.dataStruct.Record
import com.jiace.apm.core.service.ServiceHelper
import com.jiace.apm.ui.main.MonitorParam
import com.jiace.apm.until.*
import kotlin.math.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/9.
3 * Description:
4 *
5 */
class CurveView: View {

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

        /** 背景表格的行数 */
        private const val ROW_COUNT = 4

        /** 背景表格的列数 */
        private const val COLUMN_COUNT = 15

        /** 曲线表格的最大列数 */
        private const val CURVE_COLUMN_COUNT = 10

        /** 标线的位置 */
        private const val MARK_COLUMN_COUNT = 11

        /** 最大显示记录次数  */
        private const val AXIS_MIN_TIME = 60

        /** X轴的最小深度（cm） */
        private const val AXIS_MIN_DEPTH = 500

        /**
         * 最大的记录时间
         * */
        private const val MAX_RECORD_COUNT = 30

        /**最小记录记录次数  */
        private const val MIN_RECORD_COUNT = 1

        /** 点半径 */
        private const val RADIUS = 4f

        private const val DEPTH = 0
        private const val TORSION = 1
        private const val FOOTAGE = 2
        private const val ANGLE = 3

        enum class CurveType {
            Time,Depth
        }

    }

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs,0) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CurveView)
        mType = ta.getInt(R.styleable.CurveView_curve_type,DEPTH)
        when (mType) {
            DEPTH -> {
                mTitleText = getString(R.string.realtime_depth)
                mCurvePaint.color = ContextCompat.getColor(context, R.color.main_value_color)
                mRealTimePaint.color = ContextCompat.getColor(context, R.color.main_value_color)
            }

            TORSION -> {
                mTitleText = getString(R.string.realtime_torsion)
                mCurvePaint.color = ContextCompat.getColor(context, R.color.torsion_curve_colo)
                mRealTimePaint.color = ContextCompat.getColor(context, R.color.torsion_curve_colo)
            }

            FOOTAGE -> {
                mTitleText = getString(R.string.realtime_footage)
                mCurvePaint.color = ContextCompat.getColor(context, R.color.footage_curve_colo)
                mRealTimePaint.color = ContextCompat.getColor(context, R.color.footage_curve_colo)
            }

            ANGLE -> {
                mTitleText = getString(R.string.realtime_angle)
                mCurvePaint.color = ContextCompat.getColor(context, R.color.angle_curve_colo)
                mRealTimePaint.color = ContextCompat.getColor(context, R.color.angle_curve_colo)
            }
        }
        // 是否绘制X轴数值
        mIsDrawXAxis = ta.getBoolean(R.styleable.CurveView_isDrawXAxis,false)


        // 是否需要绘制极限线
        mIsDrawLimitLines = ta.getBoolean(R.styleable.CurveView_isDrawLimitLine,true)

        ta.recycle()
        invalidate()
    }

    /** 曲线类型 */
    private var mType = DEPTH

    /** X轴基准类，以深度、时间 */
    private var mCurveType = CurveType.Time

    /** 表格画笔 */
    private val mTablePaint = Paint()

    /** 上极限画笔 */
    private val mLimitMaxPaint = Paint()

    /** 下极限画笔 */
    private val mLimitMinPaint = Paint()

    /** 标题画笔 */
    private val mAixPaint = Paint()

    /** 曲线画笔 */
    private val mCurvePaint = Paint()

    /** dot画笔 */
    private val mDotPaint = Paint()

    /** realTime 画笔 */
    private val mRealTimePaint = Paint()

    /** 上极限区域 */
    private val mLimitMaxAreaPaint = Paint()

    /** 下极限区域 */
    private val mLimitMinAreaPaint = Paint()

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

    /** 标题 */
    private var mTitleText = getString(R.string.realtime_torsion)

    /** 是否绘制X轴值 */
    private var mIsDrawXAxis = false

    /** 是否需要绘制极限线 */
    private var mIsDrawLimitLines = true

    /** Y轴最大值 （单位根据不同物理量决定） */
    private var mMaxValue = 1000

    /** Y轴最小值（单位根据不同物理量决定） */
    private var mMinValue = 0

    /** 采集的数据源 */
    private var mSourceData: ArrayList<Record> = ArrayList()

    /** 单位像素对应的物理值 */
    private var mYAxisPixValue = 0f

    /** 1s/1cm 对应的像素 */
    private var mXAxisPixValue = 0f

    /** 采样间隔（s）/ (cm) */
    private var mInterval = 1

    /** 是否是实时曲线 */
    private var mIsLivingCurve = true

    /** 是否是单次触摸 */
    private var mIsOnceTouch = false

    /** 开始触摸的X位置 */
    private var mTouchStartX = 0f

    /** 移动的距离 */
    private var mMoveStartX = 0f

    /** 是否是双击 */
    private var mIsDoubleClick = false

    /** 当前最大的index */
    private var mCurrentMaxPosition = 60

    /** 当前临时最大index */
    private var mCurrentTempPosition = 60

    /** 触摸偏移量 */
    private var offsetX = 0f

    /** 上限值 */
    private var mLimitMaxValue = 600

    /** 下限值 */
    private var mLimitMinValue = 460

    /** 实时读数值 */
    private var mRealTimeValue = 420

    /** 最大显示记录次数  */
    private var mAxisTimeCount = AXIS_MIN_TIME

    /** 缩放级别 */
    private var mZoomGrade = 1
        set(value) {
            field = when {
                value < MIN_RECORD_COUNT -> {
                    mAxisTimeCount = AXIS_MIN_TIME
                    MIN_RECORD_COUNT
                }
                value > MAX_RECORD_COUNT -> {
                    mAxisTimeCount = AXIS_MIN_TIME * MAX_RECORD_COUNT
                    MAX_RECORD_COUNT
                }
                else -> {
                    mAxisTimeCount = value * AXIS_MIN_TIME
                    value
                }
            }
            invalidate()
        }

    /** 是否点击缩小按钮 */
    private var mIsClickZoomInIcon = false

    /** 是否点击放大按钮 */
    private var mIsClickZoomOutIcon = false

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

        mCurvePaint.apply {
            strokeWidth = 2f.dip2px(Application.get())
            color = ContextCompat.getColor(context, R.color.curve_color)
            style = Paint.Style.STROKE
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        mRealTimePaint.apply {
            textSize = DisplayUtil.sp2px(context,42f)
            strokeWidth = 4f.dip2px(Application.get())
            color = ContextCompat.getColor(context, R.color.curve_color)
            style = Paint.Style.FILL
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }


        mDotPaint.apply {
            color = ContextCompat.getColor(context, R.color.white)
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        mLimitMaxPaint.apply {
            strokeWidth = 1f
            color = ContextCompat.getColor(context, R.color.gray_30)
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        mLimitMinPaint.apply {
            strokeWidth = 1f
            color = ContextCompat.getColor(context, R.color.gray_30)
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        mLimitMaxAreaPaint.apply {
            strokeWidth = 1f
            color = ContextCompat.getColor(context, R.color.limit_max_area_color)
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        mLimitMinAreaPaint.apply {
            strokeWidth = 1f
            color = ContextCompat.getColor(context, R.color.limit_min_area_color)
            style = Paint.Style.FILL
            isAntiAlias = true
        }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        mTableWith = width - HORIZONTAL_PADDING - END_HORIZONTAL_PADDING
        mTableHeight = (height - 1 * TOP_PADDING - BOTTOM_PADDING).toFloat()

        mCellWith = mTableWith / COLUMN_COUNT
        mCellHeight = mTableHeight / ROW_COUNT
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        drawTitle(canvas)

        canvas.translate(HORIZONTAL_PADDING, TOP_PADDING)
        mTableRect.set(0f,0f, mTableWith, mTableHeight)
        canvas.drawRect(mTableRect,mTablePaint)

        // 计算最大最小值
        calc()
        // 计算像素值
        calcPixValue()

        drawBackgroundTable(canvas)

        // 移动至坐标原点
        canvas.translate(0f,mTableHeight)

        drawAxisTitle(canvas)

        drawCurve(canvas)

        drawCurveAndDot(canvas)

    }

    /** 绘制曲线名称 */
    private fun drawTitle(canvas: Canvas) {
        // 绘制曲线名称
        canvas.save()
        canvas.translate(mTableWith / 2f,0f)
        mAixPaint.getTextBounds(mTitleText,0,mTitleText.length,mTextRect)
        //canvas.drawText(mTitleText, 0f, TEXT_PADDING + mTextRect.height(),mAixPaint)
        canvas.restore()
    }


    private fun calcPixValue() {

        if (!isDrawLimitLineAndArea()) {
            // 计算单位像素对应的值
            mXAxisPixValue =  mCellWith * CURVE_COLUMN_COUNT / mAxisTimeCount
            mYAxisPixValue = mTableHeight / abs(mMaxValue - mMinValue)
            return
        }

        val offsetLimitValue = abs(mLimitMaxValue - mLimitMinValue) / 2

        // 计算上下最大值
        val limitMaxCount: Float
        val limitMinCount: Float
        val count: Float

        when {
            // 全部超过限值 //
            /**
             *   o
             *   o
             *   -
             *   -
             *
             * */
            //  MAX MIN > LMAX
            mMinValue > mLimitMaxValue -> {
                limitMaxCount = ceil(abs(mMaxValue - mLimitMaxValue) / offsetLimitValue.toFloat())
                mMaxValue = (mLimitMaxValue +  limitMaxCount * offsetLimitValue).toInt()
                mMinValue = (mLimitMinValue -  limitMaxCount * offsetLimitValue).toInt()
            }
            // LMIN > MAX MIN
            /**
             *
             *   -
             *   -
             *   o
             *   0
             * */
            mMaxValue < mLimitMinValue -> {

                limitMinCount = ceil(abs(mMinValue - mLimitMinValue) / offsetLimitValue.toFloat())
                mMaxValue = (mLimitMaxValue +  limitMinCount * offsetLimitValue).toInt()
                mMinValue = (mLimitMinValue -  limitMinCount * offsetLimitValue).toInt()
            }


            // MAX > LMAX  && MIN < LMIN
            /**
             *   0
             *   -
             *   -
             *   0
             * */
            mMaxValue > mLimitMaxValue && mMinValue < mLimitMinValue -> {
                limitMaxCount = ceil(abs(mMaxValue - mLimitMaxValue) / offsetLimitValue.toFloat())
                limitMinCount = ceil(abs(mMinValue - mLimitMinValue) / offsetLimitValue.toFloat())
                count = max(limitMaxCount,limitMinCount)
                mMaxValue = (mLimitMaxValue +  count * offsetLimitValue).toInt()
                mMinValue = (mLimitMinValue -  count * offsetLimitValue).toInt()
            }
            // MAX < LMAX && MIN < LMIN

            /**
             *   -
             *   o
             *   -
             *   o
             *
             * */
            mMaxValue <= mLimitMaxValue && mMinValue < mLimitMinValue -> {
                limitMinCount = ceil(abs(mMinValue - mLimitMinValue) / offsetLimitValue.toFloat())
                mMaxValue = (mLimitMaxValue +  limitMinCount * offsetLimitValue).toInt()
                mMinValue = (mLimitMinValue -  limitMinCount * offsetLimitValue).toInt()
            }

            // MAX < LMAX && MIN < LMIN
            /**
             *  -
             *  o
             *  o
             *  -
             * */
            mMaxValue <= mLimitMaxValue && mMinValue >= mLimitMinValue -> {

                mMaxValue = (mLimitMaxValue +  offsetLimitValue)
                mMinValue = (mLimitMinValue -  offsetLimitValue)
            }

            /**
             *  o
             *  -
             *  o
             *  -
             * */
            mLimitMaxValue in mMinValue until mMaxValue -> {
                limitMaxCount = ceil(abs(mMaxValue - mLimitMaxValue) / offsetLimitValue.toFloat())
                mMaxValue = (mLimitMaxValue +  limitMaxCount * offsetLimitValue).toInt()
                mMinValue = (mLimitMinValue -  limitMaxCount * offsetLimitValue).toInt()
            }
        }

        // 计算单位像素对应的值
        mXAxisPixValue =  mCellWith * CURVE_COLUMN_COUNT / mAxisTimeCount
        mYAxisPixValue = mTableHeight / abs(mMaxValue - mMinValue)
    }

    /** 绘制背景表格 */
    private fun drawBackgroundTable(canvas: Canvas) {

        mTablePaint.color = ContextCompat.getColor(context,R.color.gray_30)

        val dashPathEffect = DashPathEffect(floatArrayOf(10f,10f),0f)
        val effectPath = Path()

        // 上极限基线是否重合
        val isDrawLimitMaxLine = (mLimitMaxValue -mMinValue) * mYAxisPixValue > mCellHeight * 3 - 2 &&  (mLimitMaxValue -mMinValue) * mYAxisPixValue < mCellHeight * 3 + 2
        // 下极限基线是否重合
        val isDrawLimitMinLine = (mLimitMinValue  -mMinValue) * mYAxisPixValue > mCellHeight * 1 - 2 && (mLimitMinValue  -mMinValue) * mYAxisPixValue < mCellHeight * 1 + 2

        for (i in 1 until ROW_COUNT) {
            when (i)
            {
                1 -> {
                    // 上极限 判断是否重合
                    if (isDrawLimitMaxLine && isDrawLimitLineAndArea()) {
                        // 画虚线  判断当前值是否超过上限值 超过绘制红色
                        effectPath.reset()
                        mLimitMaxPaint.pathEffect = dashPathEffect
                        effectPath.moveTo(0f,mCellHeight * i)
                        effectPath.lineTo(mCellWith * MARK_COLUMN_COUNT,mCellHeight * i)
                        if (mRealTimeValue > mLimitMaxValue) {
                            mLimitMaxPaint.color = ContextCompat.getColor(context,R.color.red_color)
                        } else {
                            mLimitMaxPaint.color = ContextCompat.getColor(context,R.color.gray_30)
                        }
                        canvas.drawPath(effectPath,mLimitMaxPaint)
                    } else {
                        canvas.drawLine(0f,mCellHeight * i,mCellWith * MARK_COLUMN_COUNT,mCellHeight * i,mTablePaint)
                    }
                }

                3 -> {
                    // 下极限
                    if (isDrawLimitMinLine && isDrawLimitLineAndArea()) {
                        // 画虚线  判断当前值是否超过下限值 超过绘制黄色
                        effectPath.reset()
                        mLimitMinPaint.pathEffect = dashPathEffect
                        effectPath.moveTo(0f,mCellHeight * i)
                        effectPath.lineTo(mCellWith * MARK_COLUMN_COUNT,mCellHeight * i)
                        if (mRealTimeValue < mLimitMinValue) {
                            mLimitMinPaint.color = ContextCompat.getColor(context,R.color.choose_item_text_color)
                        } else {
                            mLimitMinPaint.color = ContextCompat.getColor(context,R.color.gray_30)
                        }
                        canvas.drawPath(effectPath,mLimitMinPaint)
                    } else {
                        canvas.drawLine(0f,mCellHeight * i,mCellWith * MARK_COLUMN_COUNT,mCellHeight * i,mTablePaint)
                    }
                }
                else -> {
                    canvas.drawLine(0f,mCellHeight * i,mCellWith * MARK_COLUMN_COUNT,mCellHeight * i,mTablePaint)
                }
            }
        }

        if (!isDrawLimitMaxLine  && isDrawLimitLineAndArea()) {
            effectPath.reset()
            mLimitMaxPaint.pathEffect = dashPathEffect
            effectPath.moveTo(0f,mTableHeight - (mLimitMaxValue - mMinValue) * mYAxisPixValue)
            effectPath.lineTo(mCellWith * MARK_COLUMN_COUNT,mTableHeight - (mLimitMaxValue - mMinValue) * mYAxisPixValue)
            // 画虚线  判断当前值是否超过上限值 超过绘制红色
            if (mRealTimeValue > mLimitMaxValue) {
                mLimitMaxPaint.color = ContextCompat.getColor(context,R.color.red_color)
            } else {
                mLimitMaxPaint.color = ContextCompat.getColor(context,R.color.gray_30)
            }
            canvas.drawPath(effectPath,mLimitMaxPaint)
        }

        if (!isDrawLimitMinLine  && isDrawLimitLineAndArea()) {
            effectPath.reset()
            mLimitMinPaint.pathEffect = dashPathEffect
            effectPath.moveTo(0f, mTableHeight - (mLimitMinValue - mMinValue) * mYAxisPixValue)
            effectPath.lineTo(mCellWith * MARK_COLUMN_COUNT,mTableHeight - (mLimitMinValue - mMinValue) * mYAxisPixValue)

            // 画虚线  判断当前值是否超过下限值 超过绘制黄色
            if (mRealTimeValue < mLimitMinValue) {
                mLimitMinPaint.color = ContextCompat.getColor(context,R.color.choose_item_text_color)
            } else {
                mLimitMinPaint.color = ContextCompat.getColor(context,R.color.gray_30)
            }
            canvas.drawPath(effectPath,mLimitMinPaint)
        }

        // 计算
        //val offsetCount = (offsetX / (mTableWith / AXIS_MIN_TIME)).toInt()

        // if (mIsOnceTouch) {
        //     mCurrentMaxPosition = mCurrentTempPosition - offsetCount
        // }

        // 绘制到实时数据显示处
        for (i in 1 .. MARK_COLUMN_COUNT) {
            canvas.drawLine(mCellWith * i,0f,mCellWith * i,mTableHeight,mTablePaint)
        }

        // 绘制放大缩小按钮
        drawIcon(R.drawable.ic_baseline_zoom_out_24,canvas)
        drawIcon(R.drawable.ic_baseline_zoom_in_24,canvas)

        // 绘制警示区域
        if (isDrawLimitLineAndArea() && mRealTimeValue > mLimitMaxValue) {
            val rectF = RectF(0f,0f,mCellWith * MARK_COLUMN_COUNT,(mMaxValue - mLimitMaxValue) * mYAxisPixValue)
            canvas.drawRect(rectF,mLimitMaxAreaPaint)
        }

        if (isDrawLimitLineAndArea() && mRealTimeValue < mLimitMinValue) {
            val rectF = RectF().apply {
                left = 0f
                top = (mMaxValue - mLimitMinValue) * mYAxisPixValue
                right = mCellWith * MARK_COLUMN_COUNT
                bottom = mTableHeight
            }
            canvas.drawRect(rectF,mLimitMinAreaPaint)
        }
    }

    /** 绘制图标 */
    private fun drawIcon(@DrawableRes id: Int,canvas: Canvas) {
        val bitmap = Utils.drawableToBitmap(ContextCompat.getDrawable(context,id))
        val srcRect = Rect(0,0,bitmap.width,bitmap.height)
        when (id) {
            R.drawable.ic_baseline_zoom_out_24 -> {
                canvas.drawBitmap(bitmap,srcRect,getZoomOutBitmapRect(bitmap),mAixPaint)
            }

            R.drawable.ic_baseline_zoom_in_24 -> {
                canvas.drawBitmap(bitmap,srcRect,getZoomInBitmapRect(bitmap),mAixPaint)
            }
        }
    }

    /** 绘制坐标轴值和曲线名称 */
    private fun drawAxisTitle(canvas: Canvas) {
        // 绘制Y轴方向的值
        drawYAxisValueText(canvas)

        var xAxisMaxValue = 0

        // 判断绘图类型
        when (mCurveType) {
            CurveType.Time -> {
                // 单元格X轴的值
                //xAxisMaxValue = if (mSourceData.size <= AXIS_MIN_TIME) {
                //    AXIS_MIN_TIME * mInterval
                //} else {
                //    mSourceData.size * mInterval
                //}
                xAxisMaxValue =  mAxisTimeCount
                val cellTime = mAxisTimeCount / CURVE_COLUMN_COUNT

                if (mIsDrawXAxis) {
                    for (i in  CURVE_COLUMN_COUNT downTo 0) {
                        val xAxisTextValue ="%d".format(- i * cellTime)
                        mAixPaint.getTextBounds(xAxisTextValue,0,xAxisTextValue.length,mTextRect)
                        canvas.drawText(xAxisTextValue, mCellWith *  (CURVE_COLUMN_COUNT - i),TEXT_PADDING + mTextRect.height(),mAixPaint)
                    }
                }
            }

            CurveType.Depth -> {
                // 单元格X轴的值
                val cellTime = AXIS_MIN_DEPTH / COLUMN_COUNT

                xAxisMaxValue = if (mSourceData.isEmpty()) {
                    AXIS_MIN_DEPTH
                } else {
                    val maxDepth = mSourceData.maxOfOrNull { it.Depth }!!
                    if (maxDepth <= AXIS_MIN_DEPTH) {
                        AXIS_MIN_DEPTH
                    } else {
                        (maxDepth / cellTime + 1) * cellTime
                    }
                }
                if (mIsDrawXAxis) {
                    for (i in COLUMN_COUNT downTo 0) {
                        val xAxisTextValue = formatSecondToTime(xAxisMaxValue - i *  cellTime)
                        mAixPaint.getTextBounds(xAxisTextValue,0,xAxisTextValue.length,mTextRect)
                        canvas.drawText(xAxisTextValue, mCellWith *  i,TEXT_PADDING + mTextRect.height(),mAixPaint)
                    }
                }
            }
        }
    }

    /** 绘制Y轴数值 */
    private fun drawYAxisValueText(canvas: Canvas) {
        // 绘制Y轴数值
        val maxValueText: String
        val minValueText: String

        when (mType) {

            DEPTH -> {
                maxValueText= "%d".format(mMaxValue)
                minValueText = "%d".format(mMinValue)
            }

            FOOTAGE -> {
                maxValueText= "%d".format(mMaxValue)
                minValueText = "%d".format(mMinValue)
            }

            ANGLE -> {
                maxValueText= "%.1f".format(mMaxValue / 10f)
                minValueText = "%.1f".format(mMinValue / 10f)
            }

            TORSION -> {
                maxValueText= "%.1f".format(mMaxValue / 1000f)
                minValueText = "%.1f".format(mMinValue / 1000f)
            }

            else -> {
                maxValueText= "1000"
                minValueText = "0"
            }
        }

        mAixPaint.getTextBounds(maxValueText,0,maxValueText.length,mTextRect)
        canvas.drawText(maxValueText,0f - TEXT_PADDING - mTextRect.width() / 2,-(mTableHeight - mTextRect.height() / 2f),mAixPaint)

        mAixPaint.getTextBounds(minValueText,0,minValueText.length,mTextRect)
        canvas.drawText(minValueText,0f - TEXT_PADDING - mTextRect.width() / 2, mTextRect.height() / 2f,mAixPaint)

        /** 上下极限值文本 */
        var maxLimitText = ""
        var minLimitText = ""
        when (mType) {

            FOOTAGE -> {
                maxLimitText= "%d".format(mLimitMaxValue)
                minLimitText = "%d".format(mLimitMinValue)
            }

            ANGLE -> {
                maxLimitText= "%.1f".format(mLimitMaxValue / 10f)
                minLimitText = "%.1f".format(mLimitMinValue / 10f)
            }

            TORSION -> {
                maxLimitText= "%.1f".format(mLimitMaxValue / 1000f)
                minLimitText = "%.1f".format(mLimitMinValue / 1000f)
            }

            else -> {

            }
        }

        // 没有上下值的不绘制 比如深度
        if (maxLimitText.isEmpty() || minLimitText.isEmpty()) {
            return
        }

        mAixPaint.getTextBounds(maxLimitText,0,maxLimitText.length,mTextRect)
        canvas.drawText(maxLimitText,0f - TEXT_PADDING - mTextRect.width() / 2,-(mLimitMaxValue - mMinValue) * mYAxisPixValue + mTextRect.height() / 2f,mAixPaint)

        mAixPaint.getTextBounds(minLimitText,0,minLimitText.length,mTextRect)
        canvas.drawText(minLimitText,0f - TEXT_PADDING - mTextRect.width() / 2, -(mLimitMinValue - mMinValue) * mYAxisPixValue + mTextRect.height() / 2f,mAixPaint)
    }

    /** 绘制曲线 */
    private fun drawCurve(canvas: Canvas) {
        if (mSourceData.isEmpty()) {
            return
        }
        val path = Path()

        when (mCurveType) {
            CurveType.Depth -> {
                val drawRecord = ArrayList<Record>()
                drawRecord.addAll(mSourceData)
                // 去重及排序
                drawRecord.distinctBy { it.Depth }
                drawRecord.sortBy { it.Depth }

                drawRecord.mapIndexed { index, record ->
                    when (mType) {
                        TORSION -> {
                            if (index == 0) {
                                path.moveTo(0f,-record.Torsion * mYAxisPixValue)
                            } else {
                                path.lineTo(index * mXAxisPixValue,-record.Torsion * mYAxisPixValue)
                            }
                        }

                        FOOTAGE -> {
                            if (index == 0) {
                                path.moveTo(0f,-record.Footage * mYAxisPixValue)
                            } else {
                                path.lineTo(index * mXAxisPixValue,-record.Footage * mYAxisPixValue)
                            }
                        }

                        ANGLE -> {
                            if (index == 0) {
                                path.moveTo(0f,-record.AngleOfDip * mYAxisPixValue)
                            } else {
                                path.lineTo(index * mXAxisPixValue,-record.AngleOfDip * mYAxisPixValue)
                            }
                        }
                    }
                }
            }

            CurveType.Time -> {
                val drawRecord = mSourceData.takeLast(mAxisTimeCount)
                drawRecord.mapIndexed { index, record ->
                    when (mType) {
                        DEPTH -> {
                            if (index == 0) {
                                path.moveTo((mAxisTimeCount - drawRecord.size + 1) * mXAxisPixValue,-(record.Depth - mMinValue) * mYAxisPixValue)
                            } else {
                                path.lineTo((mAxisTimeCount  - drawRecord.size + index + 1) * mXAxisPixValue,-(record.Depth - mMinValue) * mYAxisPixValue)
                            }
                        }

                        TORSION -> {
                            if (index == 0) {
                                path.moveTo((mAxisTimeCount - drawRecord.size + 1 ) * mXAxisPixValue,-(record.Torsion - mMinValue) * mYAxisPixValue)
                            } else {
                                path.lineTo((mAxisTimeCount - drawRecord.size + index + 1) * mXAxisPixValue,-(record.Torsion - mMinValue) * mYAxisPixValue)
                            }
                        }

                        FOOTAGE -> {
                            if (index == 0) {
                                path.moveTo((mAxisTimeCount - drawRecord.size + 1) * mXAxisPixValue,-(record.Footage - mMinValue) * mYAxisPixValue)
                            } else {
                                path.lineTo((mAxisTimeCount - drawRecord.size + index + 1) * mXAxisPixValue,-(record.Footage - mMinValue) * mYAxisPixValue)
                            }
                        }

                        ANGLE -> {
                            if (index == 0) {
                                path.moveTo((mAxisTimeCount - drawRecord.size + 1) * mXAxisPixValue,-(record.AngleOfDip -mMinValue) * mYAxisPixValue)
                            } else {
                                path.lineTo((mAxisTimeCount - drawRecord.size + index + 1) * mXAxisPixValue,-(record.AngleOfDip -mMinValue) * mYAxisPixValue)
                            }
                        }
                    }
                }
                canvas.drawPath(path,mCurvePaint)

                if (drawRecord.isNotEmpty()) {
                    // 绘制点和加重线
                    mTablePaint.color = ContextCompat.getColor(context,R.color.white)
                    canvas.drawLine(mXAxisPixValue * mAxisTimeCount,0f,mXAxisPixValue * mAxisTimeCount,-mTableHeight,mTablePaint)
                    canvas.drawCircle(mXAxisPixValue * mAxisTimeCount,-(mRealTimeValue - mMinValue)* mYAxisPixValue,RADIUS,mDotPaint)
                }
            }
        }
    }

    /** 计算最大最小值 */
    private fun calc() {

        val drawCurveSourceData = mSourceData.takeLast(mAxisTimeCount)

        when (mType) {

            DEPTH -> {
                if (drawCurveSourceData.isEmpty()) {
                    mMaxValue = 50
                    mMinValue = 0
                    return
                }

                val maxValue = drawCurveSourceData.maxOfOrNull { it.Depth }!!
                val minValue = drawCurveSourceData.minOfOrNull { it.Depth }!!

                mMaxValue = (maxValue / 50 + 1) * 50
                mMinValue = when (minValue) {
                    in 0..50 -> {
                        0
                    }
                    else -> {
                        (minValue / 50 - 1) * 50
                    }
                }
            }

            FOOTAGE -> {

                if (drawCurveSourceData.isEmpty()) {
                    mMaxValue = 1000
                    mMinValue = 300
                    return
                }

                val maxValue = drawCurveSourceData.maxOfOrNull { it.Footage }!!
                val minValue = drawCurveSourceData.minOfOrNull { it.Footage }!!

                mMaxValue = (maxValue / 50 + 1) * 50
                mMinValue = when (minValue) {
                    in 0..50 -> {
                        0
                    }
                    else -> {
                        (minValue / 50 - 1) * 50
                    }
                }
            }

            TORSION -> {
                if (drawCurveSourceData.isEmpty()) {
                    mMaxValue = 1000
                    mMinValue = 0
                    return
                }

                val maxValue = drawCurveSourceData.maxOfOrNull { it.Torsion }!!
                val minValue = drawCurveSourceData.minOfOrNull { it.Torsion }!!

                mMaxValue = (maxValue / 1000 + 1) * 1000
                mMinValue = when (minValue) {
                    in 0..1000 -> {
                        0
                    }
                    else -> {
                        (minValue / 1000 - 1) * 1000
                    }
                }
            }

            ANGLE -> {
                if (drawCurveSourceData.isEmpty()) {
                    mMaxValue = 10
                    mMinValue = 0
                    return
                }

                val maxValue = drawCurveSourceData.maxOfOrNull { it.AngleOfDip }!!
                val minValue = drawCurveSourceData.minOfOrNull { it.AngleOfDip }!!

                val absMaxValue = max(abs(maxValue), abs(minValue))

                mMaxValue = (absMaxValue / 10 + 1) * 10
                mMinValue = -mMaxValue
            }
        }
    }


    /** 绘制实时读数值及小白点 */
    private fun drawCurveAndDot(canvas: Canvas) {

        canvas.save()
        canvas.translate(mCellWith * MARK_COLUMN_COUNT,0f)

        val realTimeText = when (mType) {

            DEPTH -> {
              "%d".format(mRealTimeValue)
            }

            FOOTAGE -> {
               "%d".format(mRealTimeValue)
            }
            ANGLE -> {
                "%.1f".format(mRealTimeValue / 10f)
            }
            TORSION -> {
               "%.1f".format(mRealTimeValue / 1000f)
            }

            else -> {
                "0"
            }
        }

        mRealTimePaint.getTextBounds(realTimeText,0,realTimeText.length,mTextRect)
        canvas.drawText(realTimeText,
             mCellWith * (COLUMN_COUNT - MARK_COLUMN_COUNT) / 2, -mTableHeight / 2 + mTextRect.height() / 2f - 2 * TEXT_PADDING,mRealTimePaint)

        mAixPaint.getTextBounds(mTitleText,0,mTitleText.length,mTextRect)
        canvas.drawText(mTitleText,
            mCellWith * (COLUMN_COUNT - MARK_COLUMN_COUNT) / 2, -mTableHeight / 2 + mTextRect.height() + 3 * TEXT_PADDING,mAixPaint)

        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)
            return true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsOnceTouch = true
                mTouchStartX = event.x

                mIsClickZoomOutIcon =
                    event.x > 0f + HORIZONTAL_PADDING
                            && event.x < mCellWith + HORIZONTAL_PADDING
                            && event.y > 0 + mTableHeight - mCellHeight  + TOP_PADDING
                            && event.y < mTableHeight  + TOP_PADDING

                mIsClickZoomInIcon =
                    event.x > 0f + HORIZONTAL_PADDING + mCellWith * (MARK_COLUMN_COUNT - 1)
                            && event.x < mCellWith * MARK_COLUMN_COUNT + HORIZONTAL_PADDING
                            && event.y > 0 + mTableHeight - mCellHeight  + TOP_PADDING
                            && event.y < mTableHeight + TOP_PADDING

                d("FtCurve","X: ${event.x}   Y: ${event.y}")
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                mMoveStartX = event.x
                offsetX = (mMoveStartX - mTouchStartX)
                d("FtCurve","offsetX: $offsetX")
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                //mIsOnceTouch = false
                //offsetX = 0f
                //invalidate()

                if (mIsClickZoomOutIcon) {
                    mZoomGrade++
                    mIsClickZoomOutIcon = false
                }
                if (mIsClickZoomInIcon) {
                    mZoomGrade--
                    mIsClickZoomInIcon = false
                }
            }
        }
        return super.onTouchEvent(event)
    }


    /**
     * 新增一个采样点
     *
     * */
    fun updateCurve(data: Record) {
        mSourceData.add(data)
        invalidate()
    }

    /** 刷新曲线 */
    fun updateCurve(data: MonitorParam) {

        mSourceData.add(data.record)
        // 只保留1800点
        if (mSourceData.size > MAX_RECORD_COUNT * AXIS_MIN_TIME) {
            mSourceData.removeFirst()
        }

        mInterval = ParamHelper.mMonitorParam.RecordInterval
        when (mType) {

            ANGLE -> {
                mLimitMaxValue = ParamHelper.mMonitorParam.AngleOfDipMax
                mLimitMinValue = -ParamHelper.mMonitorParam.AngleOfDipMax
            }

            FOOTAGE -> {
                mLimitMaxValue = ParamHelper.mMonitorParam.FootageMax
                mLimitMinValue = ParamHelper.mMonitorParam.FootageMin

            }

            TORSION -> {
                mLimitMaxValue = ParamHelper.mMonitorParam.TorsionMax
                mLimitMinValue = ParamHelper.mMonitorParam.TorsionMin
            }
        }

        mRealTimeValue = when (mType) {
            DEPTH -> data.record.Depth
            TORSION -> data.record.Torsion
            FOOTAGE -> data.record.Footage
            ANGLE -> data.record.AngleOfDip
            else -> 0
        }
        invalidate()
    }

    /** 是否需要绘制极限区域 */
    private fun isDrawLimitLineAndArea() : Boolean {
        return mType != DEPTH && mIsDrawLimitLines
    }

    /** 获取zoom out 图标 rect */
    private fun getZoomOutBitmapRect(bitmap: Bitmap) = run {
        Rect().apply {
            left = (mCellWith / 2 - bitmap.width / 2).toInt()
            top = (mTableHeight - mCellHeight / 2 - bitmap.height / 2).toInt()
            right = (mCellWith / 2 + bitmap.width / 2).toInt()
            bottom = (mTableHeight - mCellHeight / 2 + bitmap.height / 2).toInt()
        }
    }
    /** 获取zoom in 图标 rect */
    private fun getZoomInBitmapRect(bitmap: Bitmap) = run {
        Rect().apply {
            left = (MARK_COLUMN_COUNT * mCellWith - mCellWith / 2 - bitmap.width / 2).toInt()
            top = (mTableHeight - mCellHeight / 2 - bitmap.height / 2).toInt()
            right = (MARK_COLUMN_COUNT * mCellWith - mCellWith / 2 + bitmap.width / 2).toInt()
            bottom = (mTableHeight - mCellHeight / 2 + bitmap.height / 2).toInt()
        }
    }
}