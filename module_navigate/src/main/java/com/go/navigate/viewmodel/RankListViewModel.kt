package com.go.navigate.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.common.viewModel.BaseListViewModel
import com.go.navigate.data.RankBean
import com.go.navigate.http.RankListRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class RankListViewModel(private val repository: RankListRepository): BaseListViewModel<RankBean>() {

    protected override val defaultPageStart: Int = 1

    private fun getRankList(page:Int) {
        if(isLoading()) {
            return
        }
        repository.getRankList(page = page)
            .onStart {
                LogUtils.d(TAG,"getRankList onStart page:${page}")
                updateLoadingState(curPage = page)
            }.onEach {
                LogUtils.d(TAG,"getRankList onEach it:${it}")
                if(it.isSucceed()) {
                    updateSucceedState(
                        curPage = it.data?.curPage?:defaultPageStart,
                        loadedCount = it.data?.datas.orEmpty().size)
                    updateCurPage(it.data?.curPage?:1)
                    updateDataList(curPage = page,it.data?.datas.orEmpty())
                } else {
                    updateErrorState(curPage = page, errorMsg = it.errorMsg)
                }
            }.onCompletion {
                LogUtils.d(TAG,"getRankList onCompletion it:${it}\tlistUiState:${listUiState}\tdataList.size:${dataList.size}")
            }.catch {
                LogUtils.d(TAG,"getRankList catch it:${it}")
                updateErrorState(curPage = page, errorMsg = it.message?:toString())
            }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        getRankList(1)
    }

    fun loadMore() {
        getRankList(curPage+1)
    }

    companion object {
        private const val TAG = "RankListViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RankListViewModel(repository = RankListRepository.Instance)
            }
        }
    }
}