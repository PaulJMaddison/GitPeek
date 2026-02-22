package com.repovista.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.repovista.core.model.Issue
import com.repovista.core.network.api.GitHubApi
import com.repovista.feature.issues.data.IssuesRepositoryImpl
import com.repovista.feature.issues.domain.GetIssuesPagedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class IssuesFilter(val apiState: String, val label: String) {
    Open("open", "Open"),
    Closed("closed", "Closed"),
    All("all", "All")
}

data class IssuesUiState(
    val selectedFilter: IssuesFilter = IssuesFilter.Open
)

@HiltViewModel
class IssuesViewModel @Inject constructor(
    gitHubApi: GitHubApi
) : ViewModel() {

    private val getIssuesPagedUseCase = GetIssuesPagedUseCase(IssuesRepositoryImpl(gitHubApi))

    private val _uiState = MutableStateFlow(IssuesUiState())
    val uiState: StateFlow<IssuesUiState> = _uiState.asStateFlow()

    fun onFilterChanged(filter: IssuesFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun issues(owner: String, repo: String): StateFlow<PagingData<Issue>> = _uiState
        .flatMapLatest { state ->
            getIssuesPagedUseCase(owner, repo, state.selectedFilter.apiState)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PagingData.empty()
        )
}
