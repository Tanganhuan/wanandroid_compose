package com.go.navigate.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import kotlinx.coroutines.flow.Flow

class WendaRepository(private val wendaApi: WendaApi) {

    companion object {
        val Instance by lazy {
            WendaRepository(WendaApi.Instance)
        }
    }

    fun refresh(): Flow<ApiResponse<ArticlePageInfoBean>> {
        return getWendaList(page = 1)
    }

    fun getWendaList(page:Int): Flow<ApiResponse<ArticlePageInfoBean>> {
        return wendaApi.getWendaList(page)
    }
}