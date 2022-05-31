package com.jiace.apm.ui.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.jiace.apm.BR
import com.jiace.apm.base.BaseActivity
import com.jiace.apm.databinding.MainActivityLayoutBinding

class MainActivity: BaseActivity() {

    private lateinit var mMainActivityLayoutBinding: MainActivityLayoutBinding
    private lateinit var mMainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainActivityLayoutBinding = MainActivityLayoutBinding.inflate(layoutInflater)
        mMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mMainActivityLayoutBinding.setVariable(BR.mainVM,mMainViewModel)
        setContentView(mMainActivityLayoutBinding.root)
    }
}
