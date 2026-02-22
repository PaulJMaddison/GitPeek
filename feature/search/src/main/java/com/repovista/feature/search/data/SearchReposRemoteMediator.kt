package com.repovista.feature.search.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.repovista.core.database.AppDatabase
import com.repovista.core.database.entity.CacheMetadataEntity
import com.repovista.core.database.entity.SearchRepoEntity
import com.repovista.core.network.api.GitHubApi
import com.repovista.core.network.api.asNetworkDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val CACHE_TIMEOUT_MS = 30 * 60 * 1000L

@OptIn(ExperimentalPagingApi::class)
class SearchReposRemoteMediator(
    private val query: String,
    private val gitHubApi: GitHubApi,
    private val appDatabase: AppDatabase,
    private val pageSize: Int
) : RemoteMediator<Int, SearchRepoEntity>() {

    private val normalizedQuery = query.trim()
    private val cacheKey = "search:$normalizedQuery"

    override suspend fun initialize(): InitializeAction {
        val metadata = appDatabase.cacheMetadataDao().getByKey(cacheKey)
        val isFresh = metadata != null && (System.currentTimeMillis() - metadata.lastUpdated) < CACHE_TIMEOUT_MS
        return if (isFresh) InitializeAction.SKIP_INITIAL_REFRESH else InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchRepoEntity>
    ): MediatorResult = withContext(Dispatchers.IO) {
        try {
            val metadata = appDatabase.cacheMetadataDao().getByKey(cacheKey)
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return@withContext MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> metadata?.nextPage ?: return@withContext MediatorResult.Success(true)
            }

            val response = gitHubApi.searchRepositories(normalizedQuery, page, pageSize)
            val repos = response.items.mapIndexed { index, repo ->
                SearchRepoEntity(
                    query = normalizedQuery,
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
                    searchRepoDao().clearRepos(normalizedQuery)
                }
                searchRepoDao().insertRepos(repos)
                cacheMetadataDao().upsert(
                    CacheMetadataEntity(
                        key = cacheKey,
                        nextPage = nextPage,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = nextPage == null)
        } catch (throwable: Throwable) {
            MediatorResult.Error(throwable.asNetworkDataException("Repository search"))
        }
    }
}
