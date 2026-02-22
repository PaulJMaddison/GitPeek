package com.repovista.feature.issues.domain

import androidx.paging.PagingData
import com.repovista.core.model.Issue
import kotlinx.coroutines.flow.Flow

class GetIssuesPagedUseCase(
    private val issuesRepository: IssuesRepository
) {
    operator fun invoke(owner: String, repo: String, state: String): Flow<PagingData<Issue>> =
        issuesRepository.getIssuesPaged(owner, repo, state)
}
