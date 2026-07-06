package com.go.square.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import kotlinx.coroutines.flow.Flow

class SquareRepository(private val squareApi: SquareApi) {
    companion object {
        private const val TAG = "SquareRepositoryTAG"
        val Instance by lazy {
            SquareRepository(SquareApi.Instance)
        }
    }

    fun refresh(): Flow<ApiResponse<ArticlePageInfoBean>> {
       return squareApi.userArticleList()
    }

    fun loadMoreData(page:Int): Flow<ApiResponse<ArticlePageInfoBean>> {
        return squareApi.userArticleList(page=page)
    }
}