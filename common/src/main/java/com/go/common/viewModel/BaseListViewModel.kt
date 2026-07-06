package com.go.common.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.go.common.data.ListUiState

abstract class BaseListViewModel<T>: BaseListUiStateViewModel() {

    protected open val defaultPageStart:Int = 0

    protected var curPage = 0
        private set

    private var _dataList: MutableState<List<T>> = mutableStateOf(emptyList())
    val dataList: List<T> by _dataList
    protected fun isEmptyData(): Boolean = _dataList.value.isEmpty()


    protected fun updateLoadingState(curPage:Int) {
        updateListUiState(
            if(curPage == defaultPageStart) ListUiState.Refreshing(isEmptyData = isEmptyData())
            else ListUiState.LoadingMore
        )
    }

    protected fun updateSucceedState(curPage:Int,loadedCount:Int) {
        updateListUiState(
            if(curPage == defaultPageStart) ListUiState.RefreshSucceed(loadedCount = loadedCount)
            else ListUiState.LoadMoreSucceed(loadedCount = loadedCount)
        )
    }

    protected fun updateLoadEndState() {
        updateListUiState(ListUiState.LoadMoreSucceed(loadedCount = 0))
    }

    protected fun updateErrorState(curPage:Int,errorMsg:String) {
        updateListUiState(
            if(curPage == defaultPageStart) ListUiState.RefreshError(isEmptyData = isEmptyData(), message = errorMsg)
            else ListUiState.LoadMoreError(message = errorMsg)
        )
    }

    protected fun updateDataList(curPage: Int,list:List<T>) {
        if(curPage == defaultPageStart) {
            updateDataList(list = list)
        } else {
            addDataList(list)
        }
    }

    protected fun updateDataList(list:List<T>) {
        _dataList.value = list
    }

    protected fun addDataList(list:List<T>) {
        if(list.isEmpty()) {
            return
        }
        _dataList.value += list
    }

    protected fun updateCurPage(page:Int) {
        curPage = page
    }


}