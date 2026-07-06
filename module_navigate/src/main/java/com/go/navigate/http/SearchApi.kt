package com.go.navigate.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface SearchApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(SearchApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /** 搜索
     * page 从0开始
     * */
    @FormUrlEncoded
    @POST("article/query/{page}/json")
    fun searchArticleList(
        @Path("page") page: Int = 0,
        @Field("k") keyword: String
    ): Flow<ApiResponse<ArticlePageInfoBean>>

}