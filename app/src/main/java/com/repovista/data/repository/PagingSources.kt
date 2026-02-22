package com.repovista.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.repovista.data.remote.GithubApi
import com.repovista.domain.model.Issue
import com.repovista.domain.model.Repo
import com.repovista.mappers.toDomain

class SearchRepoPagingSource(
    private val api: GithubApi,
    private val query: String
) : PagingSource<Int, Repo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> = try {
        val page = params.key ?: 1
        val response = api.searchRepositories(query, page, params.loadSize)
        val data = response.items.map { it.toDomain() }
        LoadResult.Page(data, prevKey = if (page == 1) null else page - 1, nextKey = if (data.isEmpty()) null else page + 1)
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? = state.anchorPosition
}

class StarredPagingSource(
    private val api: GithubApi,
    private val username: String
) : PagingSource<Int, Repo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> = try {
        val page = params.key ?: 1
        val data = api.getStarredRepos(username, page, params.loadSize).map { it.toDomain() }
        LoadResult.Page(data, prevKey = if (page == 1) null else page - 1, nextKey = if (data.isEmpty()) null else page + 1)
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? = state.anchorPosition
}

class IssuesPagingSource(
    private val api: GithubApi,
    private val owner: String,
    private val repo: String
) : PagingSource<Int, Issue>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Issue> = try {
        val page = params.key ?: 1
        val data = api.getIssues(owner, repo, page = page, perPage = params.loadSize).map { it.toDomain() }
        LoadResult.Page(data, prevKey = if (page == 1) null else page - 1, nextKey = if (data.isEmpty()) null else page + 1)
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, Issue>): Int? = state.anchorPosition
}
