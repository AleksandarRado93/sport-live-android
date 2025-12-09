package com.example.smart.sportlive.presentation.screens.matches.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smart.sportlive.domain.model.Competition
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.domain.model.Sport
import com.example.smart.sportlive.presentation.components.ErrorContent
import com.example.smart.sportlive.presentation.components.LoadingContent
import com.example.smart.sportlive.presentation.screens.matches.viewmodel.MatchesUiState
import com.example.smart.sportlive.presentation.screens.matches.viewmodel.MatchesViewModel

@Composable
fun MatchesScreen(
    modifier: Modifier = Modifier,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        when (val state = uiState) {
            is MatchesUiState.Loading -> LoadingContent()
            is MatchesUiState.Error -> ErrorContent()
            is MatchesUiState.Success -> MatchesContent(
                sports = state.sports,
                competitions = state.competitions,
                matches = state.matches
            )
        }
    }
}

@Composable
private fun MatchesContent(
    sports: List<Sport>,
    competitions: List<Competition>,
    matches: List<Match>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sports section
        item {
            Text(
                text = "Sports (${sports.size})",
                style = MaterialTheme.typography.titleMedium
            )
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sports) { sport ->
                    SportItem(sport = sport)
                }
            }
        }

        // Competitions section
        item {
            Text(
                text = "Competitions (${competitions.size})",
                style = MaterialTheme.typography.titleMedium
            )
        }
        items(competitions) { competition ->
            CompetitionItem(competition = competition)
        }

        // Matches section
        item {
            Text(
                text = "Matches (${matches.size})",
                style = MaterialTheme.typography.titleMedium
            )
        }
        items(matches) { match ->
            MatchItem(match = match)
        }
    }
}
