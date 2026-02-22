package com.repovista.feature.issues.domain

import androidx.paging.PagingData
import com.repovista.core.model.Issue
import kotlinx.coroutines.flow.Flow

interface IssuesRepository {
    fun getIssuesPaged(owner: String, repo: String): Flow<PagingData<Issue>>
}
