package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.navigate.data.AndroidNavigateBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface AndroidNavigateApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(AndroidNavigateApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /**
     * 导航数据
     * */
    @GET("navi/json")
    fun getAndroidNavigateData(): Flow<ApiResponse<List<AndroidNavigateBean>>>
}