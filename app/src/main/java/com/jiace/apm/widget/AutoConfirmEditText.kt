package com.jiace.apm.widget

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager

import android.content.Context.INPUT_METHOD_SERVICE
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

class AutoConfirmEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatAutoCompleteTextView(context, attrs) {
    private var mAlertDialog: AlertDialog? = null

    init {
        setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mAlertDialog != null) {
                    val imm = v.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)

                    val buttonPositive = mAlertDialog!!.getButton(DialogInterface.BUTTON_POSITIVE)
                    buttonPositive?.performClick()

                    return@OnEditorActionListener true
                }
            }

            false
        })

        imeOptions = EditorInfo.IME_ACTION_DONE
    }

    fun setParentDialog(alertDialog: AlertDialog) {
        mAlertDialog = alertDialog
    }
}
