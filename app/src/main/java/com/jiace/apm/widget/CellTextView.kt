package com.jiace.apm.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.jiace.apm.R
import com.jiace.apm.until.DisplayUtil


/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2021/11/17.
3 * Description: 单个表格view
4 *
5 */
class CellTextView: View {

    constructor(context: Context?): super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.CellTextViewStyle)
        if (ta != null) {
            text = ta.getString(R.styleable.CellTextViewStyle_deviceNo) ?: ""
            mTextColor = ta.getColor(R.styleable.CellTextViewStyle_textColor,Color.BLACK)
            mTextSize = ta.getInt(R.styleable.CellTextViewStyle_textSize,18)
            ta.recycle()
            invalidate()
        }
    }


    private val mPaint = Paint()
    private val rect = Rect()

    private var mTextColor: Int = Color.BLACK
    private var mTextSize = 16
    private var mIsDrawLeftLine = false

    var text = ""
        set(value) {
            field = value
            invalidate()
        }

    init {
        mPaint.isAntiAlias = true
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.textSize = DisplayUtil.sp2px(context,mTextSize.toFloat())
        mPaint.color = mTextColor
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null)
            return

        mPaint.color = getColor(context,R.color.param_content_background)
        canvas.drawLine(width.toFloat(),0f,width.toFloat(),height.toFloat(),mPaint)

        if (mIsDrawLeftLine) {
            canvas.drawLine(0f,0f,0f,height.toFloat(),mPaint)
        }

        mPaint.color = mTextColor
        mPaint.textSize = DisplayUtil.sp2px(context,mTextSize.toFloat())
        mPaint.getTextBounds(text,0,text.length,rect)
        canvas.drawText(text,width / 2f,height / 2f + rect.height() / 2,mPaint)
    }

    fun setTextColor(color: Int) {
        mTextColor = color
        invalidate()
    }

    fun setTextSize(textSize: Int) {
        mTextSize = textSize
        invalidate()
    }

    fun setIsDrawLeftLine(isDrawLeftLine: Boolean) {
        mIsDrawLeftLine = isDrawLeftLine
        invalidate()
    }
}