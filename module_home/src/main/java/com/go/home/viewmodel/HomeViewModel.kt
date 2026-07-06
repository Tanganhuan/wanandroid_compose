package com.go.home.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.common.viewModel.BaseViewModel
import com.go.home.data.HomeHeadData
import com.go.common.data.ListUiState
import com.go.mine.data.ArticlePageInfoBean
import com.go.home.data.isEmpty
import com.go.home.dataStore.HomeDataStore
import com.go.home.http.HomeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class HomeViewModel(val repository: HomeRepository) : BaseViewModel() {

    private var _listUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    val listUiState: ListUiState by _listUiState

    private var _homeHeadData: MutableState<HomeHeadData> = mutableStateOf(HomeHeadData())
    val homeHeadData: State<HomeHeadData> = _homeHeadData
    private var pageInfo = ArticlePageInfoBean()

    fun refreshData() {
        getHomeHeadData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getHomeHeadData() {
        if (_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore
        ) {
            return
        }

        (if(isEmpty()) HomeDataStore.Instance.read().take(1) else flowOf(_homeHeadData.value)).flatMapConcat {
            LogUtils.d(TAG,"loacCache start")
            if (isEmpty() && !it.isEmpty()) {
                _homeHeadData.value = it
            }
            _listUiState.value = ListUiState.Refreshing(isEmptyData = isEmpty())
            repository.getHomeHeadData()
        }.combine(
            flow = repository.getArticleList(0),
            transform = { head, articleList ->
                _homeHeadData.value = head.copy(articleList = articleList)
                LogUtils.d(
                    TAG,
                    "getHomeHeadData _homeHeadData.value articleList size:${_homeHeadData.value.articleList.data?.datas.orEmpty().size}"
                )
                if (articleList.isSucceed()) {
                    articleList.data?.let {
                        pageInfo = it.copy(datas = emptyList())
                    }
                }
            }
        ).onCompletion { cause ->
            _listUiState.value =
                if (cause == null)
                    ListUiState.RefreshSucceed(loadedCount = Int.MAX_VALUE)
                else
                    ListUiState.RefreshError(
                        isEmptyData = isEmpty(),
                        message = cause.message ?: cause.toString()
                    )
            saveHomeHeadData(_homeHeadData.value)
            LogUtils.d(
                TAG,
                "getHomeHeadData onCompletion cause:$cause\tpageInfo:${pageInfo}\t_listUiState:${_listUiState}"
            )
        }.catch { cause ->
            LogUtils.d(TAG, "getHomeHeadData catch:${cause.message}\t$cause")
            _listUiState.value = ListUiState.RefreshError(
                isEmptyData = isEmpty(),
                message = cause.message ?: cause.toString()
            )
        }.launchIn(viewModelScope)
    }


    fun loadMore() {
        if (_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore
        ) {
            return
        }
        LogUtils.d(TAG, "loadMore start pageInfo:$pageInfo")

        repository.getArticleList(pageInfo.curPage)
            .onStart {
                _listUiState.value = ListUiState.LoadingMore
            }.onEach { moreArticleList ->
                if (moreArticleList.isSucceed()) {
                    moreArticleList.data?.let { _pageInfo ->
                        pageInfo =
                            _pageInfo.copy(datas = emptyList(), loadedCount = _pageInfo.datas.size)
                        val moreList =
                            _homeHeadData.value.articleList.data?.datas.orEmpty() + _pageInfo.datas
                        _homeHeadData.value = _homeHeadData.value.copy(
                            articleList = moreArticleList.copy(
                                data = moreArticleList.data?.copy(datas = moreList)
                            )
                        )
                    }
                }
            }.onCompletion { cause ->
                _listUiState.value = if (cause != null)
                    ListUiState.LoadMoreError(cause.message ?: cause.toString())
                else ListUiState.LoadMoreSucceed(pageInfo.loadedCount)
                LogUtils.d(
                    TAG,
                    "loadMore onCompletion pageInfo:$pageInfo\t_listUiState:${_listUiState.value}\tarticleList size:${_homeHeadData.value.articleList.data?.datas.orEmpty().size}"
                )
            }.catch { cause ->
                LogUtils.d(TAG, "loadMore catch:${cause.message}\t$cause")
            }
            .launchIn(viewModelScope)
    }


    @OptIn(ExperimentalStdlibApi::class)
    private fun saveHomeHeadData(homeHeadData: HomeHeadData) {
        viewModelScope.launch {
            HomeDataStore.Instance.write(homeHeadData)
        }
    }

    private suspend fun loadCache() {
        if (isEmpty()) {
            HomeDataStore.Instance.read().take(1)
                .onStart {
                    LogUtils.d(TAG, "loadCache onStart")
                }.onCompletion {
                    LogUtils.d(TAG, "loadCache onCompletion")
                }.collectLatest {
                    if (!it.isEmpty()) {
                        _homeHeadData.value = it
                        _listUiState.value = ListUiState.Refreshing(isEmptyData = isEmpty())
                    }
                }
        }
    }

    private fun isEmpty(): Boolean = _homeHeadData.value.isEmpty()

    companion object {
        private const val TAG = "HomeViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(repository = HomeRepository.Instance)
            }
        }
    }
}

