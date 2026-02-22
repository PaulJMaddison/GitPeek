package com.repovista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.repovista.presentation.navigation.RepoVistaNavHost
import com.repovista.ui.theme.RepoVistaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RepoVistaTheme {
                Surface {
                    RepoVistaNavHost()
                }
            }
        }
    }
}
