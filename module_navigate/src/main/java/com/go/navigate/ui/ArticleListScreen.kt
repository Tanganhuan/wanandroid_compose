package com.go.navigate.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.common.widget.AppTopBackBox
import com.go.mine.ui.widget.ArticleItemWidget
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.navigate.R
import com.go.navigate.nav.SearchScreenKey
import com.go.navigate.viewmodel.ArticleListViewModel

private const val TAG = "ArticleListScreenTAG"
@Composable
fun ArticleListScreen(title:String, cid:Int,author:String?=null,orderType:Int, navigator: INavigator) {
    val articleListViewModel: ArticleListViewModel = viewModel(factory = ArticleListViewModel.Factory)

    var orderTypeState by rememberSaveable {
        mutableIntStateOf(orderType)
    }

    val listUiState = articleListViewModel.listUiState.value
    val articleList = articleListViewModel.articleList.value
    val onRefresh = {
        articleListViewModel.refreshArticleList(cid = cid,orderType = orderTypeState)
    }
    val onSearch:(String)->Unit = {
        navigator.navigate(SearchScreenKey.create(it))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBackBox(title = title, trailingContent = {
            Icon(painter = painterResource(
                if(orderTypeState==1)
                R.drawable.ascending
            else R.drawable.descending),
                contentDescription = null,
                modifier = Modifier.fillMaxHeight().width(50.dp).clickable{
                    orderTypeState = if(orderTypeState==0) 1 else 0
                    onRefresh()
                }.padding(8.dp))
        }) {
            navigator.goBack()
        }
        ListUiStateWidget(
            listUiState = listUiState,
            onRefresh = {
                onRefresh()
            }
        ) {
            if(articleList.isNotEmpty()) {

                PagingPullToRefreshLazyColumn(
                    listUiState = listUiState,
                    contentListSize = articleList.size,
                    onRefresh = {
                        onRefresh()
                    },
                    onLoadMore = {
                        articleListViewModel.loadMoreArticleList(cid = cid,orderType = orderTypeState)
                    }) {
                    items(count = articleList.size, key = {
                        articleList[it].id
                    }) {
                        val articleBean = articleList[it]
                        ArticleItemWidget(
                            article = articleList[it],
                            onItemClick = { url ->
                                navigator.navigate(WebViewNavKey.create(
                                    url = url,
                                    articleId = articleBean.id,
                                    isCollect = articleBean.collect
                                ))
                             },
                            onSearch = onSearch
                        )
                    }
                }
            }
        }
    }
}