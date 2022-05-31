package com.jiace.apm.core

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.jiace.apm.R
import com.jiace.apm.until.post
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @author: yw
 * @date: 2021/6/25
 * @description: 报警提示信息类
 */
class AlarmInfo(context: Context) {

    companion object {
        // <editor-fold> StatusCode
        const val STATUS_OK = 0L                //	正常
        const val STATUS_UNCONECTED = 1L.shl(1)        //	未连上前端机
        const val STATUS_FRE_ERROR = 1L.shl(2)        //	压力出错
        const val STATUS_FRE_SHORT = 1L.shl(3)        //	压力短路
        const val STATUS_FRE_EMPTY = 1L.shl(4)        //	压力未接
        const val STATUS_FRE_UNREQUEST = 1L.shl(5)        //	压力加不上/卸不下
        const val STATUS_FRE_OPPOSITE = 1L.shl(6)        //	加压反向
        const val STATUS_FRE_SUPPLY = 1L.shl(7)        //	提示补压
        const val STATUS_DIS_ERROR = 1L.shl(8)        //	位移出错
        const val STATUS_DIS_SHORT = 1L.shl(9)        //	位移短路
        const val STATUS_DIS_EMPTY = 1L.shl(10)        //	位移未接
        const val STATUS_DIS_FULL = 1L.shl(11)        //	位移满程
        const val STATUS_DIS_VACANT = 1L.shl(12)        //	位移悬空
        const val STATUS_DIS_UNEVEN_DOWN = 1L.shl(13)        //	不均匀沉降
        const val STATUS_DIS_MAX_DOWN = 1L.shl(14)        //	最大沉降
        const val STATUS_DIS_MAX_UP = 1L.shl(15)        //	最大上拔
        const val STATUS_STEADY_READY = 1L.shl(16)        //	本级已到测试时间/沉降已稳定
        const val STATUS_MOVE_TABLE = 1L.shl(17)        //	正在移表
        const val STATUS_MODIFY_DATA = 1L.shl(18)        //	正在修改数据
        const val STATUS_OPEN_PUMP = 1L.shl(19)        //	正在开油泵
        const val STATUS_MODIFY_SENSOR = 1L.shl(20)        //	更改传感器
        const val STATUS_OTHER_ALARM = 1L.shl(21)        //	其它报警提示
        const val STATUS_DIS_FAILURE = 1L.shl(22)        //	位移沉降超过失效位移
        const val STATUS_FRE_AD_ERROR = 1L.shl(23)             //      AD模块损坏
        const val STATUS_BOX_UNCONECTED = 1L.shl(24)             //      位移盒未接
        const val STATUS_CALC_BASE_VALUE = 1L.shl(25)                     //  计算基准位移
        const val STATUS_MAX_LOADING = 1L.shl(26)                             //  已达最大荷载
        const val STATUS_BLE_ERROR = 1L.shl(27)                         // 蓝牙接收模块故障
        const val STATUS_SENSOR_MISMATCH = 1L.shl(28)         // 传感器类型不匹配
        const val STATUS_FRE_BOX_UNCONECTED = 1L.shl(29)      // 荷重盒未接入
        const val STATUS_ALARM_TIP = 1L.shl(31)        //	要求用户确认时的报警提示
        const val STATUS_AP_DISCONNECT = 1L.shl(32)        // 全自动切断
        const val STATUS_TESTING_OVER = 1L.shl(33)           // 试验已结束
        // </editor-fold>

        val Mp3Map = mutableMapOf(
            Pair(STATUS_UNCONECTED, R.raw.alarm_01),
            Pair(STATUS_FRE_ERROR, R.raw.alarm_02),
            Pair(STATUS_FRE_SHORT, R.raw.alarm_03),
            Pair(STATUS_FRE_EMPTY, R.raw.alarm_04),
            Pair(STATUS_FRE_UNREQUEST, R.raw.alarm_05),
            Pair(STATUS_FRE_OPPOSITE, R.raw.alarm_06),
            Pair(STATUS_FRE_SUPPLY, R.raw.alarm_07),
            Pair(STATUS_DIS_ERROR, R.raw.alarm_08),
            Pair(STATUS_DIS_SHORT, R.raw.alarm_09),
            Pair(STATUS_DIS_EMPTY, R.raw.alarm_10),
            Pair(STATUS_DIS_FULL, R.raw.alarm_11),
            Pair(STATUS_DIS_VACANT, R.raw.alarm_12),
            Pair(STATUS_DIS_UNEVEN_DOWN, R.raw.alarm_13),
            Pair(STATUS_DIS_MAX_DOWN, R.raw.alarm_14),
            Pair(STATUS_DIS_MAX_UP, R.raw.alarm_15),
            Pair(STATUS_STEADY_READY, R.raw.alarm_16),
            Pair(STATUS_MOVE_TABLE, R.raw.alarm_17),
            Pair(STATUS_MODIFY_DATA, R.raw.alarm_18),
            Pair(STATUS_OPEN_PUMP, R.raw.alarm_19),
            Pair(STATUS_MODIFY_SENSOR, R.raw.alarm_20),
            Pair(STATUS_DIS_FAILURE, R.raw.alarm_21),
            Pair(STATUS_FRE_AD_ERROR, R.raw.alarm_22),
            Pair(STATUS_BOX_UNCONECTED, R.raw.alarm_23),
            Pair(STATUS_CALC_BASE_VALUE, R.raw.alarm_24),
            Pair(STATUS_MAX_LOADING, R.raw.alarm_25),
            Pair(STATUS_BLE_ERROR, R.raw.alarm_26),
            Pair(STATUS_SENSOR_MISMATCH, R.raw.alarm_27),
            Pair(STATUS_FRE_BOX_UNCONECTED, R.raw.alarm_28),
            Pair(Long.MAX_VALUE - 1, R.raw.alarm_29),         // 记录数据时的声音
            Pair(Long.MAX_VALUE - 2, R.raw.alarm_30),          // 开始下一级的语音提示
            Pair(STATUS_AP_DISCONNECT, R.raw.ap_disconnect),
            Pair(STATUS_TESTING_OVER, R.raw.testing_over)
        )

        fun getMp3(status: Long) {

        }
    }

