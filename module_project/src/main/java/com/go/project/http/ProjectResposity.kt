package com.go.project.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import com.go.project.data.ProjectCategoryBean
import kotlinx.coroutines.flow.Flow

class ProjectRepository(val projectApi: ProjectApi) {

    companion object {
        private const val TAG = "ProjectRepositoryTAG"
        val Instance by lazy {
            ProjectRepository(ProjectApi.Instance)
        }
    }

    fun getProjectTree(): Flow<ApiResponse<List<ProjectCategoryBean>>> {
        return projectApi.projectTree()
    }

    fun getProjectList(page:Int=1,cid:Int): Flow<ApiResponse<ArticlePageInfoBean>> {
       return projectApi.projectList(page = page,cid = cid.toString())
    }
}