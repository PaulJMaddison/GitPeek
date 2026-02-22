package com.repovista.presentation.starred

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun StarredScreen(viewModel: StarredViewModel, onRepoClick: (String, String) -> Unit) {
    val repos = viewModel.repos.collectAsLazyPagingItems()
    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        items(repos.itemCount) { idx ->
            repos[idx]?.let {
                Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable { onRepoClick(it.owner.login, it.name) }) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(it.fullName)
                        Text(it.description ?: "No description")
                    }
                }
            }
        }
    }
}
