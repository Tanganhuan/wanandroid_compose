package com.go.project.nav

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.go.common.navigation3.INavigator
import com.go.project.ui.PictureBrowserScreen
import com.go.project.ui.ProjectScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data object ProjectScreenNavKey: NavKey, Parcelable


@Serializable
@Parcelize
data class PictureBrowserScreenNavKey(val url:String): NavKey, Parcelable {

    companion object {
        fun create(url:String): NavKey {
            return PictureBrowserScreenNavKey(url)
        }
    }
}


@Composable
fun EntryProviderScope<NavKey>.ProjectNavEntries(navigator: INavigator) {
    entry<ProjectScreenNavKey> {
        ProjectScreen(navigator)
    }
    entry<PictureBrowserScreenNavKey> {
        PictureBrowserScreen(navigator,url = it.url
        )
    }
}
