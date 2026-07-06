package com.go.mine.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.go.common.R
import com.go.common.navigation3.EmptyNavigator
import com.go.common.navigation3.INavigator
import com.go.common.widget.AppTopBackBox
import com.go.common.accompanist_web.AccompanistWebChromeClient
import com.go.common.accompanist_web.AccompanistWebViewClient
import com.go.common.accompanist_web.WebView
import com.go.common.accompanist_web.WebViewNavigator
import com.go.common.accompanist_web.WebViewPool
import com.go.common.accompanist_web.WebViewState
import com.go.common.accompanist_web.rememberWebViewNavigator
import com.go.common.accompanist_web.rememberWebViewState
import com.go.common.data.RequestState
import com.go.common.http.HttpConfig
import com.go.common.theme.LocalAppThemeState
import com.go.common.widget.RequestStateDialog
import com.go.mine.ui.widget.WebViewArticleDropdownMenus
import com.go.mine.viewmodel.UserViewModel

@Preview
@Composable
fun WebViewScreenPreview() {
    WebViewScreen(
        url = "https://www.baidu.com",
        navigator = EmptyNavigator()
    )
}

private const val TAG = "WebScreenTAG"

@Composable
fun WebViewScreen(
    url: String,
    articleId: Int? = null,
    isCollect: Boolean = false,
    navigator: INavigator
) {

    SideEffect {
        LogUtils.d(TAG, "WebViewScreen url:$url\tarticleId:$articleId\tarticleId:$articleId")
    }

    var progressState by remember { mutableFloatStateOf(1.0f) }
    var titleState by remember { mutableStateOf(url) }
    var webView by remember {
        mutableStateOf<WebView?>(null)
    }

    val getTitleRunnable = remember {
        Runnable {
            if (titleState.isNotEmpty() || webView == null) {
                return@Runnable
            }
            webView?.evaluateJavascript("document.title") { jsTitle ->
                if (!jsTitle.isNullOrBlank() && jsTitle != "null") {
                    val title = jsTitle.replace("\"", "")
                    titleState = title
                }
            }
        }
    }

    val webViewState = rememberWebViewState(url = url)
    val webViewNavigator = rememberWebViewNavigator()
    val webViewClient = createWebViewClient(onPageFinished = {
        if (titleState.isNotEmpty()) {
            return@createWebViewClient
        }
        ThreadUtils.getMainHandler().removeCallbacks(getTitleRunnable)
        ThreadUtils.getMainHandler().postDelayed(getTitleRunnable, 100)
    })

    val webChromeClient = createWebChromeClient(
        onReceivedTitle = {
            titleState = it
        },
        onProgressChanged = {
            progressState = it
        }
    )

    val currentContext = LocalContext.current
    LaunchedEffect(Unit) {
        webView = WebViewPool.bind(currentContext)
    }

    BackHandler(webView != null) {
        if (webView?.canGoBack() == true && WebViewPool.canGoBack(webView)) {
            webView?.goBack()
        } else {
            navigator.goBack()
        }
    }

    val userViewModel = viewModel<UserViewModel>(
        factory = UserViewModel.Factory,
        viewModelStoreOwner = LocalActivity.current as ViewModelStoreOwner
    )
    var isCollectState by remember(userViewModel.articleCollectState) {
        mutableStateOf(userViewModel.articleCollectState[articleId]?:isCollect)
    }
    val rememberCoroutineScope = rememberCoroutineScope()
    var requestState:RequestState by remember {
        mutableStateOf(RequestState.Dismiss)
    }

    RequestStateDialog(state = requestState)
    Column(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()) {
        AppTopBackBox(
            title = titleState,
            trailingContent = {
                Row {
                    val tint = if(LocalAppThemeState.current.statusBarFollowPrimaryColor)
                        MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.primary
                    Icon(
                        painter = painterResource(
                            if (isCollectState) R.drawable.favorite_fill
                            else R.drawable.favorite
                        ),
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier
                            .clickable {

                                articleId?.let {
                                    if(isCollectState) {
                                        userViewModel.unCollectArticle(
                                            id = articleId,
                                            scope = rememberCoroutineScope,
                                            onEvent = {
                                                requestState = it
                                            }
                                        )
                                    } else {
                                        userViewModel.addCollectArticle(
                                            id = articleId,
                                            scope = rememberCoroutineScope,
                                            onEvent = {
                                                requestState = it
                                            }
                                        )
                                    }
                                }

                            }
                            .fillMaxHeight()
                            .width(40.dp)
                            .padding(10.dp)
                    )

                    var expanded by remember {
                        mutableStateOf(false)
                    }

                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier
                            .clickable {
                                expanded = !expanded
                            }
                            .fillMaxHeight()
                            .width(40.dp)
                            .padding(10.dp)
                    )
                    if (expanded) {
                        WebViewArticleDropdownMenus(
                            expanded = expanded,
                            link = url,
                            title = titleState,
                        ) {
                            expanded = false
                        }
                    }

                }
            }
        ) {
            navigator.goBack()
        }

        if (progressState < 1.0) {
            LinearProgressIndicator(
                progress = {
                    progressState
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (webView == null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentSize()
                    .size(40.dp)
            )
        } else {
            webView?.let {
                ComposeWebView(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    webView = it,
                    webViewState = webViewState,
                    webViewClient = webViewClient,
                    webChromeClient = webChromeClient,
                    webViewNavigator = webViewNavigator
                )
            }
        }
    }

}


@Composable
private fun ComposeWebView(
    modifier: Modifier,
    webView: WebView,
    webViewState: WebViewState,
    webViewNavigator: WebViewNavigator,
    webViewClient: AccompanistWebViewClient,
    webChromeClient: AccompanistWebChromeClient,
) {

    WebView(
        modifier = modifier,
        state = webViewState,
        navigator = webViewNavigator,
        onCreated = {
            it.settings.run {
                javaScriptEnabled = true
                domStorageEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
            }
        },
        client = webViewClient,
        chromeClient = webChromeClient,

        factory = {
            webView
        },
        onDispose = {
            WebViewPool.recycle(it)
        }
    )
}

@Composable
private fun createWebChromeClient(
    onReceivedTitle: (String) -> Unit,
    onProgressChanged: (Float) -> Unit
): AccompanistWebChromeClient {
    val chromeClient = remember {
        object : AccompanistWebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String?) {
                super.onReceivedTitle(view, title)
                onReceivedTitle(title ?: "")
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                onProgressChanged(newProgress / 100.0f)
            }
        }
    }
    return chromeClient
}

