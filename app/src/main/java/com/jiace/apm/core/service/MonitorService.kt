package com.jiace.apm.core.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import com.jiace.apm.MyActivityManager
import com.jiace.apm.common.dialog.SureAlertDialog
import com.jiace.apm.core.AlarmInfo
import com.jiace.apm.core.ConfigureHelper
import com.jiace.apm.core.HostTime
import com.jiace.apm.core.ParamHelper
import com.jiace.apm.core.dataStruct.*
import com.jiace.apm.core.dbf.TBBaseValueHelper
import com.jiace.apm.core.dbf.TBBasicInfoHelper
import com.jiace.apm.core.dbf.TBDetailsDataHelper
import com.jiace.apm.core.dbf.TBVirtualDeviceHelper
import com.jiace.apm.core.event.OnReceiveEndMainData
import com.jiace.apm.core.event.OnRecordChange
import com.jiace.apm.core.event.TestStatusChanged
import com.jiace.apm.until.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/11.
3 * Description: 监测服务
4 *
5 */
class MonitorService: Service() {

    /** 基本信息ID */
    private var mBasicInfoId = 0L

    /** 所有用到的传感器的基本信息 */
    private val mDeviceTableList = ArrayList<DeviceGather>()

    /** 传感器是否无误 */
    private var mIsAllSensorValid = false

    /** 判断同步锁 */
    private val mJudgeMutex = Object()

    /** 是否正在进行计算基准位移 */
    private var mIsCalcBaseValue = false

    /** 暂停监测 */
    private var mIsSuspendMonitor = false

    /** DoWork是否工作完毕 */
    private var mbIsWorkOver = true

    /** 恢复试验的时间(ms),恢复试验后,不能马上读数,可能会造成数据不准,等待一段时间 */
    private var mContinueTestTime = 0L

    /** 位移初始化完成的次数 */
    private var mDisplacementInitSuccessCount = 0L

    /** 当前需要显示的位移及压力 */
    private var mCurrentRecord = Record()

    /** doc */
    private var mDoc: Doc? = null

    /** 实时数据 */
    val mRealTimeData = RealTimeData()

    /** 工作线程 */
    private var mWorkingThread: WorkThread? = null

    /** 当前试验状态 */
    private var mMonitorState = MonitorStatus.State.IDLE


    private lateinit var mBinder: MonitorServiceBinder

    override fun onCreate() {
        super.onCreate()
        register(this)
        mBinder = MonitorServiceBinder(this)
        ServiceHelper.mVirtualDeviceService = this

        // 创建报警提示信息
        ParamHelper.mAlarmInfo = AlarmInfo(this)
    }


    inner class MonitorServiceBinder constructor(service: MonitorService): Binder() {

        private val mService: WeakReference<MonitorService> = WeakReference(service)

        fun getService() = mService.get()
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegister(this)
    }

    /** 获取上次采样时间 */
    fun getLastRecordTime() = mCurrentRecord.SampleTime.clone() as Date

    /**
     * 是否需要计算基准位移
     * @param calc Boolean
     */
    fun calcBaseValue(calc: Boolean) {
        synchronized(mJudgeMutex) {
            mIsCalcBaseValue = calc
        }
    }

    /**
     * 重新计算基准位移
     */
    fun recalcBeginValue() {
        mRealTimeData.mIsBeginValueValid = false
    }

    /** 是否处于监测中 */
    fun isMonitoring(): Boolean {
        return (mBasicInfoId > 0 && mDoc!=null && !mIsSuspendMonitor)
    }

    /** 获取当前监测状态 */
    fun getCurrentMonitorState(): MonitorStatus.State {
        return mMonitorState
    }

