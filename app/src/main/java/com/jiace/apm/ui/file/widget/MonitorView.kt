package com.jiace.apm.ui.file.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.jiace.apm.R
import com.jiace.apm.core.dataStruct.Record
import com.jiace.apm.until.applicationScope
import kotlinx.android.synthetic.main.monitor_view_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/31.
3 * Description:
4 *
5 */
class MonitorView: LinearLayout,View.OnClickListener {

    private var mSourceData = ArrayList<Record>()

    private var mTimeGap = 1000L
    private var mCurrentUpdatePosition = 0
    private var mPlayState = PlayState.Pause

    private var mIsWorking = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val mBootView = View.inflate(context, R.layout.monitor_view_layout,null)

    init {
        flow {
            while (mIsWorking) {
                if (mPlayState == PlayState.Play) {
                    if (mCurrentUpdatePosition <= mSourceData.lastIndex && mCurrentUpdatePosition >= 0) {
                        mCurrentUpdatePosition++
                        delay(mTimeGap)
                        emit(1)
                    }
                } else {
                    delay(1000)
                    emit(0)
                }
            }
        }.flowOn(Dispatchers.IO).onEach {
            if (it == 1) {
                depthView.updateCurve(mSourceData[mCurrentUpdatePosition])
                footageView.updateCurve(mSourceData[mCurrentUpdatePosition])
                torsionView.updateCurve(mSourceData[mCurrentUpdatePosition])
                angleView.updateCurve(mSourceData[mCurrentUpdatePosition])
            }
        }.launchIn(applicationScope)
        addView(mBootView)
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.playOrPauseButton -> {
                mPlayState = if (mPlayState == PlayState.Play) {
                    playOrPauseButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_pause_24))
                    PlayState.Pause
                } else {
                    playOrPauseButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_play_arrow_24))
                    PlayState.Play
                }
            }

            R.id.fastForwardButton -> {
                mTimeGap /= 2
            }

            R.id.fastRewindButton -> {
                mTimeGap *= 2
            }
        }
    }

    /**
     *  设置原始数据
     *
     * */
    fun setSourceData(sourceData: ArrayList<Record>) {
        mSourceData = sourceData
    }

    /**
     *  关闭时退出
     * */
    fun onClear() {
        mIsWorking = false
    }

    companion object {
        enum class PlayState {
            Play,Pause
        }
    }
}