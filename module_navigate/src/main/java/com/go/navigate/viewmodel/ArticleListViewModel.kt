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
import com.go.navigate.http.ArticleListRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class ArticleListViewModel(private val repository: ArticleListRepository): BaseViewModel() {

    private var articleListPageInfo: ArticlePageInfoBean = ArticlePageInfoBean()
    private var _articleListUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    private var _articleList: MutableState<List<ArticleBean>> = mutableStateOf(emptyList())

    val articleList: State<List<ArticleBean>> = _articleList
    val listUiState: State<ListUiState> = _articleListUiState


    private fun getArticleList(cid:Int, page:Int,orderType:Int) {
        if(_articleListUiState.value is ListUiState.Refreshing
            || _articleListUiState.value is ListUiState.LoadingMore) {
            return
        }
        repository.getArticlerList(page = page, cid = cid, orderType = orderType)
            .onStart {
                LogUtils.d(TAG,"getArticleList onStart page:$page")
                _articleListUiState.value =if(page==0) ListUiState.Refreshing(isEmptyData = _articleList.value.isEmpty()) else ListUiState.LoadingMore
            }.onEach {
                LogUtils.d(TAG,"getArticleList onEach:$it")
                if(it.isSucceed()) {
                    _articleListUiState.value = if(page == 0)
                        ListUiState.RefreshSucceed(loadedCount = it.data?.datas.orEmpty().size) else
                        ListUiState.LoadMoreSucceed(loadedCount = it.data?.datas.orEmpty().size)

                    _articleList.value = if(page == 0) {
                        it.data?.datas.orEmpty()
                    } else {
                        _articleList.value + it.data?.datas.orEmpty()
                    }
                    it.data?.let { data ->
                        articleListPageInfo = data.copy(datas = emptyList())
                    }
                    LogUtils.d(TAG,"_articleList.size:${_articleList.value.size}")
                } else {
                    _articleListUiState.value = if(page == 0)
                        ListUiState.RefreshError(
                            isEmptyData = _articleList.value.isEmpty(),
                            message = it.errorMsg)
                    else ListUiState.LoadMoreError(message = it.errorMsg)
                }

            }.onCompletion {
                LogUtils.d(TAG,"getArticleList onCompletion:$it\tpage:${articleListPageInfo}\t_articleList size:${_articleList.value.size}\t_articleListUiState:${_articleListUiState}")
            }.catch {
                LogUtils.d(TAG,"getArticleList catch:$it")
                _articleListUiState.value = if(page == 0)
                    ListUiState.RefreshError(
                        isEmptyData = _articleList.value.isEmpty(),
                        message = it.message?:it.toString())
                else ListUiState.LoadMoreError(message = it.message?:it.toString())
            }
            .launchIn(viewModelScope)
    }

    fun refreshArticleList(cid:Int,orderType:Int) {
        getArticleList(page = 0, cid = cid, orderType = orderType)
    }

    fun loadMoreArticleList(cid:Int,orderType:Int) {
        getArticleList(cid = cid,page = articleListPageInfo.curPage,orderType = orderType)
    }

    companion object {
        private const val TAG = "ArticleListViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ArticleListViewModel(repository = ArticleListRepository.Instance)
            }
        }
    }
}