    private val mContext = context

    /** 音频流 */
    private val mSoundPool = SoundPool.Builder()
        .setMaxStreams(Mp3Map.size)
        .build()

    /** 音频流ID */
    private val mSoundId = HashMap<Long, Int>()

    /** 锁 */
    private val mAlarmLock = ReentrantLock()
    private val mTipLock = ReentrantLock()
    private val mStatusLock = ReentrantLock()

    /** 警报链表*/
    private val mAlarmList = ArrayList<AlarmItem>()

    /** 提示信息链表 */
    private val mTipList = ArrayList<String>()

    /** 主机工作状态 */
    private var mStatus = 0L

    /** 使用协程每隔5S抛出一个报警信息并播放声音,每隔100ms抛出一个提示信息 */
    private val mJob = Job()
    private val mScope = CoroutineScope(mJob)

    /** 初始化 */
    init {
        Mp3Map.forEach { t, u ->
            mSoundId[t] = mSoundPool.load(mContext, u, 1)
        }

        // 每隔100ms抛出一个提示信息
        mScope.launch {
            val delayTime = 500L
            while (true) {
                try {
                    getLastTip()?.post()
                } catch (e: CancellationException) {
                    return@launch
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(delayTime)
            }
        }

        // 每隔5S抛出一个报警信息并播放声音
        mScope.launch {
            val delayTime = 1000L
            while (true) {
                try {
                    getAlarm(1).let {
                        if (it.size > 0) {
                            val item = it.first()
                            item.post()
                            playAlarm(item.mAlarmMark)
                        }
                    }
                } catch (e: CancellationException) {
                    return@launch
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(delayTime)
            }
        }
    }


    /**
     * 释放资源
     */
    fun close() {
        ParamHelper.mAlarmInfo = null
        // 取消任务
        mJob.cancel()
        mSoundPool.release()
    }

    /**
     * 清除所有报警信息
     */
    fun clearAllAlarm() {
        mAlarmLock.withLock {
            mAlarmList.clear()
        }
    }

    /**
     * 删除指定标识号的报警信息
     * @param alarmMark Long
     */
    fun clearAlarm(alarmMark: Long) {
        mAlarmLock.withLock {
            var i = 0
            while (i < mAlarmList.size) {
                val one = mAlarmList.get(i)
                if (one.mAlarmMark == alarmMark) {
                    mAlarmList.removeAt(i)
                } else {
                    i++
                }
            }
        }
    }

    /**
     * 添加报警信息
     * @param item AlarmItem
     */
    fun addAlarm(item: AlarmItem) {
        mAlarmLock.withLock {
            var find = false
            for (i in 0 until mAlarmList.size) {
                val one = mAlarmList.get(i)
                if (one.mAlarmMark == item.mAlarmMark) {
                    mAlarmList.set(i, item)
                    find = true
                }
            }
            if (!find) {
                mAlarmList.add(item)
            }
        }
    }

    /**
     * 取出最早的报警信息
     * @param maxCount Int
     * @return ArrayList<AlarmItem>
     */
    fun getAlarm(maxCount: Int): ArrayList<AlarmItem> {
        val list = ArrayList<AlarmItem>()
        mAlarmLock.withLock {
            var find = false
            var count = 0
            var index = 0
            while (count < maxCount && index < mAlarmList.size) {
                val one = mAlarmList.get(index)
                list.add(one)
                count++

                // 非自动删除时
                if (one.mAutoRemove != true) {
                    index++
                } else {
                    mAlarmList.removeAt(index)
                }
            }
        }
        return list
    }

    /**
     * 添加报警信息
     * @param alarmMark Long
     * @param alarm String
     * @param autoRemove Boolean
     */
    fun addAlarm(alarmMark: Long, alarm: String, autoRemove: Boolean = true) {
        AlarmItem().apply {
            mAlarmMark = alarmMark
            mAlarm = alarm
            mAutoRemove = autoRemove
        }.let {
            addAlarm(it)
        }
    }


    /**
     * 添加提示信息
     * @param tip String
     */
    fun addTip(tip: String) {
        mTipLock.withLock {
            mTipList.add(tip)
        }
    }

    /**
     * 获取最后一条提示信息
     * @return String?
     */
    fun getLastTip(): String? {
        var tip: String? = null
        mTipLock.withLock {
            try {
                tip = mTipList.last()
            } catch (e: NoSuchElementException) {
            }
            mTipList.clear()
        }
        return tip
    }


    /**
     * 设置运行状态
     * @param status Long
     */
    fun setStatus(status: Long) {
        mStatusLock.withLock {
            mStatus = status
        }
    }

    /**
     * 获取运行状态
     * @return Long
     */
    fun getStatus(): Long {
        mStatusLock.withLock {
            return mStatus
        }
    }


    /**
     * 播放报警声
     * @param status Long
     */
    fun playAlarm(status: Long) {
        mSoundId.get(status)?.let {
            try {
                mSoundPool.play(it, 1F, 1F, 0, 0, 1F)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /***
     * 记录数据时的"滴"声
     */
    fun playRecordData() {
        mSoundId.get(Long.MAX_VALUE - 1)?.let {
            try {
                mSoundPool.play(it, 1F, 1F, 0, 0, 1F)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 播放试验已结束提示音
     *
     * */
    fun playTestingOver() {
        mSoundId.get(STATUS_TESTING_OVER)?.let {
            try {
                mSoundPool.play(it, 1F, 1F, 0, 0, 1F)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** 获取所有的报警信息 */
    fun getAllAlarm() = ArrayList<AlarmItem>().apply {
        mAlarmLock.withLock {
            mAlarmList.forEach { one ->
                this.add(one.clone())
            }
        }
    }


    /**
     * 单个报警信息
     * @property mAlarmMark Long
     * @property mAlarm String
     * @property mAutoRemove Boolean
     */
    class AlarmItem {
        /** 类型标识 */
        var mAlarmMark = 0L

        /** 报警信息 */
        var mAlarm = ""

        /** 是否自动删除 */
        var mAutoRemove = true

        fun clone(): AlarmItem {
            val other = AlarmItem()
            other.mAlarmMark = this.mAlarmMark
            other.mAlarm = this.mAlarm
            other.mAutoRemove = this.mAutoRemove
            return other
        }
    }

}


