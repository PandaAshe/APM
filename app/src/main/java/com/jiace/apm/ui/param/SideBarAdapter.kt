package com.jiace.apm.ui.param

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import com.jiace.apm.R
import kotlinx.android.synthetic.main.side_bar_layout.view.*

class SideBarAdapter(val context: Context): BaseAdapter() {

    companion object {
        private val NormalPageIcons = arrayOf(
            R.drawable.ic_project_normal,
            R.drawable.ic_builder_normal,
            R.drawable.ic_warring_noraml,
            R.drawable.ic_sensor_normal,
            R.drawable.ic_device_normal
        )

        private val CheckedPageIcons = arrayOf(
            R.drawable.ic_project_checked,
            R.drawable.ic_builder_checked,
            R.drawable.ic_warring_checked,
            R.drawable.ic_sensor_checked,
            R.drawable.ic_device_checked
        )

        const val Project = 0
        const val Build = 1
        const val Monitor = 2
        const val Sensor = 3
        const val DeviceGather = 4
    }

    var checkPosition = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var mSettingNames: Array<String> = context.resources.getStringArray(R.array.setting_names)

    override fun getCount(): Int {
        return NormalPageIcons.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val bootView = convertView ?: View.inflate(context,R.layout.side_bar_layout,null)
        bootView.settingName.text = mSettingNames[position]
        if (position == checkPosition) {
            bootView.icon.setImageResource(CheckedPageIcons[position])
            bootView.settingName.setTextColor(getColor(context,R.color.choose_item_text_color))
            bootView.setBackgroundColor(getColor(context,R.color.param_content_background))
        } else {
            bootView.icon.setImageDrawable(ContextCompat.getDrawable(context, NormalPageIcons[position]))
            bootView.settingName.setTextColor(getColor(context,R.color.white))
            bootView.setBackgroundColor(getColor(context,R.color.param_side_background))
        }
        return bootView
    }
}