package com.go.navigate.http

import com.go.common.http.ApiResponse
import com.go.common.http.HttpConfig
import com.go.common.http.RetrofitManager
import com.go.navigate.data.SearchHotkeyBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface SearchInputApi {

    companion object {
        val Instance by lazy {
            RetrofitManager.getService(SearchInputApi::class.java, HttpConfig.BASE_URL)
        }
    }

    @GET("hotkey/json")
    fun getSearchHotkey(): Flow<ApiResponse<List<SearchHotkeyBean>>>
}