package com.jiace.apm.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/3/10.
3 * Description:
4 *
5 */
abstract class BaseViewModel: ViewModel() {

    private val observableEvents = MutableLiveData<ViewModelEvent>()

    fun observeViewModelEvents(): LiveData<ViewModelEvent> = observableEvents

    protected open fun postViewModelEvent(event: ViewModelEvent) {
        observableEvents.postValue(event)
    }

}