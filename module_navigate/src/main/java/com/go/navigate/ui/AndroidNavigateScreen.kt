package com.go.navigate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.common.theme.safetyColors
import com.go.common.widget.AppTopBackBox
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.navigate.viewmodel.AndroidNavigateViewModel

@Composable
fun AndroidNavigateScreen(navigator: INavigator) {
    val viewModel: AndroidNavigateViewModel = viewModel(factory = AndroidNavigateViewModel.Factory)
    val dataList = viewModel.dataList
    val listUiState = viewModel.listUiState
    val onRefresh = {
        viewModel.refresh()
    }
    val onNavigator:(String)->Unit = { url ->
        navigator.navigate(WebViewNavKey.create(url = url))
    }
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBackBox(title = stringResource(com.go.common.R.string.navigate)) {
            navigator.goBack()
        }
        ListUiStateWidget(
            listUiState = listUiState,
            onRefresh = {
                onRefresh()
            }
        ) {
            PagingPullToRefreshLazyColumn(
                listUiState = listUiState,
                contentListSize = dataList.size,
                onRefresh = {
                    onRefresh()
                },
                onLoadMore = {},
                content = {
                    dataList.forEachIndexed { index, androidNavigate ->
                        stickyHeader(key = androidNavigate.cid) {
                            Text(
                                modifier = Modifier.fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainer).padding(10.dp),
                                text = androidNavigate.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        item {
                            FlowRow {
                                androidNavigate.articles.forEachIndexed { index, navigate ->
                                    Text(navigate.title, modifier = Modifier
                                        .padding(8.dp)
                                        .border(width = 2.dp,
                                            color = safetyColors[index%safetyColors.size],
                                            shape = RoundedCornerShape(4.dp)
                                        ).clickable{
                                            onNavigator.invoke(navigate.link)
                                        }.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }

                },
            )
        }
    }
}