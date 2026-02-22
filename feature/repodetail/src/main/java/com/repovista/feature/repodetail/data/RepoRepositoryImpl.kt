package com.repovista.feature.repodetail.data

import com.repovista.core.model.RepoDetail
import com.repovista.core.network.api.GitHubApi
import com.repovista.feature.repodetail.domain.RepoRepository

class RepoRepositoryImpl(
    private val gitHubApi: GitHubApi
) : RepoRepository {
    override suspend fun getRepoDetail(owner: String, repo: String): RepoDetail =
        RepoDetailMapper.map(gitHubApi.getRepositoryDetails(owner, repo))
}
