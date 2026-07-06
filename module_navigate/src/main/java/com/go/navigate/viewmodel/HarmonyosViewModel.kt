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
import com.go.navigate.data.HarmonyosBean
import com.go.navigate.http.HarmonyosRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class HarmonyosViewModel(val repository: HarmonyosRepository): BaseViewModel() {

    private var _listUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    val listUiState: State<ListUiState> = _listUiState

    private var _harmonyosData = mutableStateOf(HarmonyosBean())
    val harmonyosData: State<HarmonyosBean> = _harmonyosData

    fun refresh() {
        repository.refresh()
            .onStart {
                LogUtils.d(TAG,"refresh start")
                _listUiState.value = ListUiState.Refreshing(isEmptyData = isEmpty())
            }.onEach {
                if(it.isSucceed()) {
                    _listUiState.value = ListUiState.LoadMoreSucceed(loadedCount = 0)
                    it.data?.let { data ->
                        _harmonyosData.value = data
                    }
                } else {
                    _listUiState.value = ListUiState.RefreshError(isEmptyData = isEmpty(),message = it.errorMsg)
                }
                LogUtils.d(TAG,"refresh onEach:$it")
            }.onCompletion {
                LogUtils.d(TAG,"refresh onCompletion:$it\t_harmonyosData:${_harmonyosData}")
            }.catch {
                LogUtils.d(TAG,"refresh catch:$it")
                _listUiState.value = ListUiState.RefreshError(isEmptyData = isEmpty(),message = it.message?:it.toString())
            }.launchIn(viewModelScope)
    }

    private fun isEmpty(): Boolean = _harmonyosData.value.links.articleList.isEmpty()

    companion object {
        private const val TAG = "ProjectViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HarmonyosViewModel(repository = HarmonyosRepository.Instance)
            }
        }
    }
}