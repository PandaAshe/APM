package com.jiace.apm.core.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.jiace.apm.core.ParamHelper
import com.jiace.apm.core.event.OnReceiveEndMainData
import com.jiace.apm.until.Utils
import com.jiace.apm.until.post
import java.lang.ref.WeakReference
import java.util.concurrent.LinkedBlockingQueue

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/13.
3 * Description: 与前端通信服务
4 *
5 */
class MainDeviceService: Service() {


    companion object {
        /** 正常工作模式 */
        const val STATUS_WORKING = 0

        /** 扫描前端机 */
        const val STATUS_SCANNING = 1

        /** 正在连接前端机 */
        const val STATUS_WAIT_FOR_CONNECT = 2
    }

    /**采集仪主机编号 */
    @Volatile
    private var mSampleMachineId = 1111

    /** 主机编号 */
    private var mMachineId = ""

    /** 是否与采集仪连接 */
    private var mIsConnected = false

    /** 最后一次接收到采集仪的数据时间 */
    private var mLastReceiveDataTime = 0L

    /** 通讯消息发送队列 */
    private lateinit var mSendQueue: LinkedBlockingQueue<Any>

    /** 通讯接收消息队列 */
    private lateinit var mReceiveQueue: LinkedBlockingQueue<Any>

    /** 队列的最大容量 */
    private val mQueueCapacity = 100

    /** 等待发送响应 */
    private val mResponseWaiting = Any()

    /** 等待响应的时间 */
    private val mResponseTime = 1000L

    /**是否开启调试模式 */
    private var mIsDebugEnable = false


    /** 工作线程 */
    private var mWorkThread: MainDeviceService.ReceiveThread? = null

    private lateinit var mBinder: MainDeviceBinder


    inner class MainDeviceBinder constructor(service: MainDeviceService): Binder() {

        private val mService : WeakReference<MainDeviceService> = WeakReference(service)

        fun getService() = mService.get()
    }

    override fun onCreate() {
        super.onCreate()
        mBinder = MainDeviceBinder(this)
        ServiceHelper.mMainDeviceService = this
    }

    override fun onBind(intent: Intent): IBinder {

        return mBinder
    }

    /** 开始工作 */
    fun start() {
        mWorkThread = ReceiveThread()
        mWorkThread?.start()
    }

    /** 结束任务 */
    fun stop() {
        mWorkThread?.cancel()
        mWorkThread = null
    }


    /** 是否连接 */
    fun isConnected(): Boolean {
        return mIsConnected
    }

    /** 发送当前数据 */
    fun writeToDevice() {

    }

    private inner class ReceiveThread: Thread("ReceiveThread") {

        override fun run() {
            while (!isInterrupted) {
                try {
                    doWork()
                    sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    return
                }
            }
        }

        fun cancel() {
            try {
                interrupt()
                this.join(2000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun doWork() {
            val frame =  Frame()
            frame.makeFrame(mSampleMachineId,Frame.Respond_Machine_Statue,SensorData().toBytes())
            onReceiveData(frame.toBytes()!!)
        }
    }

    /** 接收收采集仪数据 */
    private fun onReceiveData(data: ByteArray) {
        val frame = Frame()
        frame.getFrameFromBuffer(data,0)
        if (frame.isValid()) {
            when (frame.getCommand()) {
                Frame.Respond_Config_Sensor -> {
                    if (frame.getSenderId() != mSampleMachineId) {
                        return
                    }

                    if (!Utils.stringToBytes(ParamHelper.mSensorParam.TorsionBluetoothNo,9).contentEquals(frame.getData())) {
                        return
                    }
                    // todo 配置成功
                }

                Frame.Respond_Machine_Statue -> {
                    if (frame.getSenderId() != mSampleMachineId) {
                        return
                    }

                    if (frame.getData() == null) {
                        return
                    }

                    /**
                     * @see com.jiace.apm.core.service.MonitorService.onReceiveSensorStatus
                     * */
                    OnReceiveEndMainData().apply {
                        sensorData.convertFromBytes(frame.getData()!!)
                    }.post()
                }
            }
        }
    }
}