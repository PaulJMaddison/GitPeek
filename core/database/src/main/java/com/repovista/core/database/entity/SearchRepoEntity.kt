package com.repovista.core.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "search_repos",
    primaryKeys = ["query", "repoId"],
    indices = [Index(value = ["query", "page"]), Index(value = ["query", "repoId"]) ]
)
data class SearchRepoEntity(
    val query: String,
    val repoId: Long,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val language: String?,
    val ownerAvatarUrl: String,
    val page: Int,
    val indexInPage: Int
)
