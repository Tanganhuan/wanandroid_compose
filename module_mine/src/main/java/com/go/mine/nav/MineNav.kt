package com.go.mine.nav

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.mine.ui.CollectScreen
import com.go.mine.ui.DarkLightModeScreen
import com.go.mine.ui.LoginScreen
import com.go.mine.ui.MineScreen
import com.go.mine.ui.RegisterScreen
import com.go.mine.ui.SettingsScreen
import com.go.mine.ui.ThemeSettingScreen
import com.go.mine.ui.WebViewScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data object MineScreenNavKey: NavKey, Parcelable

@Serializable
@Parcelize
data object ThemeSettingScreenNavKey: NavKey,Parcelable

@Parcelize
@Serializable
sealed class ConditionalNavKey(val requiresLogin: Boolean = false) : NavKey,Parcelable

@Serializable
@Parcelize
data class LoginScreenNavKey(val redirectToKey: ConditionalNavKey? = null): ConditionalNavKey(),Parcelable

@Serializable
@Parcelize
data object RegisterScreenNavKey: NavKey,Parcelable

@Serializable
@Parcelize
data object SettingsScreenNavKey: NavKey,Parcelable

@Serializable
@Parcelize
data object DarkLightModeScreenNavKey: NavKey,Parcelable

@Serializable
@Parcelize
data object CollectScreenNavKey: ConditionalNavKey(requiresLogin = true),Parcelable

@Composable
fun EntryProviderScope<NavKey>.MineNavEntries(navigator: INavigator) {

    entry<MineScreenNavKey> {
        MineScreen(navigator)
    }

    entry<ThemeSettingScreenNavKey> {
        ThemeSettingScreen(navigator)
    }

    entry<LoginScreenNavKey> {
        LoginScreen(navigator,it)
    }

    entry<RegisterScreenNavKey> {
        RegisterScreen(navigator)
    }

    entry<SettingsScreenNavKey> {
        SettingsScreen(navigator)
    }

    entry<DarkLightModeScreenNavKey> {
        DarkLightModeScreen(navigator)
    }

    entry<WebViewNavKey> {
        WebViewScreen(
            navigator = navigator,
            url = it.url,
            articleId = it.articleId,
            isCollect = it.isCollect,
        )
    }

    entry<CollectScreenNavKey> {
        CollectScreen(navigator = navigator)
    }
}

