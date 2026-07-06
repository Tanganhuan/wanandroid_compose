package com.go.navigate.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticlerListApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(ArticlerListApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /**
     * 文章列表
     * */
    @GET("article/list/{page}/json")
    fun getArticlerList(
        //分页 page 从 0 开始，或者是从1开始的都有，要区分情况
        @Path("page")page:Int,
        //知识体系或者教程的id,如果为null,则是首页文章。
        @Query("cid")cid:Int,
        //按照作者昵称搜索文章
        @Query("author")author:String?=null,
        //正序，传入 order_type=1   倒序，传入 order_type=0
        @Query("order_type")orderType:Int?=1
    ): Flow<ApiResponse<ArticlePageInfoBean>>
}