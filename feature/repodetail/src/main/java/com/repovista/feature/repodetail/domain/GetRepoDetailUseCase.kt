package com.repovista.feature.repodetail.domain

import com.repovista.core.model.RepoDetail

class GetRepoDetailUseCase(
    private val repoRepository: RepoRepository
) {
    suspend operator fun invoke(owner: String, repo: String): RepoDetail =
        repoRepository.getRepoDetail(owner, repo)
}
