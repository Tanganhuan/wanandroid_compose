package com.go.wanandroid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.blankj.utilcode.util.LogUtils
import com.go.common.accompanist_web.WebViewPool
import com.go.wanandroid.R
import com.go.wanandroid.data.persistent.AppPreferencesDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(callback: (Boolean) -> Unit) {
    LogUtils.d("LocalViewModelStoreOwnerTAG","SplashScreen:${LocalViewModelStoreOwner.current}")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.jetpack_compose_select_screen_icon),
            contentDescription = null
        )
    }

    val currentContext = LocalContext.current
    LaunchedEffect(Unit) {
        WebViewPool.init(currentContext.applicationContext)
        delay(1500.milliseconds)
        AppPreferencesDataStore.isFirstTimeToUseApp()
            .take(1)
            .collect {
                callback(it)
            }
    }
}


