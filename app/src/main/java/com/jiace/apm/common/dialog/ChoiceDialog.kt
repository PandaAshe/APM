package com.jiace.apm.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.jiace.apm.R
import com.jiace.apm.base.HideBarDialog
import com.jiace.apm.core.event.AutoCloseChoiceEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * @author: yw
 * @date: 2021/7/10
 * @description: 多按钮信息显示框
 */
class ChoiceDialog (context: Context, title: String, message: String): HideBarDialog(context) {

    private val mActions = arrayListOf<((dialog:Dialog) -> Unit)?>(null, null, null, null)

    private val mMessageTv: TextView
    private val mTitleTv: TextView
    private val mChoicesTV:ArrayList<TextView>
    private val mVerticalDividers:ArrayList<View>
    private var mAutoCloseAction:((dialog:Dialog) -> Unit)? = null

    companion object {
        private const val MaxChoice = 4
    }

    init {

        val bootView = View.inflate(context, R.layout.choice_dialog_layout, null)
        mMessageTv = bootView.findViewById(R.id.tvText)
        mMessageTv.text = message

        mTitleTv = bootView.findViewById(R.id.tvTitle)
        mTitleTv.text = title

        mChoicesTV = ArrayList<TextView>()
        mChoicesTV.add(bootView.findViewById(R.id.choice0))
        mChoicesTV.add(bootView.findViewById(R.id.choice1))
        mChoicesTV.add(bootView.findViewById(R.id.choice2))
        mChoicesTV.add(bootView.findViewById(R.id.choice3))

        mVerticalDividers = ArrayList<View>()
        mVerticalDividers.add(bootView.findViewById(R.id.vertical_divider_0))
        mVerticalDividers.add(bootView.findViewById(R.id.vertical_divider_1))
        mVerticalDividers.add(bootView.findViewById(R.id.vertical_divider_2))

        window?.requestFeature(Window.FEATURE_NO_TITLE)
        setView(bootView)
    }


    /**
     * 设置按钮文字及点击事件
     * @param index Int
     * @param text String
     * @param action Function1<[@kotlin.ParameterName] Dialog, Unit>?
     */
    fun setChoice(index:Int, text:String, action:((dialog:Dialog)-> Unit)?) {
        mChoicesTV[index].text = text
        mActions[index] = action
        refresh()
    }

    private fun refresh() {
        // 设置选项文字及点击事件
        mChoicesTV.forEachIndexed{ index, textView ->

            if(mActions[index] == null) {
                textView.visibility = View.GONE
            } else {
                textView.visibility = View.VISIBLE
            }

            textView.setOnClickListener {
                mActions[index]?.invoke(this)
            }
        }

        // 隐藏分割竖线
        for (i in 0 until MaxChoice-1) {
            if( mActions[i] == null) {
                mVerticalDividers[i].visibility = View.GONE
            } else {
                mVerticalDividers[i].visibility = View.VISIBLE
            }
        }
    }


    fun getChoiceText(index:Int) = run{
        if(index < MaxChoice) {
            return@run mChoicesTV[index]
        } else {
            return@run  null
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onClose(event: AutoCloseChoiceEvent) {
        mAutoCloseAction?.invoke(this)
    }

    /** 设置自动关闭响应 */
    fun setAutoCloseAction(action:((dialog:Dialog) -> Unit)?) {
        EventBus.getDefault().register(this)
        mAutoCloseAction = action
    }


    /** 禁止自动关闭对话框 */
    fun forbidAutoClose() {
        EventBus.getDefault().unregister(this)
        mAutoCloseAction = null
    }
}