package com.go.navigate.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.mine.data.ArticleBean
import com.go.common.viewModel.BaseListViewModel
import com.go.navigate.http.SearchRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class SearchViewModel(private val repository: SearchRepository): BaseListViewModel<ArticleBean>() {

    protected override val defaultPageStart: Int = 0

    private fun searchArticleList(page:Int,keyword:String) {
        repository.searchArticleList(page = page,keyword = keyword)
            .onStart {
                LogUtils.d(TAG,"searchArticleList onStart")
                updateLoadingState(curPage = page)
            }.onEach {
                LogUtils.d(TAG,"searchArticleList onEach:$it")
                if(it.isSucceed()) {
                    updateSucceedState(curPage = page, loadedCount = it.data?.datas.orEmpty().size)
                    updateCurPage(page = page)
                    updateDataList(curPage = page,list = it.data?.datas.orEmpty())
                } else {
                    updateErrorState(curPage = page, errorMsg = it.errorMsg)
                }
            }.onCompletion {
                LogUtils.d(TAG,"searchArticleList onCompletion:$it\tlistUiState:${listUiState}")
            }.catch {
                LogUtils.d(TAG,"searchArticleList catch:$it")
                updateErrorState(curPage = page, errorMsg = it.message?:it.toString())
            }
            .launchIn(viewModelScope)
    }

    fun refresh(keyword: String) {
        searchArticleList(page = defaultPageStart, keyword = keyword)
    }

    fun loadMore(keyword: String) {
        searchArticleList(page = curPage+1, keyword = keyword)
    }

    companion object {
        private const val TAG = "SearchViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(repository = SearchRepository.Instance)
            }
        }
    }
}