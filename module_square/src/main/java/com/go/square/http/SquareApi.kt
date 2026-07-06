package com.go.square.http

import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.mine.data.ArticlePageInfoBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

interface SquareApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(SquareApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /**  广场列表数据
     * page从0开始 */
    @GET("user_article/list/{page}/json")
    fun userArticleList(@Path("page") page:Int = 0): Flow<ApiResponse<ArticlePageInfoBean>>
}