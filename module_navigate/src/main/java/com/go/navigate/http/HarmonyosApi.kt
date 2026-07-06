package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.navigate.data.HarmonyosBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface HarmonyosApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(HarmonyosApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /** 鸿蒙专栏 */
    @GET("harmony/index/json")
    fun getHarmonyArticleList(): Flow<ApiResponse<HarmonyosBean>>
}