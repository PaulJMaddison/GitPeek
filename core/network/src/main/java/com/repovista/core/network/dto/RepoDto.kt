package com.repovista.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RepoDto(
    val id: Long,
    val name: String,
    @Json(name = "full_name") val fullName: String,
    val description: String?,
    val language: String?,
    @Json(name = "stargazers_count") val stargazersCount: Int,
    @Json(name = "forks_count") val forksCount: Int,
    @Json(name = "open_issues_count") val openIssuesCount: Int,
    @Json(name = "html_url") val htmlUrl: String,
    @Json(name = "updated_at") val updatedAt: String,
    val owner: OwnerDto
)
