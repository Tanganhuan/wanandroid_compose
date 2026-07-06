package com.go.navigate.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

interface WendaApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(WendaApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /** 问答
     * page 从 1 开始。
     * */
    @GET("wenda/list/{page}/json")
    fun getWendaList(@Path("page") page:Int=1): Flow<ApiResponse<ArticlePageInfoBean>>
}