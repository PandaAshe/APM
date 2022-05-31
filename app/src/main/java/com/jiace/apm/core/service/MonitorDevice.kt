package com.jiace.apm.core.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/12.
3 * Description:
4 *
5 */
class MonitorDevice(val context: Context) {

    private var mMonitorService: MonitorService? = null

    private val mMonitorServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mMonitorService = (service as MonitorService.MonitorServiceBinder).getService()
            if (mMonitorService == null) {
                return
            }
            mMonitorService?.start()

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mMonitorService?.stop()
            mMonitorService = null
        }
    }

    fun start() {
        val intent = Intent(context,MonitorService::class.java)
        context.bindService(intent,mMonitorServiceConnection,Context.BIND_AUTO_CREATE)
    }

    fun stop() {
        try {
            context.unbindService(mMonitorServiceConnection)
            mMonitorService?.stop()
            mMonitorService = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}