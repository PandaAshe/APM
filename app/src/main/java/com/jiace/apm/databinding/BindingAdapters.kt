/*
 * Copyright © 2017-2021 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.jiace.apm.databinding

import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jiace.apm.core.dataStruct.Record
import com.jiace.apm.ui.main.MonitorParam
import com.jiace.apm.widget.CardView
import com.jiace.apm.widget.CurveView
import com.jiace.apm.widget.ItemDivider
import com.jiace.apm.widget.ToggleSwitch
import kotlinx.android.synthetic.main.card_view_layout.view.*
import java.util.Optional


/**
 * Static methods for use by generated code in the Android data binding library.
 */
object BindingAdapters {

    @JvmStatic
    @BindingAdapter("color")
    fun setColor(view: View,newId: Int) {
        when (view) {
            is TextView -> {
                view.setTextColor(ContextCompat.getColor(view.context,newId))
            }
            is Button -> {
                view.setTextColor(ContextCompat.getColor(view.context,newId))
            }
            else -> {

            }
        }
    }

    @JvmStatic
    @BindingAdapter("checked")
    fun setChecked(view: ToggleSwitch, checked: Boolean) {
        view.setCheckedInternal(checked)
    }

    @JvmStatic
    @BindingAdapter("filter")
    fun setFilter(view: TextView, filter: InputFilter) {
        view.filters = arrayOf(filter)
    }

    /*@JvmStatic
    @BindingAdapter("items", "layout", "fragment")
    fun <E> setItems(view: LinearLayout,
                     oldList: ObservableList<E>?, oldLayoutId: Int, @Suppress("UNUSED_PARAMETER") oldFragment: Fragment?,
                     newList: ObservableList<E>?, newLayoutId: Int, newFragment: Fragment?) {
        if (oldList === newList && oldLayoutId == newLayoutId)
            return
        var listener: ItemChangeListener<E>? = ListenerUtil.getListener(view, R.id.item_change_listener)
        // If the layout changes, any existing listener must be replaced.
        if (listener != null && oldList != null && oldLayoutId != newLayoutId) {
            listener.setList(null)
            listener = null
            // Stop tracking the old listener.
            ListenerUtil.trackListener<Any?>(view, null, R.id.item_change_listener)
        }
        // Avoid adding a listener when there is no new list or layout.
        if (newList == null || newLayoutId == 0)
            return
        if (listener == null) {
            listener = ItemChangeListener(view, newLayoutId, newFragment)
            ListenerUtil.trackListener(view, listener, R.id.item_change_listener)
        }
        // Either the list changed, or this is an entirely new listener because the layout changed.
        listener.setList(newList)
    }*/

    @JvmStatic
    @BindingAdapter("items", "layout")
    fun <E> setItems(view: LinearLayout,
                     oldList: Iterable<E>?, oldLayoutId: Int,
                     newList: Iterable<E>?, newLayoutId: Int) {
        if (oldList === newList && oldLayoutId == newLayoutId)
            return
        view.removeAllViews()
        if (newList == null)
            return
        val layoutInflater = LayoutInflater.from(view.context)
        for (item in newList) {
            val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, newLayoutId, view, false)
            //binding.setVariable(BR.collection, newList)
            //binding.setVariable(BR.item, item)
            binding.executePendingBindings()
            view.addView(binding.root)
        }
    }

    @JvmStatic
    @BindingAdapter(requireAll = false, value = ["items", "layout", "configurationHandler"])
    fun <K, E : Keyed<out K>> setItems(view: RecyclerView,
                                       oldList: ObservableKeyedArrayList<K, E>?, oldLayoutId: Int,
                                       @Suppress("UNUSED_PARAMETER") oldRowConfigurationHandler: ObservableKeyedRecyclerViewAdapter.RowConfigurationHandler<*, *>?,
                                       newList: ObservableKeyedArrayList<K, E>?, newLayoutId: Int,
                                       newRowConfigurationHandler: ObservableKeyedRecyclerViewAdapter.RowConfigurationHandler<*, *>?) {
        if (view.layoutManager == null)
            view.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        if (oldList === newList && oldLayoutId == newLayoutId)
            return
        view.addItemDecoration(ItemDivider(view.context,ItemDivider.VERTICAL_LIST))
        // The ListAdapter interface is not generic, so this cannot be checked.
        @Suppress("UNCHECKED_CAST") var adapter = view.adapter as? ObservableKeyedRecyclerViewAdapter<K, E>?
        // If the layout changes, any existing adapter must be replaced.
        if (adapter != null && oldList != null && oldLayoutId != newLayoutId) {
            adapter.setList(null)
            adapter = null
        }
        // Avoid setting an adapter when there is no new list or layout.
        if (newList == null || newLayoutId == 0)
            return
        if (adapter == null) {
            adapter = ObservableKeyedRecyclerViewAdapter(view.context, newLayoutId, newList)
            view.adapter = adapter
        }
        adapter.setRowConfigurationHandler(newRowConfigurationHandler)
        // Either the list changed, or this is an entirely new listener because the layout changed.
        adapter.setList(newList)
    }




    @JvmStatic
    @BindingAdapter("onBeforeCheckedChanged")
    fun setOnBeforeCheckedChanged(view: ToggleSwitch,
                                  listener: ToggleSwitch.OnBeforeCheckedChangeListener?) {
        view.setOnBeforeCheckedChangeListener(listener)
    }

    @JvmStatic
    @BindingAdapter("onFocusChange")
    fun setOnFocusChange(view: EditText, listener: View.OnFocusChangeListener?) {
        view.onFocusChangeListener = listener
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun setOptionalText(view: TextView, text: Optional<*>?) {
        view.text = text?.map { it.toString() }?.orElse("") ?: ""
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun setCardText(view: CardView, text: Optional<*>?) {
        view.value = text?.map { it.toString() }?.orElse("") ?: ""
    }

    @JvmStatic
    fun tryParseInt(s: String?): Int {
        if (s == null)
            return 0
        return try {
            Integer.parseInt(s)
        } catch (_: Throwable) {
            0
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(view: CardView,text: String) {
        if (view.value != text) {
            view.value = text
        }
    }

    @BindingAdapter("android:updateRecord")
    @JvmStatic
    fun updateRecord(view: CurveView,data: MonitorParam) {
        view.updateCurve(data)
    }

}
