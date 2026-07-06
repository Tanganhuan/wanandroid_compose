package com.go.common.http

import java.net.URL

object HttpConfig {
    const val HTTP_PROTOCOL = "http"
    const val HTTPS_PROTOCOL = "https"

    const val BASE_URL = "https://wanandroid.com"
    const val USER_LOGIN = "${BASE_URL}/user/login"
    const val USER_LOGOUT = "${BASE_URL}/user/logout/json"
    const val OUT_DATED_HOST = "www.wanandroid.com"
    const val NEW_HOST = "wanandroid.com"

    const val ALL_ROUTE = "https://wanandroid.com/route/list"
    fun showRoute(id:Any):String = "https://wanandroid.com/route/show/$id"

    const val ALL_WENDA = "https://wanandroid.com/wenda"
    fun showWenda(id:Any) = "https://www.wanandroid.com/wenda/show/$id"

    fun showColumn(cid:Any) = "https://wanandroid.com/article/list/0?cid=$cid"

    fun toolsIcon(icon:String) = "$BASE_URL/resources/image/pc/tools/$icon"

    fun handleWanandroidUrl(url:String):String {
        return  runCatching {
            var finalUrl = url
            if(!url.startsWith(HTTP_PROTOCOL) &&
                !url.contains(NEW_HOST)
                && !url.contains(OUT_DATED_HOST)) {
                finalUrl = "${BASE_URL}${url}"
            }
            val newUrl = URL(finalUrl)
            if(newUrl.host.startsWith(OUT_DATED_HOST)) {
                finalUrl = finalUrl.replace(OUT_DATED_HOST, NEW_HOST)
            }
            if(newUrl.protocol == HTTP_PROTOCOL) {
                finalUrl = finalUrl.replace(HTTP_PROTOCOL, HTTPS_PROTOCOL)
            }
            finalUrl
        }.getOrNull()?:url
    }
}