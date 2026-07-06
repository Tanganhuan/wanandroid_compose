package com.go.navigate.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import kotlinx.coroutines.flow.Flow

class SearchRepository(private val searchApi: SearchApi) {

    companion object {
        val Instance by lazy {
            SearchRepository(SearchApi.Instance)
        }
    }

    fun searchArticleList(page:Int,keyword:String): Flow<ApiResponse<ArticlePageInfoBean>> {
        return searchApi.searchArticleList(page = page,keyword = keyword)
    }

}