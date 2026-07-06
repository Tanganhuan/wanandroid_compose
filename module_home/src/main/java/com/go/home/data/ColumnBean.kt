package com.go.home.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.String

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class ColumnBean(
    val chapterId: Int = -1,
    val chapterName: String = "",
    val columnId: Int = -1,
    val id: Int = -1,
    val name: String = "",
    val subChapterId: Int = -1,
    val subChapterName: String = "",
    val url: String = "",
    val userId: Int = -1,
): Parcelable