package com.repovista.feature.profile.domain

import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.repovista.core.model.GitHubUser
import com.repovista.core.model.RepoSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ProfileUseCasesTest {

    @Test
    fun `GetUserUseCase delegates username to repository and returns user`() = runBlocking {
        val expectedUser = GitHubUser(
            login = "octocat",
            name = "The Octocat",
            avatarUrl = "https://avatars.githubusercontent.com/u/583231?v=4",
            followers = 10,
            following = 3,
            publicRepos = 2,
            bio = "Mascot"
        )
        val repository = FakeProfileRepository(userResult = expectedUser)
        val useCase = GetUserUseCase(repository)

        val result = useCase("octocat")

        assertThat(repository.lastUserRequest).isEqualTo("octocat")
        assertThat(result).isEqualTo(expectedUser)
    }

    @Test
    fun `GetUserReposPagedUseCase delegates username and returns repository flow`() {
        val expectedFlow: Flow<PagingData<RepoSummary>> = emptyFlow()
        val repository = FakeProfileRepository(userReposFlow = expectedFlow)
        val useCase = GetUserReposPagedUseCase(repository)

        val result = useCase("google")

        assertThat(repository.lastUserReposRequest).isEqualTo("google")
        assertThat(result).isSameInstanceAs(expectedFlow)
    }

    private class FakeProfileRepository(
        private val userResult: GitHubUser = GitHubUser(
            login = "fallback",
            name = null,
            avatarUrl = "",
            followers = 0,
            following = 0,
            publicRepos = 0,
            bio = null
        ),
        private val userReposFlow: Flow<PagingData<RepoSummary>> = emptyFlow()
    ) : ProfileRepository {

        var lastUserRequest: String? = null
        var lastUserReposRequest: String? = null

        override suspend fun getUser(username: String): GitHubUser {
            lastUserRequest = username
            return userResult
        }

        override fun getUserReposPaged(username: String): Flow<PagingData<RepoSummary>> {
            lastUserReposRequest = username
            return userReposFlow
        }

        override fun getStarredReposPaged(username: String): Flow<PagingData<RepoSummary>> =
            emptyFlow()
    }
}
