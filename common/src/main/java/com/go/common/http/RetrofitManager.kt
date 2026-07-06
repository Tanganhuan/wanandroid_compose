package com.go.common.http

import com.blankj.utilcode.util.LogUtils
import com.coder.vincent.sharp_retrofit.call_adapter.flow.FlowCallAdapterFactory
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.go.common.BaseApplication
import com.go.common.moshi.moshi
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


/** 请求超时时间 */
private const val TIME_OUT_SECONDS = 60L
/** OkHttpClient相关配置 */
val okHttpClient: OkHttpClient
    get() = OkHttpClient.Builder()
        // 请求过滤器
        .addInterceptor(WanAndroidUrlInterceptor())
        .addInterceptor(logInterceptor)
        //设置缓存配置,缓存最大10M,设置了缓存之后可缓存请求的数据到目录中
        .cache(Cache(BaseApplication.Instance.getExternalFilesDir("net_cache")?:BaseApplication.Instance.cacheDir, 50 * 1024 * 1024))
        // 请求超时时间
        .connectTimeout(TIME_OUT_SECONDS,TimeUnit.SECONDS)
        .readTimeout(TIME_OUT_SECONDS,TimeUnit.SECONDS)
        .writeTimeout(TIME_OUT_SECONDS,TimeUnit.SECONDS)
        .cookieJar(cookieJar)
        .build()

/** 请求cookie */
val cookieJar: PersistentCookieJar by lazy {
    WebViewPersistentCookieJar(
        SetCookieCache(),
        SharedPrefsCookiePersistor(BaseApplication.Instance)
    )
}

object RetrofitManager {

    /**
     * Retrofit相关配置
     */
    fun <T> getService(serviceClass: Class<T>, baseUrl: String): T {
        LogUtils.d("getService serviceClass:${serviceClass}\tbaseUrl:${baseUrl}")

        return Retrofit.Builder()
            .client(okHttpClient)
            // 使用Moshi更适合Kotlin
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(FlowCallAdapterFactory.create(true))
            .baseUrl(baseUrl)
            .build()
            .create(serviceClass)
    }
}