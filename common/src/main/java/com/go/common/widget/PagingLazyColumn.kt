package com.go.common.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.LogUtils
import com.go.common.R
import com.go.common.data.ListUiState


@Composable
fun PagingPullToRefreshLazyColumn(
    modifier: Modifier = Modifier,
    lazyListState:LazyListState = rememberLazyListState(),
    pullToRefreshState:PullToRefreshState = rememberPullToRefreshState(),
    listUiState: ListUiState,
    contentListSize:Int,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    topContent: (LazyListScope.() -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {

    PullToRefreshBox(
        modifier = modifier,
        state = pullToRefreshState,
        isRefreshing = listUiState is ListUiState.Refreshing,
        onRefresh = {
            onRefresh()
        },
        indicator = {
            Indicator(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = listUiState is ListUiState.Refreshing,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(),
            state = lazyListState
        ) {
            topContent?.invoke(this)
            content.invoke(this)
            if(contentListSize >0) {
                item {
                    LoadMoreItem(listUiState = listUiState, onLoadMore = onLoadMore)
                }
            }
        }
    }
}

@Preview
@Composable
fun LoadMoreItemPreview() {
    LoadMoreItem(listUiState = ListUiState.LoadMoreError("加载失败"), onLoadMore = {})
}

private const val TAG = "PagingLazyColumnTAG"
@Composable
fun LoadMoreItem(listUiState: ListUiState, onLoadMore: () -> Unit) {

    var isVisibility by remember {
        mutableStateOf(false)
    }

    SideEffect {
        LogUtils.d(TAG, "LoadMoreItem isVisibility:$isVisibility\tlistUiState:$listUiState\tonLoadMore:$onLoadMore")
    }

    val rememberUpdatedOnLoadMore by rememberUpdatedState(onLoadMore)
    LaunchedEffect(isVisibility,listUiState) {
        if(isVisibility &&
            (listUiState is ListUiState.RefreshSucceed
                    || (listUiState is ListUiState.LoadMoreSucceed && listUiState.loadedCount>0))) {
            rememberUpdatedOnLoadMore()
        }
    }

    Box(
        modifier = Modifier
            .clickable {
                onLoadMore()
            }.onVisibilityChanged { visibility ->
                LogUtils.d(TAG, "LoadMoreItem onVisibilityChanged:$visibility\tlistUiState:$listUiState")
                isVisibility = visibility
            }
            .fillMaxWidth()
            .wrapContentSize()
            .height(50.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.Center)
        ) {
            if (listUiState is ListUiState.LoadingMore) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = when(listUiState) {
                    is ListUiState.LoadMoreError -> stringResource(com.go.common.R.string.load_more_failed_please_try_again)
                    is ListUiState.LoadMoreSucceed -> {
                        if(listUiState.loadedCount<=0) {
                            stringResource(R.string.all_data_has_been_loaded)
                        } else {
                            stringResource(com.go.common.R.string.loading_more_please_wait)
                        }
                    }
                    is ListUiState.LoadingMore,
                    is ListUiState.Idel,
                    is ListUiState.RefreshError,
                    is ListUiState.Refreshing,
                    is ListUiState.RefreshSucceed
                         -> stringResource(com.go.common.R.string.loading_more_please_wait)
                }
            )
        }
    }
}