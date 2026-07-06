package com.go.wechat.http

import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.ApiResponse
import com.go.wechat.data.WechatAuthorBean
import kotlinx.coroutines.flow.Flow

class WechatRepository(val wechatApi: WechatApi) {

    fun getWxAuthorList(): Flow<ApiResponse<List<WechatAuthorBean>>> {
        return wechatApi.getWxAuthorList()
    }

    fun getWxArticleList(id:Int,page:Int=1): Flow<ApiResponse<ArticlePageInfoBean>> {
        return wechatApi.getWxArticleList(id=id,page=page)
    }

    companion object {
        val Instance by lazy {
            WechatRepository(wechatApi = WechatApi.Instance)
        }
    }
}