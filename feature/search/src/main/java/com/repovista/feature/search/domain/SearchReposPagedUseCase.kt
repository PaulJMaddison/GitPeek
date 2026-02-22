package com.repovista.feature.search.domain

import androidx.paging.PagingData
import com.repovista.core.model.RepoSummary
import kotlinx.coroutines.flow.Flow

class SearchReposPagedUseCase(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(query: String): Flow<PagingData<RepoSummary>> =
        searchRepository.searchReposPaged(query)
}
