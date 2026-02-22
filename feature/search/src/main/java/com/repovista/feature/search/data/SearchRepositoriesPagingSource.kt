package com.repovista.feature.search.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.repovista.core.model.RepoSummary
import com.repovista.core.network.api.GitHubApi
import com.repovista.core.network.api.asNetworkDataException
import com.repovista.core.network.mapper.mapWith

class SearchRepositoriesPagingSource(
    private val gitHubApi: GitHubApi,
    private val query: String,
    private val pageSize: Int
) : PagingSource<Int, RepoSummary>() {

    override fun getRefreshKey(state: PagingState<Int, RepoSummary>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepoSummary> {
        val page = params.key ?: 1
        return try {
            val response = gitHubApi.searchRepositories(
                query = query,
                page = page,
                perPage = pageSize
            )
            val mapped = response.items.mapWith(SearchRepoMapper)
            val nextKey = if (mapped.isEmpty()) null else page + 1
            LoadResult.Page(
                data = mapped,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (throwable: Throwable) {
            LoadResult.Error(throwable.asNetworkDataException("Repository search"))
        }
    }
}
