package com.go.navigate.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.mine.data.ArticlePageInfoBean
import com.go.common.data.ListUiState
import com.go.common.viewModel.BaseViewModel
import com.go.navigate.data.KnowledgeList
import com.go.navigate.data.KnowledgeSystemBean
import com.go.navigate.dataStore.KnowledgeListStore
import com.go.navigate.http.KnowledgeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take

class KnowledgeViewModel(private val knowledgeRepository: KnowledgeRepository) : BaseViewModel() {

    private var knowledgeListPageInfo: ArticlePageInfoBean = ArticlePageInfoBean()
    private var _knowledgeTreeUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    val knowledgeTreeUiState: State<ListUiState> = _knowledgeTreeUiState

    private var _knowledgeTree: MutableState<List<KnowledgeSystemBean>> =
        mutableStateOf(emptyList())
    val knowledgeTree: State<List<KnowledgeSystemBean>> = _knowledgeTree

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getKnowledgeTree() {
        KnowledgeListStore.Instance.read().take(1)
            .onStart {
            LogUtils.d(TAG, "getKnowledgeTree onStart")
            _knowledgeTreeUiState.value =
                ListUiState.Refreshing(isEmptyData = _knowledgeTree.value.isEmpty())
        }.flatMapConcat {
            if(it.data.isNotEmpty()) {
                _knowledgeTree.value = it.data
                _knowledgeTreeUiState.value = ListUiState.Refreshing(isEmptyData = it.data.isEmpty())
            }
            knowledgeRepository.getKnowledgeTree()
        }.onEach {
            LogUtils.d(TAG, "getKnowledgeTree onEach:$it")
            if (it.isSucceed()) {
                _knowledgeTreeUiState.value = ListUiState.LoadMoreSucceed(loadedCount = 0)
                _knowledgeTree.value = it.data.orEmpty()
                if(it.data.orEmpty().isNotEmpty()) {
                    KnowledgeListStore.Instance.write(bean = KnowledgeList(it.data.orEmpty()))
                }
            } else {
                _knowledgeTreeUiState.value = ListUiState.RefreshError(
                    message = it.errorMsg,
                    isEmptyData = _knowledgeTree.value.isEmpty()
                )
            }
        }.onCompletion {
            LogUtils.d(TAG, "getKnowledgeTree onCompletion:$it")
        }.catch {
            LogUtils.d(TAG, "getKnowledgeTree catch:$it")
            _knowledgeTreeUiState.value = ListUiState.RefreshError(
                isEmptyData = _knowledgeTree.value.isEmpty(),
                message = it.message ?: it.toString()
            )
        }
            .launchIn(viewModelScope)
    }

    fun getKnowledgeList(page: Int, cid: Int) {

    }

    fun refreshKnowledgeList(cid: Int) {
        getKnowledgeList(page = 0, cid = cid)
    }

    fun loadMoreKnowledgeList(cid: Int) {
        getKnowledgeList(page = knowledgeListPageInfo.curPage, cid = cid)
    }

    companion object {
        private const val TAG = "KnowledgeViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                KnowledgeViewModel(knowledgeRepository = KnowledgeRepository.Instance)
            }
        }
    }
}