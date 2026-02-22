package com.repovista.feature.profile.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.repovista.core.model.GitHubUser
import com.repovista.core.model.RepoSummary
import com.repovista.core.network.api.GitHubApi
import com.repovista.core.network.api.asNetworkDataException
import com.repovista.feature.profile.domain.ProfileRepository
import kotlinx.coroutines.flow.Flow

private const val DEFAULT_PAGE_SIZE = 30

class ProfileRepositoryImpl(
    private val gitHubApi: GitHubApi
) : ProfileRepository {

    override suspend fun getUser(username: String): GitHubUser =
        try {
            UserMapper.map(gitHubApi.getUser(username))
        } catch (throwable: Throwable) {
            throw throwable.asNetworkDataException("Loading user profile")
        }

    override fun getUserReposPaged(username: String): Flow<PagingData<RepoSummary>> =
        Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                UserReposPagingSource(gitHubApi, username, DEFAULT_PAGE_SIZE)
            }
        ).flow

    override fun getStarredReposPaged(username: String): Flow<PagingData<RepoSummary>> =
        Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                StarredReposPagingSource(gitHubApi, username, DEFAULT_PAGE_SIZE)
            }
        ).flow
}
