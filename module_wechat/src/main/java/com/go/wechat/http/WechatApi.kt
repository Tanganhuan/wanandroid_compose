package com.go.wechat.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.wechat.data.WechatAuthorBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WechatApi {

    /** 公从号作者列表*/
    @GET("wxarticle/chapters/json")
    fun getWxAuthorList(): Flow<ApiResponse<List<WechatAuthorBean>>>

    @GET("wxarticle/list/{id}/{page}/json")
    fun getWxArticleList(@Path("id")id:Int,@Path("page") page:Int): Flow<ApiResponse<ArticlePageInfoBean>>

    @GET("wxarticle/list/{id}/{page}/json")
    fun searchWxArticleList(@Path("id")id:Int,@Path("page") page:Int,@Query("k")searchKey:String): Flow<ApiResponse<ArticlePageInfoBean>>

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(WechatApi::class.java, HttpConfig.BASE_URL)
        }
    }
}