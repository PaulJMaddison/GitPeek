package com.repovista.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.repovista.core.model.RepoSummary
import com.repovista.core.ui.components.EmptyState
import com.repovista.core.ui.components.ErrorState
import com.repovista.core.ui.components.IssueListItem
import com.repovista.core.ui.components.LoadingState
import com.repovista.core.ui.components.RepoListItem
import com.repovista.core.ui.components.UserHeader

private data class BottomDestination(val label: String, val route: String)

private val bottomDestinations = listOf(
    BottomDestination("Search", Routes.Search.route),
    BottomDestination("Profile", Routes.Profile.template("octocat"))
)

sealed class Routes(val route: String) {
    data object Search : Routes("search")
    data object Profile : Routes("profile/{username}") {
        fun template(username: String): String = "profile/${Uri.encode(username)}"
    }
    data object RepoDetail : Routes("repo/{owner}/{repo}") {
        fun template(owner: String, repo: String): String = "repo/${Uri.encode(owner)}/${Uri.encode(repo)}"
    }
    data object Issues : Routes("issues/{owner}/{repo}") {
        fun template(owner: String, repo: String): String = "issues/${Uri.encode(owner)}/${Uri.encode(repo)}"
    }
}

@Composable
fun RepoVistaRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute == Routes.Search.route || currentRoute?.startsWith("profile/") == true) {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = if (destination.label == "Search") {
                                currentRoute == Routes.Search.route
                            } else {
                                currentRoute == Routes.Profile.route || currentRoute?.startsWith("profile/") == true
                            },
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text("•") },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        RepoVistaNavHost(navController = navController, innerPadding = innerPadding)
    }
}

