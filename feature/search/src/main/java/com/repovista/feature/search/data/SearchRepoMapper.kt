package com.repovista.feature.search.data

import com.repovista.core.model.RepoSummary
import com.repovista.core.network.dto.RepoDto
import com.repovista.core.network.mapper.DtoMapper

object SearchRepoMapper : DtoMapper<RepoDto, RepoSummary> {
    override fun map(input: RepoDto): RepoSummary = RepoSummary(
        id = input.id,
        fullName = input.fullName,
        description = input.description,
        stars = input.stargazersCount,
        language = input.language,
        ownerAvatarUrl = input.owner.avatarUrl
    )
}
