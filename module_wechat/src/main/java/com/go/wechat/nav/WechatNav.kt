package com.go.wechat.nav

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.go.common.navigation3.INavigator
import com.go.wechat.ui.WechatScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data object WechatScreenKey: NavKey, Parcelable

@Composable
fun EntryProviderScope<NavKey>.WechatNavEntries(navigator: INavigator) {
    entry<WechatScreenKey> {
        WechatScreen(navigator)
    }
}