package com.repovista.domain.usecase

import androidx.paging.PagingData
import com.repovista.domain.model.Issue
import com.repovista.domain.model.Repo
import com.repovista.domain.model.User
import com.repovista.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoriesUseCase @Inject constructor(private val repository: RepoRepository) {
    operator fun invoke(query: String): Flow<PagingData<Repo>> = repository.searchRepositories(query)
}

class GetUserProfileUseCase @Inject constructor(private val repository: RepoRepository) {
    suspend operator fun invoke(username: String): User = repository.getUser(username)
}

class GetRepoDetailsUseCase @Inject constructor(private val repository: RepoRepository) {
    suspend operator fun invoke(owner: String, repo: String): Repo = repository.getRepository(owner, repo)
}

class GetStarredReposUseCase @Inject constructor(private val repository: RepoRepository) {
    operator fun invoke(username: String): Flow<PagingData<Repo>> = repository.getStarredRepos(username)
}

class GetIssuesUseCase @Inject constructor(private val repository: RepoRepository) {
    operator fun invoke(owner: String, repo: String): Flow<PagingData<Issue>> = repository.getIssues(owner, repo)
}
