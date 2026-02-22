package com.repovista.domain.repository

import androidx.paging.PagingData
import com.repovista.domain.model.Issue
import com.repovista.domain.model.Repo
import com.repovista.domain.model.User
import kotlinx.coroutines.flow.Flow

interface RepoRepository {
    fun searchRepositories(query: String): Flow<PagingData<Repo>>
    suspend fun getUser(username: String): User
    suspend fun getRepository(owner: String, repo: String): Repo
    fun getStarredRepos(username: String): Flow<PagingData<Repo>>
    fun getIssues(owner: String, repo: String): Flow<PagingData<Issue>>
}
