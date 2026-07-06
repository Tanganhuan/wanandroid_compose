package com.go.home.nav

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.go.common.navigation3.INavigator

import com.go.home.ui.HomeScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data object HomeScreenNavKey: NavKey, Parcelable

@Serializable
@Parcelize
data object HomeScreen1NavKey: NavKey, Parcelable


@Composable
fun EntryProviderScope<NavKey>.HomeNavEntries(navigator: INavigator) {
    entry<HomeScreenNavKey> {
        HomeScreen(navigator)
    }
}