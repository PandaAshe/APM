/*
 * Copyright © 2017-2021 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.jiace.apm.until

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import com.jiace.apm.Application
import kotlinx.coroutines.CoroutineScope

fun Context.resolveAttribute(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}

val Any.applicationScope: CoroutineScope
    get() = Application.getCoroutineScope()

/*val Preference.activity: SettingsActivity
    get() = context as? SettingsActivity
            ?: throw IllegalStateException("Failed to resolve SettingsActivity")*/

/*val Preference.lifecycleScope: CoroutineScope
    get() = activity.lifecycleScope*/

/** 显示加载对话框 */
/*fun showProcessDialog(context: Context,message: String): ProgressDialog {
    return ProgressDialog.show(context, getString(R.string.dialog_title),message,false,false)
}*/

/** 隐藏输入框 */
fun hindKeyboard(view: View) {
    val imm = Application.get().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

