package com.go.navigate.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import kotlinx.coroutines.flow.Flow


class ArticleListRepository(private val articlerListApi: ArticlerListApi) {

    companion object {
        private const val TAG = "ArticleListRepositoryTAG"
        val Instance by lazy {
            ArticleListRepository(ArticlerListApi.Instance)
        }
    }

    fun getArticlerList(cid:Int, page:Int, author:String?=null, orderType:Int?=0): Flow<ApiResponse<ArticlePageInfoBean>> {
        return articlerListApi.getArticlerList(page = page,cid=cid,author = author,orderType=orderType)
    }
}