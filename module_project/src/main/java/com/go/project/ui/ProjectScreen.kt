package com.go.project.ui

import androidx.compose.foundation.ScrollState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
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
import com.go.navigate.nav.SearchScreenKey
import com.go.project.data.ProjectCategoryBean
import com.go.project.nav.PictureBrowserScreenNavKey
import com.go.project.viewmodel.ProjectViewModel

private const val TAG = "ProjectScreenTAG"

@Composable
fun ProjectScreen(navigator: INavigator) {
    LogUtils.d("LocalViewModelStoreOwnerTAG","ProjectScreen:${LocalViewModelStoreOwner.current}")
    val homeViewModel: ProjectViewModel = viewModel(factory = ProjectViewModel.Factory)
    val projectList = homeViewModel.projectList.value
    val listUiState = homeViewModel.listUiState.value
    val projectCategoryList = homeViewModel.projectCategoryList.value

    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val selectedProjectCategoryId =
        if(projectCategoryList.isEmpty() || selectedTabIndex > projectCategoryList.size-1) {
            -1
        } else projectCategoryList[selectedTabIndex].id

    val articleList = projectList[selectedProjectCategoryId]


    ProjectScreen(
        selectedTabIndex = selectedTabIndex,
        listUiState = listUiState,
        projectCategoryList = projectCategoryList,
        articleList = articleList,
        onRefresh = {
            LogUtils.d(TAG,"onRefresh selectedProjectCategoryId:$selectedProjectCategoryId")
            homeViewModel.refresh(if(selectedProjectCategoryId<=-1)null else selectedProjectCategoryId)
        }, onLoadMore = {
            homeViewModel.loadMore(selectedProjectCategoryId)
        }, onNavigator = {
            navigator.navigate(it)
        }, onSelectedTabIndexChange = {
            LogUtils.d(TAG,"onSelectedTabIndexChange:$it")
            selectedTabIndex = it
            val selectedProjectCategoryId = projectCategoryList[selectedTabIndex].id
            homeViewModel.refresh(selectedProjectCategoryId)
        }
    )
}

@Preview
@Composable
fun ProjectScreenPreview() {

}

@Composable
fun ProjectScreen(
    selectedTabIndex: Int,
    listUiState: ListUiState,
    projectCategoryList: List<ProjectCategoryBean>,
    articleList: List<ArticleBean>?,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onNavigator: (NavKey) -> Unit,
    onSelectedTabIndexChange: (Int) -> Unit
) {

    Column {
        AppTopBox(title = stringResource(com.go.common.R.string.project))

        ListUiStateWidget(listUiState = listUiState, onRefresh = onRefresh, content = {
            ProjectContentWidget(
                projectCategoryList = projectCategoryList,
                selectedTabIndex = selectedTabIndex,
                onSelectedTabIndexChange = onSelectedTabIndexChange,
                listUiState = listUiState,
                onRefresh = onRefresh,
                articleList = articleList,
                onLoadMore = onLoadMore,
                onNavigator = onNavigator
            )
        })
    }
}

@Composable
private fun ProjectContentWidget(
    scrollState: ScrollState = rememberScrollState(),
    projectCategoryList: List<ProjectCategoryBean>,
    selectedTabIndex: Int,
    onSelectedTabIndexChange: (Int) -> Unit,
    listUiState: ListUiState,
    onRefresh: () -> Unit,
    articleList: List<ArticleBean>?,
    onLoadMore: () -> Unit,
    onNavigator: (NavKey) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ProjectCategoryTabRow(
            scrollState = scrollState,
            projectCategoryList = projectCategoryList,
            selectedTabIndex = selectedTabIndex,
            onSelectedTabIndexChange = onSelectedTabIndexChange
        )

        if (listUiState is ListUiState.RefreshError) {
            RefreshErrorWidget(listUiState = listUiState, onRefresh = onRefresh)
        } else if (articleList.orEmpty().isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        } else {
            PagingPullToRefreshLazyColumn(
                listUiState = listUiState,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
                articleList = articleList.orEmpty(),
                onNavigator = onNavigator
            )
        }
    }
}

@Composable
private fun PagingPullToRefreshLazyColumn(
    listUiState: ListUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    articleList: List<ArticleBean>,
    onNavigator: (NavKey) -> Unit
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
                        onNavigator.invoke(PictureBrowserScreenNavKey.create(it))
                    },
                    onItemClick = { url ->
                        onNavigator.invoke(WebViewNavKey.create(
                            url = url,
                            articleId = articleBean.id,
                            isCollect = articleBean.collect
                        ))
                    }, onSearch = {
                        onNavigator.invoke(SearchScreenKey.create(it))
                    }
                )
            }
        },
    )
}

@Composable
private fun ProjectCategoryTabRow(
    scrollState: ScrollState = rememberScrollState(),
    projectCategoryList: List<ProjectCategoryBean>,
    selectedTabIndex: Int,
    onSelectedTabIndexChange: (Int) -> Unit
) {
    PrimaryScrollableTabRow(
        scrollState = scrollState,
        selectedTabIndex = selectedTabIndex,
        edgePadding = 20.dp,
        tabs = {
            projectCategoryList.forEachIndexed { index, bean ->
                Box(modifier = Modifier
                    .clickable {
                        onSelectedTabIndexChange(index)
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