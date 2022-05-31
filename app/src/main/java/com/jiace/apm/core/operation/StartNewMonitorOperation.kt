package com.jiace.apm.core.operation

import android.content.Context
import android.os.Message
import com.jiace.apm.common.dialog.SelfCheckDialog
import com.jiace.apm.core.service.ServiceHelper
import com.jiace.apm.until.DialogUtil

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/23.
3 * Description:
4 *
5 */
class StartNewMonitorOperation(context: Context) : DoSomeOperation(context) {

    private var mIsCheckSuccess = false

    override fun someOperation() {

        if (DialogUtil.getChoice(mContext, "开始新监测", "是否开始新试验?", "取消", "开始") == 0) {
            return
        }

        // 参数自检
        mHandler.sendEmptyMessage(0)
        synchronized(mLocker) {
            mLocker.wait()
        }
        if (!mIsCheckSuccess) {
            return
        }

        if (DialogUtil.getChoice(mContext, "开始试验", "所有参数检查无误,是否开始试验?", "取消", "开始试验") == 0) {
            return
        }

        // 开始新的监测
        ServiceHelper.mVirtualDeviceService?.startNewMonitor()

    }

    override fun handleMessage(msg: Message?) {
        val dialog = SelfCheckDialog(mContext)
        dialog.setContinuteAction {
            mIsCheckSuccess = true
            synchronized(mLocker) {
                mLocker.notifyAll()
            }
        }
        dialog.setCancelAction {
            mIsCheckSuccess = false
            synchronized(mLocker) {
                mLocker.notifyAll()
            }
        }
        dialog.show()
    }
}