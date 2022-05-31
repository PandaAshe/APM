package com.jiace.apm.common.dialog

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.jiace.apm.R
import com.jiace.apm.base.HideBarDialog
import com.jiace.apm.until.getString
import kotlinx.android.synthetic.main.loading_dialog_layout.view.*

/**
2 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司
3 * FileName: LoadingDialog
4 * Author: Mrw
5 * Date: 2021/6/16 14:45
6 * Description: 加载中对话框
7 * History:
10 */
class LoadingDialog(context: Context,tip: String = getString(R.string.loading)): HideBarDialog(context) {

    var callback: (() -> Unit)? = null

    private var mMessageTv: TextView
    private var mCancelButton: Button

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        View.inflate(context,R.layout.loading_dialog_layout,null).apply {
            this.onCancelClick.setOnClickListener {
                callback?.invoke()
            }
            this.loadingText.text = tip
            setView(this)

            mMessageTv = this.loadingText
            mCancelButton = this.onCancelClick
        }
    }

    fun setMessageText(message: String) {
        mMessageTv.text = message
    }

    fun setCancelButtonGone() {
        mCancelButton.visibility = View.GONE
    }

}