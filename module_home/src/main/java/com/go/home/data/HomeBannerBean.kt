package com.go.home.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class HomeBannerBean(
    val desc: String = "",
    val id: Int = -1,
    val imagePath: String = "",
    val isVisible: Int = -1,
    val order: Int = -1,
    val title: String = "",
    val type: Int = -1,
    val url: String = ""
): Parcelable