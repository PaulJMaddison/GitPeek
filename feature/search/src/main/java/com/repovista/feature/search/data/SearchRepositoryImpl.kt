package com.repovista.feature.search.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.repovista.core.database.AppDatabase
import com.repovista.core.model.RepoSummary
import com.repovista.core.network.api.GitHubApi
import com.repovista.feature.search.domain.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DEFAULT_PAGE_SIZE = 30

class SearchRepositoryImpl(
    private val gitHubApi: GitHubApi,
    private val appDatabase: AppDatabase
) : SearchRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun searchReposPaged(query: String): Flow<PagingData<RepoSummary>> =
        Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            remoteMediator = SearchReposRemoteMediator(
                query = query,
                gitHubApi = gitHubApi,
                appDatabase = appDatabase,
                pageSize = DEFAULT_PAGE_SIZE
            ),
            pagingSourceFactory = {
                appDatabase.searchRepoDao().getReposPagingSource(query.trim())
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                RepoSummary(
                    id = entity.repoId,
                    fullName = entity.fullName,
                    description = entity.description,
                    stars = entity.stars,
                    language = entity.language,
                    ownerAvatarUrl = entity.ownerAvatarUrl
                )
            }
        }
}
