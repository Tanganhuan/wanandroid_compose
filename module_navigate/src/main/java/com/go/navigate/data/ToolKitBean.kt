package com.go.navigate.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class ToolKitBean(
    val desc: String = "",
    val icon: String = "",
    val id: Int = -1,
    val isNew: Int = -1,
    val link: String = "",
    val name: String = "",
    val order: Int = -1,
    val showInTab: Int = -1,
    val tabName: String = "",
    val visible: Int = -1,
): Parcelable