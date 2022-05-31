package com.jiace.apm.core.operation

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.jiace.apm.core.service.ServiceHelper
import kotlin.concurrent.thread


/**
 * @author: yw
 * @date: 2021-08-20
 * @description: 人工操作
 */
abstract class DoSomeOperation(protected val mContext:Context) {
     protected val mLocker = java.lang.Object()
     protected val mHandler = object : Handler(Looper.getMainLooper()) {
          override fun handleMessage(msg: Message) {
               this@DoSomeOperation.handleMessage(msg)
          }
     }

     protected var mThread:Thread? = null

     /**
      * 启动操作
      */
     fun doWork() {
          mThread = thread {
               someOperation()
          }
     }

     /**
      * 具体操作
      */
     abstract fun someOperation()

     /**
      * 消息处理
      */
     abstract fun handleMessage(msg:Message?)

     /**
      * 操作是否完成
      * @return Boolean
      */
     fun isOperationOver(): Boolean {
          return (mThread?.isAlive != true)
     }

     protected fun getVirtualDeviceService() = ServiceHelper.mVirtualDeviceService
}