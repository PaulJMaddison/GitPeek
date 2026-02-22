package com.repovista.presentation.repo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repovista.domain.model.Repo
import com.repovista.domain.usecase.GetRepoDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface RepoUiState {
    data object Loading : RepoUiState
    data class Error(val message: String) : RepoUiState
    data class Data(val repo: Repo) : RepoUiState
}

@HiltViewModel
class RepoDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRepoDetailsUseCase: GetRepoDetailsUseCase
) : ViewModel() {
    val owner: String = checkNotNull(savedStateHandle["owner"])
    val repoName: String = checkNotNull(savedStateHandle["repo"])

    private val _uiState: MutableStateFlow<RepoUiState> = MutableStateFlow(RepoUiState.Loading)
    val uiState: StateFlow<RepoUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = try {
                RepoUiState.Data(getRepoDetailsUseCase(owner, repoName))
            } catch (e: Exception) {
                RepoUiState.Error(e.message ?: "Failed to load repository")
            }
        }
    }
}
