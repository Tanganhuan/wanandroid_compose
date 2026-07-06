package com.go.home.http

import com.go.common.http.ApiResponse
import com.go.home.data.HomeHeadData
import com.go.mine.data.ArticlePageInfoBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf

class HomeRepository(private val homeApi: HomeApi) {

    companion object {
        private const val TAG = "HomeRepositoryTAG"
        val Instance by lazy {
            HomeRepository(HomeApi.Instance)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getHomeHeadData(): Flow<HomeHeadData> {
        var homeHeadData = HomeHeadData()
        return homeApi.bannerList().flatMapConcat {
            homeHeadData = homeHeadData.copy(banner = it)
            homeApi.popularRouteList()
        }.flatMapConcat {
            homeHeadData = homeHeadData.copy(popularRoute = it)
            homeApi.popularWendaList()
        }.flatMapConcat {
            homeHeadData = homeHeadData.copy(popularWenda = it)
            homeApi.popularColumnList()
        }.flatMapConcat {
            homeHeadData = homeHeadData.copy(popularColumn = it)
            homeApi.articleTopList()
        }.flatMapConcat {
            homeHeadData = homeHeadData.copy(articleTopList = it)
            flowOf(homeHeadData)
        }
    }

    fun getArticleList(pageId: Int = 0): Flow<ApiResponse<ArticlePageInfoBean>> {
        return homeApi.articleList(pageId)
    }
}