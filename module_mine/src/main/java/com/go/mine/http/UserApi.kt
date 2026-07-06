package com.go.mine.http

import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.common.http.ApiResponse
import com.go.mine.data.ArticleBean
import com.go.mine.data.ArticlePageInfoBean
import com.go.mine.data.UserBean
import com.go.mine.data.UserCoinInfo
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(UserApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /** 登录 */
    @FormUrlEncoded
    @POST("user/login")
    fun login(
        @Field("username") username: String,
        @Field("password") pwd: String
    ): Flow<ApiResponse<UserBean>>

    /**
     * 获取个人积分
     * */
    @GET("lg/coin/userinfo/json")
    fun getUserinfo(): Flow<ApiResponse<UserCoinInfo>>

    /** 注册 */
    @FormUrlEncoded
    @POST("user/register")
    fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): Flow<ApiResponse<Any?>>

    /** 退出登陆 */
    @GET("user/logout/json")
    fun logout(): Flow<ApiResponse<Any?>>

    /** 收藏站外文章 */
    @FormUrlEncoded
    @POST("lg/collect/add/json")
    fun addCollectArticle(
        @Field("title") title: String,
        @Field("author") author: String,
        @Field("link") link: String
    ): Flow<ApiResponse<Any?>>

    /** 收藏站内文章 */
    @POST("lg/collect/{id}/json")
    fun addCollectArticle(
        @Path("id") id: Int,
    ): Flow<ApiResponse<Any?>>

    /** 取消收藏站内文章 */
    @POST("lg/uncollect/{id}/json")
    fun unCollectArticle(
        @Path("id") id: Int,
        @Query("originId") originId: Int = -1
    ): Flow<ApiResponse<Any?>>


    /** 取消收藏站内文章 */
    @POST("lg/uncollect_originId/{originId}/json")
    fun unCollectArticle(
        @Path("originId") originId: Int,
    ): Flow<ApiResponse<Any?>>


    /**
     * 收藏站外文章列表
     * page从0开始。
     * */
    @GET("lg/collect/list/{page}/json")
    fun getCollectList(@Path("page")page:Int): Flow<ApiResponse<ArticlePageInfoBean>>

    /**
     * 收藏站内文章列表
     * page从0开始。
     * */
    @GET("lg/collect/usertools/json")
    fun getCollectArticleList(): Flow<ApiResponse<List<ArticleBean>>>
}