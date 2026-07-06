package com.go.home.data

import com.go.common.http.ApiResponse
import com.go.mine.data.ArticleBean
import com.go.mine.data.ArticlePageInfoBean
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable
import kotlin.collections.List

val emptyHomeHeadData = HomeHeadData()

@Serializable
@JsonClass(generateAdapter = true)
data class HomeHeadData(
    val banner: ApiResponse<List<HomeBannerBean>> = ApiResponse.create(emptyList()),
    val popularRoute: ApiResponse<List<RouteBean>> = ApiResponse.create(emptyList()),
    val popularWenda: ApiResponse<List<ArticleBean>> = ApiResponse.create(emptyList()),
    val popularColumn: ApiResponse<List<ColumnBean>> = ApiResponse.create(emptyList()),
    val articleTopList: ApiResponse<List<ArticleBean>> = ApiResponse.create(emptyList()),
    val articleList: ApiResponse<ArticlePageInfoBean> = ApiResponse.create(ArticlePageInfoBean()),
)

fun HomeHeadData.isEmpty():Boolean {
    return (banner.data.orEmpty().isEmpty())
            ||(popularRoute.data.orEmpty().isEmpty())
            ||(popularWenda.data.orEmpty().isEmpty())
            ||(popularColumn.data.orEmpty().isEmpty())
            ||(articleTopList.data.orEmpty().isEmpty())
            ||(articleList.data?.datas.orEmpty().isEmpty())
}