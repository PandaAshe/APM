package com.jiace.apm.base

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.jiace.apm.until.NavigationBarUtil

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/30.
3 * Description:
4 *
5 */
open class HideBarDialog(context: Context): AlertDialog(context) {

    /** 对话框的宽度和亮度相对于整个屏幕的比例 */
    protected var mWidthRatio = 0.5f
    protected var mHeightRatio = 0.5f


    override fun show() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        super.show()
        //6.0系统加强后台管理，禁止在其他应用和窗口弹提醒弹窗，如果要弹，必须使用TYPE_APPLICATION_OVERLAY，否则弹不出
        //window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        if(NavigationBarUtil.checkNavigationBarShow(context, window!!)) {
            super.show()
        } else {
            NavigationBarUtil.focusNotAle(window)
            super.show()
            NavigationBarUtil.hideNavigationBar(window)
            NavigationBarUtil.clearFocusNotAle(window)
        }
        hideBottomUIMenu()
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    private fun hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        // lower api
        val v = this.window?.decorView
        v?.systemUiVisibility = View.GONE
    }

    /**
     * 设置窗口大小
     */
    fun setDialogSize() {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = wm.defaultDisplay
        val wd = window
        val lp = wd?.attributes
        lp?.let {
            it.gravity = Gravity.CENTER
            val size = Point()
            d.getSize(size)
            it.width = (size.x*mWidthRatio).toInt()
            it.height = (size.y*mHeightRatio).toInt()
            wd.attributes = it
        }
    }

    /**
     * 设置对话框相对于整个屏幕的比例
     * @param fWidthRatio Float
     * @param fHeightRatio Float
     */
    fun setSizeRatio(fWidthRatio:Float, fHeightRatio:Float) {
        if(mWidthRatio != fWidthRatio ||
            mHeightRatio != fHeightRatio) {
            mWidthRatio = fWidthRatio
            mHeightRatio = fHeightRatio
        }
    }

}