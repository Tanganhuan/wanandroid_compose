package com.go.project.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.project.data.ProjectCategoryBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProjectApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(ProjectApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /** 项目分类 */
    @GET("project/tree/json")
    fun projectTree(): Flow<ApiResponse<List<ProjectCategoryBean>>>

    /**  项目列表数据
     * page 从1开始 */
    @GET("project/list/{page}/json")
    fun projectList(@Path("page") page:Int = 1,@Query("cid") cid: String): Flow<ApiResponse<ArticlePageInfoBean>>

}