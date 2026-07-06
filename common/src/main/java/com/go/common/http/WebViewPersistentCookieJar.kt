package com.go.common.http

import android.webkit.CookieManager
import com.blankj.utilcode.util.LogUtils
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import okhttp3.Cookie
import okhttp3.HttpUrl

class WebViewPersistentCookieJar(
    cache: CookieCache,
    persistor: CookiePersistor,
): PersistentCookieJar(cache,persistor) {

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        super.saveFromResponse(url, cookies)

        if(url.toString() == HttpConfig.USER_LOGIN || url.toString()== HttpConfig.USER_LOGOUT) {
            LogUtils.d(TAG,"saveFromResponse url:$url\tcookies:$cookies")
            setToWebkitCookieManager(cookies, url)
        }
    }

    override fun clear() {
        super.clear()
    }

    private fun setToWebkitCookieManager(cookies: List<Cookie>, url: HttpUrl) {

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            cookies.filter { it.persistent }.forEach { cookie ->
                // 将 OkHttp 的 Cookie 转换为 WebView 可识别的格式并设置
                val cookieValue: String = cookie.name + "=" + cookie.value +
                        "; domain=" + cookie.domain +
                        "; path=" + cookie.path
                LogUtils.d(TAG, "saveFromResponse url:$url\tcookieValue:$cookieValue")
                setCookie(url.toUrl().toString(), cookieValue)
            }
            flush()
        }
    }

    companion object {
        const val TAG = "WebViewPersistentCookieJarTAG"
    }
}

