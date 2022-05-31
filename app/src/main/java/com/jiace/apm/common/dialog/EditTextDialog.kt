package com.jiace.apm.common.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.InputFilter
import android.view.View
import com.jiace.apm.R
import com.jiace.apm.base.HideBarDialog
import kotlinx.android.synthetic.main.dialog_button_layout.view.*
import kotlinx.android.synthetic.main.edit_text_alert_dialog_layout.view.*


class EditTextDialog(context: Context) : HideBarDialog(context) {
    private val contentView = View.inflate(context, R.layout.edit_text_alert_dialog_layout, null)
    private var cancelAction: (() -> Unit)? = null
    private var positionAction: ((newValue: String) -> Unit)? = null

    init {
        contentView.oK.setTextColor(Color.RED)
        contentView.oK.setOnClickListener {
            positionAction?.invoke(contentView.inputText.text.toString().trim())
        }

        contentView.cancel.setOnClickListener {
            dismiss()
            cancelAction?.invoke()
        }
        super.setView(contentView)
    }

    // <editor-fold desc = "对外方法">

    fun setText(message: String) {
        contentView.inputText.setText(message)
        contentView.inputText.setSelectAllOnFocus(true)
    }

    fun setEditType(editType: Int) {
        contentView.inputText.inputType = editType
    }

    fun setTitle(title: String) {
        contentView.title.text = title
    }

    fun setOnCancelListener(action: () -> Unit) {
        this.cancelAction = action
    }

    fun setOnPositionListener(action: (newValue: String) -> Unit) {
        this.positionAction = action
    }

    fun setSingleButton(action: (newValue: String) -> Unit) {
        this.positionAction = action
        contentView.cancel.visibility = View.GONE
        contentView.divider.visibility = View.GONE
    }

    fun setFilters(filters: Array<InputFilter>) {
        this.contentView.inputText.filters = filters

    }
    // </editor-fold>

}