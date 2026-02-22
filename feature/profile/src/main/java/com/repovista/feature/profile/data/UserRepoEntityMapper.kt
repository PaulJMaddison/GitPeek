package com.repovista.feature.profile.data

import com.repovista.core.database.entity.UserRepoEntity
import com.repovista.core.model.RepoSummary

fun UserRepoEntity.toRepoSummary(): RepoSummary = RepoSummary(
    id = repoId,
    fullName = fullName,
    description = description,
    stars = stars,
    language = language,
    ownerAvatarUrl = ownerAvatarUrl
)
