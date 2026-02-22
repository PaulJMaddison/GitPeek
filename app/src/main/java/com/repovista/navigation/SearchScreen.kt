package com.repovista.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.repovista.core.model.RepoSummary
import com.repovista.core.ui.components.EmptyState
import com.repovista.core.ui.components.ErrorState
import com.repovista.core.ui.components.LoadingState
import com.repovista.core.ui.components.RepoListItem

@Composable
fun SearchScreen(
    onOpenProfile: (String) -> Unit,
    onOpenRepo: (owner: String, repo: String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val repositories = viewModel.repositories.collectAsLazyPagingItems()
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = repositories.loadState.refresh is LoadState.Loading,
        onRefresh = repositories::refresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = viewModel::onQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Search repositories") },
                    placeholder = { Text("e.g. android compose") }
                )
            }

            when (val refreshState = repositories.loadState.refresh) {
                is LoadState.Loading -> {
                    item { LoadingState(message = "Searching repositories") }
                }

                is LoadState.Error -> {
                    item {
                        ErrorState(
                            message = refreshState.error.toUserMessage("Failed to search repositories."),
                            onRetry = repositories::retry
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    if (uiState.query.isBlank()) {
                        item {
                            EmptyState(
                                title = "Start searching",
                                description = "Type a repository name to discover projects."
                            )
                        }
                    } else if (repositories.itemCount == 0) {
                        item {
                            EmptyState(
                                title = "No repositories found",
                                description = "Try a different keyword or broaden your query."
                            )
                        }
                    } else {
                        items(
                            count = repositories.itemCount,
                            key = { index -> repositories[index]?.id ?: index }
                        ) { index ->
                            repositories[index]?.let { repo ->
                                SearchResultItem(
                                    repo = repo,
                                    onOpenProfile = onOpenProfile,
                                    onOpenRepo = onOpenRepo
                                )
                            }
                        }
                    }
                }
            }

            when (val appendState = repositories.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is LoadState.Error -> {
                    item {
                        ErrorState(
                            message = appendState.error.toUserMessage("Failed to load more results."),
                            onRetry = repositories::retry
                        )
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    repo: RepoSummary,
    onOpenProfile: (String) -> Unit,
    onOpenRepo: (owner: String, repo: String) -> Unit
) {
    val owner = repo.fullName.substringBefore("/")
    val repoName = repo.fullName.substringAfter("/", missingDelimiterValue = repo.fullName)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .clickable { onOpenProfile(owner) }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = repo.ownerAvatarUrl,
                contentDescription = "Avatar for $owner",
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = owner,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        RepoListItem(
            name = repo.fullName,
            description = repo.description,
            stars = repo.stars,
            language = repo.language,
            ownerAvatarUrl = repo.ownerAvatarUrl,
            onClick = { onOpenRepo(owner, repoName) }
        )
    }
}
