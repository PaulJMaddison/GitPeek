package com.repovista.feature.issues.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.repovista.core.model.Issue
import com.repovista.core.network.api.GitHubApi
import com.repovista.feature.issues.domain.IssuesRepository
import kotlinx.coroutines.flow.Flow

private const val DEFAULT_PAGE_SIZE = 30

class IssuesRepositoryImpl(
    private val gitHubApi: GitHubApi
) : IssuesRepository {

    override fun getIssuesPaged(owner: String, repo: String, state: String): Flow<PagingData<Issue>> =
        Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                IssuesPagingSource(gitHubApi, owner, repo, state, DEFAULT_PAGE_SIZE)
            }
        ).flow
}
