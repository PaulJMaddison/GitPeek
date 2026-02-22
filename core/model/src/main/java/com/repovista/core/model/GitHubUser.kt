package com.repovista.core.model

data class GitHubUser(
    val login: String,
    val name: String?,
    val avatarUrl: String,
    val followers: Int,
    val following: Int,
    val publicRepos: Int,
    val bio: String?
)
