package com.repovista.presentation.repo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.repovista.presentation.components.ErrorView
import com.repovista.presentation.components.LoadingView

@Composable
fun RepoDetailsScreen(viewModel: RepoDetailsViewModel, onOpenIssues: (String, String) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val s = state) {
        is RepoUiState.Loading -> LoadingView()
        is RepoUiState.Error -> ErrorView(s.message)
        is RepoUiState.Data -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = s.repo.fullName)
            Text(text = s.repo.description ?: "No description")
            Text(text = "⭐ ${s.repo.stars} • Forks ${s.repo.forks} • Open issues ${s.repo.openIssues}")
            Button(onClick = { onOpenIssues(viewModel.owner, viewModel.repoName) }) { Text("View Issues") }
        }
    }
}
