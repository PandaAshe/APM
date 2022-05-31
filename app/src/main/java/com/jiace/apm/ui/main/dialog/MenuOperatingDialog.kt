package com.jiace.apm.ui.main.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.jiace.apm.R
import com.jiace.apm.base.HideBarDialog
import com.jiace.apm.core.service.MonitorStatus
import com.jiace.apm.core.service.ServiceHelper
import kotlinx.android.synthetic.main.menu_operating_layout.view.*
import kotlinx.android.synthetic.main.operating_item_layout.view.*

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/23.
3 * Description:
4 *
5 */
class MenuOperatingDialog(context: Context): HideBarDialog(context), AdapterView.OnItemClickListener {

    private val bootView = layoutInflater.inflate(R.layout.menu_operating_layout,null)

    private var mCurrentStatus = MonitorStatus.State.IDLE

    init {

        mCurrentStatus = ServiceHelper.mVirtualDeviceService?.getCurrentMonitorState() ?: MonitorStatus.State.IDLE

        when (mCurrentStatus) {
            MonitorStatus.State.Monitoring -> {
                OPERATING_TIP[Position_Monitor] = "结束监测"
                OPERATING_TIP[Position_Suspend] = "暂停监测"
            }

            MonitorStatus.State.Suspend -> {
                OPERATING_TIP[Position_Monitor] = "结束监测"
                OPERATING_TIP[Position_Suspend] = "继续监测"
            }

            MonitorStatus.State.IDLE -> {
                OPERATING_TIP[Position_Monitor] = "开始新监测"
                OPERATING_TIP[Position_Suspend] = "暂停监测"
            }
        }

        bootView.operatingView.onItemClickListener = this
        bootView.operatingView.adapter = OperatingAdapter(context)

        setView(bootView)

    }

    var mOnMenuOperatingListener: OnMenuOperatingListener? = null

    interface OnMenuOperatingListener {
        fun onMenuOperatingListener(operatingId: Int)
    }

    companion object {

        const val Position_Monitor = 0
        const val Position_Suspend = 1
        const val Position_Go_Back = 2

        const val OPERATING_STOP = 1
        const val OPERATING_START = 2
        const val OPERATING_RESUME_TESTING = 3
        const val OPERATING_SUSPEND_TESTING = 4

        private val OPERATING_IMAGE = arrayOf(
            R.mipmap.ic_start_normal,
            R.drawable.ic_suspend,
            R.drawable.ic_reply_black_24dp
        )

        private val OPERATING_TIP = arrayOf(
            "开始新试验",
            "暂停试验",
            "返回"
        )
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == Position_Go_Back) {
            dismiss()
            return
        }
        when (position) {
            Position_Monitor -> {
                when (mCurrentStatus) {
                    MonitorStatus.State.IDLE -> {
                        mOnMenuOperatingListener?.onMenuOperatingListener(OPERATING_START)
                    }

                    else -> {
                        mOnMenuOperatingListener?.onMenuOperatingListener(OPERATING_STOP)
                    }
                }
            }

            Position_Suspend -> {
                when (mCurrentStatus) {
                    MonitorStatus.State.IDLE -> {
                        dismiss()
                    }

                    MonitorStatus.State.Monitoring -> {
                        mOnMenuOperatingListener?.onMenuOperatingListener(OPERATING_SUSPEND_TESTING)
                    }

                    MonitorStatus.State.Suspend -> {
                        mOnMenuOperatingListener?.onMenuOperatingListener(OPERATING_RESUME_TESTING)
                    }
                }
            }
        }
    }

    /**
     * 焦点丢失时，自动消失
     * @param hasFocus Boolean
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if(!hasFocus) {
            dismiss()
        }
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (window != null) {
            window!!.setGravity(Gravity.CENTER)
            val m = window!!.windowManager
            val d = m.defaultDisplay
            val p = window!!.attributes
            // p.height = (d.height * 0.65).toInt()
            p.width = (d.width * 0.5).toInt()
            window!!.attributes = p
        }
    }*/

    inner class OperatingAdapter(val context: Context) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val bootView =
                convertView ?: View.inflate(context, R.layout.operating_item_layout, null)
            if (position < OPERATING_IMAGE.size) {
                bootView.image.setImageDrawable(
                    ContextCompat.getDrawable(context, OPERATING_IMAGE[position])
                )
                bootView.tip.text = OPERATING_TIP[position]
            }

            when (mCurrentStatus) {
                MonitorStatus.State.IDLE -> {
                    bootView.tip.setTextColor(context.getColor(android.R.color.black))
                }
                MonitorStatus.State.Monitoring -> {
                    when (position) {
                        Position_Go_Back -> {
                            bootView.tip.setTextColor(context.getColor(android.R.color.black))
                        }
                        else -> {
                            bootView.tip.setTextColor(context.getColor(android.R.color.holo_red_dark))
                        }
                    }
                }
                MonitorStatus.State.Suspend -> {
                    when (position) {
                        Position_Monitor -> {
                            bootView.tip.setTextColor(context.getColor(android.R.color.holo_red_dark))
                        }
                        else -> {
                            bootView.tip.setTextColor(context.getColor(android.R.color.black))
                        }
                    }
                }
            }
            return bootView
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return OPERATING_IMAGE.size
        }
    }
}