package com.repovista.feature.profile.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.repovista.core.database.AppDatabase
import com.repovista.core.database.entity.CacheMetadataEntity
import com.repovista.core.database.entity.UserRepoEntity
import com.repovista.core.network.api.GitHubApi
import com.repovista.core.network.api.asNetworkDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val CACHE_TIMEOUT_MS = 30 * 60 * 1000L

@OptIn(ExperimentalPagingApi::class)
class UserReposRemoteMediator(
    username: String,
    private val gitHubApi: GitHubApi,
    private val appDatabase: AppDatabase,
    private val pageSize: Int
) : RemoteMediator<Int, UserRepoEntity>() {

    private val normalizedUsername = username.trim()
    private val cacheKey = "userRepos:$normalizedUsername"

    override suspend fun initialize(): InitializeAction {
        val metadata = appDatabase.cacheMetadataDao().getByKey(cacheKey)
        val isFresh = metadata != null && (System.currentTimeMillis() - metadata.lastUpdated) < CACHE_TIMEOUT_MS
        return if (isFresh) InitializeAction.SKIP_INITIAL_REFRESH else InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserRepoEntity>
    ): MediatorResult = withContext(Dispatchers.IO) {
        try {
            val metadata = appDatabase.cacheMetadataDao().getByKey(cacheKey)
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return@withContext MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> metadata?.nextPage ?: return@withContext MediatorResult.Success(true)
            }

            val response = gitHubApi.listUserRepositories(normalizedUsername, page, pageSize)
            val repos = response.mapIndexed { index, repo ->
                UserRepoEntity(
                    username = normalizedUsername,
                    repoId = repo.id,
                    fullName = repo.fullName,
                    description = repo.description,
                    stars = repo.stargazersCount,
                    language = repo.language,
                    ownerAvatarUrl = repo.owner.avatarUrl,
                    page = page,
                    indexInPage = index
                )
            }
            val nextPage = if (repos.isEmpty()) null else page + 1

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appDatabase.userRepoDao().clearRepos(normalizedUsername)
                }
                appDatabase.userRepoDao().insertRepos(repos)
                appDatabase.cacheMetadataDao().upsert(
                    CacheMetadataEntity(
                        key = cacheKey,
                        nextPage = nextPage,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = nextPage == null)
        } catch (throwable: Throwable) {
            MediatorResult.Error(throwable.asNetworkDataException("Loading user repositories"))
        }
    }
}
