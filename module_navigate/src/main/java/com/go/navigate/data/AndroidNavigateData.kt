package com.go.navigate.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

val emptyAndroidNavigate = AndroidNavigateBean()
@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class AndroidNavigateBean(
    val articles: List<NavigateBean> = emptyList(),
    val cid: Int = -1,
    val name: String = "",
): Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class NavigateBean(
    val link: String = "",
    val title: String = "",
): Parcelable


@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class AndroidNavigateTreeBean(
    val data: List<AndroidNavigateBean> = emptyList(),
): Parcelable