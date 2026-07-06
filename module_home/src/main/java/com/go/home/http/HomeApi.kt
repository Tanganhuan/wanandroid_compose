package com.go.home.http

import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.home.data.ColumnBean
import com.go.home.data.HomeBannerBean
import com.go.home.data.RouteBean
import com.go.mine.data.ArticleBean
import com.go.mine.data.ArticlePageInfoBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(HomeApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /** 首页banner */
    @GET("banner/json")
    fun bannerList(): Flow<ApiResponse<List<HomeBannerBean>>>

    /** 热门问答 */
    @GET("popular/wenda/json")
    fun popularWendaList(): Flow<ApiResponse<List<ArticleBean>>>

    /** 所有问答
     * pageId 从 1 开始*/
    @GET("wenda/list/{pageId}/json")
    fun wendaList(@Path("pageId") page:Int): Flow<ApiResponse<List<ArticleBean>>>

    /** 热门专栏 */
    @GET("popular/column/json")
    fun popularColumnList(): Flow<ApiResponse<List<ColumnBean>>>

    /** 热门路线 */
    @GET("popular/route/json")
    fun popularRouteList(): Flow<ApiResponse<List<RouteBean>>>

    /** 置顶文章 */
    @GET("article/top/json")
    fun articleTopList(): Flow<ApiResponse<List<ArticleBean>>>

    /** 首页文章列表
     * 从0开始。*/
    @GET("article/list/{pageId}/json")
    fun articleList(@Path("pageId") pageId:Int = 0): Flow<ApiResponse<ArticlePageInfoBean>>

}