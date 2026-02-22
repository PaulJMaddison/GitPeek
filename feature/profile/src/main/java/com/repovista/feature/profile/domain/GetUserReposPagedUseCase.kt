package com.repovista.feature.profile.domain

import androidx.paging.PagingData
import com.repovista.core.model.RepoSummary
import kotlinx.coroutines.flow.Flow

class GetUserReposPagedUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(username: String): Flow<PagingData<RepoSummary>> =
        profileRepository.getUserReposPaged(username)
}
