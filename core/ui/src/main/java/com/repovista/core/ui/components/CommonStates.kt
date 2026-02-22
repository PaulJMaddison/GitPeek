package com.repovista.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.repovista.core.model.Issue
import com.repovista.core.ui.theme.RepoVistaTheme
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LoadingState(message: String = "Loading...") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        androidx.compose.material3.CircularProgressIndicator()
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
        },
        title = "Something went wrong",
        message = message,
        action = {
            OutlinedButton(onClick = onRetry) {
                Text("Retry")
            }
        },
        modifier = modifier
    )
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        icon?.let {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape,
                tonalElevation = 2.dp
            ) {
                Row(modifier = Modifier.padding(16.dp)) { it() }
            }
        }
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        action?.let {
            Row(modifier = Modifier.padding(top = 8.dp)) { it() }
        }
    }
}

@Composable
fun RepoListItem(
    name: String,
    description: String?,
    stars: Int,
    language: String?,
    ownerAvatarUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(14.dp)
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            val ownerName = name.substringBefore("/")
            AsyncImage(
                model = ownerAvatarUrl,
                contentDescription = "Avatar for $ownerName",
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                description?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text(stars.toString()) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                    language?.takeIf { it.isNotBlank() }?.let { lang ->
                        AssistChip(
                            onClick = {},
                            enabled = false,
                            label = { Text(lang) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Storage,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IssueListItem(issue: Issue, modifier: Modifier = Modifier) {
    val createdAt = remember(issue.createdAt) {
        DateTimeFormatter.ofPattern("MMM d, yyyy")
            .withZone(ZoneId.systemDefault())
            .format(issue.createdAt)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = issue.title, style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text("#${issue.number}") }
                )
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text(issue.state.replaceFirstChar(Char::uppercase)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.BugReport,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
            Text(
                text = "by @${issue.authorLogin} • ${issue.comments} comments • $createdAt",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UserHeader(
    login: String,
    name: String?,
    avatarUrl: String,
    bio: String?,
    followers: Int,
    following: Int,
    publicRepos: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar for $login",
                    modifier = Modifier
                        .size(70.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                )
                Column {
                    Text(text = name ?: login, style = MaterialTheme.typography.titleLarge)
                    Text(text = "@$login", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            bio?.takeIf { it.isNotBlank() }?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatChip("Followers", followers)
                StatChip("Following", following)
                StatChip("Repos", publicRepos)
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: Int) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value.toString(), style = MaterialTheme.typography.titleSmall)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RepoListItemPreview() {
    RepoVistaTheme {
        RepoListItem(
            name = "octocat/Hello-World",
            description = "My first repository on GitHub!",
            stars = 42,
            language = "Kotlin",
            ownerAvatarUrl = "",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyStatePreview() {
    RepoVistaTheme {
        EmptyState(
            title = "Search GitHub repositories",
            message = "Find repositories, explore details, and track issues.",
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Storage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun IssueListItemPreview() {
    RepoVistaTheme {
        IssueListItem(
            issue = Issue(
                id = 1L,
                number = 128,
                title = "Crash on startup in dark mode",
                state = "open",
                authorLogin = "octocat",
                comments = 4,
                createdAt = java.time.Instant.now()
            )
        )
    }
}
