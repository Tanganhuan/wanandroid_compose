package com.go.navigate.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.mine.data.ArticleBean
import com.go.mine.data.ArticlePageInfoBean
import com.go.common.data.ListUiState
import com.go.common.viewModel.BaseViewModel
import com.go.navigate.http.WendaRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class WendaViewModel(private val repository: WendaRepository): BaseViewModel() {

    private var pageInfo: ArticlePageInfoBean = ArticlePageInfoBean()
    private var _listUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    val listUiState: State<ListUiState> = _listUiState

    private var _wendaList: MutableState<List<ArticleBean>> = mutableStateOf(emptyList())
    val wendaList: State<List<ArticleBean>> = _wendaList
    private fun isEmptyData(): Boolean = _wendaList.value.isEmpty()

    fun refresh() {
        if(_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore) {
            return
        }
        _listUiState.value = ListUiState.Refreshing(isEmptyData = isEmptyData())
        repository.refresh()
            .onStart {
                LogUtils.d(TAG,"refresh onStart")
            }.onEach {
                LogUtils.d(TAG,"refresh onEach:$it")
                if(it.isSucceed()) {
                    _listUiState.value = ListUiState.RefreshSucceed(loadedCount = it.data?.datas.orEmpty().size)
                    it.data?.let { data ->
                        pageInfo = data.copy(datas = emptyList())
                        _wendaList.value = data.datas
                    }
                } else {
                    _listUiState.value = ListUiState.RefreshError(message = it.errorMsg, isEmptyData = isEmptyData())
                }
            }.onCompletion {
                LogUtils.d(TAG,"refresh onCompletion:$it")
            }.catch {
                LogUtils.d(TAG,"refresh catch:$it")
                _listUiState.value = ListUiState.RefreshError(message = it.message?:it.toString(), isEmptyData = isEmptyData())
            }
            .launchIn(viewModelScope)
    }

    fun loadMore() {
        if(_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore) {
            return
        }
        _listUiState.value = ListUiState.LoadingMore
        repository.getWendaList(pageInfo.curPage+1)
            .onStart {
                LogUtils.d(TAG,"loadMore onStart")
            }.onEach {
                LogUtils.d(TAG,"loadMore onEach:$it")
                if(it.isSucceed()) {
                    it.data?.let { data ->
                        _listUiState.value = ListUiState.LoadMoreSucceed(loadedCount = data.datas.size)
                        pageInfo = data.copy(datas = emptyList())
                        _wendaList.value += data.datas
                    }
                } else {
                    _listUiState.value = ListUiState.LoadMoreError(message = it.errorMsg)
                }
            }.onCompletion {
                LogUtils.d(TAG,"loadMore onCompletion:$it")
            }.catch {
                LogUtils.d(TAG,"loadMore catch:$it")
                _listUiState.value = ListUiState.LoadMoreError(message = it.message?:it.toString())
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "ProjectViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WendaViewModel(repository = WendaRepository.Instance)
            }
        }
    }
}