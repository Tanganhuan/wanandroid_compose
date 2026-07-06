package com.go.mine.data

// 登录页面的 UI 状态
sealed class LoginUiState {
    // 初始空闲状态
    object Idle : LoginUiState()

    // 正在登录加载中
    object Logging : LoginUiState()

    // 正在登录加载中
    object Logouting : LoginUiState()
    object LogoutSucceed : LoginUiState()

    // 登录成功，携带用户信息或跳转标识
    data object LoggedIn : LoginUiState()

    // 登录失败，携带错误提示
    data class Error(val errorMessage: String) : LoginUiState()
}