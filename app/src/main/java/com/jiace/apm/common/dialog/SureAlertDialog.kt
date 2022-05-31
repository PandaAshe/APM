package com.jiace.apm.common.dialog

import android.app.Dialog
import android.content.Context
import android.support.annotation.ColorRes
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import com.jiace.apm.R
import com.jiace.apm.base.HideBarDialog
import kotlinx.android.synthetic.main.dialog_button_layout.view.*


class SureAlertDialog(context: Context) : HideBarDialog(context), View.OnClickListener {

    private var mOnPositionListener: OnPositionListener? = null
    private var mOnNegativeListener: OnNegativeListener? = null

    private var mPositionAction: ((dialog: Dialog) -> Unit)? = null
    private var mNegativeAction: ((dialog: Dialog) -> Unit)? = null

    override fun onClick(v: View?) {
        mOnPositionListener?.onPositionListener(this)
        mPositionAction?.invoke(this)
    }

    private var messageTv: TextView
    private var positionTv: TextView
    private var negativeTv: TextView
    private var titleTv:TextView

    init {
        val bootView = layoutInflater.inflate(R.layout.sure_alert_dialog_layout, null)
        // bootView.oK.setOnClickListener(this)
        titleTv = bootView.findViewById(R.id.title)
        messageTv = bootView.findViewById(R.id.message)
        positionTv = bootView.findViewById(R.id.oK)
        negativeTv = bootView.findViewById(R.id.cancel)
        positionTv.setOnClickListener(this)
        bootView.cancel.setOnClickListener {
            dismiss()
            mOnNegativeListener?.onNegativeListener(this)
            mNegativeAction?.invoke(this)
        }
        super.setView(bootView)
    }

    fun setMessageTextColor(colorId: Int) {
        messageTv.setTextColor(getColor(context,colorId))
    }

    fun setMessage(message: String) {
        messageTv.text = message
    }

    fun setPositionButton(text: String, onPositionListener: OnPositionListener) {
        positionTv.text = text
        mOnPositionListener = onPositionListener
    }

    fun setPositionButton(text: String, positionAction: (dialog: Dialog) -> Unit) {
        positionTv.text = text
        this.mPositionAction = positionAction
    }

    fun setNegativeButton(text: String, negativeAction: (dialog: Dialog) -> Unit) {
        negativeTv.text = text
        this.mNegativeAction = negativeAction
    }

    fun setNegativeButton(text: String, onNegativeListener: OnNegativeListener) {
        negativeTv.text = text
        mOnNegativeListener = onNegativeListener
    }

    fun isCancelShow(show: Boolean) {
        if (show) {
            negativeTv.visibility = View.VISIBLE
        } else {
            negativeTv.visibility = View.INVISIBLE
        }
    }


    interface OnPositionListener {
        fun onPositionListener(dialog: Dialog)
    }

    interface OnNegativeListener {
        fun onNegativeListener(dialog: Dialog)
    }

    fun setPositionButtonColor(@ColorRes colorId: Int) {
        positionTv.setTextColor(ContextCompat.getColor(context, colorId))
    }

    /**
     * 设置对话框的标题
     * @param title String
     */
    fun setTitle(title:String) {
        titleTv.text = title
    }
}