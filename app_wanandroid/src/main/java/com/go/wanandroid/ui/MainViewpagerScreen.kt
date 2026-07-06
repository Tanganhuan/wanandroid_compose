package com.go.wanandroid.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.go.common.navigation3.INavigator
import com.go.wanandroid.nav.TOP_LEVEL_ROUTES
import com.go.wanandroid.ui.widget.MainBottomBar
import kotlinx.coroutines.launch

private const val TAG = "MainViewpagerScreenTAG"
@Composable
fun MainViewpagerScreen(navigator: INavigator) {

    var currentNavKey by rememberSaveable {
        mutableStateOf(TOP_LEVEL_ROUTES.first().first)
    }
    val rememberCoroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {

        val pagerState = rememberPagerState(pageCount = {
            TOP_LEVEL_ROUTES.size
        })

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            TOP_LEVEL_ROUTES[page].second.content(navigator)
        }
        HorizontalDivider()
        MainBottomBar(currentNavKey) {
            currentNavKey = it
            rememberCoroutineScope.launch {
                pagerState.scrollToPage(TOP_LEVEL_ROUTES.find { item ->
                    item.first == currentNavKey
                }.let {
                    TOP_LEVEL_ROUTES.indexOf(it)
                })
            }
        }

    }
}


