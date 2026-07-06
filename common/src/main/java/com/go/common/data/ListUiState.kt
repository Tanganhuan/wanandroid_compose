package com.go.common.data

sealed interface ListUiState {

    data object Idel : ListUiState // 空闲中
    data class Refreshing(val isEmptyData:Boolean) : ListUiState // 下拉刷新中
    data class RefreshSucceed(val loadedCount:Int) : ListUiState // 刷新加载成功
    data class RefreshError(val isEmptyData:Boolean,val message: String) : ListUiState // 刷新加载失败

    data object LoadingMore : ListUiState // 上拉加载更多中
    data class LoadMoreSucceed(val loadedCount:Int) : ListUiState // 上拉加载成功
    data class LoadMoreError(val message: String) : ListUiState // 上拉加载失败
}
