package com.repovista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repovista.core.network.auth.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
) : ViewModel() {

    val token: StateFlow<String?> = tokenRepository.token.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun saveToken(newToken: String?) {
        viewModelScope.launch {
            tokenRepository.setToken(newToken)
        }
    }
}
