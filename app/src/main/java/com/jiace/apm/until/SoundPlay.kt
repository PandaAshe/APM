package com.jiace.apm.until

import android.media.AudioAttributes
import android.media.SoundPool

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/3/11.
3 * Description:
4 *
5 */
object SoundPlay {

    const val SUCCESS = 0
    const val FAILED = 1

    private val mSoundMaps =  HashMap<Int,Int>()

    private val mSoundPool = SoundPool.Builder().apply {
        setMaxStreams(10)
        setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
    }.build()

    fun init() {
        mSoundMaps.apply {
            // todo 添加声音
            //put(SUCCESS, mSoundPool.load(Application.get(), R.raw.barcodebeep,1))
            //put(FAILED, mSoundPool.load(Application.get(),R.raw.serror,1))
        }
    }

    fun play(id: Int) {
        try {
            mSoundPool.play(mSoundMaps[id]!!,1f,1f,1,0,1f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}