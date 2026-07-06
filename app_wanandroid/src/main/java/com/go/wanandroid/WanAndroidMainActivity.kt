package com.go.wanandroid

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.blankj.utilcode.util.LogUtils
import com.go.common.datastore.AppThemeStateDataStore
import com.go.common.lifecycle.AppLifecycleHandler
import com.go.common.theme.SystemUiController
import com.go.common.theme.emptyAppThemeState
import com.go.wanandroid.ui.MainScreen
import com.go.wanandroid.ui.GuideScreen
import com.go.wanandroid.ui.SplashScreen
import com.go.wanandroid.ui.theme.AppWanandroidComposeTheme

enum class LaunchScreenEnum(val index: Int, val content: @Composable (MutableState<Int>) -> Unit) {
    Splash(index = 0, content = { mutableState ->
        SplashScreen {
            mutableState.value = if (it) Guide.ordinal else Main.ordinal
        }
    }),
    Guide(index = 1, content = { mutableState ->
        GuideScreen {
            mutableState.value = Main.ordinal
        }
    }),

    Main(index = 2, content = {
        MainScreen()
    }),
}

class WanAndroidMainActivity : ComponentActivity() {

    companion object {
        const val TAG = "WanAndroidMainActivityTAG"
    }

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 31) {
            val splashScreen = installSplashScreen()
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        LogUtils.d(TAG, "onCreate savedInstanceState:$savedInstanceState")
        setContent {
            LogUtils.d(TAG, "WanAndroidMainActivity:${LocalViewModelStoreOwner.current}")

            var appThemeState =
                AppThemeStateDataStore.read().collectAsState(initial = emptyAppThemeState).value
            if(appThemeState.darkThemeFollowSystem) {
                appThemeState = appThemeState.copy(darkTheme = isSystemInDarkTheme())
            }
            val systemUiController = remember {
                SystemUiController(activity = this)
            }

            val selectIndex = remember { mutableIntStateOf(0) }
            val isSystemInDarkTheme = isSystemInDarkTheme()

            if(selectIndex.intValue >= LaunchScreenEnum.Main.ordinal) {
                systemUiController.ShowSystemBars()
            } else {
                systemUiController.HideSystemBars()
            }

            LocalActivity.current?.window?.let { window ->
                AppLifecycleHandler(onReforeground = {
                    LogUtils.d(TAG,"appThemeState:$appThemeState\tisSystemInDarkTheme:$isSystemInDarkTheme")
                    if(selectIndex.intValue >= LaunchScreenEnum.Main.ordinal) {
                        systemUiController.showSystemBars(window)
                    } else {
                        systemUiController.hideSystemBars(window)
                    }
                })
            }

            AppWanandroidComposeTheme(
                appThemeState = appThemeState,
                systemUiController = systemUiController,
            ) {
                Surface {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()) {

                        LaunchScreenEnum.entries.find {
                            it.index == selectIndex.intValue
                        }?.content(selectIndex)
                    }
                }
            }
        }
    }
}

