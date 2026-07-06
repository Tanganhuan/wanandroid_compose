package com.go.navigate.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.LogUtils
import com.go.common.navigation3.INavigator
import com.go.common.widget.AppTopBackBox
import com.go.mine.ui.widget.ArticleItemWidget
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.navigate.nav.ArticleListScreenKey
import com.go.navigate.viewmodel.CourseViewModel

private const val TAG = "CourseChapterScreenTAG"
@Composable
fun CourseScreen(navigator: INavigator) {
    val courseViewModel: CourseViewModel = viewModel(factory = CourseViewModel.Factory)
    SideEffect {
        LogUtils.d(TAG,"courseViewModel:${courseViewModel.hashCode()}")
    }
    val listUiState = courseViewModel.courseListUiState.value
    val courseList = courseViewModel.courseList.value
    val onRefresh = {
        courseViewModel.getCourseList()
    }
    val onNavigator:(String,Int)->Unit = { title,id ->
        navigator.navigate(ArticleListScreenKey.create(
            title = title,
            cid = id,
            orderType = 1)
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBackBox(title = stringResource(com.go.common.R.string.course)) {
            navigator.goBack()
        }
        ListUiStateWidget(listUiState = listUiState, onRefresh = {
            onRefresh()
        }) {
            PagingPullToRefreshLazyColumn(
                listUiState = listUiState,
                contentListSize = courseList.size,
                onRefresh = {
                    onRefresh()
                },
                onLoadMore = {},
                content = {
                    items(count = courseList.size,key = {
                        courseList[it].id
                    }) {index ->
                        ArticleItemWidget(
                            visibleCollectRow = false,
                            article = courseList[index],
                            onItemClick = {
                                onNavigator(courseList[index].title, courseList[index].id)
                            }
                        )
                    }
                }
            )
        }
    }
}