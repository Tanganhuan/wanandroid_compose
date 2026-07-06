package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.navigate.data.RankPageBean
import kotlinx.coroutines.flow.Flow

class RankListRepository(private val rankListApi: RankListApi) {

    companion object {
        val Instance by lazy {
            RankListRepository(RankListApi.Instance)
        }
    }

    fun getRankList(page:Int): Flow<ApiResponse<RankPageBean>> {
        return rankListApi.getRankList(page = page)
    }
}