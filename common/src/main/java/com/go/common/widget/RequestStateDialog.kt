package com.go.common.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.go.common.R
import com.go.common.data.RequestState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Preview
@Composable
fun RequestStateDialogPreview() {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center) {
            BasicAlertDialogContent(state = RequestState.Loading("收藏中..."))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestStateDialog(
    state: RequestState,
) {

    var showDialog by remember { mutableStateOf(false) }
    if(state is RequestState.Success || state is RequestState.Error) {
        LaunchedEffect(state) {
            delay(1500.milliseconds)
            if(showDialog) {
                showDialog = false
            }
        }
    }else if(state is RequestState.Dismiss) {
        showDialog = false
    } else if(state is RequestState.Loading) {
        showDialog = true
    }

    if(!showDialog) {
        return
    }

    BasicAlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = false,
        ),
        onDismissRequest = {
            showDialog = false
        },

        content = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicAlertDialogContent(state = state)
                }
            }
        },
    )
}

@Composable
private fun BasicAlertDialogContent(state: RequestState) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
        ) {
            Box(modifier = Modifier.weight(1f).align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center) {
                when (state) {
                    is RequestState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is RequestState.Success -> {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp).align(Alignment.Center)
                        )
                    }

                    is RequestState.Error -> {
                        Icon(
                            painter = painterResource(R.drawable.error),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp).align(Alignment.Center)
                        )
                    }
                    else -> {

                    }
                }
            }

            Text(
                text = state.msg,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 14.dp).fillMaxWidth()
            )
        }
    }
}