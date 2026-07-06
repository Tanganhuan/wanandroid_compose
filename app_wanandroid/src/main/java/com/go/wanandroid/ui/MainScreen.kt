package com.go.wanandroid.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.blankj.utilcode.util.LogUtils
import com.go.common.navigation3.LocalNavigator
import com.go.home.nav.HomeNavEntries
import com.go.mine.nav.LoginScreenNavKey
import com.go.mine.nav.MineNavEntries
import com.go.mine.viewmodel.UserViewModel
import com.go.navigate.nav.NavigateNavEntries
import com.go.project.nav.ProjectNavEntries
import com.go.square.nav.SquareNavEntries
import com.go.wanandroid.nav.MainViewPagerNavKey
import com.go.wanandroid.nav.Navigator
import com.go.wechat.nav.WechatNavEntries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val userViewModel = viewModel<UserViewModel>(factory = UserViewModel.Factory,
        viewModelStoreOwner = LocalActivity.current as ViewModelStoreOwner)
    val backStack = rememberNavBackStack(MainViewPagerNavKey)
    val navigator = remember {
        Navigator(
            backStack = backStack,
            onNavigateToRestrictedKey = { redirectToKey -> LoginScreenNavKey(redirectToKey) },
            isLoggedIn = { userViewModel.isLogined() }
        )
    }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        LogUtils.d("LocalViewModelStoreOwnerTAG","MainScreen:${LocalViewModelStoreOwner.current}")
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<MainViewPagerNavKey> {
                    MainViewpagerScreen(navigator)
                }
                HomeNavEntries(navigator)
                SquareNavEntries(navigator)
                ProjectNavEntries(navigator)
                NavigateNavEntries(navigator)
                WechatNavEntries(navigator)
                MineNavEntries(navigator)
            },
            onBack = { backStack.removeAt(backStack.lastIndex) },
            // 1. 定义前进动画 (从右向左滑入)
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
            },
            // 2. 定义返回动画 (从左向右滑出)
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                // Slide in from left when navigating back
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
        )
    }
}


