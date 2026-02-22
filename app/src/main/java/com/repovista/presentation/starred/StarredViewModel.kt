package com.repovista.presentation.starred

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.repovista.domain.usecase.GetStarredReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StarredViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getStarredReposUseCase: GetStarredReposUseCase
) : ViewModel() {
    val username: String = checkNotNull(savedStateHandle["username"])
    val repos = getStarredReposUseCase(username).cachedIn(viewModelScope)
}
