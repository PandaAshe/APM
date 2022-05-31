package com.jiace.apm.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.jiace.apm.R

class CustomProcessBar: View
{
    private val mPaint: Paint by lazy { Paint() }
    private val mRect: RectF by lazy { RectF() }

    private val mProcessColor by lazy { ContextCompat.getColor(context, R.color.process_color) }

    private var mWidth = 0f
    private var mHeight = 0f

    private var mPercent = 0

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    {
        mWidth = width.toFloat()
        mHeight = height.toFloat()
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?)
    {
        if (canvas == null)
            return
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.BLUE
        canvas.drawColor(Color.GRAY)
        mRect.set(0f,0f,mWidth,mHeight)
        //canvas.drawRect(mRect,mPaint)

        // 绘制当前进度
        mPaint.color = mProcessColor
        mPaint.style = Paint.Style.FILL
        mRect.set(0f,0f,mWidth * mPercent / 100f,mHeight)
        canvas.drawRect(mRect,mPaint)
    }

    fun updateProcess()
    {
        mPercent += 100
        if (mPercent >= 100)
            mPercent = 100
        invalidate()
    }
}