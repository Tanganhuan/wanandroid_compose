package com.go.common.navigation3

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey

@Stable
interface INavigator {

    fun navigate(key: NavKey)
    fun goBack()
}

class EmptyNavigator(): INavigator {
    override fun navigate(key: NavKey) {}

    override fun goBack() {}
}
