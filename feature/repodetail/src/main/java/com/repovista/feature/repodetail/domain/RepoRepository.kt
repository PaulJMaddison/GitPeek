package com.repovista.feature.repodetail.domain

import com.repovista.core.model.RepoDetail

interface RepoRepository {
    suspend fun getRepoDetail(owner: String, repo: String): RepoDetail
}
