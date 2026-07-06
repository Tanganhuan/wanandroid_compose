package com.go.navigate.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


val emptySearchHotkeyList = SearchHotkeyList()

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class SearchHotkeyBean(
    val id: Int = -1,
    val link: String = "",
    val name: String = "",
    val order: Int = -1,
    val visible: Int = -1,
): Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class SearchHotkeyList(
    val data: List<SearchHotkeyBean> = emptyList()
): Parcelable
