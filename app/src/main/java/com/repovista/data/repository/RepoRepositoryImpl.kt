package com.repovista.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.repovista.data.remote.GithubApi
import com.repovista.domain.model.Issue
import com.repovista.domain.model.Repo
import com.repovista.domain.model.User
import com.repovista.domain.repository.RepoRepository
import com.repovista.mappers.toDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepoRepositoryImpl @Inject constructor(
    private val api: GithubApi
) : RepoRepository {
    override fun searchRepositories(query: String): Flow<PagingData<Repo>> =
        Pager(PagingConfig(pageSize = 20)) { SearchRepoPagingSource(api, query) }.flow

    override suspend fun getUser(username: String): User = api.getUser(username).toDomain()

    override suspend fun getRepository(owner: String, repo: String): Repo = api.getRepository(owner, repo).toDomain()

    override fun getStarredRepos(username: String): Flow<PagingData<Repo>> =
        Pager(PagingConfig(pageSize = 20)) { StarredPagingSource(api, username) }.flow

    override fun getIssues(owner: String, repo: String): Flow<PagingData<Issue>> =
        Pager(PagingConfig(pageSize = 20)) { IssuesPagingSource(api, owner, repo) }.flow
}
