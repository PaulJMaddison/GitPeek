package com.repovista.feature.profile.domain

import androidx.paging.PagingData
import com.repovista.core.model.GitHubUser
import com.repovista.core.model.RepoSummary
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getUser(username: String): GitHubUser
    fun getUserReposPaged(username: String): Flow<PagingData<RepoSummary>>
    fun getStarredReposPaged(username: String): Flow<PagingData<RepoSummary>>
}
