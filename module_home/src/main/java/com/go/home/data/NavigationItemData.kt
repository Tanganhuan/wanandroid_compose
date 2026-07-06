package com.go.home.data

import androidx.navigation3.runtime.NavKey
import com.go.common.BaseApplication
import com.go.common.http.HttpConfig
import com.go.common.navigation3.WebViewNavKey
import com.go.home.R
import com.go.navigate.nav.AndroidNavigateScreenKey
import com.go.navigate.nav.CourseScreenKey
import com.go.navigate.nav.HarmonyosScreenKey
import com.go.navigate.nav.KnowledgeTreeScreenKey
import com.go.navigate.nav.RankListScreenKey
import com.go.navigate.nav.ToolKitScreenKey
import com.go.navigate.nav.WendaScreenKey

data class NavigationItemData(val name: String, val icon: Int, val onClick:()->Unit)


internal fun navigationData(onNavigate: (NavKey) -> Unit): List<NavigationItemData> {
    val navItems = listOf<NavigationItemData>(
        NavigationItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.harmony_os),
            icon = R.drawable.harmony_os,
            onClick = {
                onNavigate(HarmonyosScreenKey)
            }
        ),
        NavigationItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.route),
            icon = R.drawable.route,
            onClick = {
                onNavigate(WebViewNavKey.create(HttpConfig.ALL_ROUTE))
            }),
        NavigationItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.questions_and_answers),
            icon = R.drawable.questions_and_answers,
            onClick = {
                onNavigate(WendaScreenKey)
            }
        ),
        NavigationItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.course),
            icon = R.drawable.course,
            onClick = {
                onNavigate(CourseScreenKey)
            }),
        NavigationItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.system),
            icon = R.drawable.system_fill,
            onClick = {
                onNavigate(KnowledgeTreeScreenKey)
            }
        ),
        NavigationItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.tool_kit),
            icon = R.drawable.tool_kit,
            onClick = {
                onNavigate(ToolKitScreenKey)
            },
        ),
        NavigationItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.ranking_list),
            icon = R.drawable.ranking_list,
            onClick = {
                onNavigate(RankListScreenKey)
            }
        ),
        NavigationItemData(
            name = BaseApplication.Instance.getString(com.go.common.R.string.navigate),
            icon = R.drawable.navigation,
            onClick = {
                onNavigate(AndroidNavigateScreenKey)
            }
        ),
    )
    return navItems
}