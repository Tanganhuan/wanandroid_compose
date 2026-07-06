package com.go.common.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.blankj.utilcode.util.LogUtils
import com.go.common.R
import com.go.common.data.ListUiState

@Composable
fun ListUiStateWidget(
    listUiState: ListUiState,
    onRefresh:()->Unit,
    content: @Composable () -> Unit
) {
    LogUtils.d("ListUiStateWidgetTAG","ListUiStateWidget listUiState:$listUiState")
    if(listUiState is  ListUiState.Idel) {
        Box {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }
        LaunchedEffect(Unit) {
            onRefresh()
        }
    } else if(listUiState is ListUiState.Refreshing && listUiState.isEmptyData){
        Box {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }
    } else if(listUiState is ListUiState.RefreshError && listUiState.isEmptyData) {
        RefreshErrorWidget(listUiState, onRefresh)
    }else if(listUiState is ListUiState.RefreshSucceed && listUiState.loadedCount<=0) {
        RefreshEmptyWidget(onRefresh)
    } else{
        content()
    }
}

@Composable
fun RefreshErrorWidget(
    listUiState: ListUiState.RefreshError,
    onRefresh: () -> Unit
) {
    Box {
        Text(
            "${stringResource(R.string.refresh_failed_please_try_again)}\r\n${listUiState.message}",
            modifier = Modifier
                .clickable {
                    onRefresh()
                }
                .fillMaxSize()
                .wrapContentSize(),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun RefreshEmptyWidget(
    onRefresh: () -> Unit
) {
    Box {
        Text(
            stringResource(R.string.empty_data_has_been_loaded),
            modifier = Modifier
                .clickable {
                    onRefresh()
                }
                .fillMaxSize()
                .wrapContentSize(),
            textAlign = TextAlign.Center
        )
    }
}
