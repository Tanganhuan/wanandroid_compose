package com.go.navigate.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.common.data.ListUiState
import com.go.common.viewModel.BaseViewModel
import com.go.navigate.data.ToolKitBean
import com.go.navigate.http.ToolKitRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class ToolKitViewModel(private val repository: ToolKitRepository): BaseViewModel() {

    private var _listUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    val listUiState: State<ListUiState> = _listUiState

    private var _toolKitList: MutableState<List<ToolKitBean>> = mutableStateOf(emptyList())
    val toolKitList: State<List<ToolKitBean>> = _toolKitList

    fun getToolsList() {
        if(_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore) {
            return
        }
        repository.getToolsList()
            .onStart {
                LogUtils.d(TAG,"getToolsList onStart")
                _listUiState.value = ListUiState.Refreshing(isEmptyData = _toolKitList.value.isEmpty())
            }.onEach {
                LogUtils.d(TAG,"getToolsList onEach:$it")
                if(it.isSucceed()) {
                    _listUiState.value = ListUiState.LoadMoreSucceed(loadedCount = 0)
                    it.data?.let {
                        _toolKitList.value = it
                    }
                } else {
                    _listUiState.value = ListUiState.RefreshError(message = it.errorMsg, isEmptyData = _toolKitList.value.isEmpty())
                }
            }.onCompletion {
                LogUtils.d(TAG,"getToolsList onCompletion:$it")
            }.catch {
                LogUtils.d(TAG,"getToolsList catch:$it")
                _listUiState.value = ListUiState.RefreshError(message = it.message?:it.toString(), isEmptyData = _toolKitList.value.isEmpty())
            }
            .launchIn(viewModelScope)
    }


    companion object {
        private const val TAG = "ToolKitViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ToolKitViewModel(repository = ToolKitRepository.Instance)
            }
        }
    }
}