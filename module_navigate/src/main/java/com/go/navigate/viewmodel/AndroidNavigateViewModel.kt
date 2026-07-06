package com.go.navigate.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.common.viewModel.BaseListViewModel
import com.go.navigate.data.AndroidNavigateBean
import com.go.navigate.data.AndroidNavigateTreeBean
import com.go.navigate.dataStore.AndroidNavigateTreeStore
import com.go.navigate.http.AndroidNavigateRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take

class AndroidNavigateViewModel(private val repository: AndroidNavigateRepository) :
    BaseListViewModel<AndroidNavigateBean>() {

    fun refresh() {
        getAndroidNavigateData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAndroidNavigateData() {
        if (isLoading()) {
            return
        }
        AndroidNavigateTreeStore.Instance.read().take(1)
            .onStart {
                LogUtils.d(TAG, "getAndroidNavigateData onStart")
            }.flatMapConcat {
                LogUtils.d(TAG, "getAndroidNavigateData flatMapConcat")
                if (it.data.isNotEmpty()) {
                    updateDataList(it.data)
                }
                updateLoadingState(defaultPageStart)
                repository.getAndroidNavigateData()
            }.onEach {
                LogUtils.d(TAG, "getAndroidNavigateData onEach:$it")
                if (it.isSucceed()) {
                    updateLoadEndState()
                    updateDataList(curPage = defaultPageStart, it.data.orEmpty())
                    if (it.data.orEmpty().isNotEmpty()) {
                        AndroidNavigateTreeStore.Instance.write(bean = AndroidNavigateTreeBean(data = it.data.orEmpty()))
                    }
                } else {
                    updateErrorState(curPage = defaultPageStart, errorMsg = it.errorMsg)
                }
            }.onCompletion {
                LogUtils.d(TAG, "getAndroidNavigateData onCompletion:$it")
            }.catch {
                LogUtils.d(TAG, "getAndroidNavigateData catch:$it")
                updateErrorState(curPage = defaultPageStart, errorMsg = it.message ?: it.toString())
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "AndroidNavigateViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AndroidNavigateViewModel(repository = AndroidNavigateRepository.Instance)
            }
        }
    }
}