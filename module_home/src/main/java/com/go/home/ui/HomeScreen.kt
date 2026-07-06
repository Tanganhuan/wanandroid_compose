package com.go.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import com.blankj.utilcode.util.LogUtils
import com.go.common.BaseApplication
import com.go.mine.data.ArticleBean
import com.go.common.http.HttpConfig
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.common.widget.AppTopBox
import com.go.common.widget.ColumnItemsBox
import com.go.common.widget.ItemBoxData
import com.go.home.R
import com.go.home.data.ColumnBean
import com.go.home.data.HomeHeadData
import com.go.common.data.ListUiState
import com.go.home.data.RouteBean
import com.go.home.viewmodel.HomeViewModel
import com.go.mine.ui.widget.ArticleItemWidget
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.home.data.HomeBannerBean
import com.go.home.data.HomeTabItemData
import com.go.home.data.navigationData
import com.go.navigate.nav.SearchInputScreenKey
import com.go.navigate.nav.SearchScreenKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "HomeScreenTAG"


@OptIn(ExperimentalGridApi::class)
@Preview(apiLevel = 36)
@Composable
fun HomeScreenPreview() {

}

@Composable
fun HomeScreen(navigator: INavigator) {
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val listUiState = homeViewModel.listUiState
    val homeHeadData = homeViewModel.homeHeadData.value

    LogUtils.d("LocalViewModelStoreOwnerTAG","HomeScreen:${LocalViewModelStoreOwner.current}")
    SideEffect {
        LogUtils.d(TAG,"SideEffect HomeScreen listUiState:${listUiState}\thomeViewModel:${homeViewModel.hashCode()}")
    }

    HomeScreen(
        listUiState = listUiState,
        homeHeadData = homeHeadData,
        onRefresh = {
            homeViewModel.refreshData()
        },
        onLoadMore = {
            homeViewModel.loadMore()
        },
        onNavigate = {
            navigator.navigate(it)
        },
        onSearch = {
            navigator.navigate(SearchScreenKey.create(keyword = it))
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    listUiState: ListUiState,
    homeHeadData: HomeHeadData,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onNavigate: (NavKey) -> Unit,
    onSearch:(String)->Unit,
) {

    Column {
        AppTopBox(
            title = stringResource(com.go.common.R.string.home),
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = stringResource(com.go.common.R.string.search),
                    modifier = Modifier
                        .clickable {
                            onNavigate(SearchInputScreenKey)
                        }
                        .height(50.dp)
                        .padding(start = 10.dp, end = 10.dp)
                )
            }
        )

        ListUiStateWidget(
            listUiState = listUiState,
            onRefresh = onRefresh
        ) {

            val articleTopList = homeHeadData.articleTopList.data.orEmpty()
            val articleList = homeHeadData.articleList.data?.datas.orEmpty()
            val route = homeHeadData.popularRoute.data.orEmpty()
            val wenda = homeHeadData.popularWenda.data.orEmpty()
            val column = homeHeadData.popularColumn.data.orEmpty()

            PagingPullToRefreshLazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                listUiState = listUiState,
                contentListSize = articleTopList.size,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
                topContent = {
                    if(homeHeadData.banner.data.orEmpty().isNotEmpty()) {
                        item {
                            BannerBrowseCarousel(homeHeadData.banner.data.orEmpty()) { url ->
                                onNavigate(WebViewNavKey.create(url))
                            }
                        }
                    }
                    item {
                        PopularPager(
                            onNavigate = onNavigate,
                            route = route,
                            wenda = wenda,
                            column = column
                        )
                    }
                },
                content = {
                    items(articleTopList.size, key = {
                        "articleTopList:${articleTopList[it].id}"
                    }) { index ->
                        val articleBean = articleTopList[index]
                        ArticleItemWidget(
                            isTopArticle = true,
                            article = articleBean,
                            onItemClick = {
                                onNavigate(WebViewNavKey.create(
                                    url = it,
                                    articleId = articleBean.id,
                                    isCollect = articleBean.collect)
                                )
                            },
                            onSearch = onSearch
                        )
                    }

                    items(articleList.size, key = {
                        "articleList:${articleList[it].id}"
                    }) { index ->
                        val articleBean = articleList[index]
                        ArticleItemWidget(
                            isTopArticle = false,
                            article = articleBean,
                            onItemClick = {
                                onNavigate(WebViewNavKey.create(
                                    url = it,
                                    articleId = articleBean.id,
                                    isCollect = articleBean.collect)
                                )
                            },
                            onSearch = onSearch
                        )
                    }
                })
        }
    }
}


