package com.go.common.http

import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class ApiResponse<T>(
    val data: T?,
    val errorCode: Int = -1,
    val errorMsg: String = ""
) {
    fun isSucceed() = errorCode == 0 || data!=null

    companion object {
        fun<T> create(data:T): ApiResponse<T> {
            return ApiResponse(data)
        }
    }
}