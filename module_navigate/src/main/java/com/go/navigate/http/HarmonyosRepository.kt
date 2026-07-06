package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.navigate.data.HarmonyosBean
import kotlinx.coroutines.flow.Flow

class HarmonyosRepository(val harmonyosApi: HarmonyosApi) {


    fun refresh(): Flow<ApiResponse<HarmonyosBean>> {
        return harmonyosApi.getHarmonyArticleList()
    }

    companion object {
        private const val TAG = "ProjectRepositoryTAG"
        val Instance by lazy {
            HarmonyosRepository(HarmonyosApi.Instance)
        }
    }
}