package com.jiace.apm.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.jiace.apm.R
import kotlinx.android.synthetic.main.card_view_layout.view.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/4/29.
3 * Description:
4 *
5 */
class CardView: LinearLayout {

    private val bootView = View.inflate(context, R.layout.card_view_layout, null)
    var value = "0"
        set(value) {
            field = value
            bootView.valueTv.text = field
        }


    constructor(context: Context) : super(context)
    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet, 0) {
        val ta = context?.obtainStyledAttributes(attributeSet, R.styleable.CardViewStyle)
        if (ta != null) {
            //val textValue = ta.getString(R.styleable.CardViewStyle_text)
            // bootView.valueTv.text = textValue
            bootView.titleTv.text = ta.getString(R.styleable.CardViewStyle_title)
            bootView.valueTv.setTextColor(ta.getColor(R.styleable.CardViewStyle_color,ContextCompat.getColor(context,android.R.color.holo_orange_light)))
            ta.recycle()
        }
    }

    init {

        addView(bootView)
    }


}