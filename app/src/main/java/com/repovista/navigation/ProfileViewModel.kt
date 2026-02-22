package com.repovista.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.repovista.core.database.AppDatabase
import com.repovista.core.model.GitHubUser
import com.repovista.core.model.RepoSummary
import com.repovista.core.network.api.GitHubApi
import com.repovista.core.network.auth.TokenRepository
import com.repovista.feature.profile.data.ProfileRepositoryImpl
import com.repovista.feature.profile.domain.GetStarredReposPagedUseCase
import com.repovista.feature.profile.domain.GetUserReposPagedUseCase
import com.repovista.feature.profile.domain.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val KEY_INPUT_USERNAME = "profile_input_username"
private const val KEY_ACTIVE_USERNAME = "profile_active_username"
private const val KEY_SELECTED_TAB = "profile_selected_tab"

enum class ProfileTab { Repos, Starred }

data class ProfileUiState(
    val inputUsername: String = "",
    val activeUsername: String = "",
    val selectedTab: ProfileTab = ProfileTab.Repos,
    val user: GitHubUser? = null,
    val isLoadingUser: Boolean = false,
    val errorMessage: String? = null,
    val showTokenDialog: Boolean = false,
    val tokenInput: String = "",
    val isSavingToken: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    gitHubApi: GitHubApi,
    appDatabase: AppDatabase,
    private val tokenRepository: TokenRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val profileRepository = ProfileRepositoryImpl(gitHubApi, appDatabase)
    private val getUserUseCase = GetUserUseCase(profileRepository)
    private val getUserReposPagedUseCase = GetUserReposPagedUseCase(profileRepository)
    private val getStarredReposPagedUseCase = GetStarredReposPagedUseCase(profileRepository)

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            inputUsername = savedStateHandle[KEY_INPUT_USERNAME] ?: "",
            activeUsername = savedStateHandle[KEY_ACTIVE_USERNAME] ?: "",
            selectedTab = ProfileTab.entries.getOrElse(savedStateHandle[KEY_SELECTED_TAB] ?: 0) { ProfileTab.Repos }
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val reposPagingData: StateFlow<PagingData<RepoSummary>> = _uiState
        .flatMapLatest { state ->
            if (state.activeUsername.isBlank()) flowOf(PagingData.empty()) else getUserReposPagedUseCase(state.activeUsername)
        }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PagingData.empty())

    val starredPagingData: StateFlow<PagingData<RepoSummary>> = _uiState
        .flatMapLatest { state ->
            if (state.activeUsername.isBlank()) flowOf(PagingData.empty()) else getStarredReposPagedUseCase(state.activeUsername)
        }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PagingData.empty())

    init {
        if (_uiState.value.activeUsername.isNotBlank()) {
            loadProfile(_uiState.value.activeUsername)
        }
    }

    fun initializeUsername(username: String) {
        if (_uiState.value.activeUsername.isBlank() && username.isNotBlank()) {
            _uiState.update { it.copy(inputUsername = username) }
            savedStateHandle[KEY_INPUT_USERNAME] = username
            loadProfile(username)
        }
    }

    fun onUsernameChanged(value: String) {
        _uiState.update { it.copy(inputUsername = value) }
        savedStateHandle[KEY_INPUT_USERNAME] = value
    }

    fun onSelectTab(tab: ProfileTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        savedStateHandle[KEY_SELECTED_TAB] = tab.ordinal
    }

    fun loadProfileFromInput() {
        loadProfile(_uiState.value.inputUsername)
    }

    fun retryLoadProfile() {
        loadProfile(_uiState.value.activeUsername.ifBlank { _uiState.value.inputUsername })
    }

    private fun loadProfile(rawUsername: String) {
        val username = rawUsername.trim()
        if (username.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter a username to load a profile.") }
            return
        }

        _uiState.update {
            it.copy(
                inputUsername = username,
                activeUsername = username,
                isLoadingUser = true,
                errorMessage = null
            )
        }
        savedStateHandle[KEY_INPUT_USERNAME] = username
        savedStateHandle[KEY_ACTIVE_USERNAME] = username

        viewModelScope.launch {
            runCatching { getUserUseCase(username) }
                .onSuccess { user ->
                    _uiState.update { it.copy(user = user, isLoadingUser = false, errorMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            user = null,
                            isLoadingUser = false,
                            errorMessage = error.message ?: "Unable to load profile right now."
                        )
                    }
                }
        }
    }

    fun openTokenDialog() {
        viewModelScope.launch {
            val currentToken = tokenRepository.token.first().orEmpty()
            _uiState.update { it.copy(showTokenDialog = true, tokenInput = currentToken) }
        }
    }

    fun closeTokenDialog() {
        _uiState.update { it.copy(showTokenDialog = false, isSavingToken = false) }
    }

    fun onTokenChanged(value: String) {
        _uiState.update { it.copy(tokenInput = value) }
    }

    fun saveToken() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingToken = true) }
            tokenRepository.setToken(_uiState.value.tokenInput.trim().ifBlank { null })
            _uiState.update { it.copy(isSavingToken = false, showTokenDialog = false) }
        }
    }
}
