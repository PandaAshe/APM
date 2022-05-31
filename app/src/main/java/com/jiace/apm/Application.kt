package com.jiace.apm

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.lang.ref.WeakReference

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/2/24.
3 * Description:
4 *
5 */
class Application : android.app.Application() {

    private lateinit var preferencesDataStore: DataStore<Preferences>
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()

        try {
            sFolder  =(Environment.getExternalStorageDirectory().toString() + "/APM/")
            sCrashLogFile = (Environment.getExternalStorageDirectory().toString() + "/APM/Crash.txt")
            sTempFolder = ("$cacheDir/Temp/")
            sDataFolder = (Environment.getExternalStorageDirectory().toString() + "/APM/Data/")
            sVersionFolder = (Environment.getExternalStorageDirectory().toString() + "/APM/Version/")
        } catch (ignore: Exception) {

        }

        preferencesDataStore = PreferenceDataStoreFactory.create { applicationContext.preferencesDataStoreFile("settings") }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            coroutineScope.launch {
                AppCompatDelegate.setDefaultNightMode(
                    if (UserKnobs.darkTheme.first()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        // CrashHandler()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                MyActivityManager.setCurrentActivity(activity)
            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {

            }

        })

    }

    companion object {

        private lateinit var weakSelf: WeakReference<Application>

        var sFolder:String = ""
        var sCrashLogFile: String = ""
        var sTempFolder: String = ""
        var sDataFolder: String = ""
        var sVersionFolder: String = ""

        @JvmStatic
        fun get() = weakSelf.get()!!

        @JvmStatic
        fun getPreferencesDataStore() = get().preferencesDataStore

        @JvmStatic
        fun getCoroutineScope() = get().coroutineScope
    }

    init {
        weakSelf = WeakReference(this)
    }

}