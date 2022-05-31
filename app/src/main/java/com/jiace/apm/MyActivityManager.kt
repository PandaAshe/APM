package com.jiace.apm

import android.app.Activity
import android.content.Context
import java.lang.ref.WeakReference


/**
 * @author: yw
 * @date: 2021/8/29
 * @description: 保存当前激活的activity
 */
object MyActivityManager  {

    private var sCurrentActivityWeakRef: WeakReference<Activity>? = null

    fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef!!.get()
        }
        return currentActivity
    }

    fun getCurrentActivityContext(): Context? {
        var currentActivity: Activity? = null
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef!!.get()
        }
        return currentActivity?.baseContext
    }

    fun setCurrentActivity(activity: Activity?) {
        sCurrentActivityWeakRef?.clear()
        sCurrentActivityWeakRef = WeakReference<Activity>(activity)
    }
}

