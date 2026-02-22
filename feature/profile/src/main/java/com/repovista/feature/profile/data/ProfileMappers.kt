package com.repovista.feature.profile.data

import com.repovista.core.model.GitHubUser
import com.repovista.core.model.RepoSummary
import com.repovista.core.network.dto.RepoDto
import com.repovista.core.network.dto.UserDto
import com.repovista.core.network.mapper.DtoMapper

object UserMapper : DtoMapper<UserDto, GitHubUser> {
    override fun map(input: UserDto): GitHubUser = GitHubUser(
        login = input.login,
        name = input.name,
        avatarUrl = input.avatarUrl,
        followers = input.followers,
        following = input.following,
        publicRepos = input.publicRepos,
        bio = input.bio
    )
}

object RepoSummaryMapper : DtoMapper<RepoDto, RepoSummary> {
    override fun map(input: RepoDto): RepoSummary = RepoSummary(
        id = input.id,
        fullName = input.fullName,
        description = input.description,
        stars = input.stargazersCount,
        language = input.language,
        ownerAvatarUrl = input.owner.avatarUrl
    )
}
