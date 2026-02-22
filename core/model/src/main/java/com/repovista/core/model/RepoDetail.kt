package com.repovista.core.model

data class RepoDetail(
    val id: Long,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val language: String?,
    val ownerAvatarUrl: String,
    val forks: Int,
    val openIssues: Int,
    val watchers: Int,
    val topics: List<String>?,
    val defaultBranch: String,
    val htmlUrl: String
)
