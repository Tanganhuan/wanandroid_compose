package com.go.common.viewModel

import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.LogUtils

open class BaseViewModel: ViewModel() {

    companion object {
        private const val TAG ="BaseViewModelTAG"
    }

    init {
        LogUtils.d(TAG,"${this.javaClass.simpleName}\tBaseViewModel run init:${hashCode()}")
    }

    override fun onCleared() {
        super.onCleared()
        LogUtils.d(TAG,"${this.javaClass.simpleName}\tBaseViewModel run onCleared:${hashCode()}")
    }
}