    /**
     * 计算基准值
     *
     * */
    private fun calcBeginValue():Boolean {
        var ret = true
        var save = false

        // 开始试验时,才需要计算
        if(isMonitoring()) {

            // 获取最后一次的数据
            val lastRecord = mDoc!!.getLastRecord()

            // 计算实际位移
            if(!mRealTimeData.mIsBeginValueValid) {
                // 绝对位移是否有误
                if(!ErrorHelper.isErrorValue(mRealTimeData.mSChannel)) {
                    // 根据上次记录值计算初始位移
                    mRealTimeData.mDisplacementBeginValue = mRealTimeData.mSChannel +lastRecord.Footage* (-1)
                    mRealTimeData.mIsBeginValueValid = true
                } else {
                    ret = false
                }
                // 需要保存位移基准值
                save = true
            }
        }

        // 需要保存位移基准点且位移基准点已计算完毕时
        if(save && ret) {
            // 将基准位移保存到数据库中
            // 判断其位移是否有误
            var error = false
            val baseValue = mRealTimeData.mDisplacementBeginValue
            if(ErrorHelper.isErrorValue(baseValue)) {
                error = true
            }
            // 没有错误时,才保存到数据库中
            if(!error) {
                TBBaseValueHelper.updateBaseValue(mBasicInfoId, baseValue)
            }
        }
        return ret
    }


    /** 开始新的监测 */
    fun startNewMonitor() {
        mIsSuspendMonitor = false
        mMonitorState = MonitorStatus.State.Monitoring
        // 设置上一次的测试状态为已完成
        TBBasicInfoHelper.updateTestingMark(ParamHelper.mLastBasicInfoId, false)

        // 清除虚拟机的测试状态
        TBVirtualDeviceHelper.updateParam(TBVirtualDeviceHelper.IsMonitor, 1)

        // 需要计算基准位移
        calcBaseValue(true)

        recalcBeginValue()

        createDoc()

        // 试验状态发生了变化
        TestStatusChanged().post()

        val tempBasicInfoId = mDoc!!.mBasicInfo.mBasicInfoId

        // 初始值
        val initRecord = mCurrentRecord
        initRecord.let {
            it.BasicInfoId = tempBasicInfoId
            it.RecordType = Record.Normal
            it.SampleTime = HostTime.getHostTime()
            it.CreateTime = HostTime.getHostTime()
            // 位移全部清零
            it.Truns = 0
            it.Depth = 0
            it.Footage = 0
            it.AngleOfDip = 0
            it.RecordCount = 1
        }
        mCurrentRecord = initRecord.clone()
        // 保存数据
        recordData(initRecord)

    }

    /** 结束监测 */
    fun stopMonitor() {
        endMonitor(true)
        mMonitorState = MonitorStatus.State.IDLE


    }

    /** 暂停监测 */
    fun suspendMonitor() {
        mIsSuspendMonitor = true
        mMonitorState = MonitorStatus.State.Suspend
    }

    /** 恢复监测 */
    fun resumeMonitor() {
        mIsSuspendMonitor = false
        mMonitorState = MonitorStatus.State.Monitoring
    }

    /**
     *  创建数据
     * */
    private fun createDoc() {
        val info = BasicInfo().apply {
            mIsMonitor = 1
            mMachineId = ConfigureHelper.MachineId
            mProjectName = ParamHelper.mProjectParam.ProjectName
            mBuildPosition = ParamHelper.mProjectParam.BuildPosition
            mProjectParam = ParamHelper.mProjectParam.clone()
            mSampleMachineId = ParamHelper.mSensorParam.SampleMachineId
            mMonitorParam = ParamHelper.mMonitorParam.clone()
            mSensorParam = ParamHelper.mSensorParam.clone()
            mBuildParam = ParamHelper.mBuildParam.clone()
            mSerialNo = ParamHelper.mProjectParam.SerialNo
            mPileNo = ParamHelper.mProjectParam.PileNo
            updateSourceParam()
        }
        mDoc = Doc(info)

        mBasicInfoId = info.mBasicInfoId
    }

