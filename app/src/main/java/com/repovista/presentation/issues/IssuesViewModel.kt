package com.repovista.presentation.issues

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.repovista.domain.usecase.GetIssuesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IssuesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getIssuesUseCase: GetIssuesUseCase
) : ViewModel() {
    val owner: String = checkNotNull(savedStateHandle["owner"])
    val repo: String = checkNotNull(savedStateHandle["repo"])
    val issues = getIssuesUseCase(owner, repo).cachedIn(viewModelScope)
}
