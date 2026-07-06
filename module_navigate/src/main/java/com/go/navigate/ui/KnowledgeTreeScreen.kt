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
import com.go.common.theme.safetyColors
import com.go.common.widget.AppTopBackBox
import com.go.common.widget.ListUiStateWidget
import com.go.common.widget.PagingPullToRefreshLazyColumn
import com.go.navigate.nav.ArticleListScreenKey
import com.go.navigate.viewmodel.KnowledgeViewModel

@Composable
fun KnowledgeTreeScreen(navigator: INavigator) {

    val knowledgeViewModel: KnowledgeViewModel = viewModel(factory = KnowledgeViewModel.Factory)
    val knowledgeTree = knowledgeViewModel.knowledgeTree.value
    val knowledgeTreeUiState = knowledgeViewModel.knowledgeTreeUiState.value
    val onRefresh = {
        knowledgeViewModel.getKnowledgeTree()
    }
    val onNavigator:(String,Int)->Unit = { title,id ->
        navigator.navigate(ArticleListScreenKey.create(title = title,cid = id))
    }
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBackBox(title = stringResource(com.go.common.R.string.system)) {
            navigator.goBack()
        }
        ListUiStateWidget(listUiState = knowledgeTreeUiState, onRefresh = {
            onRefresh()
        }) {
            PagingPullToRefreshLazyColumn(
                listUiState = knowledgeTreeUiState,
                contentListSize = knowledgeTree.size,
                onRefresh = {
                    onRefresh()
                },
                onLoadMore = {},
                content = {
                    knowledgeTree.forEachIndexed { index, knowledge ->
                        stickyHeader(key = knowledge.id) {
                            Text(
                                modifier = Modifier.fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainer).padding(10.dp),
                                text = knowledge.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        item {
                            FlowRow {
                                knowledge.children.forEachIndexed { index, knowledge ->
                                    Text(knowledge.name, modifier = Modifier
                                        .padding(8.dp)
                                        .border(width = 2.dp,
                                            color = safetyColors[index%safetyColors.size],
                                            shape = RoundedCornerShape(4.dp)
                                        ).clickable{
                                            onNavigator.invoke(knowledge.name,knowledge.id)
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