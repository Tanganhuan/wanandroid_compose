package com.go.wanandroid.nav

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.blankj.utilcode.util.LogUtils
import com.go.common.navigation3.INavigator
import com.go.mine.nav.ConditionalNavKey

private const val TAG = "NavigatorTAG"
class Navigator(
    val backStack: NavBackStack<NavKey>,
    private val onNavigateToRestrictedKey: (targetKey: ConditionalNavKey?) -> ConditionalNavKey,
    private val isLoggedIn: () -> Boolean): INavigator {

    override fun navigate(key: NavKey) {
        LogUtils.d(TAG,"navigate key:${key}")
        if(key is ConditionalNavKey && key.requiresLogin && !isLoggedIn()) {
            val loginKey = onNavigateToRestrictedKey(key)
            backStack.add(loginKey)
        } else {
            backStack.add(key)
        }
    }

    override fun goBack() {
        LogUtils.d(TAG,"goBack backStack.size1:${backStack.size}")
        if(backStack.size <=1) {
            return
        }
        backStack.removeAt(backStack.lastIndex)
        LogUtils.d(TAG,"goBack backStack.size2:${backStack.size}")
    }
}