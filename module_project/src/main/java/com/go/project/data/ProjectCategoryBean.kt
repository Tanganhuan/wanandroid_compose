package com.go.project.data

data class ProjectCategoryBean(
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
)