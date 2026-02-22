package com.repovista.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: Long,
    val login: String,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "html_url") val htmlUrl: String,
    val bio: String?,
    val company: String?,
    val location: String?,
    @Json(name = "public_repos") val publicRepos: Int,
    val followers: Int,
    val following: Int
)
