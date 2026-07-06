package com.go.navigate.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.navigate.data.KnowledgeSystemBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KnowledgeSystemApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(KnowledgeSystemApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /**
     * 体系数据
     * */
    @GET("tree/json")
    fun knowledgeTree(): Flow<ApiResponse<List<KnowledgeSystemBean>>>

    /**
     * 单个教程下所有文章列表
     * page 从 0 开始
     * */
    @GET("article/list/{page}/json")
    fun getKnowledgeList(@Path("page")page:Int,@Query("cid")cid: Int): Flow<ApiResponse<ArticlePageInfoBean>>

}