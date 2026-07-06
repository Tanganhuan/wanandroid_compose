package com.go.common.extension

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

val Color.hex: String
get() = String.format("#%08X", this.toArgb())

fun Color.toLong() = this.value

fun ULong.toColor() = Color(this)