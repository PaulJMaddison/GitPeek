package com.repovista.feature.search.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.repovista.core.model.RepoSummary
import com.repovista.core.network.api.GitHubApi
import com.repovista.feature.search.domain.SearchRepository
import kotlinx.coroutines.flow.Flow

private const val DEFAULT_PAGE_SIZE = 30

class SearchRepositoryImpl(
    private val gitHubApi: GitHubApi
) : SearchRepository {

    override fun searchReposPaged(query: String): Flow<PagingData<RepoSummary>> =
        Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                SearchRepositoriesPagingSource(
                    gitHubApi = gitHubApi,
                    query = query,
                    pageSize = DEFAULT_PAGE_SIZE
                )
            }
        ).flow
}