@OptIn(ExperimentalGridApi::class)
@Composable
private fun PopularPager(
    onNavigate: (NavKey) -> Unit,
    route: List<RouteBean>,
    wenda: List<ArticleBean>,
    column: List<ColumnBean>,
) {
    val rememberCoroutineScope = rememberCoroutineScope()
    val tabRowItems = remember { listOf(
        HomeTabItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.navigate),
            content = {
                HomeNavigateItem(onNavigate)
            }
        ),

        HomeTabItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.the_latest_learning_route), content = {
                PopularRoute(route) {
                    onNavigate(WebViewNavKey.create(it))
                }
            }
        ),

        HomeTabItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.the_most_popular_q_a), content = {
                PopularWenda(wenda) {
                    onNavigate(WebViewNavKey.create(it))
                }
            }
        ),

        HomeTabItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.the_most_popular_column), content = {
                PopularColumn(column) {
                    onNavigate(WebViewNavKey.create(it))
                }
            }
        )
    ) }
    val rememberPagerState = rememberPagerState(pageCount = { tabRowItems.size })
    val containerDpSize = LocalWindowInfo.current.containerDpSize.width.value - 10*2
    PrimaryScrollableTabRow(
        selectedTabIndex = rememberPagerState.currentPage,
        minTabWidth = (containerDpSize / 4).dp,
        edgePadding = 10.dp
    ) {
        tabRowItems.forEachIndexed { index, item ->
            Tab(selected = false, text = { Text(item.name) }, onClick = {
                rememberCoroutineScope.launch {
                    rememberPagerState.animateScrollToPage(index)
                }
            })
        }
    }
    HorizontalPager(
        rememberPagerState,
        modifier = Modifier.fillMaxWidth().height(175.dp),
    ) { page ->
        tabRowItems[page].content()
    }
}

@Composable
@OptIn(ExperimentalGridApi::class)
private fun HomeNavigateItem(onNavigate: (NavKey) -> Unit) {
    val navItems = remember { navigationData(onNavigate) }
    Grid(
        modifier = Modifier.fillMaxSize()
            .padding(10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer),
        config = {
            repeat(4) { column(1.fr) }
            repeat(1) { row(1.fr) }
        }
    ) {
        navItems.forEachIndexed { index, data ->
            Box(
                modifier = Modifier
                    .height(70.dp)
                    .fillMaxWidth()
                    .clickable {
                        data.onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(data.icon),
                        contentDescription = null,
                    )
                    Text(data.name)
                }
            }
        }
    }
}

@Composable
private fun PopularColumn(column: List<ColumnBean>, onClick: (String) -> Unit) {

    val columnItems = column.map { columnItem ->
        ItemBoxData.ClickableItemData(title = columnItem.name, onClick = {
            onClick(HttpConfig.showColumn(columnItem.subChapterId))
        })
    }

    ColumnItemsBox(columnItems)
}

@Composable
private fun PopularWenda(wenda: List<ArticleBean>, onClick: (String) -> Unit) {

    val wendaItems = wenda.map { wendaItem ->
        ItemBoxData.ClickableItemData(title = wendaItem.title, onClick = {
            onClick(HttpConfig.showWenda(wendaItem.id))
        })
    }
    ColumnItemsBox(wendaItems)
}

@Composable
private fun PopularRoute(route: List<RouteBean>, onClick: (String) -> Unit) {

    val routeItems = route.map { routeItem ->
        ItemBoxData.ClickableItemData(title = routeItem.name, onClick = {
            onClick(HttpConfig.showRoute(routeItem.id))
        })
    }
    ColumnItemsBox(routeItems)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BannerBrowseCarousel(
    banner: List<HomeBannerBean>, onClick: (String) -> Unit
) {
    val containerDpSize = LocalWindowInfo.current.containerDpSize
    // 获取屏幕宽度（单位为 dp）
    val size = banner.size
    val rememberCarouselState = rememberCarouselState(itemCount = {
        size
    })

    LaunchedEffect(banner) {
        while (isActive) {
            delay(8000)
            if (rememberCarouselState.isScrollInProgress) {
                continue
            }
            var nextItem = rememberCarouselState.currentItem + 1
            if (nextItem >= size) {
                nextItem = 0
            }
            rememberCarouselState.animateScrollToItem(nextItem)
        }
    }

    HorizontalMultiBrowseCarousel(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        state = rememberCarouselState,
        preferredItemWidth = containerDpSize.width,
        minSmallItemWidth = 0.dp,
    ) { index ->
        val url = banner[index].imagePath
        LogUtils.d(TAG, "BannerBrowseCarousel url:$url")
        Box {
            AsyncImage(
                model = url,
                placeholder = painterResource(R.drawable.search),
                error = painterResource(R.drawable.search),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                onError = {
                    LogUtils.d(TAG, "onError:${it.result.throwable}")
                },
                modifier = Modifier
                    .clickable {
                        onClick.invoke(banner[index].url)
                    }
                    .fillMaxWidth()
                    .align(Alignment.Center))
        }

    }
}