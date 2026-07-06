package com.go.square.nav

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.go.common.navigation3.INavigator
import com.go.square.ui.SquareScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data object SquareScreenNavKey: NavKey, Parcelable

@Composable
fun EntryProviderScope<NavKey>.SquareNavEntries(navigator: INavigator) {
    entry<SquareScreenNavKey> {
        SquareScreen(navigator)
    }
}