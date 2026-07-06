package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.navigate.data.KnowledgeSystemBean
import kotlinx.coroutines.flow.Flow

class KnowledgeRepository(private val knowledgeSystemApi: KnowledgeSystemApi) {

    companion object {
        private const val TAG = "KnowledgeRepositoryTAG"
        val Instance by lazy {
            KnowledgeRepository(KnowledgeSystemApi.Instance)
        }
    }

    fun getKnowledgeTree(): Flow<ApiResponse<List<KnowledgeSystemBean>>> {
        return knowledgeSystemApi.knowledgeTree()
    }
}