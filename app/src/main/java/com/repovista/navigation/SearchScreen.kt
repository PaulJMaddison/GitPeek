package com.repovista.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.repovista.core.ui.components.EmptyState
import com.repovista.core.ui.components.ErrorState
import com.repovista.core.ui.components.LoadingState
import com.repovista.core.ui.components.RepoListItem

private val suggestions = listOf("kotlin", "compose", "android", "retrofit")

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchScreen(
    onOpenProfile: (String) -> Unit,
    onOpenRepo: (owner: String, repo: String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val repositories = viewModel.repositories.collectAsLazyPagingItems()
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(topBar = { TopAppBar(title = { Text("Search") }) }) { innerPadding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = repositories.loadState.refresh is LoadState.Loading,
            onRefresh = repositories::refresh,
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SearchBar(
                        query = uiState.query,
                        onQueryChange = viewModel::onQueryChanged,
                        onSearch = viewModel::onSearch,
                        active = false,
                        onActiveChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search repositories") },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        trailingIcon = {
                            if (uiState.query.isNotBlank()) {
                                IconButton(onClick = { viewModel.onQueryChanged("") }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Clear search")
                                }
                            }
                        }
                    ) {}
                }

                when (val refreshState = repositories.loadState.refresh) {
                    is LoadState.Loading -> item { LoadingState(message = "Searching repositories") }
                    is LoadState.Error -> item {
                        ErrorState(
                            message = refreshState.error.toUserMessage("Failed to search repositories."),
                            onRetry = repositories::retry
                        )
                    }
                    is LoadState.NotLoading -> {
                        if (uiState.query.isBlank()) {
                            item {
                                EmptyState(
                                    title = "Search GitHub repositories",
                                    message = "Find repositories, explore details, and track issues.",
                                    icon = { Icon(Icons.Outlined.Search, contentDescription = null) }
                                )
                            }
                            item {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(suggestions) { chip ->
                                        AssistChip(onClick = { viewModel.onSearch(chip) }, label = { Text(chip) })
                                    }
                                }
                            }
                        } else if (repositories.itemCount == 0) {
                            item {
                                EmptyState(
                                    title = "No repositories found",
                                    message = "Try a different keyword or broaden your query."
                                )
                            }
                        } else {
                            items(count = repositories.itemCount, key = { index -> repositories[index]?.id ?: index }) { index ->
                                repositories[index]?.let { repo ->
                                    val owner = repo.fullName.substringBefore("/")
                                    val repoName = repo.fullName.substringAfter("/", missingDelimiterValue = repo.fullName)
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
                        }
                    }
                }

                when (val appendState = repositories.loadState.append) {
                    is LoadState.Loading -> item {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) { CircularProgressIndicator() }
                    }
                    is LoadState.Error -> item {
                        ErrorState(
                            message = appendState.error.toUserMessage("Failed to load more results."),
                            onRetry = repositories::retry
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}
