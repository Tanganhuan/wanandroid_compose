package com.go.home.data

import androidx.compose.runtime.Composable

data class HomeTabItemData(val name: String, val content: @Composable () -> Unit)