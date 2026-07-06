package com.go.common.http

import com.blankj.utilcode.util.LogUtils
import com.go.common.BuildConfig

/**
 * okhttp 日志拦截器
 */
private const val TAG = "HttpLoggingInterceptorTAG"
internal val logInterceptor = CustomHttpLoggingInterceptor(logger = object : CustomHttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        LogUtils.d(TAG,message)
    }
}).setLevel(if (BuildConfig.DEBUG) CustomHttpLoggingInterceptor.Level.BODY else CustomHttpLoggingInterceptor.Level.BASIC)