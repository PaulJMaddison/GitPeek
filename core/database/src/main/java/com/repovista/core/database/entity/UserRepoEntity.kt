package com.repovista.core.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "user_repos",
    primaryKeys = ["username", "repoId"],
    indices = [Index(value = ["username", "page"]), Index(value = ["username", "repoId"]) ]
)
data class UserRepoEntity(
    val username: String,
    val repoId: Long,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val language: String?,
    val ownerAvatarUrl: String,
    val page: Int,
    val indexInPage: Int
)
