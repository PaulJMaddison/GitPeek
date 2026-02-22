package com.repovista.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IssueDto(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val user: UserDto,
    @Json(name = "comments") val commentsCount: Int,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "pull_request") val pullRequest: PullRequestRefDto?
)

@JsonClass(generateAdapter = true)
data class PullRequestRefDto(
    val url: String
)

fun List<IssueDto>.withoutPullRequests(): List<IssueDto> =
    filter { it.pullRequest == null }