@Composable
private fun RepoVistaNavHost(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Routes.Search.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Routes.Search.route) {
            SearchScreen(
                onOpenProfile = { navController.navigate(Routes.Profile.template(it)) },
                onOpenRepo = { owner, repo -> navController.navigate(Routes.RepoDetail.template(owner, repo)) }
            )
        }
        composable(
            route = Routes.Profile.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStack ->
            ProfileScreen(
                username = Uri.decode(backStack.arguments?.getString("username").orEmpty()),
                onOpenRepo = { owner, repo -> navController.navigate(Routes.RepoDetail.template(owner, repo)) }
            )
        }
        composable(
            route = Routes.RepoDetail.route,
            arguments = listOf(navArgument("owner") { type = NavType.StringType }, navArgument("repo") { type = NavType.StringType })
        ) { backStack ->
            RepoDetailScreen(
                owner = Uri.decode(backStack.arguments?.getString("owner").orEmpty()),
                repo = Uri.decode(backStack.arguments?.getString("repo").orEmpty()),
                onOpenIssues = { owner, repo -> navController.navigate(Routes.Issues.template(owner, repo)) },
                onBack = navController::popBackStack
            )
        }
        composable(
            route = Routes.Issues.route,
            arguments = listOf(navArgument("owner") { type = NavType.StringType }, navArgument("repo") { type = NavType.StringType })
        ) { backStack ->
            IssuesScreen(
                owner = Uri.decode(backStack.arguments?.getString("owner").orEmpty()),
                repo = Uri.decode(backStack.arguments?.getString("repo").orEmpty()),
                onBack = navController::popBackStack
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(
    username: String,
    onOpenRepo: (owner: String, repo: String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val repos = viewModel.reposPagingData.collectAsLazyPagingItems()
    val starredRepos = viewModel.starredPagingData.collectAsLazyPagingItems()
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(username) { viewModel.initializeUsername(username) }

    Scaffold(topBar = { TopAppBar(title = { Text("Profile") }) }) { innerPadding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = uiState.isLoadingUser,
            onRefresh = {
                viewModel.retryLoadProfile()
                repos.refresh()
                starredRepos.refresh()
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    androidx.compose.material3.Card {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = uiState.inputUsername,
                                onValueChange = viewModel::onUsernameChanged,
                                label = { Text("Username") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(onClick = viewModel::loadProfileFromInput) { Text("Load") }
                        }
                    }
                }
                item {
                    OutlinedButton(onClick = viewModel::openTokenDialog, modifier = Modifier.fillMaxWidth()) {
                        Text("Edit token")
                    }
                }

                when {
                    uiState.isLoadingUser && uiState.user == null -> item { LoadingState(message = "Loading profile") }
                    uiState.errorMessage != null && uiState.user == null -> item {
                        ErrorState(message = uiState.errorMessage ?: "Unknown error", onRetry = viewModel::retryLoadProfile)
                    }
                    uiState.user == null -> item {
                        EmptyState(
                            title = "Load a GitHub profile",
                            message = "Enter a username above to view repositories and starred projects.",
                            icon = { Icon(Icons.Outlined.PersonSearch, contentDescription = null) }
                        )
                    }
                    else -> {
                        item {
                            UserHeader(
                                login = (uiState.user ?: return@item).login,
                                name = (uiState.user ?: return@item).name,
                                avatarUrl = (uiState.user ?: return@item).avatarUrl,
                                bio = (uiState.user ?: return@item).bio,
                                followers = (uiState.user ?: return@item).followers,
                                following = (uiState.user ?: return@item).following,
                                publicRepos = (uiState.user ?: return@item).publicRepos
                            )
                        }
                        item {
                            if (uiState.isLoadingUser) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        item {
                            TabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
                                ProfileTab.entries.forEach { tab ->
                                    Tab(
                                        selected = tab == uiState.selectedTab,
                                        onClick = { viewModel.onSelectTab(tab) },
                                        text = { Text(if (tab == ProfileTab.Repos) "Repos" else "Starred") }
                                    )
                                }
                            }
                        }
                        profileRepoItems(
                            repositories = if (uiState.selectedTab == ProfileTab.Repos) repos else starredRepos,
                            onOpenRepo = onOpenRepo
                        )
                    }
                }
            }
        }
    }

    if (uiState.showTokenDialog) {
        AlertDialog(
            onDismissRequest = viewModel::closeTokenDialog,
            title = { Text("GitHub token") },
            text = {
                OutlinedTextField(
                    value = uiState.tokenInput,
                    onValueChange = viewModel::onTokenChanged,
                    label = { Text("Personal access token") },
                    singleLine = true
                )
            },
            confirmButton = { Button(onClick = viewModel::saveToken, enabled = !uiState.isSavingToken) { Text("Save") } },
            dismissButton = { OutlinedButton(onClick = viewModel::closeTokenDialog, enabled = !uiState.isSavingToken) { Text("Cancel") } }
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.profileRepoItems(
    repositories: LazyPagingItems<RepoSummary>,
    onOpenRepo: (owner: String, repo: String) -> Unit
) {
    when (val refreshState = repositories.loadState.refresh) {
        is LoadState.Loading -> item { LoadingState(message = "Loading repositories") }
        is LoadState.Error -> item {
            ErrorState(
                message = refreshState.error.toUserMessage("Failed to load repositories."),
                onRetry = repositories::retry
            )
        }
        is LoadState.NotLoading -> {
            if (repositories.itemCount == 0) {
                item {
                    EmptyState(
                        title = "No repositories",
                        message = "Nothing to show in this section yet."
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { CircularProgressIndicator() }
        }
        is LoadState.Error -> item {
            ErrorState(
                message = appendState.error.toUserMessage("Failed to load more repositories."),
                onRetry = repositories::retry
            )
        }
        else -> Unit
    }
}

@Composable
fun RepoDetailScreen(
    owner: String,
    repo: String,
    onOpenIssues: (owner: String, repo: String) -> Unit,
    onBack: () -> Unit,
    viewModel: RepoDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(owner, repo) { viewModel.loadRepoDetail(owner, repo) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(repo) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                uiState.isLoading && uiState.repoDetail == null -> LoadingState(message = "Loading repository details")
                uiState.errorMessage != null && uiState.repoDetail == null -> {
                    ErrorState(message = uiState.errorMessage ?: "Unknown error", onRetry = { viewModel.loadRepoDetail(owner, repo) })
                }
                uiState.repoDetail != null -> {
                    val detail = uiState.repoDetail ?: return@Column
                    Text(text = detail.fullName)
                    Text(text = detail.description ?: "No description available")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        androidx.compose.material3.AssistChip(onClick = {}, enabled = false, label = { Text("⭐ ${detail.stars}") })
                        androidx.compose.material3.AssistChip(onClick = {}, enabled = false, label = { Text("🍴 ${detail.forks}") })
                        androidx.compose.material3.AssistChip(onClick = {}, enabled = false, label = { Text("🐞 ${detail.openIssues}") })
                    }
                    detail.language?.let {
                        androidx.compose.material3.AssistChip(onClick = {}, enabled = false, label = { Text(it) })
                    }
                    detail.topics?.takeIf { it.isNotEmpty() }?.let { topics ->
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(topics) { topic ->
                                androidx.compose.material3.AssistChip(onClick = {}, enabled = false, label = { Text(topic) })
                            }
                        }
                    }
                    Button(modifier = Modifier.fillMaxWidth(), onClick = { onOpenIssues(owner, repo) }) { Text("View issues") }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(detail.htmlUrl))
                            context.startActivity(intent)
                        }
                    ) { Text("Open on GitHub") }
                }
                else -> EmptyState(title = "No repository details", message = "Try again to load this repository.")
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun IssuesScreen(
    owner: String,
    repo: String,
    onBack: () -> Unit,
    viewModel: IssuesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val issuesFlow = remember(owner, repo) { viewModel.issues(owner, repo) }
    val issues = issuesFlow.collectAsLazyPagingItems()
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Issues") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TabRow(selectedTabIndex = uiState.selectedFilter.ordinal) {
                IssuesFilter.entries.forEach { filter ->
                    Tab(
                        selected = uiState.selectedFilter == filter,
                        onClick = {
                            viewModel.onFilterChanged(filter)
                            issues.refresh()
                        },
                        text = { Text(filter.label) }
                    )
                }
            }

            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = issues.loadState.refresh is LoadState.Loading,
                onRefresh = issues::refresh,
                modifier = Modifier.weight(1f)
            ) {
                when (val refreshState = issues.loadState.refresh) {
                    is LoadState.Loading -> LoadingState(message = "Loading issues")
                    is LoadState.Error -> ErrorState(
                        message = refreshState.error.toUserMessage("Unable to load issues right now."),
                        onRetry = issues::retry
                    )
                    is LoadState.NotLoading -> {
                        if (issues.itemCount == 0) {
                            EmptyState(
                                title = "No issues found",
                                message = "No ${uiState.selectedFilter.label.lowercase()} issues for $owner/$repo yet."
                            )
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(count = issues.itemCount, key = { index -> issues[index]?.id ?: index }) { index ->
                                    issues[index]?.let { issue -> IssueListItem(issue = issue) }
                                }
                                when (val appendState = issues.loadState.append) {
                                    is LoadState.Loading -> item {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                    is LoadState.Error -> item {
                                        ErrorState(
                                            message = appendState.error.toUserMessage("Unable to load more issues."),
                                            onRetry = issues::retry
                                        )
                                    }
                                    else -> Unit
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
