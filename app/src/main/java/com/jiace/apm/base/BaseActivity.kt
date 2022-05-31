package com.jiace.apm.base

import androidx.lifecycle.Observer

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/3/10.
3 * Description:
4 *
5 */
abstract class BaseActivity: ThemeChangeActivity() {


    protected open fun handleViewModelAction(event: ViewModelEvent) {
        event.handle(this)
    }

    fun observeViewModelEvents(viewModel: BaseViewModel) {
        viewModel.observeViewModelEvents().observe(this, Observer {
            val event = it.takeUnless { it == null || it.handled } ?: return@Observer
            handleViewModelAction(event)
        })
    }
}