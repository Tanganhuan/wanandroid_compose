package com.go.navigate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.go.common.navigation3.INavigator
import com.go.common.theme.safetyColors
import com.go.common.widget.ListUiStateWidget
import com.go.navigate.R
import com.go.navigate.data.SearchHotkeyBean
import com.go.navigate.data.emptySearchHotkeyList
import com.go.navigate.nav.SearchScreenKey
import com.go.navigate.viewmodel.SearchInputViewModel

@Preview
@Composable
fun SearchInputScreenPreview() {
    Surface {
        SearchBar(onBack = {}, onSearch = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInputScreen(navigator: INavigator) {
    val viewModel: SearchInputViewModel = viewModel(factory = SearchInputViewModel.Factory)
    val dataList = viewModel.dataList
    val listUiState = viewModel.listUiState
    val searchInputList = viewModel.searchInputList.collectAsStateWithLifecycle(initialValue = emptySearchHotkeyList).value
    val onRefresh = {
        viewModel.refresh()
    }
    val onBack = {
        navigator.goBack()
    }

    val onSearch:(String)->Unit = {
        navigator.navigate(SearchScreenKey.create(it))
        viewModel.addSearchInput(it)
    }

    val onNavigator:(String)->Unit = {
        navigator.navigate(SearchScreenKey.create(it))
    }

    ListUiStateWidget(listUiState = listUiState,onRefresh = onRefresh) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()){
            SearchBar(onBack = onBack,onSearch = onSearch)
            SearchInputWidget(
                title = stringResource(R.string.hot_search),
                dataList = dataList,
                onNavigator = onNavigator)

            if(searchInputList.data.isNotEmpty()) {
                SearchInputWidget(
                    title = stringResource(R.string.historical_search),
                    dataList = searchInputList.data,
                    onNavigator = {
                        onNavigator(it)
                        viewModel.addSearchInput(it)
                    })
            }

        }
    }
}

@Composable
private fun SearchInputWidget(
    title:String,
    dataList: List<SearchHotkeyBean>,
    onNavigator: (String)->Unit,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(10.dp)
    )

    FlowRow {
        dataList.forEachIndexed { index, bean ->
            Text(
                bean.name, modifier = Modifier
                    .padding(8.dp)
                    .border(
                        width = 2.dp,
                        color = safetyColors[index % safetyColors.size],
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable {
                        onNavigator(bean.name)
                    }
                    .padding(10.dp)
            )
        }
    }
}

@Composable
private fun SearchBar(onBack:()->Unit,onSearch:(String)->Unit) {
    var searchText by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .height(48.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(24.dp)
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(com.go.common.R.drawable.keyboard_backspace),
                contentDescription = "返回",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable {
                        onBack.invoke()
                    }
                    .size(48.dp)
                    .padding(start = 12.dp, end = 12.dp)

            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {

                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                BasicTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    maxLines = 1,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch(searchText)
                        },
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                if (searchText.isEmpty()) {
                    Text(
                        text = "请输入关键词...",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart)
                    )
                }
            }
        }
    }
}
