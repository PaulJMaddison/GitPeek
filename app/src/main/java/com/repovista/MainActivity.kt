package com.repovista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val savedToken by viewModel.token.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hello RepoVista")
        Text(
            text = if (savedToken.isNullOrBlank()) {
                "GitHub token not set (optional, recommended for higher rate limits)."
            } else {
                "GitHub token is configured."
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp)
        )
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("GitHub Token Settings")
        }
    }

    if (showDialog) {
        TokenSettingsDialog(
            initialToken = savedToken.orEmpty(),
            onDismiss = { showDialog = false },
            onSave = { token ->
                viewModel.saveToken(token)
                showDialog = false
            }
        )
    }
}

@Composable
private fun TokenSettingsDialog(
    initialToken: String,
    onDismiss: () -> Unit,
    onSave: (String?) -> Unit
) {
    var tokenInput by remember(initialToken) { mutableStateOf(initialToken) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("GitHub Personal Access Token") },
        text = {
            Column {
                Text("Optional, but recommended to reduce GitHub API rate limiting.")
                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    label = { Text("Token") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(tokenInput) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
