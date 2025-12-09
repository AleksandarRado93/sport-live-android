package com.example.smart.sportlive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.smart.sportlive.presentation.screens.matches.MatchesScreen
import com.example.smart.sportlive.presentation.ui.theme.SportLiveTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SportLiveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MatchesScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
