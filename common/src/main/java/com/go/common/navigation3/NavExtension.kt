package com.go.common.navigation3

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay

val LocalNavigator = staticCompositionLocalOf<INavigator> {
    error("未提供 Navigator！请确保在 CompositionLocalProvider 中包装。")
}

// 定义一个通用的无动画 Entry 扩展函数
inline fun <reified T : NavKey> EntryProviderScope<NavKey>.noTransitionEntry(
    noinline content: @Composable (T) -> Unit
) {
    entry<T>(
        metadata = metadata {
            // 配置常规进入/退出无动画
            put(NavDisplay.TransitionKey) {
                slideInHorizontally(initialOffsetX = { 0 }) togetherWith
                        slideOutHorizontally(targetOffsetX = { 0 })
            }
            // 配置返回（Pop）无动画
            put(NavDisplay.PopTransitionKey) {
                slideInHorizontally(initialOffsetX = { 0 }) togetherWith
                        slideOutHorizontally(targetOffsetX = { 0 })
            }
            // 配置预测性返回无动画
            put(NavDisplay.PredictivePopTransitionKey) {
                slideInHorizontally(initialOffsetX = { 0 }) togetherWith
                        slideOutHorizontally(targetOffsetX = { 0 })
            }
        },
        content = content
    )
}