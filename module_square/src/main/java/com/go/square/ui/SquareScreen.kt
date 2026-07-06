package com.go.square.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.LogUtils
import com.go.mine.data.ArticleBean
import com.go.common.data.ListUiState
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.common.widget.AppTopBox
import com.go.mine.ui.widget.ArticleItemWidget
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.navigate.nav.SearchScreenKey
import com.go.square.viewmodel.SquareViewModel

@Preview
@Composable
fun SquareScreenPreview() {

}

private const val TAG = "SquareScreenTAG"
@Composable
fun SquareScreen(navigator: INavigator) {
    LogUtils.d("LocalViewModelStoreOwnerTAG","SquareScreen:${LocalViewModelStoreOwner.current}")
    val homeViewModel: SquareViewModel = viewModel(factory = SquareViewModel.Factory)
    val listUiState = homeViewModel.listUiState.value
    val squareList = homeViewModel.squareList.value.data?.datas.orEmpty()

    SquareScreen(
        navigator = navigator,
        listUiState = listUiState,
        articleList = squareList,
        onRefresh = {
            homeViewModel.refresh()
        },
        onLoadMore = {
            homeViewModel.loadMore()
        })
}

@Composable
fun SquareScreen(
    navigator: INavigator,
    listUiState: ListUiState,
    articleList: List<ArticleBean>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit
) {

    Column {
        AppTopBox(title = stringResource(com.go.common.R.string.square))
        ListUiStateWidget(
            listUiState = listUiState,
            onRefresh = {
                if (listUiState is ListUiState.Refreshing
                    || listUiState is ListUiState.LoadingMore
                ) {
                    return@ListUiStateWidget
                }
                onRefresh()
            }
        ) {
            PagingPullToRefreshLazyColumn(
                listUiState = listUiState,
                contentListSize = articleList.size,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
                content = {
                    items(articleList.size, key = {
                        articleList[it].id
                    }) { index ->
                        val articleBean = articleList[index]
                        ArticleItemWidget(
                            article = articleBean,
                            onItemClick = { url->
                                navigator.navigate(WebViewNavKey.create(
                                    url = url,
                                    articleId = articleBean.id,
                                    isCollect = articleBean.collect
                                ))
                            }, onSearch = {
                                navigator.navigate(SearchScreenKey.create(it))
                            }
                        )
                    }
                })
        }
    }
}