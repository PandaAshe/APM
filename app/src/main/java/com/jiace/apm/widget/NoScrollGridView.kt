package com.jiace.apm.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView

/**
 * @date on 2018/5/30
 */
class NoScrollGridView :GridView
{
    constructor(context: Context):super(context)

    constructor(context: Context,attrs:AttributeSet):super(context,attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2,MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }

}