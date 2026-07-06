package com.go.common.http

import com.go.common.http.HttpConfig.NEW_HOST
import com.go.common.http.HttpConfig.OUT_DATED_HOST
import okhttp3.Interceptor
import okhttp3.Response

class WanAndroidUrlInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        return if(originalUrl.host.startsWith(OUT_DATED_HOST)) {
            // 2. 在这里拦截并修改最终的 HttpUrl
            // 例如：将旧域名替换为新域名
            val newUrl = originalUrl.newBuilder()
                .host(NEW_HOST)
                .build()
            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(newRequest)

        } else {
            chain.proceed(originalRequest)
        }
    }
}