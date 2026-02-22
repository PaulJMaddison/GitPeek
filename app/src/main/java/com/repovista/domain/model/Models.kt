package com.repovista.domain.model

data class RepoOwner(
    val login: String,
    val avatarUrl: String
)

data class Repo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val forks: Int,
    val openIssues: Int,
    val language: String?,
    val htmlUrl: String,
    val owner: RepoOwner
)

data class User(
    val id: Long,
    val username: String,
    val name: String?,
    val bio: String?,
    val avatarUrl: String,
    val followers: Int,
    val following: Int,
    val publicRepos: Int
)

data class Issue(
    val id: Long,
    val title: String,
    val body: String?,
    val state: String,
    val htmlUrl: String,
    val author: RepoOwner
)
