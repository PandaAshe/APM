package com.jiace.apm.base

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/3/10.
3 * Description:
4 *
5 */
open class ViewModelEvent {

    var handled: Boolean = false
        private  set

    open fun handle(baseActivity: BaseActivity) {
        handled = true
    }


}