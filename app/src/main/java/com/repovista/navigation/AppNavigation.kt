package com.repovista.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.repovista.core.ui.components.EmptyState
import com.repovista.core.ui.components.ErrorState
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
        fun template(username: String): String = "profile/$username"
    }
    data object RepoDetail : Routes("repo/{owner}/{repo}") {
        fun template(owner: String, repo: String): String = "repo/$owner/$repo"
    }
    data object Issues : Routes("issues/{owner}/{repo}") {
        fun template(owner: String, repo: String): String = "issues/$owner/$repo"
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
                            selected = when (destination.label) {
                                "Search" -> currentRoute == Routes.Search.route
                                else -> currentRoute == Routes.Profile.route || currentRoute?.startsWith("profile/") == true
                            },
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
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
private fun RepoVistaNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
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
            val username = backStack.arguments?.getString("username").orEmpty()
            ProfileScreen(
                username = username,
                onOpenRepo = { owner, repo -> navController.navigate(Routes.RepoDetail.template(owner, repo)) }
            )
        }
        composable(
            route = Routes.RepoDetail.route,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("repo") { type = NavType.StringType }
            )
        ) { backStack ->
            RepoDetailScreen(
                owner = backStack.arguments?.getString("owner").orEmpty(),
                repo = backStack.arguments?.getString("repo").orEmpty(),
                onOpenIssues = { owner, repo -> navController.navigate(Routes.Issues.template(owner, repo)) }
            )
        }
        composable(
            route = Routes.Issues.route,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("repo") { type = NavType.StringType }
            )
        ) { backStack ->
            IssuesScreen(
                owner = backStack.arguments?.getString("owner").orEmpty(),
                repo = backStack.arguments?.getString("repo").orEmpty()
            )
        }
    }
}

@Composable
fun SearchScreen(
    onOpenProfile: (String) -> Unit,
    onOpenRepo: (owner: String, repo: String) -> Unit
) {
    val sampleRepos = listOf(
        Triple("google/accompanist", "A collection of extension libraries for Jetpack Compose", 6600),
        Triple("android/compose-samples", "Official Jetpack Compose samples", 18600)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Search", modifier = Modifier.padding(bottom = 4.dp))
        }
        item {
            Button(onClick = { onOpenProfile("octocat") }) {
                Text("Open @octocat profile")
            }
        }
        items(sampleRepos) { repo ->
            val (name, description, stars) = repo
            val owner = name.substringBefore("/")
            val repoName = name.substringAfter("/")
            RepoListItem(
                name = name,
                description = description,
                stars = stars,
                language = "Kotlin",
                ownerAvatarUrl = "https://github.com/$owner.png",
                onClick = { onOpenRepo(owner, repoName) }
            )
        }
    }
}

@Composable
fun ProfileScreen(
    username: String,
    onOpenRepo: (owner: String, repo: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UserHeader(
            login = username,
            name = "The Octocat",
            avatarUrl = "https://github.com/$username.png",
            bio = "GitHub mascot and open source explorer.",
            followers = 9000,
            following = 9,
            publicRepos = 8
        )
        RepoListItem(
            name = "$username/Hello-World",
            description = "My first repository on GitHub!",
            stars = 2400,
            language = "Ruby",
            ownerAvatarUrl = "https://github.com/$username.png",
            onClick = { onOpenRepo(username, "Hello-World") }
        )
    }
}

@Composable
fun RepoDetailScreen(
    owner: String,
    repo: String,
    onOpenIssues: (owner: String, repo: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "$owner/$repo", modifier = Modifier.padding(bottom = 12.dp))
        LoadingState(message = "Loading repository details")
        Button(
            onClick = { onOpenIssues(owner, repo) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("View Issues")
        }
    }
}

@Composable
fun IssuesScreen(owner: String, repo: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Issues for $owner/$repo")
        ErrorState(message = "Unable to load issues right now.", onRetry = {})
        EmptyState(title = "No issues", description = "This repository has no open issues.")
    }
}
