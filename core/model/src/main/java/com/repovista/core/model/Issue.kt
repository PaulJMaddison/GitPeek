package com.repovista.core.model

import java.time.Instant

data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    val authorLogin: String,
    val comments: Int,
    val createdAt: Instant
)
