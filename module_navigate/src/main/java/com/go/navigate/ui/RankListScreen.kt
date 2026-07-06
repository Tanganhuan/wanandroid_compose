package com.go.navigate.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.go.common.navigation3.INavigator
import com.go.common.widget.AppTopBackBox
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.navigate.R
import com.go.navigate.nav.SearchScreenKey
import com.go.navigate.viewmodel.RankListViewModel

@Composable
fun RankListScreen(navigator: INavigator) {
    val rankListViewModel: RankListViewModel = viewModel(factory = RankListViewModel.Factory)
    val dataList = rankListViewModel.dataList
    val listUiState = rankListViewModel.listUiState
    val onRefresh = {
        rankListViewModel.refresh()
    }
    val loadMore = {
        rankListViewModel.loadMore()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBackBox(title = stringResource(com.go.common.R.string.ranking_list)) {
            navigator.goBack()
        }

        ListUiStateWidget(listUiState = listUiState, onRefresh = {onRefresh()}) {

            PagingPullToRefreshLazyColumn(
                listUiState = listUiState,
                onRefresh = { onRefresh() },
                onLoadMore = { loadMore() },
                contentListSize = dataList.size
            ) {
                items(count = dataList.size, key = {
                    dataList[it].userId
                }) {
                    val user = dataList[it]
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 2.dp)
                        .clickable {
                            navigator.navigate(SearchScreenKey.create(keyword =
                                if(user.username.contains("*")
                                    && user.username.lastIndexOf("*")!=user.username.length-1)
                                    user.username.substring(user.username.lastIndexOf("*")+1)
                                else user.username)
                            )
                        },
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(user.username, style = MaterialTheme.typography.titleLarge)
                            Text(stringResource(R.string.rank, user.rank))
                            Text(stringResource(R.string.coin, user.coinCount))
                        }
                    }
                }
            }
        }
    }
}