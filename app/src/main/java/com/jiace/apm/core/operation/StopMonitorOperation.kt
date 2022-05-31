package com.jiace.apm.core.operation

import android.content.Context
import android.os.Message
import com.jiace.apm.core.service.ServiceHelper
import com.jiace.apm.until.DialogUtil

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/23.
3 * Description:
4 *
5 */
class StopMonitorOperation(context: Context): DoSomeOperation(context) {

    override fun someOperation() {
        if (DialogUtil.getChoice(mContext,"提示","是否提前结束监测？","取消","结束") == 1) {
            ServiceHelper.mVirtualDeviceService?.stopMonitor()
        }
    }

    override fun handleMessage(msg: Message?) {

    }
}