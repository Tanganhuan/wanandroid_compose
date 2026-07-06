package com.go.wanandroid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.blankj.utilcode.util.LogUtils
import com.go.wanandroid.R
import com.go.wanandroid.data.persistent.AppPreferencesDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(callback:()->Unit={}) {
    LogUtils.d("LocalViewModelStoreOwnerTAG","GuideScreen:${LocalViewModelStoreOwner.current}")
    val imgItems = remember {
        listOf(
            R.drawable.splash_1,
            R.drawable.splash_2,
            R.drawable.splash_3,
            R.drawable.splash_4,
        )
    }

    val rememberCoroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = {
            imgItems.size
        })

        HorizontalPager(state = pagerState) { page ->
            Image(painter = painterResource(imgItems[page]),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = null)
        }

        if(pagerState.currentPage != imgItems.size-1) {
            Row(
                Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .statusBarsPadding()
                    .align(Alignment.BottomCenter)
                    .offset(0.dp, (-100).dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterVertically)
                            .background(color)
                            .size(if (pagerState.currentPage == iteration) 12.dp else 10.dp)
                    )
                }
            }
        }

        if(pagerState.currentPage == imgItems.size-1) {
            Button(
                modifier = Modifier
                    .offset(0.dp, (-100).dp)
                    .statusBarsPadding()
                    .align(Alignment.BottomCenter),
                onClick = {
                    LogUtils.d("SplashScreen", "onClick:${System.currentTimeMillis()}")
                    rememberCoroutineScope.launch {
                        AppPreferencesDataStore.setIsFirstTimeToUseApp(false)
                        callback()
                    }

                }) {
                Text("Go Compose")
            }
        }
    }
}
