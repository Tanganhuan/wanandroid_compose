package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.navigate.data.CourseBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface CourseApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(CourseApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /** 教程列表 */
    @GET("chapter/547/sublist/json")
    fun getCourseList(): Flow<ApiResponse<List<CourseBean>>>


}