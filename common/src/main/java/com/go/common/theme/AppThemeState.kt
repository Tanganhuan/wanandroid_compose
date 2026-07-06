package com.go.common.theme

import android.os.Parcelable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

val emptyAppThemeState = AppThemeState()
@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class AppThemeState(
    val darkThemeFollowSystem: Boolean = true,
    val darkTheme: Boolean = false,
    val primaryColor: ULong = Color.Blue.value,
    val statusBarFollowPrimaryColor: Boolean = false
): Parcelable

val LocalAppThemeState = staticCompositionLocalOf { emptyAppThemeState }

