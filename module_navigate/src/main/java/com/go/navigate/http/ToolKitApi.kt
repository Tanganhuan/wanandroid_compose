package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.navigate.data.ToolKitBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface ToolKitApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(ToolKitApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /** 问答
     * page 从 1 开始。
     * */
    @GET("tools/list/json")
    fun getToolsList(): Flow<ApiResponse<List<ToolKitBean>>>

}