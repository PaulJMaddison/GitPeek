package com.repovista.feature.search.domain

import androidx.paging.PagingData
import com.repovista.core.model.RepoSummary
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchReposPaged(query: String): Flow<PagingData<RepoSummary>>
}
