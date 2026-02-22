package com.repovista.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.repovista.domain.model.Repo

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onRepoClick: (String, String) -> Unit,
    onUserClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val repos = viewModel.repos.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Search repositories") },
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn {
            items(count = repos.itemCount, key = repos.itemKey { it.id }) { index ->
                repos[index]?.let { repo ->
                    RepoRow(repo = repo, onRepoClick = onRepoClick, onUserClick = onUserClick)
                }
            }
        }
    }
}

@Composable
private fun RepoRow(repo: Repo, onRepoClick: (String, String) -> Unit, onUserClick: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable { onRepoClick(repo.owner.login, repo.name) }) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = repo.fullName)
            Text(text = repo.description ?: "No description")
            Row {
                Text(text = "⭐ ${repo.stars}")
                Text(text = "  ${repo.language ?: "Unknown"}")
                Text(text = "  by ${repo.owner.login}", modifier = Modifier.clickable { onUserClick(repo.owner.login) })
            }
        }
    }
}
