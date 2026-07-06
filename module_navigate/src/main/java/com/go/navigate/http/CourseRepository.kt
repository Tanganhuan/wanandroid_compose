package com.go.navigate.http

import com.go.mine.data.ArticleBean
import com.go.common.http.ApiResponse
import com.go.navigate.data.toArticleBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CourseRepository(private val courseApi: CourseApi) {

    companion object {
        private const val TAG = "CourseRepositoryTAG"
        val Instance by lazy {
            CourseRepository(CourseApi.Instance)
        }
    }

    fun getCourseList(): Flow<ApiResponse<List<ArticleBean>>> {
        return courseApi.getCourseList().map { courseList ->
            if(courseList.isSucceed()) {
                courseList.data?.let {
                    ApiResponse.create(data = it.map {
                        it.toArticleBean()
                    })
                }?:ApiResponse.create(emptyList())

            } else {
                ApiResponse(
                    data = emptyList(),
                    errorMsg = courseList.errorMsg,
                    errorCode = courseList.errorCode)
            }
        }
    }

}