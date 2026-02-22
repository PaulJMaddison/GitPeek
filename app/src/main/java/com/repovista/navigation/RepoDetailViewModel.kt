package com.repovista.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repovista.core.model.RepoDetail
import com.repovista.core.network.api.GitHubApi
import com.repovista.feature.repodetail.data.RepoRepositoryImpl
import com.repovista.feature.repodetail.domain.GetRepoDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RepoDetailUiState(
    val isLoading: Boolean = false,
    val repoDetail: RepoDetail? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class RepoDetailViewModel @Inject constructor(
    gitHubApi: GitHubApi
) : ViewModel() {

    private val getRepoDetailUseCase = GetRepoDetailUseCase(RepoRepositoryImpl(gitHubApi))

    private val _uiState = MutableStateFlow(RepoDetailUiState())
    val uiState: StateFlow<RepoDetailUiState> = _uiState.asStateFlow()

    fun loadRepoDetail(owner: String, repo: String) {
        if (owner.isBlank() || repo.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Repository information is missing.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getRepoDetailUseCase(owner = owner, repo = repo) }
                .onSuccess { repoDetail ->
                    _uiState.update { it.copy(isLoading = false, repoDetail = repoDetail, errorMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            repoDetail = null,
                            errorMessage = error.message ?: "Failed to load repository details."
                        )
                    }
                }
        }
    }
}
