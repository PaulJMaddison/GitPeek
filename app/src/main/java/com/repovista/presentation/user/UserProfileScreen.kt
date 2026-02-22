package com.repovista.presentation.user

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
import coil.compose.AsyncImage
import com.repovista.presentation.components.ErrorView
import com.repovista.presentation.components.LoadingView

@Composable
fun UserProfileScreen(viewModel: UserViewModel, onOpenStarred: (String) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val s = state) {
        is UserUiState.Loading -> LoadingView()
        is UserUiState.Error -> ErrorView(s.message)
        is UserUiState.Data -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            AsyncImage(model = s.user.avatarUrl, contentDescription = null)
            Text(text = s.user.name ?: s.user.username)
            Text(text = s.user.bio ?: "No bio")
            Text(text = "Followers: ${s.user.followers} • Following: ${s.user.following}")
            Button(onClick = { onOpenStarred(s.user.username) }) { Text("View starred repos") }
        }
    }
}
