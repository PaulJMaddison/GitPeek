package com.repovista.feature.profile.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.repovista.core.database.AppDatabase
import com.repovista.core.model.GitHubUser
import com.repovista.core.model.RepoSummary
import com.repovista.core.network.api.GitHubApi
import com.repovista.core.network.api.asNetworkDataException
import com.repovista.feature.profile.domain.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DEFAULT_PAGE_SIZE = 30

class ProfileRepositoryImpl(
    private val gitHubApi: GitHubApi,
    private val appDatabase: AppDatabase
) : ProfileRepository {

    override suspend fun getUser(username: String): GitHubUser =
        try {
            UserMapper.map(gitHubApi.getUser(username))
        } catch (throwable: Throwable) {
            throw throwable.asNetworkDataException("Loading user profile")
        }

    @OptIn(ExperimentalPagingApi::class)
    override fun getUserReposPaged(username: String): Flow<PagingData<RepoSummary>> =
        Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            remoteMediator = UserReposRemoteMediator(
                username = username,
                gitHubApi = gitHubApi,
                appDatabase = appDatabase,
                pageSize = DEFAULT_PAGE_SIZE
            ),
            pagingSourceFactory = {
                appDatabase.userRepoDao().getReposPagingSource(username.trim())
            }
        ).flow.map { pagingData -> pagingData.map { it.toRepoSummary() } }

    override fun getStarredReposPaged(username: String): Flow<PagingData<RepoSummary>> =
        Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                StarredReposPagingSource(gitHubApi, username, DEFAULT_PAGE_SIZE)
            }
        ).flow
}
