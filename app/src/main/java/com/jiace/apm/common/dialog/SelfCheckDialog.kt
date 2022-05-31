package com.jiace.apm.common.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.jiace.apm.R
import com.jiace.apm.base.HideBarDialog
import com.jiace.apm.core.service.ServiceHelper

/**
 * @author: yw
 * @date: 2021-07-09
 * @description: 参数自检
 */
class SelfCheckDialog(private val mContext: Context) : HideBarDialog(mContext) {
    private val mListView: ListView
    private val mOperation: TextView
    private val mCancel: TextView
    private val mAdapter: IncorrectListViewAdapter
    private var mListString: ArrayList<String> = ArrayList<String>()
    private val mText: TextView

    // 是否继续
    var mIsContinue = false
        private set

    // 参数是否正确
    private var mIsCorrect = false

    // 后续操作
    private var mContinuteAction: ((dialog: Dialog) -> Unit)? = null
    private var mCancelAction: ((dialog: Dialog) -> Unit)? = null


    init {

        val view = layoutInflater.inflate(R.layout.self_check_dialog, null)
        mListView = view.findViewById(R.id.lvIncorrects)
        mOperation = view.findViewById(R.id.oK)
        mText = view.findViewById(R.id.tvText)
        mCancel = view.findViewById(R.id.cancel)
        mCancel.text = "取消"

        mCancel.setOnClickListener {
            mIsContinue = false
            dismiss()
            mCancelAction?.invoke(this)
        }

        mOperation.setOnClickListener {
            if (mIsCorrect) {
                mIsContinue = true
                dismiss()
                mContinuteAction?.invoke(this)
            } else {
                refresh()
            }
        }

        mAdapter = IncorrectListViewAdapter(ArrayList<String>(), mContext)
        mListView.adapter = mAdapter

        refresh()

        setView(view)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWidthRatio = 0.5f
        mHeightRatio = 0.9f
        setDialogSize()
    }

    /** 自检成功的后续操作 */
    fun setContinuteAction(action: ((dialog: Dialog) -> Unit)?) {
        mContinuteAction = action
    }

    fun setCancelAction(action: ((dialog: Dialog) -> Unit)?) {
        mCancelAction = action
    }

    private fun refresh() {
        mListString = ServiceHelper.mVirtualDeviceService?.checkParam()!!
        if (mListString.size > 0) {
            mText.visibility = View.GONE
            mListView.visibility = View.VISIBLE
            mAdapter.updateIncorrectList(mListString)
            mOperation.text = "重新检测"
            mIsCorrect = false
        } else {
            mText.visibility = View.VISIBLE
            mListView.visibility = View.GONE
            mOperation.text = "下一步"
            mIsCorrect = true
        }
    }


    class IncorrectListViewAdapter(list: ArrayList<String>, context: Context) : BaseAdapter() {

        private val mContext: Context = context
        private var mList: ArrayList<String> = list

        /**
         * 更新异常信息
         * @param list ArrayList<String>
         */
        fun updateIncorrectList(list: ArrayList<String>) {
            mList = list

            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return mList.size
        }

        override fun getItem(position: Int): String {
            return mList.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var holder: ViewHolder? = null
            var view = convertView
            if (convertView == null) {
                view = View.inflate(mContext, R.layout.item_incorrect_list, null)
                holder = ViewHolder(view)
            } else {
                holder = convertView.tag as ViewHolder
            }
            holder.mIncorrect.text = getItem(position)
            return view!!
        }
    }


    class ViewHolder(view: View) {
        var mIcon: ImageView = view.findViewById(R.id.ivIcon)
        var mIncorrect: TextView = view.findViewById(R.id.tvText)

        init {
            view.tag = this
        }
    }
}