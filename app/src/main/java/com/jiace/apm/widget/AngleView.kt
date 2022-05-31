package com.jiace.apm.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.jiace.apm.until.dip2px
import kotlin.math.min
import com.jiace.apm.R
/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/7.
3 * Description:
4 *
5 */
class AngleView: View {

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    /** 背景圆环画笔 */
    private val mBackgroundPaint =  Paint()
    /** dot画笔 */
    private val mDotPaint =  Paint()
    /** 标签画笔 */
    private val mLPaint = Paint()
    /** 圆环直径 */
    private var mDiameter  = 1f
    /** 文本rect */
    private val mRect = Rect()

    private val mRadius = 6f.dip2px(context)

    /** 偏转角 */
    var angle = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        mBackgroundPaint.apply {
            strokeWidth = 2f.dip2px(context)
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context, R.color.white)
            isAntiAlias = true
        }

        mDotPaint.apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.teal_200)
            isAntiAlias = true
        }

        mLPaint.apply {
            textAlign = Paint.Align.CENTER
            textSize = 14f.dip2px(context)
            color = ContextCompat.getColor(context, R.color.white)
            isAntiAlias = true
        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mDiameter = (min(width,height).toFloat() * 0.7).toFloat()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        canvas.translate(width / 2f,height / 2f)
        mBackgroundPaint.strokeWidth = 2f.dip2px(context)
        mBackgroundPaint.color = ContextCompat.getColor(context,R.color.gray_60)
        canvas.drawCircle(0f,0f,mDiameter / 2f,mBackgroundPaint)

        mBackgroundPaint.color = ContextCompat.getColor(context,R.color.teal_200)
        canvas.drawCircle(0f,0f,mDiameter / 4f,mBackgroundPaint)

        mBackgroundPaint.color = ContextCompat.getColor(context,R.color.gray_60)
        mBackgroundPaint.strokeWidth = 1f.dip2px(context)
        canvas.drawLine(-mDiameter / 2f,0f,mDiameter / 2f,0f,mBackgroundPaint)
        canvas.drawLine(0f,-mDiameter / 2f,0f,mDiameter / 2f,mBackgroundPaint)

        mLPaint.getTextBounds(L,0,L.length,mRect)
        canvas.drawText(L, - mDiameter / 2f - mRect.width(),mRect.height() / 2f,mLPaint)

        mLPaint.getTextBounds(RT,0,RT.length,mRect)
        canvas.drawText(RT, mDiameter / 2f + mRect.width(),mRect.height() / 2f,mLPaint)

        mLPaint.getTextBounds(B,0,B.length,mRect)
        canvas.drawText(B, 0f,- mDiameter / 2f - mRadius ,mLPaint)

        mLPaint.getTextBounds(F,0,F.length,mRect)
        canvas.drawText(F, 0f, mDiameter / 2f + mRect.height() + mRadius ,mLPaint)


        canvas.drawCircle(0f,0f,mRadius,mDotPaint)
    }

    companion object {
        const val L = "L"
        const val RT = "R"
        const val F = "F"
        const val B = "B"
    }


}