package com.go.common.data

sealed class RequestState(open val msg:String) {
    data class Loading(override val msg: String) : RequestState(msg)      // 请求中
    data class Success(override val msg: String) : RequestState(msg)     // 请求成功
    data class Error(override val msg: String) : RequestState(msg)       // 请求失败
    data object Dismiss : RequestState("")// 请求完成结束
}
