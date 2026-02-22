package com.repovista.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.repovista.core.model.RepoSummary
import com.repovista.core.network.api.GitHubApi
import com.repovista.feature.search.data.SearchRepositoryImpl
import com.repovista.feature.search.domain.SearchReposPagedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val SEARCH_DEBOUNCE_MS = 400L

data class SearchUiState(
    val query: String = ""
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    gitHubApi: GitHubApi
) : ViewModel() {

    private val searchUseCase = SearchReposPagedUseCase(SearchRepositoryImpl(gitHubApi))

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val repositories: StateFlow<PagingData<RepoSummary>> = _uiState
        .debounce(SEARCH_DEBOUNCE_MS)
        .map { it.query.trim() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(PagingData.empty())
            } else {
                searchUseCase(query)
            }
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PagingData.empty()
        )

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }
}
