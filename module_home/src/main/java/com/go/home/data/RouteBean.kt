package com.go.home.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class RouteBean(
//    val articleList: List<Any>,
    val author: String = "",
//    val children: List<Any>,
    val courseId: Int = -1,
    val cover: String = "",
    val desc: String = "",
    val id: Int = -1,
    val lisense: String = "",
    val lisenseLink: String = "",
    val name: String = "",
    val order: Int = -1,
    val parentChapterId: Int = -1,
    val type: Int = -1,
    val userControlSetTop: Boolean = false,
    val visible: Int = -1
): Parcelable