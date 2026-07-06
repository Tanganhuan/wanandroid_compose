package com.go.mine.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.go.common.viewModel.BaseViewModel
import com.go.mine.data.RegisterUiState
import com.go.mine.http.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(val repository: UserRepository) : BaseViewModel() {

    private var _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun register(username: String, password: String, repassword: String) {
        if(_uiState.value == RegisterUiState.InProgress) {
            return
        }

        _uiState.value = RegisterUiState.InProgress
        viewModelScope.launch {
            repository.register(username = username,
                password = password,
                repassword = repassword).collect {
                    if(it.isSucceed()) {
                        _uiState.value = RegisterUiState.Succeed
                    } else {
                        _uiState.value = RegisterUiState.Error(it.errorMsg)
                    }
            }
        }
    }

    companion object {
        private const val TAG = "UserViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegisterViewModel(repository = UserRepository.Instance)
            }
        }
    }
}