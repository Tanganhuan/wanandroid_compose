package com.go.navigate.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.go.common.http.HttpConfig
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.common.widget.AppTopBackBox
import com.go.common.widget.ListUiStateWidget
import com.go.navigate.viewmodel.ToolKitViewModel

@OptIn(ExperimentalGridApi::class)
@Preview(apiLevel = 36)
@Composable
fun ToolKitScreenPreview() {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(3),
//        contentPadding = PaddingValues(10.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        (0..100).forEach {
//            item {
//                Card() {
//                    Text("LazyVerticalGrid Item")
//                }
//            }
//        }
//
//    }
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun ToolKitScreen(navigator: INavigator) {
    val toolKitViewModel: ToolKitViewModel = viewModel(factory = ToolKitViewModel.Factory)
    val listUiState = toolKitViewModel.listUiState.value
    val toolKitList = toolKitViewModel.toolKitList.value

    val onRefresh = {
        toolKitViewModel.getToolsList()
    }
    val onNavigator:(String)-> Unit = { link ->
        navigator.navigate(WebViewNavKey.create(link))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBackBox(title = stringResource(com.go.common.R.string.tool_kit)) {
            navigator.goBack()
        }
        ListUiStateWidget(
            listUiState = listUiState,
            onRefresh = {
                onRefresh()
            }
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(if(LocalConfiguration.current.orientation== Configuration.ORIENTATION_LANDSCAPE) 2 else 1),
                contentPadding = PaddingValues(10.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalItemSpacing = 5.dp
            ) {
                toolKitList.forEachIndexed { index, bean ->
                    item {
                        Card(modifier = Modifier.clickable {
                            onNavigator(bean.link)
                        }) {

                            Row(modifier = Modifier.padding(10.dp)) {
                                AsyncImage(
                                    model = HttpConfig.toolsIcon(bean.icon),
                                    placeholder = painterResource(com.go.common.R.drawable.default_project_img),
                                    contentDescription = null,
                                    modifier = Modifier.size(70.dp).padding(end = 10.dp)
                                )
                                Column {
                                    Text(
                                        text = bean.name,
                                        fontWeight = FontWeight.Medium,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(bean.desc)
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}