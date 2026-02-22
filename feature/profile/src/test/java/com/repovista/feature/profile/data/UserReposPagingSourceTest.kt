package com.repovista.feature.profile.data

import androidx.paging.PagingSource
import com.google.common.truth.Truth.assertThat
import com.repovista.core.network.api.GitHubApi
import com.repovista.core.network.api.NetworkDataException
import com.repovista.core.network.dto.IssueDto
import com.repovista.core.network.dto.OwnerDto
import com.repovista.core.network.dto.RepoDto
import com.repovista.core.network.dto.SearchRepositoriesResponseDto
import com.repovista.core.network.dto.UserDto
import org.junit.Test

class UserReposPagingSourceTest {

    @Test
    suspend fun `load returns mapped page on success`() {
        val repos = listOf(
            repoDto(id = 1L, fullName = "octocat/one"),
            repoDto(id = 2L, fullName = "octocat/two")
        )
        val api = FakeGitHubApi(userReposResult = repos)
        val pagingSource = UserReposPagingSource(api, username = "octocat", pageSize = 30)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 30,
                placeholdersEnabled = false
            )
        )

        val page = result as PagingSource.LoadResult.Page
        assertThat(api.lastUserReposUsername).isEqualTo("octocat")
        assertThat(api.lastUserReposPage).isEqualTo(1)
        assertThat(page.prevKey).isNull()
        assertThat(page.nextKey).isEqualTo(2)
        assertThat(page.data.map { it.fullName }).containsExactly("octocat/one", "octocat/two").inOrder()
    }

    @Test
    suspend fun `load returns network data exception on failure`() {
        val api = FakeGitHubApi(userReposThrowable = IllegalStateException("boom"))
        val pagingSource = UserReposPagingSource(api, username = "octocat", pageSize = 30)

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 3,
                loadSize = 30,
                placeholdersEnabled = false
            )
        )

        val error = (result as PagingSource.LoadResult.Error).throwable
        assertThat(error).isInstanceOf(NetworkDataException::class.java)
        assertThat(error.message).contains("Loading user repositories failed")
    }

    private fun repoDto(id: Long, fullName: String): RepoDto = RepoDto(
        id = id,
        name = fullName.substringAfter('/'),
        fullName = fullName,
        description = "desc",
        language = "Kotlin",
        stargazersCount = 10,
        forksCount = 1,
        openIssuesCount = 0,
        htmlUrl = "https://github.com/$fullName",
        updatedAt = "2025-01-01T00:00:00Z",
        owner = OwnerDto(
            id = 1L,
            login = fullName.substringBefore('/'),
            avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
            htmlUrl = "https://github.com/${fullName.substringBefore('/')}"
        )
    )

    private class FakeGitHubApi(
        private val userReposResult: List<RepoDto> = emptyList(),
        private val userReposThrowable: Throwable? = null
    ) : GitHubApi {

        var lastUserReposUsername: String? = null
        var lastUserReposPage: Int? = null

        override suspend fun listUserRepositories(username: String, page: Int, perPage: Int): List<RepoDto> {
            lastUserReposUsername = username
            lastUserReposPage = page
            userReposThrowable?.let { throw it }
            return userReposResult
        }

        override suspend fun searchRepositories(query: String, page: Int, perPage: Int): SearchRepositoriesResponseDto =
            error("Not needed in this test")

        override suspend fun getUser(username: String): UserDto =
            error("Not needed in this test")

        override suspend fun listStarredRepositories(username: String, page: Int, perPage: Int): List<RepoDto> =
            error("Not needed in this test")

        override suspend fun getRepositoryDetails(owner: String, repository: String): RepoDto =
            error("Not needed in this test")

        override suspend fun listRepositoryIssues(
            owner: String,
            repository: String,
            state: String,
            page: Int,
            perPage: Int
        ): List<IssueDto> = error("Not needed in this test")
    }
}
