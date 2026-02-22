package com.repovista.usecase

import androidx.paging.PagingData
import app.cash.turbine.test
import com.repovista.domain.model.Repo
import com.repovista.domain.repository.RepoRepository
import com.repovista.domain.usecase.SearchRepositoriesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SearchRepositoriesUseCaseTest {
    @Test
    fun `delegates to repository`() = runTest {
        val repository = mockk<RepoRepository>()
        every { repository.searchRepositories("android") } returns flowOf(PagingData.empty<Repo>())

        val useCase = SearchRepositoriesUseCase(repository)

        useCase("android").test {
            awaitItem()
            awaitComplete()
        }
    }
}
