package com.go.wechat.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.LogUtils
import com.go.mine.data.ArticleBean
import com.go.common.data.ListUiState
import com.go.common.extension.parseHtmlEntities
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.common.widget.AppTopBox
import com.go.mine.ui.widget.ArticleItemWidget
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.common.widget.RefreshErrorWidget
import com.go.wechat.data.WechatAuthorBean
import com.go.wechat.viewmodel.WechatViewModel

private const val TAG = "WechatScreenTAG"
@Composable
fun WechatScreen(navigator: INavigator) {
    val homeViewModel: WechatViewModel = viewModel(factory = WechatViewModel.Factory)

    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val listUiState = homeViewModel.listUiState.value
    val wechatAuthorList = homeViewModel.wechatAuthorList.value

    val selectedAuthorId = if(selectedTabIndex>wechatAuthorList.size-1) -1 else wechatAuthorList[selectedTabIndex].id

    val articleList = homeViewModel.articleList.value[selectedAuthorId]
    SideEffect {
        LogUtils.d(TAG,"selectedTabIndex:$selectedTabIndex\tlistUiState:$listUiState\tselectedAuthorId:$selectedAuthorId")
    }
    val onRefresh = {
        homeViewModel.refresh(selectedAuthorId)
    }
    val onLoadMore = {
        homeViewModel.loadMore(selectedAuthorId)
    }
    val onNavigator:(ArticleBean)->Unit = { articleBean ->
        navigator.navigate(WebViewNavKey.create(
            url = articleBean.link,
            articleId = articleBean.id,
            isCollect = articleBean.collect
        ))
    }

    val onSelectedTabIndexChange:(Int,WechatAuthorBean)->Unit = {index,bean ->
        selectedTabIndex = index
        homeViewModel.refresh(bean.id)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBox(title = stringResource(com.go.common.R.string.wechat_account))
        ListUiStateWidget(listUiState = listUiState, onRefresh = {
            onRefresh.invoke()
        }) {
            if(wechatAuthorList.isNotEmpty()) {
                PrimaryScrollableTabRow(
                    scrollState = rememberScrollState(),
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 20.dp,
                    tabs = {
                        wechatAuthorList.forEachIndexed { index, bean ->
                            Box(modifier = Modifier
                                .clickable {
                                    onSelectedTabIndexChange(index,bean)
                                }
                                .height(48.dp)) {
                                Text(
                                    text = bean.name.parseHtmlEntities(),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(start = 4.dp, end = 4.dp).align(Alignment.Center)
                                )
                            }
                        }
                    }
                )
            }

            if (listUiState is ListUiState.RefreshError) {
                RefreshErrorWidget(listUiState = listUiState, onRefresh = onRefresh)
            } else if (articleList.orEmpty().isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            } else {
                articleList?.let {
                    PagingPullToRefreshLazyColumn(
                        listUiState = listUiState,
                        articleList = articleList,
                        onRefresh = {
                            onRefresh.invoke()
                        },
                        onLoadMore = {
                            onLoadMore.invoke()
                        },
                        onNavigator = {
                            onNavigator.invoke(it)
                        },)
                }
            }
        }
    }
}


@Composable
private fun PagingPullToRefreshLazyColumn(
    listUiState: ListUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    articleList: List<ArticleBean>,
    onNavigator: (ArticleBean) -> Unit
) {
    PagingPullToRefreshLazyColumn(
        listUiState = listUiState,
        contentListSize = articleList.size,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
        content = {
            items(
                count = articleList.size,
                key = {
                    "project:${articleList[it].id}"
                }
            ) {
                val articleBean = articleList[it]
                ArticleItemWidget(
                    article = articleBean
                    ,onPicClick = {
                        onNavigator.invoke(articleBean)
                    },
                    onItemClick = {
                        onNavigator.invoke(articleBean)
                    }
                )
            }
        },
    )
}