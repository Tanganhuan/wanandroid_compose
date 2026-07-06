package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.navigate.data.RankPageBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

interface RankListApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(RankListApi::class.java, HttpConfig.BASE_URL)
        }
    }

    /**
     * 积分排行榜接口
     * page 从1开始
     */
    @GET("coin/rank/{page}/json")
    fun getRankList(@Path("page")page:Int): Flow<ApiResponse<RankPageBean>>
}