package com.go.navigate.nav

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.go.common.navigation3.INavigator
import com.go.navigate.ui.AndroidNavigateScreen
import com.go.navigate.ui.ArticleListScreen
import com.go.navigate.ui.CourseScreen
import com.go.navigate.ui.HarmonyosScreen
import com.go.navigate.ui.KnowledgeTreeScreen
import com.go.navigate.ui.RankListScreen
import com.go.navigate.ui.SearchInputScreen
import com.go.navigate.ui.SearchScreen
import com.go.navigate.ui.ToolKitScreen
import com.go.navigate.ui.WendaScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data object NavigateScreenKey: NavKey, Parcelable

@Serializable
@Parcelize
data object HarmonyosScreenKey: NavKey, Parcelable

@Serializable
@Parcelize
data object WendaScreenKey: NavKey, Parcelable

@Serializable
@Parcelize
data object CourseScreenKey: NavKey, Parcelable

@Serializable
@Parcelize
data class ArticleListScreenKey(
    val title: String,
    val cid:Int,
    val author:String?=null,
    val orderType:Int = 0,
): NavKey, Parcelable {
    companion object {
        fun create(title:String,cid: Int,author:String?=null,orderType:Int?=null):ArticleListScreenKey {
            return ArticleListScreenKey(title=title,cid=cid,author = author,orderType = orderType?:0)
        }
    }
}

@Serializable
@Parcelize
data object KnowledgeTreeScreenKey: NavKey, Parcelable

@Serializable
@Parcelize
data object ToolKitScreenKey: NavKey, Parcelable

@Serializable
@Parcelize
data object RankListScreenKey: NavKey, Parcelable

@Serializable
@Parcelize
data object AndroidNavigateScreenKey: NavKey, Parcelable

@Serializable
@Parcelize
data class SearchScreenKey(val keyword:String): NavKey, Parcelable {

    companion object {
        fun create(keyword:String):SearchScreenKey {
            return SearchScreenKey(keyword = keyword)
        }
    }
}

@Serializable
@Parcelize
data object SearchInputScreenKey: NavKey, Parcelable {
}

@Composable
fun EntryProviderScope<NavKey>.NavigateNavEntries(navigator: INavigator) {

    entry<HarmonyosScreenKey> {
        HarmonyosScreen(navigator)
    }

    entry<WendaScreenKey> {
        WendaScreen(navigator)
    }

    entry<CourseScreenKey> {
        CourseScreen(navigator)
    }

    entry<ArticleListScreenKey> {
        ArticleListScreen(title = it.title, cid = it.cid, author = it.author,orderType = it.orderType,navigator = navigator)
    }

    entry<KnowledgeTreeScreenKey> {
        KnowledgeTreeScreen(navigator)
    }
    entry<ToolKitScreenKey> {
        ToolKitScreen(navigator)
    }

    entry<RankListScreenKey> {
        RankListScreen(navigator)
    }

    entry<AndroidNavigateScreenKey> {
        AndroidNavigateScreen(navigator)
    }

    entry<SearchScreenKey> {
        SearchScreen(keyword = it.keyword,navigator = navigator)
    }

    entry<SearchInputScreenKey> {
        SearchInputScreen(navigator = navigator)
    }
}