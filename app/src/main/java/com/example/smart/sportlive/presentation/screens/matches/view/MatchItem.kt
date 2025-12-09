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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart.sportlive.R
import com.example.smart.sportlive.domain.model.DateCategory
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.presentation.components.CompetitionIcon
import com.example.smart.sportlive.presentation.components.TeamAvatar
import com.example.smart.sportlive.presentation.components.TeamRowWithAvatar
import com.example.smart.sportlive.presentation.ui.theme.DarkCard
import com.example.smart.sportlive.presentation.ui.theme.TealAccent
import com.example.smart.sportlive.presentation.ui.theme.TextPrimary
import com.example.smart.sportlive.presentation.ui.theme.TextSecondary
import com.example.smart.sportlive.presentation.ui.theme.spacing

@Composable
fun MatchItem(match: Match, isLive: Boolean = false) {
    val spacing = MaterialTheme.spacing
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier.padding(spacing.default)
        ) {
            if (isLive) {
                // Live match header: Competition icon + name + play icon + time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    match.competition?.let { competition ->
                        competition.iconUrl?.let { iconUrl ->
                            CompetitionIcon(iconUrl = iconUrl)
                            Spacer(modifier = Modifier.width(spacing.small))
                        }
                        Text(
                            text = competition.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }

                    match.currentTime?.let { time ->
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Text(
                            text = "▶",
                            color = TealAccent,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Text(
                            text = time,
                            style = MaterialTheme.typography.labelMedium,
                            color = TealAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.medium))

                // Live match teams with scores
                TeamRowWithAvatar(
                    teamName = match.homeTeam,
                    avatarUrl = match.homeTeamAvatar,
                    score = match.result?.home
                )
                Spacer(modifier = Modifier.height(spacing.small))
                TeamRowWithAvatar(
                    teamName = match.awayTeam,
                    avatarUrl = match.awayTeamAvatar,
                    score = match.result?.away
                )
            } else {
                // Prematch layout - team logos on sides, info in center
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home team with avatar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        TeamAvatar(
                            avatarUrl = match.homeTeamAvatar,
                            size = 56
                        )
                        Spacer(modifier = Modifier.height(spacing.small))
                        Text(
                            text = match.homeTeam,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Center - competition info + time
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Competition icon + name
                        match.competition?.let { competition ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                competition.iconUrl?.let { iconUrl ->
                                    CompetitionIcon(iconUrl = iconUrl)
                                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                                }
                            }
                            Text(
                                text = competition.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(spacing.extraSmall))
                        }
                        
                        // Date category label
                        match.dateCategory?.let {
                            Text(
                                text = it.toLabel(),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                        
                        // Time
                        Text(
                            text = match.date.split(" ").getOrNull(1) ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Away team with avatar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        TeamAvatar(
                            avatarUrl = match.awayTeamAvatar,
                            size = 56
                        )
                        Spacer(modifier = Modifier.height(spacing.small))
                        Text(
                            text = match.awayTeam,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateCategory.toLabel(): String {
    return when (this) {
        DateCategory.TODAY -> stringResource(R.string.date_today)
        DateCategory.TOMORROW -> stringResource(R.string.date_tomorrow)
        DateCategory.WEEKEND -> stringResource(R.string.date_weekend)
        DateCategory.NEXT_WEEK -> stringResource(R.string.date_next_week)
    }
}
