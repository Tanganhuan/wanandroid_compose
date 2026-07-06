package com.go.navigate.data

import android.os.Parcelable
import com.go.mine.data.ArticleBean
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class CourseBean(
//    val articleList: List<Any>,
    val author: String,
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

fun CourseBean.toArticleBean():ArticleBean {
    return ArticleBean(
        id = id,
        link = "",
        shareUser = "",
        author = author,
        niceDate = "",
        envelopePic = cover,
        title = name,
        desc = desc)
}