package com.repovista.data.remote.dto

import com.squareup.moshi.Json

data class GithubSearchResponseDto(
    @Json(name = "items") val items: List<GithubRepoDto>
)

data class GithubRepoDto(
    val id: Long,
    val name: String,
    @Json(name = "full_name") val fullName: String,
    val description: String?,
    @Json(name = "stargazers_count") val stargazersCount: Int,
    @Json(name = "forks_count") val forksCount: Int,
    @Json(name = "open_issues_count") val openIssuesCount: Int,
    val language: String?,
    @Json(name = "html_url") val htmlUrl: String,
    val owner: GithubUserSummaryDto
)

data class GithubUserSummaryDto(
    val login: String,
    @Json(name = "avatar_url") val avatarUrl: String
)

data class GithubUserDto(
    val id: Long,
    val login: String,
    val name: String?,
    val bio: String?,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "followers") val followers: Int,
    @Json(name = "following") val following: Int,
    @Json(name = "public_repos") val publicRepos: Int
)

data class GithubIssueDto(
    val id: Long,
    val title: String,
    val body: String?,
    val state: String,
    @Json(name = "html_url") val htmlUrl: String,
    val user: GithubUserSummaryDto
)
