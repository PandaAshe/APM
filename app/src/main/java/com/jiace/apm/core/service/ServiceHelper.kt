package com.jiace.apm.core.service

object ServiceHelper {

    /** 测试进程 */
    var mVirtualDeviceService:MonitorService? = null

    /** 通讯服务 */
    var mMainDeviceService:MainDeviceService? = null

    /** 是否是处于试验中 */
    fun isTesting() = mVirtualDeviceService != null && mVirtualDeviceService!!.isMonitoring()

    /** 前端机是否连接 */
    fun isEndConnection(): Boolean {
       return mMainDeviceService?.isConnected() == true
    }
}