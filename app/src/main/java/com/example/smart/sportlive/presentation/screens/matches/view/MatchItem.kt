package com.example.smart.sportlive.presentation.screens.matches.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.presentation.ui.theme.DarkCard
import com.example.smart.sportlive.presentation.ui.theme.TealAccent
import com.example.smart.sportlive.presentation.ui.theme.TextPrimary
import com.example.smart.sportlive.presentation.ui.theme.TextSecondary

@Composable
fun MatchItem(match: Match, isLive: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row: Competition name + current time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                match.competition?.let { competition ->
                    Text(
                        text = competition.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }

                if (isLive) {
                    match.currentTime?.let { time ->
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "▶",
                            color = TealAccent,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = time,
                            style = MaterialTheme.typography.labelMedium,
                            color = TealAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLive) {
                // Live match layout - teams with scores
                TeamRow(
                    teamName = match.homeTeam,
                    score = match.result?.home
                )
                Spacer(modifier = Modifier.height(8.dp))
                TeamRow(
                    teamName = match.awayTeam,
                    score = match.result?.away
                )
            } else {
                // Prematch layout - centered with time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home team
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = match.homeTeam,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }

                    // Center - date/time
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = match.date.split(" ").getOrNull(1) ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Away team
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = match.awayTeam,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamRow(
    teamName: String,
    score: Int?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = teamName,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
        score?.let {
            Text(
                text = it.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
