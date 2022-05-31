package com.jiace.apm.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.BaseAdapter
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getColor
import com.jiace.apm.R
import com.jiace.apm.common.dialog.SureAlertDialog
import com.jiace.apm.common.dialog.EditTextDialog
import com.jiace.apm.core.dataStruct.DeviceGather
import com.jiace.apm.core.dbf.TBDeviceGatherHelper
import com.jiace.apm.ui.param.dialog.DeviceMangerDialog
import com.jiace.apm.until.applicationScope
import com.jiace.apm.until.checkTextIsValid
import com.jiace.apm.until.showCenterToast
import kotlinx.android.synthetic.main.cell_list_layout.view.*
import kotlinx.android.synthetic.main.grade_item_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.collections.ArrayList

/**
* Copyright (C), 2015-2021, 武汉嘉测科技有限公司
* FileName: SensorManagerView
* Author: Mrw
* Date: 2021/6/9 22:47
* History:
 */
class SensorManagerView : LinearLayout {

    private var mType = DeviceGather.SampleMachine
    private var mSensorAdapter: SensorAdapter
    private val bootView = View.inflate(context, R.layout.cell_list_layout,null)

    private val mSensorArrayList = ArrayList<DeviceGather>()

    constructor(context: Context) : super(context)
    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet, 0) {
        val ta = context?.obtainStyledAttributes(attributeSet, R.styleable.SensorManagerView)
        if (ta != null) {
            mType = ta.getInt(R.styleable.SensorManagerView_type, -1)
            bootView.tipTitle.text = DeviceGather.getDeviceName(mType)

            flow {
                emit(TBDeviceGatherHelper.queryDeviceTables(mType))
            }.flowOn(Dispatchers.IO).catch { e ->
                e.printStackTrace()
            }.onEach {
                mSensorArrayList.addAll(it)
                mSensorAdapter.notifyDataSetChanged()
            }.launchIn(applicationScope)
            ta.recycle()
        }
    }

    init {
        mSensorAdapter = SensorAdapter()
        bootView.lv.adapter = mSensorAdapter
        bootView.lv.setOnItemClickListener { _, _, position, _ ->
            when (mType) {

                DeviceGather.SampleMachine -> {
                    if (position == mSensorArrayList.size) {
                        showInputDialog(DeviceGather.SampleMachine,"请输入采集仪编号")
                    }
                }

                DeviceGather.TorsionSensor -> {
                    if (position == mSensorArrayList.size) {
                        DeviceMangerDialog(context,true,DeviceGather(mType)).apply {
                            setOnPositionListener { device, _ ->
                                if (mSensorArrayList.any { it.No == device.No }) {
                                    showCenterToast(context,"编号已存在！")
                                    return@setOnPositionListener
                                }
                                TBDeviceGatherHelper.updateDeviceTable(device)
                                mSensorArrayList.add(position,device)
                                mSensorAdapter.notifyDataSetChanged()
                            }
                            show()
                        }
                    } else {
                        DeviceMangerDialog(context,false,mSensorArrayList[position]).apply {
                            show()
                        }
                    }
                }

                DeviceGather.AngleOfDipSensor -> {
                    if (position == mSensorArrayList.size) {
                        showInputDialog(DeviceGather.AngleOfDipSensor,"请输入倾角传感器编号")
                    }
                }

                DeviceGather.Displacement -> {
                    if (position == mSensorArrayList.size) {
                        showInputDialog(DeviceGather.Displacement,"请输入激光传感器编号")
                    }
                }

                DeviceGather.Bluetooth -> {
                    if (position == mSensorArrayList.size) {
                        showInputDialog(DeviceGather.Displacement,"请输入蓝牙模块编号")
                    }
                }
            }
        }

        bootView.lv.setOnItemLongClickListener { _, _, position, _ ->
            if (position == mSensorArrayList.size)
                return@setOnItemLongClickListener true
            SureAlertDialog(context).apply {
                setMessage("确定要删除 ${mSensorArrayList[position].No} 吗？")
                setPositionButton("删除") {
                    it.dismiss()
                    TBDeviceGatherHelper.deleteDeviceTable(mType,mSensorArrayList[position].No)
                    mSensorArrayList.removeAt(position)
                    mSensorAdapter.notifyDataSetChanged()
                }
                setNegativeButton("取消") {
                    it.dismiss()
                }
                show()
            }
            return@setOnItemLongClickListener true
        }
        addView(bootView)
    }

    inner class SensorAdapter : BaseAdapter() {

        override fun getItem(position: Int): Any {
            return Any()
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return mSensorArrayList.size + 1
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val bootView = convertView ?: View.inflate(context, R.layout.grade_item_layout, null)

            if (position == mSensorArrayList.lastIndex + 1) {
                bootView.gradeValue.text = "新增"
            } else {
                bootView.gradeValue.text = mSensorArrayList[position].No
            }
            bootView.gradeValue.setTextColor(getColor(context,R.color.teal_200))
            return bootView
        }
    }

    private fun showInputDialog(type: Int,title: String) {
        val editTextDialog = EditTextDialog(context)
        editTextDialog.apply {
            setText("")
            setEditType(EditorInfo.TYPE_CLASS_TEXT)
            setTitle(title)
            setCancelable(false)
            setOnPositionListener {inputText ->
                if (inputText.isEmpty())
                    return@setOnPositionListener
                dismiss()
                if (inputText.isEmpty()) {
                    showCenterToast(context,"输入的编号不能为空！")
                    return@setOnPositionListener
                }
                if (!checkTextIsValid(inputText)) {
                    showCenterToast(context,"输入的编号包含非法字符！")
                    return@setOnPositionListener
                }

                if (mSensorArrayList.any { it.No == inputText }) {
                    showCenterToast(context,"编号已存在！")
                    return@setOnPositionListener
                }

                DeviceGather().let {
                    it.Type = type
                    it.No = inputText.uppercase(Locale.getDefault())
                    TBDeviceGatherHelper.updateDeviceTable(it)
                    mSensorArrayList.add(it)
                }
                mSensorAdapter.notifyDataSetChanged()
            }
            show()
        }
    }
}
