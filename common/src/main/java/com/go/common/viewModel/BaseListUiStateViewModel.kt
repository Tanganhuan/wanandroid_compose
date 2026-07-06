package com.go.common.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.go.common.data.ListUiState

abstract class BaseListUiStateViewModel: BaseViewModel() {

    private var _listUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    val listUiState by _listUiState

    fun updateListUiState(listUiState:ListUiState) {
        _listUiState.value = listUiState
    }

    fun resetListUiState() {
        _listUiState.value = ListUiState.Idel
    }

    fun isLoading(): Boolean {
        return _listUiState.value is ListUiState.Refreshing
                || _listUiState.value is ListUiState.LoadingMore
    }

}