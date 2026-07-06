package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.navigate.data.AndroidNavigateBean
import kotlinx.coroutines.flow.Flow

class AndroidNavigateRepository(private val api: AndroidNavigateApi) {

    companion object {
        private const val TAG = "ArticleListRepositoryTAG"
        val Instance by lazy {
            AndroidNavigateRepository(AndroidNavigateApi.Instance)
        }
    }

    fun getAndroidNavigateData(): Flow<ApiResponse<List<AndroidNavigateBean>>> {
        return api.getAndroidNavigateData()
    }
}