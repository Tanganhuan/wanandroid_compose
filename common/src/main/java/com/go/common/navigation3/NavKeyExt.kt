package com.go.common.navigation3

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import com.go.common.http.HttpConfig
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class WebViewNavKey(
    val url:String,
    val articleId: Int? = null,
    val isCollect: Boolean = false
): NavKey, Parcelable {

    companion object {
        fun create(url: String,
                   articleId: Int? = null,
                   isCollect: Boolean = false
        ): WebViewNavKey {
            return WebViewNavKey(
                url = HttpConfig.handleWanandroidUrl(url),
                articleId = articleId,
                isCollect = isCollect
            )
        }
    }
}