package com.go.navigate.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.common.viewModel.BaseListViewModel
import com.go.navigate.data.SearchHotkeyBean
import com.go.navigate.data.SearchHotkeyList
import com.go.navigate.dataStore.SearchHotkeyListStore
import com.go.navigate.dataStore.SearchInputStore
import com.go.navigate.http.SearchInputRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList

class SearchInputViewModel(private val repository: SearchInputRepository) :
    BaseListViewModel<SearchHotkeyBean>() {

    fun refresh() {
        getSearchHotkey()
    }

    val searchInputList = SearchInputStore.Instance.read()

    fun addSearchInput(searchInput:String) {
        SearchInputStore.Instance.read().take(1).onStart {
            LogUtils.d(TAG,"addSearchInput onStart")
        }.onEach {
            val newList = listOf(SearchHotkeyBean(name = searchInput)) + it.data.filter {
                it.name != searchInput
            }

            LogUtils.d(TAG,"addSearchInput onEach newList:$newList")
            SearchInputStore.Instance.write(bean = SearchHotkeyList(newList))
        }.onCompletion {
            LogUtils.d(TAG,"addSearchInput onCompletion")
        }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSearchHotkey() {
        SearchHotkeyListStore.Instance.read().take(1)
            .onStart {
                LogUtils.d(TAG, "getSearchHotkey onStart")
            }.flatMapConcat {
                if (it.data.isNotEmpty()) {
                    updateDataList(it.data)
                }
                updateLoadingState(curPage = defaultPageStart)
                repository.getSearchHotkey()
            }.onEach {
                LogUtils.d(TAG, "getSearchHotkey onEach:$it")
                if (it.isSucceed()) {
                    SearchHotkeyListStore.Instance.write(bean = SearchHotkeyList(it.data.orEmpty()))
                    updateLoadEndState()
                    updateDataList(it.data.orEmpty())
                } else {
                    updateErrorState(curPage = defaultPageStart, errorMsg = it.errorMsg)
                }
            }.onCompletion {
                LogUtils.d(TAG, "getSearchHotkey onCompletion:$it")
            }.catch {
                LogUtils.d(TAG, "getSearchHotkey catch:$it")
                updateErrorState(curPage = defaultPageStart, errorMsg = it.message ?: it.toString())
            }.launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "SearchInputViewModel"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchInputViewModel(repository = SearchInputRepository.Instance)
            }
        }
    }
}