package com.go.mine.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * 用户信息
 */

val emptyUserBean: UserBean = UserBean()

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class UserBean(
    val admin: Boolean = false,
    val coinCount: Int = 0,
    val chapterTops: List<String> = listOf(),
    val collectIds: List<String> = listOf(),
    val email: String = "",
    val icon: String = "",
    val id: Int = -1,
    val nickname: String = "",
    val password: String = "",
    val publicName: String = "",
    val token: String = "",
    val type: Int = 0,
    val username: String = ""
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
@Serializable
data class UserCoinInfo(
    val coinCount: Int = -1,
    val level: Int = -1,
    val nickname: String = "",
    val rank: String = "",
    val userId: Int = -1,
    val username: String = ""
): Parcelable
