package com.repovista.mappers

import com.repovista.data.remote.dto.GithubIssueDto
import com.repovista.data.remote.dto.GithubRepoDto
import com.repovista.data.remote.dto.GithubUserDto
import com.repovista.domain.model.Issue
import com.repovista.domain.model.Repo
import com.repovista.domain.model.RepoOwner
import com.repovista.domain.model.User

fun GithubRepoDto.toDomain(): Repo = Repo(
    id = id,
    name = name,
    fullName = fullName,
    description = description,
    stars = stargazersCount,
    forks = forksCount,
    openIssues = openIssuesCount,
    language = language,
    htmlUrl = htmlUrl,
    owner = RepoOwner(owner.login, owner.avatarUrl)
)

fun GithubUserDto.toDomain(): User = User(
    id = id,
    username = login,
    name = name,
    bio = bio,
    avatarUrl = avatarUrl,
    followers = followers,
    following = following,
    publicRepos = publicRepos
)

fun GithubIssueDto.toDomain(): Issue = Issue(
    id = id,
    title = title,
    body = body,
    state = state,
    htmlUrl = htmlUrl,
    author = RepoOwner(user.login, user.avatarUrl)
)
