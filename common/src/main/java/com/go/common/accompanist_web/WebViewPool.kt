package com.go.common.accompanist_web

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Looper
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.blankj.utilcode.util.LogUtils

object WebViewPool {
    private const val TAG = "WebViewPoolTAG"
    private val pool = mutableListOf<WebView>()
    private const val MAX_SIZE = 2 // 最大缓存数量
    const val WARM_UP_HISTORY_URL = "file:///android_asset/warm_up_url.html"
    private val _webChromeClient = WebChromeClient()
    private val _webViewClient = WebViewClient()
    fun init(appContext: Context){
        LogUtils.d(TAG,"init appContext:$appContext pool.size:${pool.size}")
        if (pool.isEmpty()){
            Looper.getMainLooper().queue.addIdleHandler {
                pool.add(WebView(MutableContextWrapper(appContext)).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    webChromeClient = _webChromeClient
                    webViewClient = _webViewClient
                    loadUrl(WARM_UP_HISTORY_URL)
                })
                return@addIdleHandler false
            }
        }
    }

    fun bind(context: Context): WebView {
        LogUtils.d(TAG,"bind context:$context pool.size:${pool.size}")
        val webView = if(pool.isNotEmpty()) pool.removeAt(0).also {
            (it.context as MutableContextWrapper).baseContext = context
            it.clearHistory()
        } else WebView(MutableContextWrapper(context))
        return webView
    }

    fun recycle(webView: WebView){
        LogUtils.d(TAG,"recycle webView:$webView")
        try {
            webView.apply {
                stopLoading()
                clearHistory()
                removeAllViews()
                (parent as? ViewGroup)?.removeView(webView)
                (context as MutableContextWrapper).baseContext = webView.context.applicationContext

                webChromeClient = _webChromeClient
                webViewClient = _webViewClient
                webView.loadUrl(WARM_UP_HISTORY_URL)
            }
        } finally {
            if (pool.size < MAX_SIZE){
                pool.add(webView)
            } else {
                webView.destroy()
            }
        }
    }

    fun release() {
        pool.forEach {
            it.destroy()
        }
        pool.clear()
    }

    fun canGoBack(webView: WebView?): Boolean {
        val copyBackForwardList = webView?.copyBackForwardList()
        val size = copyBackForwardList?.size ?: 0
        val currentIndex = copyBackForwardList?.currentIndex ?: 0
        val url = copyBackForwardList?.getItemAtIndex(0)?.url
        LogUtils.d(TAG, "canGoBack webView:$webView\tsize:$size\tcurrentIndex:$currentIndex\turl:$url")
        if (size >= 2 && currentIndex >= 1
            && ((copyBackForwardList?.getItemAtIndex(currentIndex)?.url == WebViewPool.WARM_UP_HISTORY_URL)
                    || (copyBackForwardList?.getItemAtIndex(currentIndex - 1)?.url == WebViewPool.WARM_UP_HISTORY_URL))
        ) {
            return false
        }
        return true
    }
}