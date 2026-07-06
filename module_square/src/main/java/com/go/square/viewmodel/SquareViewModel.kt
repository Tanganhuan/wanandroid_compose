package com.go.square.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.common.data.ListUiState
import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import com.go.common.viewModel.BaseViewModel
import com.go.square.http.SquareRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class SquareViewModel(private val repository: SquareRepository): BaseViewModel() {
    private var pageInfo:ArticlePageInfoBean = ArticlePageInfoBean()
    private var _listUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    val listUiState: State<ListUiState> = _listUiState

    private var _squareList = mutableStateOf(ApiResponse.create<ArticlePageInfoBean>(ArticlePageInfoBean()))
    val squareList: State<ApiResponse<ArticlePageInfoBean>> = _squareList

    private fun isEmptyData(): Boolean = squareList.value.data?.datas.orEmpty().isEmpty()


    fun refresh() {
        if(_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore) {
            return
        }
        _listUiState.value = ListUiState.Refreshing(isEmptyData = isEmptyData())
        repository.refresh()
            .onStart {
                LogUtils.d(TAG,"refresh onStart _squareList size:${_squareList.value.data?.datas.orEmpty().size}")
            }
            .onEach {
                LogUtils.d(TAG,"refresh onEach:$it")
                it.data?.let { _pageInfo ->
                    pageInfo = _pageInfo.copy(datas = emptyList())
                }
                _squareList.value = it
                _listUiState.value = ListUiState.RefreshSucceed(loadedCount = it.data?.datas.orEmpty().size)
            }
            .onCompletion {
                LogUtils.d(TAG,"refresh onCompletion:$it _squareList size:${_squareList.value.data?.datas.orEmpty().size}")
            }.catch {
                LogUtils.d(TAG,"refresh catch:$it")
                _listUiState.value = ListUiState.RefreshError(isEmptyData = isEmptyData(), message = it.message?:it.toString())
            }
            .launchIn(viewModelScope)
    }

    fun loadMore() {
        if(_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore) {
            return
        }
        _listUiState.value = ListUiState.LoadingMore
        repository.loadMoreData(pageInfo.curPage)
            .onEach {
                LogUtils.d(TAG,"loadMore onEach:$it")
                it.data?.let { _pageInfo ->
                    pageInfo = _pageInfo.copy(datas = emptyList(), loadedCount = _pageInfo.datas.size)
                }
                val moreList = it.data?.datas.orEmpty()
                val allList = _squareList.value.data?.datas.orEmpty() + moreList
                _squareList.value = it.copy(data = it.data?.copy(datas = allList))
                _listUiState.value = ListUiState.LoadMoreSucceed(pageInfo.loadedCount)

            }.onCompletion {
                LogUtils.d(TAG,"loadMore onCompletion:$it")
            }.catch {
                LogUtils.d(TAG,"refresh catch:$it")
                _listUiState.value = ListUiState.LoadMoreError(message = it.message?:it.toString())
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "SquareViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SquareViewModel(repository = SquareRepository.Instance)
            }
        }
    }
}