@Composable
private fun createWebViewClient(onPageFinished: (WebView) -> Unit): AccompanistWebViewClient {


    val accompanistWebViewClient = remember {

        object : AccompanistWebViewClient() {

            fun replaceRequest(request: WebResourceRequest?): WebResourceRequest? {
                var finalRequest = request
                if (request?.url?.scheme?.contains("http") == false) {
                    return null
                }

                if (request?.url?.host?.startsWith(HttpConfig.OUT_DATED_HOST) == true) {
                    finalRequest = object : WebResourceRequest {
                        override fun getMethod() = request.method

                        override fun getRequestHeaders() = request.requestHeaders

                        override fun getUrl(): Uri {
                            return if (request.url?.host?.contains(HttpConfig.OUT_DATED_HOST) == true) {
                                request.url.toString().replace(
                                    HttpConfig.OUT_DATED_HOST,
                                    HttpConfig.NEW_HOST
                                ).toUri().also {
                                    LogUtils.d(
                                        TAG,
                                        "shouldInterceptRequest getUrl newUrl:$it url:${request.url} "
                                    )
                                }
                            } else {
                                request.url
                            }
                        }

                        override fun hasGesture() = request.hasGesture()

                        override fun isForMainFrame() = request.isForMainFrame

                        @RequiresApi(Build.VERSION_CODES.N)
                        override fun isRedirect() = request.isRedirect
                    }
                }
                return finalRequest
            }

            fun replaceShouldInterceptRequest(url: String?): String? {
                return if (url == null) {
                    null
                } else if (url.contains(HttpConfig.OUT_DATED_HOST)) {
                    url.replace(HttpConfig.OUT_DATED_HOST, HttpConfig.NEW_HOST).also {
                        LogUtils.d(TAG, "replaceShouldInterceptRequest newUrl$it\turl:$url")
                    }
                } else if (!url.startsWith("http")) {
                    null
                } else {
                    url
                }
            }

            override fun onPageStarted(
                view: WebView,
                url: String?,
                favicon: Bitmap?
            ) {
                LogUtils.d(TAG, "onPageStarted url:$url")
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String?) {
                LogUtils.d(TAG, "onPageFinished url:$url")
                super.onPageFinished(view, url)
                onPageFinished(view)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                LogUtils.d(TAG, "shouldOverrideUrlLoading url:${request?.url}")
                val replaceRequest = replaceRequest(request) ?: return true
                return super.shouldOverrideUrlLoading(view, replaceRequest)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                LogUtils.d(TAG, "shouldOverrideUrlLoading url:$url")
                val replaceShouldInterceptRequest =
                    replaceShouldInterceptRequest(url) ?: return true
                return super.shouldOverrideUrlLoading(view, replaceShouldInterceptRequest)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                LogUtils.d(TAG, "shouldInterceptRequest url:${request?.url}")
                val replaceRequest = replaceRequest(request) ?: return null
                return super.shouldInterceptRequest(view, replaceRequest)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                url: String?
            ): WebResourceResponse? {
                LogUtils.d(TAG, "shouldInterceptRequest url:$url")
                val replaceShouldInterceptRequest =
                    replaceShouldInterceptRequest(url) ?: return null
                return super.shouldInterceptRequest(view, replaceShouldInterceptRequest)
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                LogUtils.d(
                    TAG,
                    "onReceivedError errorCode:${error?.errorCode}\tdescription:${error?.description}"
                )
                super.onReceivedError(view, request, error)
            }
        }
    }
    return accompanistWebViewClient
}