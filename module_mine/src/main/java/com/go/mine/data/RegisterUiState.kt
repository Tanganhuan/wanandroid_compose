package com.go.mine.data

sealed class RegisterUiState {
    // 初始空闲状态
    object Idle : RegisterUiState()

    // 正在注册中
    object InProgress : RegisterUiState()

    object Succeed : RegisterUiState()

    // 登录失败，携带错误提示
    data class Error(val errorMessage: String) : RegisterUiState()
}