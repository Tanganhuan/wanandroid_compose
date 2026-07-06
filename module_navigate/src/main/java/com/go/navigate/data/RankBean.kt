package com.go.navigate.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class RankBean(
    val coinCount: Int = -1,
    val level: Int = -1,
    val nickname: String = "",
    val rank: String = "",
    val userId: Int = -1,
    val username: String = "",

): Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class RankPageBean(
    val curPage: Int = -1,
    val datas: List<RankBean> = emptyList(),
    val offset: Int = -1,
    val over: Boolean = false,
    val pageCount: Int = -1,
    val size: Int = -1,
    val total: Int = -1,
): Parcelable
