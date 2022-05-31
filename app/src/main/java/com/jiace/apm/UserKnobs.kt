package com.jiace.apm

import androidx.datastore.preferences.core.*
import com.jiace.apm.until.applicationScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/2/24.
3 * Description:
4 *
5 */
object UserKnobs {

    /** 主题参数 */
    private val DARK_THEME = booleanPreferencesKey("dark_theme")
    val darkTheme: Flow<Boolean>
        get() = Application.getPreferencesDataStore().data.map {
            it[DARK_THEME] ?: false
        }
    /** 设置主题颜色 */
    fun setDarkTheme(isNightMode: Boolean) {
        applicationScope.launch {
            Application.getPreferencesDataStore().edit {
                it[DARK_THEME] = isNightMode
            }
        }
    }
}