package com.example.smart.sportlive.presentation.screens.matches.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.presentation.ui.theme.DarkCard
import com.example.smart.sportlive.presentation.ui.theme.DarkSurface
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
            if (isLive) {
                // Live match header: Competition icon + name + play icon + time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    match.competition?.let { competition ->
                        competition.iconUrl?.let { iconUrl ->
                            CompetitionIcon(iconUrl = iconUrl)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = competition.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }

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

                Spacer(modifier = Modifier.height(12.dp))

                // Live match teams with scores
                TeamRowWithAvatar(
                    teamName = match.homeTeam,
                    avatarUrl = match.homeTeamAvatar,
                    score = match.result?.home
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                        Spacer(modifier = Modifier.height(8.dp))
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
                                    Spacer(modifier = Modifier.width(4.dp))
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
                            Spacer(modifier = Modifier.height(4.dp))
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
                        Spacer(modifier = Modifier.height(8.dp))
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

private fun com.example.smart.sportlive.domain.model.DateCategory.toLabel(): String {
    return when (this) {
        com.example.smart.sportlive.domain.model.DateCategory.TODAY -> "Danas"
        com.example.smart.sportlive.domain.model.DateCategory.TOMORROW -> "Sutra"
        com.example.smart.sportlive.domain.model.DateCategory.WEEKEND -> "Vikend"
        com.example.smart.sportlive.domain.model.DateCategory.NEXT_WEEK -> "Sledeća Nedelja"
    }
}

@Composable
private fun CompetitionIcon(iconUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TeamAvatar(
    avatarUrl: String?,
    size: Int = 32
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(DarkSurface),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    .data(avatarUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(size.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun TeamRowWithAvatar(
    teamName: String,
    avatarUrl: String?,
    score: Int?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            TeamAvatar(avatarUrl = avatarUrl, size = 28)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = teamName,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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
