package com.jiace.apm.until

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.jiace.apm.common.dialog.ChoiceDialog
import kotlin.concurrent.thread

/**
 * @author: yw
 * @date: 2021/8/22
 * @description: 多选一对话框（只能运行在非UI线程，否则会造成UI阻塞）
 */
class UserChoice(
    private val mContext:Context,
    private val mTitle:String,
    private val mMessage:String,
    private  val mChoice0:String?,
    private  val mChoice1:String?,
    private val mChoice2:String?,
    private val mChoice3:String?) {

    private val mLocker = java.lang.Object()
    private var mResult = -1

    private val mHandler = object:Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
                ChoiceDialog(mContext, mTitle, mMessage).apply {
                    mChoice0?.let {
                        setChoice(0, it) { dialog ->
                            mResult = 0
                            dialog.dismiss()
                        }
                    }
                    mChoice1?.let {
                        setChoice(1, it) { dialog ->
                            mResult = 1
                            dialog.dismiss()
                        }
                    }

                    mChoice2?.let {
                        setChoice(2, it) { dialog ->
                            mResult = 2
                            dialog.dismiss()
                        }
                    }

                    mChoice3?.let {
                        setChoice(3, it) { dialog ->
                            mResult = 3
                            dialog.dismiss()
                        }
                    }

                    setOnDismissListener {
                        synchronized(mLocker) {
                            mLocker.notifyAll()
                        }
                    }
                }.show()
            }
    }



    constructor(
        mContext:Context,
        mTitle:String,
        mMessage:String,
        mChoice0:String?,
        mChoice1:String?,
        mChoice2:String?):this(mContext, mTitle, mMessage, mChoice0, mChoice1, mChoice2, null) {

        }

    constructor(
        mContext:Context,
        mTitle:String,
        mMessage:String,
        mChoice0:String?,
        mChoice1:String?
    ):this(mContext, mTitle, mMessage, mChoice0, mChoice1, null) {

    }

    constructor(
        mContext:Context,
        mTitle:String,
        mMessage:String,
        mChoice0:String?
    ):this(mContext, mTitle, mMessage, mChoice0, null) {

    }


    fun getResult():Int {
        thread {
            mHandler.sendMessage(mHandler.obtainMessage())
            synchronized(mLocker) {
                mLocker.wait()
            }
        }.join()
        return mResult
    }
}