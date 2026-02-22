package com.repovista.feature.repodetail.data

import com.repovista.core.model.RepoDetail
import com.repovista.core.network.dto.RepoDto
import com.repovista.core.network.mapper.DtoMapper

object RepoDetailMapper : DtoMapper<RepoDto, RepoDetail> {
    override fun map(input: RepoDto): RepoDetail = RepoDetail(
        id = input.id,
        fullName = input.fullName,
        description = input.description,
        stars = input.stargazersCount,
        language = input.language,
        ownerAvatarUrl = input.owner.avatarUrl,
        forks = input.forksCount,
        openIssues = input.openIssuesCount,
        watchers = input.watchersCount ?: 0,
        topics = input.topics,
        defaultBranch = input.defaultBranch.orEmpty(),
        htmlUrl = input.htmlUrl
    )
}
