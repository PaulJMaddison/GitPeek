package com.repovista.core.model

data class RepoSummary(
    val id: Long,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val language: String?,
    val ownerAvatarUrl: String
)
