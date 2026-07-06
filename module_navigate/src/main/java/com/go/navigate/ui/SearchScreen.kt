package com.go.navigate.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.common.widget.AppTopBackBox
import com.go.mine.ui.widget.ArticleItemWidget
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.navigate.nav.SearchScreenKey
import com.go.navigate.viewmodel.SearchViewModel

@Composable
fun SearchScreen(keyword:String,navigator: INavigator) {

    val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)
    val listUiState = searchViewModel.listUiState
    val dataList = searchViewModel.dataList

    val onRefresh = {
        searchViewModel.refresh(keyword)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBackBox(title = "${stringResource(com.go.common.R.string.search)}:$keyword") {
            navigator.goBack()
        }
        ListUiStateWidget(listUiState = listUiState, onRefresh = onRefresh) {
            PagingPullToRefreshLazyColumn(
                listUiState = listUiState,
                contentListSize = dataList.size,
                onRefresh = onRefresh,
                onLoadMore = {
                    searchViewModel.loadMore(keyword)
                },
            ) {
                items(count = dataList.size, key = {
                    dataList[it].id
                }) {
                    val articleBean = dataList[it]
                    ArticleItemWidget(
                        article = dataList[it],
                        onItemClick = { url ->
                            navigator.navigate(WebViewNavKey.create(
                                url = url,
                                articleId = articleBean.id,
                                isCollect = articleBean.collect
                            ))
                        },
                        onSearch = {
                            navigator.navigate(SearchScreenKey.create(it))
                        }
                    )
                }
            }
        }
    }
}