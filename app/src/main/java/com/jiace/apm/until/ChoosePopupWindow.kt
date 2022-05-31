package com.jiace.apm.until

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.PopupWindow
import androidx.core.content.ContextCompat.getColor
import com.jiace.apm.R
import kotlinx.android.synthetic.main.popup_window_layout.view.*
import kotlinx.android.synthetic.main.simple_list_item.view.*


/**
 * 显示参数选择对话框
 * */
class ChoosePopupWindow(val context: Context,val checkValue: Int,val chooseArray: Array<String>,callback: (chooseValue: Int)-> Unit): PopupWindow(context) {

    init {
        contentView = View.inflate(context, R.layout.popup_window_layout,null)
        contentView.chooseLv.adapter = ChooseAdapter()
        contentView.chooseLv.setOnItemClickListener { _, _, i, _ ->
            dismiss()
            if (checkValue != i) {
                callback.invoke(i)
            }
        }
        setBackgroundDrawable(context.getDrawable(R.drawable.curve_view_background))
        isOutsideTouchable = true
        isTouchable = true
        elevation = 10f
        width = 120f.dip2px(context).toInt()
    }

    inner class ChooseAdapter: BaseAdapter() {
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val bootView = p1 ?: View.inflate(context,R.layout.simple_list_item,null)
            if (checkValue == p0) {
                bootView.text1.setTextColor(getColor(context,R.color.purple_700))
            } else {
                bootView.text1.setTextColor(getColor(context,R.color.black))
            }
            bootView.text1.text = chooseArray[p0]
            return bootView
        }

        override fun getItem(p0: Int): Any {
            return  p0
        }

        override fun getItemId(p0: Int): Long {
            return  p0.toLong()
        }

        override fun getCount() = chooseArray.size
    }
}