package com.go.common.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.blankj.utilcode.util.LogUtils

private const val TAG = "AppLifecycleHandlerTAG"
@Composable
fun AppLifecycleHandler(
    onForeground: () -> Unit = {},
    onReforeground: () -> Unit = {},
    onBackground: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        var isOnStop = false
        val observer = LifecycleEventObserver { _, event ->
            LogUtils.d(TAG,"AppLifecycleHandler LifecycleEventObserver event:$event\tisOnStop:$isOnStop")
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if(isOnStop) {
                        onReforeground()
                    }
                    isOnStop = false
                    onForeground()
                }
                Lifecycle.Event.ON_STOP -> {
                    isOnStop = true
                    onBackground()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

}