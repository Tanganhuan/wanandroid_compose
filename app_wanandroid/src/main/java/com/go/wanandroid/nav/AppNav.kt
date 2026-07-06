package com.go.wanandroid.nav

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.go.common.navigation3.INavigator
import com.go.home.nav.HomeScreenNavKey
import com.go.home.ui.HomeScreen
import com.go.mine.nav.MineScreenNavKey
import com.go.mine.ui.MineScreen
import com.go.project.nav.ProjectScreenNavKey
import com.go.project.ui.ProjectScreen
import com.go.square.nav.SquareScreenNavKey
import com.go.square.ui.SquareScreen
import com.go.wanandroid.R
import com.go.wechat.nav.WechatScreenKey
import com.go.wechat.ui.WechatScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class NavBarItem(
    val icon: Int,
    val selectedIcon: Int,
    val label: Int,
    val content: @Composable (navigator: INavigator) -> Unit,
) : Parcelable


@Serializable
data object MainViewPagerNavKey : NavKey

val TOP_LEVEL_ROUTES = listOf<Pair<NavKey, NavBarItem>>(
    HomeScreenNavKey to NavBarItem(
        icon = R.drawable.home,
        selectedIcon = R.drawable.home_selected,
        label = com.go.common.R.string.home,
        content = { navigator ->
            HomeScreen(navigator)
        }
    ),
    SquareScreenNavKey to NavBarItem(
        icon = R.drawable.square,
        selectedIcon = R.drawable.square_selected,
        label = com.go.common.R.string.square,
        content = { navigator ->
            SquareScreen(navigator)
        }
    ),
    ProjectScreenNavKey to NavBarItem(
        icon = R.drawable.project,
        selectedIcon = R.drawable.project_selected,
        label = com.go.common.R.string.project,
        content = { navigator ->
            ProjectScreen(navigator)
        }
    ),
    WechatScreenKey to NavBarItem(
        icon = R.drawable.wechat_account,
        selectedIcon = R.drawable.wechat_acount_selected,
        label = com.go.common.R.string.wechat_account,
        content = { navigator ->
            WechatScreen(navigator)
        }
    ),
    MineScreenNavKey to NavBarItem(
        icon = R.drawable.mine,
        selectedIcon = R.drawable.mine_selected,
        label = com.go.common.R.string.mine,
        content = { navigator ->
            MineScreen(navigator)
        }
    ),
)


