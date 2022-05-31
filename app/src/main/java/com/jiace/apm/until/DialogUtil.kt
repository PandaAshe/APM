package com.jiace.apm.until

import android.app.Dialog
import android.content.Context
import com.jiace.apm.common.dialog.ChoiceDialog
import kotlinx.coroutines.*

/**
 * @author: yw
 * @date: 2021/7/17
 * @description: 与对话框相关的功能
 */
object DialogUtil {

    /** 自动隐藏对话框 */
    fun showAutoInfo(
        context: Context,
        title: String,
        message: String,
        info:String,
        steayTime:Int,
        action:((dialog:Dialog) -> Unit)
    ) {
        ChoiceDialog(context, title, message).apply {

            var job:Job? = null

            var countDown = steayTime
            // 显示时开始倒计时
            setOnShowListener {
                job = GlobalScope.launch(Dispatchers.Main) {
                    while(countDown > 0) {
                        getChoiceText(0)?.text = "${info}(${countDown})"
                        delay(1000)
                        countDown--
                    }
                    action.invoke(it as Dialog)
            }
            setChoice(0, info) {
                // 取消任务
                job?.cancel()
                action.invoke(it)
                }
            }
        }.show()
    }

    fun showInfo(
        context: Context,
        title: String,
        message: String,
        choice0: String
    ) {
        getChoice(
            context,
            title,
            message,
            choice0
        )
    }

    fun getChoice(
        context: Context,
        title: String,
        message: String,
        choice0: String
    ):Int {
        return getChoice(
            context,
            title,
            message,
            choice0,
            null
        )
    }

    /**
     * 二选一
     * @param title 标题
     * @param message 信息
     * @param choice0 选项一
     * @param choice1 选项二
     * @return 获取用户的选项
     */
    fun getChoice(
        context: Context,
        title: String,
        message: String,
        choice0: String,
        choice1: String?
    ): Int {
        return getChoice(
            context,
            title,
            message,
            choice0,
            choice1,
            null
        )
    }

    /**
     * 三选一
     * @param context
     * @param title
     * @param message
     * @param choice0
     * @param choice1
     * @param choice2
     * @return
     */
    fun getChoice(
        context: Context,
        title: String,
        message: String,
        choice0: String,
        choice1: String?,
        choice2: String?
    ): Int {
        return getChoice(
            context,
            title,
            message,
            choice0,
            choice1,
            choice2,
            null
        )
    }


    /**
     * 多选一
     * @param context Context
     * @param title String
     * @param message String
     * @param choice0 String?
     * @param choice1 String?
     * @param choice2 String?
     * @param choice3 String?
     * @return Int
     */
    fun getChoice(
        context: Context,
        title: String,
        message: String,
        choice0: String?,
        choice1: String?,
        choice2: String?,
        choice3: String?
    ):Int {
        return UserChoice(
            context,
            title,
            message,
            choice0,
            choice1,
            choice2,
            choice3
        ).getResult()
    }
}