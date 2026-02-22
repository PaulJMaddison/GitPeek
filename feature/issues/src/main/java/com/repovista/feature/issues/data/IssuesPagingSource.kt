package com.repovista.feature.issues.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.repovista.core.model.Issue
import com.repovista.core.network.api.GitHubApi
import com.repovista.core.network.api.asNetworkDataException
import com.repovista.core.network.dto.withoutPullRequests
import com.repovista.core.network.mapper.mapWith

class IssuesPagingSource(
    private val gitHubApi: GitHubApi,
    private val owner: String,
    private val repo: String,
    private val pageSize: Int
) : PagingSource<Int, Issue>() {

    override fun getRefreshKey(state: PagingState<Int, Issue>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Issue> {
        val page = params.key ?: 1
        return try {
            val issues = gitHubApi.listRepositoryIssues(owner, repo, page, pageSize)
                .withoutPullRequests()
                .mapWith(IssueMapper)
            LoadResult.Page(
                data = issues,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (issues.isEmpty()) null else page + 1
            )
        } catch (throwable: Throwable) {
            LoadResult.Error(throwable.asNetworkDataException("Loading repository issues"))
        }
    }
}
