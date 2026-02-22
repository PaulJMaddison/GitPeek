package com.repovista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.repovista.core.ui.theme.RepoVistaTheme
import com.repovista.navigation.RepoVistaRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RepoVistaTheme {
                RepoVistaRoot()
            }
        }
    }
}
