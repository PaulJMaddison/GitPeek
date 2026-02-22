package com.repovista.presentation.issues

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
fun IssuesScreen(viewModel: IssuesViewModel) {
    val issues = viewModel.issues.collectAsLazyPagingItems()
    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        items(issues.itemCount) { idx ->
            issues[idx]?.let {
                Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(it.title)
                        Text(it.body ?: "No description")
                    }
                }
            }
        }
    }
}
