package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.navigate.data.ToolKitBean
import kotlinx.coroutines.flow.Flow

class ToolKitRepository(private val toolKitApi: ToolKitApi) {

    companion object {
        val Instance by lazy {
            ToolKitRepository(ToolKitApi.Instance)
        }
    }

    fun getToolsList(): Flow<ApiResponse<List<ToolKitBean>>> {
        return toolKitApi.getToolsList()
    }
}