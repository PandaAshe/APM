package com.jiace.apm.base

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.jiace.apm.UserKnobs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/3/3.
3 * Description: 主题切换基类
4 *
5 */
open class ThemeChangeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            UserKnobs.darkTheme.onEach {
                val newMode = if (it) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }

                if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                    AppCompatDelegate.setDefaultNightMode(newMode)
                    UserKnobs.setDarkTheme(newMode == AppCompatDelegate.MODE_NIGHT_YES)
                    recreate()
                }
            }.launchIn(lifecycleScope)
        }
    }
}