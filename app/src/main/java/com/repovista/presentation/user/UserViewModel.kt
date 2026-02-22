package com.repovista.presentation.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repovista.domain.model.User
import com.repovista.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface UserUiState {
    data object Loading : UserUiState
    data class Error(val message: String) : UserUiState
    data class Data(val user: User) : UserUiState
}

@HiltViewModel
class UserViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {
    private val username: String = checkNotNull(savedStateHandle["username"])
    private val _uiState: MutableStateFlow<UserUiState> = MutableStateFlow(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = try {
                UserUiState.Data(getUserProfileUseCase(username))
            } catch (e: Exception) {
                UserUiState.Error(e.message ?: "Failed to load user")
            }
        }
    }
}
