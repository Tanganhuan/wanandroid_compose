package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.navigate.data.SearchHotkeyBean
import kotlinx.coroutines.flow.Flow

class SearchInputRepository(private val searchInputApi: SearchInputApi) {

    fun getSearchHotkey(): Flow<ApiResponse<List<SearchHotkeyBean>>> {
        return searchInputApi.getSearchHotkey()
    }

    companion object {
        val Instance by lazy {
            SearchInputRepository(SearchInputApi.Instance)
        }
    }

}