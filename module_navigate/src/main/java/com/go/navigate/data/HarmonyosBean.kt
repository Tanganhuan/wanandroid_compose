package com.go.navigate.data

import com.go.mine.data.ArticleBean
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import android.os.Parcelable
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class HarmonyosBean(
    val links: ChapterBean = ChapterBean(),
    @Json(name = "open_sources")
    val openSources: ChapterBean = ChapterBean(),
    val tools: ChapterBean = ChapterBean()
): Parcelable


@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class ChapterBean(
    val articleList: List<ArticleBean> = emptyList(),
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
