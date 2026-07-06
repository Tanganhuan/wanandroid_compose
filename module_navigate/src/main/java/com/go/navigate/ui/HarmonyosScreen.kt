package com.go.navigate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.LogUtils
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.common.widget.AppTopBackBox
import com.go.mine.ui.widget.ArticleItemWidget
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.navigate.data.ChapterBean
import com.go.navigate.viewmodel.HarmonyosViewModel

@Composable
fun HarmonyosScreen(navigator: INavigator) {
    LogUtils.d("LocalViewModelStoreOwnerTAG","HarmonyosScreen:${LocalViewModelStoreOwner.current}")
    val harmonyosViewModel: HarmonyosViewModel = viewModel(factory = HarmonyosViewModel.Factory)
    val listUiState = harmonyosViewModel.listUiState.value
    val data = harmonyosViewModel.harmonyosData.value
    val onRefresh = {
        harmonyosViewModel.refresh()
    }
    val onNavigator:(String)->Unit = {
        navigator.navigate(WebViewNavKey.create(it))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBackBox(title = stringResource(com.go.common.R.string.harmony_os)) {
            navigator.goBack()
        }

        ListUiStateWidget(listUiState = listUiState, onRefresh = onRefresh) {
            PagingPullToRefreshLazyColumn(
                listUiState = listUiState,
                onRefresh = onRefresh,
                onLoadMore = {  },
                contentListSize = data.links.articleList.size
            ) {
                if(data.links.articleList.isNotEmpty()) {
                    harmonyChapter(data.links, onItemClick = onNavigator)
                }

                if(data.openSources.articleList.isNotEmpty()) {
                    harmonyChapter(data.openSources, onItemClick = onNavigator)
                }

                if(data.tools.articleList.isNotEmpty()) {
                    harmonyChapter(data.tools, onItemClick = onNavigator)
                }
            }
        }
    }
}

private fun LazyListScope.harmonyChapter(chapterBean:ChapterBean,onItemClick:(String)->Unit) {
    stickyHeader(key = chapterBean.id) {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer).padding(10.dp),
                text = chapterBean.articleList.first().chapterName,
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider()
        }

    }
    items(
        count = chapterBean.articleList.size,
        key = { chapterBean.articleList[it].id }
    ) {
        ArticleItemWidget(
            article = chapterBean.articleList[it],
            onItemClick = {
                onItemClick(it)
            }
        )
    }
}