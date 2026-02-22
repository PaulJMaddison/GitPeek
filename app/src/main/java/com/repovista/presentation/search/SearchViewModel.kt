package com.repovista.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.repovista.domain.usecase.SearchRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SearchUiState(val query: String = "android")

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase
) : ViewModel() {
    private val state = MutableStateFlow(SearchUiState())
    val uiState = state

    val repos = state.flatMapLatest { searchRepositoriesUseCase(it.query) }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), androidx.paging.PagingData.empty())

    fun onQueryChange(query: String) = state.update { it.copy(query = query) }
}