    /** 获取倒计时(S) */
    fun getCountdown():Int {
        return when (ParamHelper.mMonitorParam.MonitorType) {
            MonitorParam.MonitorType_Time -> {
                mRealTimeData.mMonitorStatus.leftTime
            }

            MonitorParam.MonitorType_Depth -> {
                mRealTimeData.mMonitorStatus.leftDistance
            }
            else -> 0
        }
    }

    /** 获取DOC */
    fun getDoc() = mDoc

    private fun releaseDoc() {
        mDoc?.close()
        mDoc = null
    }

    /** 开始监测 */
    fun start() {
        //mWorkingThread = WorkThread()
        // mWorkThread?.start()
        mJob = applicationScope.launch {
            while (true) {
                try {
                    doWork()
                    delay(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {

                }
            }
        }

        mJob?.start()
    }

    /** 结束监测 */
    fun stop() {
        mWorkThread?.cancel()

        mJob?.cancel()
    }

    // <editor-fold desc="工作线程">
    private var mWorkThread: WorkThread? = null

    private var mJob: Job? = null

    private inner class WorkThread: Thread("MonitorServiceThread") {

        private val mTimerGap = 500L

        override fun run() {
            while (!isInterrupted) {
                try {
                    doWork()
                    sleep(mTimerGap)
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
    }

    /** 监测流程 */
    fun doWork() {

        // 判断与数据采集仪是否掉线

        calcBeginValue()

        // 查询传感器状态是否正确
        mIsAllSensorValid = checkDeviceStatus()
        if (!mIsAllSensorValid) {
            return
        }

        // 如果监测未开始直接返回
        if (!isMonitoring()) {
            return
        }
        // 上次数据处理是否已完毕，未完成直接返回，防止同一前端机的报警信息提示框不断出现
        if (!mbIsWorkOver) {
            return
        }
        // 监测是否暂停
        if (mIsSuspendMonitor) {
            return
        }

        // 开始处理数据
        mbIsWorkOver = true

        if (!isDisplacementInit() || (SystemClock.elapsedRealtime() - mContinueTestTime < 5000L)) {
            mDisplacementInitSuccessCount = 0
            mbIsWorkOver = false
            return
        } else {
            mDisplacementInitSuccessCount++
        }

        if (mDisplacementInitSuccessCount < 5) {
            mbIsWorkOver = true
            return
        }

        if (isMonitoring()) {
            // 记录数据
            // 传感器没有异常时
            if(isNeedSample() && checkDeviceStatus()) {
                sample()
            }
        }
        mbIsWorkOver = true
    }

    /** 监测传感器状态 */
    private fun checkDeviceStatus():Boolean {
        var status = 0L
        var isContinue = true

        // 扭矩传感器
        if (ErrorHelper.isErrorValue(mRealTimeData.mRealVoltage)) {
            isContinue = false
            status = AlarmInfo.STATUS_FRE_ERROR
            ParamHelper.mAlarmInfo?.addAlarm(status, "扭矩传感器出错!")
        } else {
            ParamHelper.mAlarmInfo?.clearAlarm(status)
            if (ErrorHelper.isErrorValue(mRealTimeData.mTorsionValue)) {
                status = AlarmInfo.STATUS_FRE_AD_ERROR
                ParamHelper.mAlarmInfo?.addAlarm(status, "扭矩传感器计算错误!")
                isContinue = false
            } else {
                ParamHelper.mAlarmInfo?.clearAlarm(status)
            }
        }

        // 激光测距
        if (ErrorHelper.isErrorValue(mRealTimeData.mSChannel)) {
            isContinue = false
            status = AlarmInfo.STATUS_DIS_ERROR
            ParamHelper.mAlarmInfo?.addAlarm(status, "激光传感器未连接!")
        } else {
            ParamHelper.mAlarmInfo?.clearAlarm(status)

            if (mRealTimeData.mSChannel in 4001 downTo 10) {
                status = AlarmInfo.STATUS_DIS_EMPTY
                ParamHelper.mAlarmInfo?.addAlarm(status, "激光传感器不在合理范围内!")
            } else {
                ParamHelper.mAlarmInfo?.clearAlarm(status)
            }
        }

        // 倾角
        if (ErrorHelper.isErrorValue(mRealTimeData.mAngleOfDip)) {
            isContinue = false
            status = AlarmInfo.STATUS_DIS_ERROR
            ParamHelper.mAlarmInfo?.addAlarm(status, "倾角传感器未连接!")
        } else {
            ParamHelper.mAlarmInfo?.clearAlarm(status)
        }


        if (isMonitoring() && isContinue) {

            // 判断扭矩
            if (mRealTimeData.mTorsionValue > ParamHelper.mMonitorParam.TorsionMax || mRealTimeData.mTorsionValue < ParamHelper.mMonitorParam.TorsionMin) {
                status = AlarmInfo.STATUS_FRE_SUPPLY
                ParamHelper.mAlarmInfo?.addAlarm(status, "安装扭矩超出限值!")
                // isContinue = false
            } else {
                status = AlarmInfo.STATUS_FRE_SUPPLY
                ParamHelper.mAlarmInfo?.clearAlarm(status)
            }

            // 判断进尺
            if (mRealTimeData.mAngleOfDip > ParamHelper.mMonitorParam.AngleOfDipMax || mRealTimeData.mAngleOfDip < -ParamHelper.mMonitorParam.AngleOfDipMax) {
                status = AlarmInfo.STATUS_FRE_OPPOSITE
                ParamHelper.mAlarmInfo?.addAlarm(status, "倾角超出限值!")
                //isContinue = false
            } else {
                status = AlarmInfo.STATUS_FRE_OPPOSITE
                ParamHelper.mAlarmInfo?.clearAlarm(status)
            }

            // 判断倾角
            if (mRealTimeData.mFootage > ParamHelper.mMonitorParam.FootageMax || mRealTimeData.mFootage < ParamHelper.mMonitorParam.FootageMin) {
                status = AlarmInfo.STATUS_FRE_UNREQUEST
                ParamHelper.mAlarmInfo?.addAlarm(status, "进尺超出限值!")
                //isContinue = false
            } else {
                status = AlarmInfo.STATUS_FRE_UNREQUEST
                ParamHelper.mAlarmInfo?.clearAlarm(status)
            }
        }


        return isContinue
    }

    /** 检查激光测距是否初始化完成 */
    private fun isDisplacementInit(): Boolean {
        var ret = true
        if (isMonitoring()) {
            if (!mRealTimeData.mIsBeginValueValid) {
                ret = false
            }
        }
        return ret
    }

    /**
     * 是否需要采样
     * @return Boolean
     */
    private fun isNeedSample():Boolean {
        if(isMonitoring()) {
            val lastRecord = mDoc?.getLastRecord()
            lastRecord ?: return false

            when (ParamHelper.mMonitorParam.MonitorType) {
                MonitorParam.MonitorType_Depth -> {
                    // 理论进尺间隔
                    val recordInterval = ParamHelper.mMonitorParam.RecordInterval
                    if (mCurrentRecord.Depth - lastRecord.Depth >=  recordInterval) {
                        return true
                    }
                }

                MonitorParam.MonitorType_Time -> {
                    // 理论时间间隔
                    val time  = ParamHelper.mMonitorParam.RecordInterval
                    var realSeconds = (HostTime.getHostTime().time - getLastRecordTime().time) / 1000
                    if(realSeconds < 0) {
                        realSeconds = 0
                    }
                    // 实际间隔时间大于等于采样时间时,返回true
                    if(realSeconds >= time) {
                        return true
                    }
                }
            }
        }
        return  false
    }

    /**
     * 记录数据
     * @param isAuto Boolean 是否为自动采集的数据
     * @param isEndTest Boolean 是否为结束试验时的记录数据
     */
    fun sample(isAuto: Boolean = true, isEndMonitor: Boolean = false) {
        if (isMonitoring()) {
            // 采样时间,采样次数,测试时间,数据类型
            mCurrentRecord.SampleTime = HostTime.getHostTime()
            mCurrentRecord.CreateTime = HostTime.getHostTime()
            mCurrentRecord.RecordCount++
            mCurrentRecord.RecordType = if (isAuto) Record.Normal else Record.SampleEarly

            // 保存数据
            recordData(mCurrentRecord)
        }
    }

    /**
     * 保存数据
     *
     * */
    private fun recordData(record: Record) {
        val newRecord = mDoc!!.addOneData(record)
        newRecord.GUID = UUID.randomUUID().toString()
        // 数据改变
        OnRecordChange().post()
        TBDetailsDataHelper.insertDetailsData(newRecord)
        ParamHelper.mAlarmInfo?.playRecordData()
    }

    /**
     * 是否要以更新位移
     * @return Boolean
     */
    private fun isCanUpdateDisplacement():Boolean {
        var ret = true
        if(isMonitoring()) {
            synchronized(mJudgeMutex) {
                ret = true
            }
        }
        return  ret
    }

    /** 结束监控 */
    private fun endMonitor(isAuto: Boolean) {
        if (isMonitoring()) {
            // 修改数据库中的信息
            TBVirtualDeviceHelper.updateParam(TBVirtualDeviceHelper.IsMonitor, 0)

            // 修改试验标志
            TBBasicInfoHelper.updateTestingMark(mBasicInfoId, false)

            // 释放资源
            mBasicInfoId = 0
            releaseDoc()
            // 清除所有的报警信息
            ParamHelper.mAlarmInfo?.playTestingOver()
            ParamHelper.mAlarmInfo?.clearAllAlarm()
            // 试验结束后,恢复为默认值
            mIsSuspendMonitor = false

            // 试验状态发生了变化
            TestStatusChanged().post()
        }
    }

    /** 更新下次记录数据的时间或者间隔 */
    private fun updateLeftTimeAndDistance() {
        val left: Int
        if (isMonitoring()) {
            when (ParamHelper.mMonitorParam.MonitorType) {

                MonitorParam.MonitorType_Depth -> {
                    left = ParamHelper.mMonitorParam.RecordInterval.toIntValue() - (mRealTimeData.mMonitorStatus.mDisplacementValue - mDoc!!.getLastRecord().Depth)
                    mRealTimeData.mMonitorStatus.leftDistance = left
                }

                MonitorParam.MonitorType_Time -> {
                    var realSeconds = (HostTime.getHostTime().time - getLastRecordTime().time) / 1000
                    d(TAG,"HostTime.getHostTime() = ${HostTime.getHostTime()}   LastRecordTime() = ${getLastRecordTime()}")
                    if (realSeconds < 0)  {
                        realSeconds = 0
                    }
                    d(TAG,"realSeconds = $realSeconds")
                    left = ParamHelper.mMonitorParam.RecordInterval.toIntValue()- realSeconds.toInt()
                    d(TAG,"leftTime = $left")
                    mRealTimeData.mMonitorStatus.leftTime = left
                }
            }
        }
    }

    /** 更新当前数据 */
    private fun updateCurrentData() {
        mCurrentRecord.Depth = mRealTimeData.mSChannel
        mCurrentRecord.Footage = mRealTimeData.mFootage
        mCurrentRecord.Truns = mRealTimeData.mTurns
        mCurrentRecord.AngleOfDip = mRealTimeData.mAngleOfDip
        mCurrentRecord.TorsionSensorVoltage = mRealTimeData.mRealVoltage
        mCurrentRecord.Torsion = mRealTimeData.mTorsionValue
        /**
         * 更新UI
         * @see com.jiace.apm.ui.main.MainViewModel.onUpdateRecord
         * */
        mCurrentRecord.post()
    }

    /**
     * 判断数据是否正确
     * 开始试验时进行调用,如果有问题,不能开始试验
     * @return ArrayList<String> 有问题的信息
     */
    fun checkParam(): ArrayList<String> {
        val errorMessages = ArrayList<String>()

        ParamHelper.mProjectParam.let {

            if (it.ProjectName.isEmpty()) {
                errorMessages.add("未输入工程名称")
            }

            if (it.PileNo.isEmpty()) {
                errorMessages.add("未输入编号")
            }

            if (it.SerialNo.isEmpty()) {
                errorMessages.add("未输入流水号")
            }

            if (it.BaseAnchorNo.isEmpty()) {
                errorMessages.add("未输入基锚号")
            }

            if (it.BaseNo.isEmpty()) {
                errorMessages.add("未输入基础编号")
            }

            if (it.BuildPosition.isEmpty()) {
                errorMessages.add("未输入施工部位")
            }

            if (it.TallNo.isEmpty()) {
                errorMessages.add("未输入塔位编号")
            }
        }


        //
        ParamHelper.mBuildParam.let {

            if (it.MachineNo.isEmpty()) {
                errorMessages.add("未输入机器编号")
            }

            if (it.MachineType.isEmpty()) {
                errorMessages.add("未输入机器类型")
            }

            if (it.AnchorPlateNo.isEmpty()) {
                errorMessages.add("未输入锚盘编号")
            }

            if (it.AnchorDiameter <= 0) {
                errorMessages.add("输入的锚杆直径不正确")
            }

            if (it.AnchorPlateCount <= 0) {
                errorMessages.add("输入的锚杆数量不正确")
            }
        }

        ParamHelper.mMonitorParam.let {

            if (it.TorsionMax <= 0) {
                errorMessages.add("输入的扭矩上限值不正确")
            }

            if (it.TorsionMin <= 0) {
                errorMessages.add("输入的扭矩下限值不正确")
            }

            if (it.FootageMax <= 0) {
                errorMessages.add("输入的进尺上限值不正确")
            }

            if (it.FootageMin <= 0) {
                errorMessages.add("输入的进尺下限值不正确")
            }

            if (it.AngleOfDipMax <= 0) {
                errorMessages.add("输入的最大允许倾角不正确")
            }
        }

        ParamHelper.mSensorParam.let {
            if (it.SampleMachineId.isEmpty()) {
                errorMessages.add("未配置采集仪主机编号")
            }

            if (it.AngleOfDipSensorId.isEmpty()) {
                errorMessages.add("未配置倾角传感器编号")
            }

            if (it.TorsionSensorId.isEmpty()) {
                errorMessages.add("未配置扭矩传感器编号")
            }

            if (it.TorsionBluetoothNo.isEmpty()) {
                errorMessages.add("未配置扭矩传感器蓝牙编号")
            }

            if (it.DisplacementSensorId.isEmpty()) {
                errorMessages.add("未配置激光传感器编号")
            }

            if (!it.isLoadSensorValid()) {
                errorMessages.add("扭矩传感器参照异常")
            }

            if (mRealTimeData.mSChannel < 4 || mRealTimeData.mSChannel > 1000) {
                errorMessages.add("激光传感器未在有效的测距范围内")
            }
        }
        return errorMessages
    }
    // </editor-fold>

    /** 接收到采集仪数据 */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onReceiveSensorStatus(onReceiveEndMainData: OnReceiveEndMainData) {
        mRealTimeData.mMonitorStatus.apply {
            convertFromSampleMachine(onReceiveEndMainData.sensorData)
        }
        mRealTimeData.calcValue(isMonitoring(),isCanUpdateDisplacement())
        updateCurrentData()
        updateLeftTimeAndDistance()
    }

    companion object {

        private const val TAG = "MonitorService"

    }


}