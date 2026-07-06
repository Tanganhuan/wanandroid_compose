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
import com.go.navigate.viewmodel.WendaViewModel

@Composable
fun WendaScreen(navigator: INavigator) {
    val wendaViewModel: WendaViewModel = viewModel(factory = WendaViewModel.Factory)
    val wendaList = wendaViewModel.wendaList.value
    val listUiState = wendaViewModel.listUiState.value
    val onRefresh = {
        wendaViewModel.refresh()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBackBox(title = stringResource(com.go.common.R.string.questions_and_answers)) {
            navigator.goBack()
        }

        ListUiStateWidget(listUiState = listUiState, onRefresh = {onRefresh()}) {

            PagingPullToRefreshLazyColumn(
                listUiState = listUiState,
                onRefresh = { onRefresh() },
                onLoadMore = { wendaViewModel.loadMore() },
                contentListSize = wendaList.size
            ) {
                items(count = wendaList.size, key = {
                    wendaList[it].id
                }) {
                    val articleBean = wendaList[it]
                    ArticleItemWidget(
                        article = wendaList[it],
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