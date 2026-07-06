package com.go.common.theme

import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.blankj.utilcode.util.LogUtils
import com.go.common.extension.toColor

class SystemUiController(
    private val activity: ComponentActivity
) {

    companion object {
        const val TAG = "SystemUiControllerTAG"
    }

    @Composable
    fun StatusBarDarkIcons(appThemeState: AppThemeState) {
        LogUtils.d(TAG,"StatusBarDarkIcons appThemeState:$appThemeState")
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = !appThemeState.darkTheme
            isAppearanceLightNavigationBars = !appThemeState.darkTheme
        }

        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        activity.window.decorView.setBackgroundColor(
            if (appThemeState.darkTheme) Color.Black.toArgb()
            else if (appThemeState.statusBarFollowPrimaryColor)
                appThemeState.primaryColor.toColor().toArgb()
            else MaterialTheme.colorScheme.surface.toArgb()
        )
        activity.HandleEnableEdgeToEdge(
            appThemeState = appThemeState,
            isDarkTheme = appThemeState.darkTheme
        )
    }

    @Composable
    private fun ComponentActivity.HandleEnableEdgeToEdge(
        appThemeState: AppThemeState,
        isDarkTheme: Boolean
    ) {
        LogUtils.d(TAG,"HandleEnableEdgeToEdge isDarkTheme:$isDarkTheme")
        val surface = MaterialTheme.colorScheme.surface
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = if (appThemeState.statusBarFollowPrimaryColor)
                    appThemeState.primaryColor.toColor().toArgb()
                else surface.toArgb(),
                darkScrim = Color.Black.toArgb()
            ) {
                isDarkTheme
            },
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = if (appThemeState.statusBarFollowPrimaryColor)
                    appThemeState.primaryColor.toColor().toArgb()
                else surface.toArgb(),
                darkScrim = Color.Black.toArgb()
            ) {
                isDarkTheme
            }
        )
    }

    @Composable
    fun HideSystemBars() {
        LocalActivity.current?.window?.let { window ->
            hideSystemBars(window)
        }
    }

    @Composable
    fun ShowSystemBars() {
        LocalActivity.current?.window?.let { window ->
            showSystemBars(window)
        }
    }

    fun hideSystemBars(window: Window) {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        // 隐藏系统栏
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        // 设置滑动恢复行为（用户从边缘滑动时临时显示）
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    fun showSystemBars(window: Window) {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }

}