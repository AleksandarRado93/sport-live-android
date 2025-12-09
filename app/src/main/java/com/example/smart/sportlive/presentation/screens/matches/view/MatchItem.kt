package com.example.smart.sportlive.presentation.screens.matches.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smart.sportlive.domain.model.Match

@Composable
fun MatchItem(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "${match.homeTeam} vs ${match.awayTeam}",
                style = MaterialTheme.typography.bodyLarge
            )
            match.result?.let { result ->
                Text(
                    text = "${result.home} - ${result.away}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = "Status: ${match.status}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

