package com.jiace.apm.core

import android.os.SystemClock
import com.jiace.apm.until.applicationScope
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @author: yw
 * @date: 2021-05-31
 * @description: 主机时间，和系统时间可能会不同
 */
object HostTime {

    /** 和系统时间相差的毫秒数 */
    private var mGapMilSeconds = 0L

    /** 主机启动时的时刻(ms) */
    private var mHostStardTicker= 0L

    /** 计数器(s) */
    private var mTimeCount = 0L

    /** 定时任务 */
    private var mTimerJob: Job? = null

    /** 主机启动时的时间 */
    private var mHostTime = Date()

    /** 是否获取到正确的网络时间 */
    private var mIsGetInterNetTime = false

    /**
     * 获取主机的运行时间(ms)
     * @return Long
     */
    fun getTickerCount() = run{
        SystemClock.elapsedRealtime() - mHostStardTicker
    }

    private val mLock = ReentrantLock()

    /** 北京时间 */
    private var mBeijingTime = Date()

    /** 获取到北京时间的时间节点(ms) */
    private var mGetBeijingTimeTicket = 0L

    /** 获取北京时间任务 */
    private var mBeijingTimerJob: Job? = null

    /**
     * 初始化
     */
    fun init() {
        mGapMilSeconds = ConfigureHelper.DetalTime
        mHostStardTicker = SystemClock.elapsedRealtime()

        // 使用协程启动定时计数
        mTimerJob = applicationScope.launch {
            while (true) {
                try {
                    delay(1000)
                    mLock.withLock {
                        mTimeCount++
                    }
                }catch (e:Exception) {
                    e.printStackTrace()
                    return@launch
                }
            }
        }

        val now = Calendar.getInstance()
        now.add(Calendar.MILLISECOND, mGapMilSeconds.toInt())
        mHostTime = now.time


        mBeijingTimerJob = applicationScope.launch(Dispatchers.IO) {
            var exit = false
            try {
                while (!exit) {
                    if (!mIsGetInterNetTime) {
                        getBeiJingTime()
                    } else {
                        exit = true
                        return@launch
                    }
                    delay(5000)
                }
            }catch (e:java.lang.Exception) {
                e.printStackTrace()
                return@launch
            }
        }

    }

    /**
     * 终止定时器
     */
    fun close() {
        mTimerJob?.cancel()
        mTimerJob = null

        mBeijingTimerJob?.cancel()
        mBeijingTimerJob = null
    }

    /**
     * 设置主机时间,实际保存的是与系统时间的差值
     * 开始试验后,一般不允许再设置主机的时间
     * @param date Date
     */
    fun setHostTime(date: Date) {
        mLock.withLock {
            // 计算时间差
            val gap = date.time -getHostTime().time

            // 与之前的时间差进行比较,判断是否需要更新
            if(gap != 0L) {
                mGapMilSeconds += gap
                ConfigureHelper.DetalTime = mGapMilSeconds
                mHostTime.time += gap
            }
        }
    }

    /**
     * 获取主机当前时间(主机启动时的时间+主机运行的时间)
     * @return Date
     */
    fun getHostTime():Date {
        val now = Date()
        //mLock.withLock {
        //    now.time = mHostTime.time + mTimeCount * 1000
        //}
        return now
    }

    /**
     * 获取正确的北京时间
     * @return Date
     */
    fun getBeiJingTime():Date {
        return Date()

        /*synchronized(mBeijingTime) {
            if(!mIsGetInterNetTime) {
                try {
                    val t = RsWebService.getTime()
                    t?.let {
                        mBeijingTime = it
                        mGetBeijingTimeTicket = System.currentTimeMillis()
                        mIsGetInterNetTime = true
                    }
                }catch (e:java.lang.Exception) {
                    mIsGetInterNetTime = false
                }
            }

            // 使用NTP时间
            if(!mIsGetInterNetTime) {
                try {
                    val ntp = "time.nist.gov"
                    val port = 13
                    val socket = Socket(ntp, port)
                    socket.use {
                        if(it.isConnected) {
                            var result = HttpUtils.changeInputStream(it.getInputStream())
                            if (result.isNotEmpty()) {
                                result = result.trim()
                                val list = result.split(" ")
                                result = "20" + list.get(1) + " " +list.get(2)
                                val t = Utils.getDateTimeFormat().parse(result)
                                mBeijingTime = Date(t.time + 8*60*60*1000)
                                mGetBeijingTimeTicket = System.currentTimeMillis()
                                mIsGetInterNetTime = true
                            }
                        }
                    }
                }catch (e:java.lang.Exception) {
                    mIsGetInterNetTime = false
                }
            }

            if(mIsGetInterNetTime) {
                val t = mBeijingTime.time + (System.currentTimeMillis() - mGetBeijingTimeTicket)
                return Date(t)
            } else {
                return Date()
            }
        }*/
    }

    /** 是否获取到正确的北京时间 */
    fun isGetInterNetTime():Boolean {
        if(!mIsGetInterNetTime) {
            getBeiJingTime()
        }
        return mIsGetInterNetTime
    }
}