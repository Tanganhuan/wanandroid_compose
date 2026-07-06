package com.go.mine.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class ArticleBean(
    val adminAdd: Boolean = false,
    val apkLink: String = "",
    val audit: Int = -1,
    val author: String = "",
    val canEdit: Boolean = false,
    val chapterId: Int = -1,
    val chapterName: String = "",
    val collect: Boolean = false,
    val courseId: Int = -1,
    val desc: String = "",
    val descMd: String = "",
    val envelopePic: String = "",
    val fresh: Boolean = false,
    val host: String = "",
    val id: Int = -1,
    val isAdminAdd: Boolean = false,
    val link: String = "",
    val niceDate: String = "",
    val niceShareDate: String = "",
    val origin: String = "",
    val originId: Int = -1,
    val prefix: String = "",
    val projectLink: String = "",
    val publishTime: Long = -1L,
    val realSuperChapterId: Int = -1,
    val selfVisible: Int = -1,
    val shareDate: Long? = -1L,
    val shareUser: String = "",
    val superChapterId: Int = -1,
    val superChapterName: String = "",
//    val tags: List<Any>,
    val title: String = "",
    val type: Int = -1,
    val userId: Int = -1,
    val visible: Int = -1,
    val zan: Int = -1,
): Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class ArticlePageInfoBean (
    val curPage: Int = 0,
    val datas: List<ArticleBean> = emptyList(),
    val offset: Int = 0,
    val over: Boolean = false,
    val pageCount: Int = -1,
    val size: Int = -1,
    val total: Int = -1,
    val loadedCount:Int = -1,
): Parcelable
