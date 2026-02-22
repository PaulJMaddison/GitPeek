package com.repovista.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.repovista.presentation.issues.IssuesScreen
import com.repovista.presentation.issues.IssuesViewModel
import com.repovista.presentation.repo.RepoDetailsScreen
import com.repovista.presentation.repo.RepoDetailsViewModel
import com.repovista.presentation.search.SearchScreen
import com.repovista.presentation.search.SearchViewModel
import com.repovista.presentation.starred.StarredScreen
import com.repovista.presentation.starred.StarredViewModel
import com.repovista.presentation.user.UserProfileScreen
import com.repovista.presentation.user.UserViewModel

sealed class Route(val value: String) {
    data object Search : Route("search")
    data object User : Route("user/{username}")
    data object Repo : Route("repo/{owner}/{repo}")
    data object Starred : Route("starred/{username}")
    data object Issues : Route("issues/{owner}/{repo}")
}

@Composable
fun RepoVistaNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.Search.value) {
        composable(Route.Search.value) {
            SearchScreen(
                viewModel = hiltViewModel<SearchViewModel>(),
                onRepoClick = { o, r -> navController.navigate("repo/$o/$r") },
                onUserClick = { u -> navController.navigate("user/$u") }
            )
        }
        composable(Route.User.value, arguments = listOf(navArgument("username") { type = NavType.StringType })) {
            UserProfileScreen(
                viewModel = hiltViewModel<UserViewModel>(),
                onOpenStarred = { navController.navigate("starred/$it") }
            )
        }
        composable(Route.Repo.value) {
            RepoDetailsScreen(
                viewModel = hiltViewModel<RepoDetailsViewModel>(),
                onOpenIssues = { o, r -> navController.navigate("issues/$o/$r") }
            )
        }
        composable(Route.Starred.value) {
            StarredScreen(
                viewModel = hiltViewModel<StarredViewModel>(),
                onRepoClick = { o, r -> navController.navigate("repo/$o/$r") }
            )
        }
        composable(Route.Issues.value) {
            IssuesScreen(viewModel = hiltViewModel<IssuesViewModel>())
        }
    }
}
