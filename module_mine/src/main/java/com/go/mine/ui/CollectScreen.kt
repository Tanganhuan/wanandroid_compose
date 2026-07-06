package com.go.mine.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.LogUtils
import com.go.common.data.ListUiState
import com.go.common.data.RequestState
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.common.widget.AppTopBackBox
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.common.widget.RequestStateDialog
import com.go.mine.R
import com.go.mine.ui.widget.ArticleItemWidget
import com.go.mine.ui.widget.UncollectDropdownMenuItem
import com.go.mine.viewmodel.UserViewModel
import kotlinx.coroutines.launch

private const val TAG = "CollectScreenTAG"

@Composable
fun CollectScreen(navigator: INavigator) {

    val userViewModel = viewModel<UserViewModel>(
        viewModelStoreOwner = LocalActivity.current as ViewModelStoreOwner,
        factory = UserViewModel.Factory
    )
    val listUiState = userViewModel.listUiState
    val dataList = userViewModel.dataList

    val rememberCoroutineScope = rememberCoroutineScope()
    val lazyListState: LazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val onRefresh = {
        userViewModel.refreshCollectList()
    }

    LaunchedEffect(listUiState) {
        if(listUiState is ListUiState.RefreshSucceed) {
            rememberCoroutineScope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            LogUtils.d(TAG,"CollectScreen DisposableEffect")
            userViewModel.resetListUiState()
        }
    }


    var requestState: RequestState by remember {
        mutableStateOf(RequestState.Dismiss)
    }

    RequestStateDialog(requestState)

    Column {
        AppTopBackBox(title = stringResource(R.string.my_favorite)){
            navigator.goBack()
        }
        ListUiStateWidget(
            listUiState = listUiState,
            onRefresh = onRefresh,
        ) {
            PagingPullToRefreshLazyColumn(
                listUiState = listUiState,
                lazyListState = lazyListState,
                contentListSize = dataList.size,
                onRefresh = onRefresh,
                onLoadMore = {
                    userViewModel.loadMoreCollectList()
                },
                content = {
                    items(count = dataList.size,
                        key = { index ->
                            dataList[index].id
                        }
                    ) { index ->
                        val articleBean = dataList[index]
                        ArticleItemWidget(
                            canCollect = false,
                            article = articleBean,
                            onItemClick = {
                                navigator.navigate(WebViewNavKey.create(
                                    url = it,
                                    articleId = articleBean.originId,
                                    isCollect = true
                                ))
                            },
                            extradDropdownMenuItems = { articleBean,onDismissRequest ->
                                UncollectDropdownMenuItem(articleBean = articleBean) { id ->
                                    onDismissRequest()
                                    userViewModel.unCollectArticle(
                                        id = id,
                                        originId = articleBean.originId,
                                        scope = rememberCoroutineScope
                                    ) { _requestState->
                                        requestState = _requestState
                                        rememberCoroutineScope.launch {
                                            lazyListState.scrollToItem(0)
                                            userViewModel.refreshCollectList()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}