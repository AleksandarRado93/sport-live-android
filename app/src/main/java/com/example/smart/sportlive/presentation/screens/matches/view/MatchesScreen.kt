package com.example.smart.sportlive.presentation.screens.matches.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smart.sportlive.domain.model.DateCategory
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
                liveMatches = state.liveMatches,
                prematchMatches = state.prematchMatches,
                selectedSportId = state.selectedSportId,
                selectedDateCategory = state.selectedDateCategory,
                onSportSelected = viewModel::onSportSelected,
                onDateCategorySelected = viewModel::onDateCategorySelected
            )
        }
    }
}

@Composable
private fun MatchesContent(
    sports: List<Sport>,
    liveMatches: List<Match>,
    prematchMatches: List<Match>,
    selectedSportId: Int?,
    selectedDateCategory: DateCategory,
    onSportSelected: (Int) -> Unit,
    onDateCategorySelected: (DateCategory) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sport tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sports) { sport ->
                    FilterChip(
                        selected = sport.id == selectedSportId,
                        onClick = { onSportSelected(sport.id) },
                        label = { Text(sport.name) }
                    )
                }
            }
        }

        // Live matches section
        item {
            Text(
                text = "MEČEVI UŽIVO",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (liveMatches.isEmpty()) {
            item {
                Text(
                    text = "Nema utakmica uživo",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(liveMatches) { match ->
                MatchItem(match = match)
            }
        }

        // Prematch section
        item {
            Text(
                text = "PREMATCH PONUDA",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Date category tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DateCategory.entries) { category ->
                    FilterChip(
                        selected = category == selectedDateCategory,
                        onClick = { onDateCategorySelected(category) },
                        label = { Text(category.toDisplayName()) }
                    )
                }
            }
        }

        if (prematchMatches.isEmpty()) {
            item {
                Text(
                    text = "Nema dostupnih utakmica",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(prematchMatches) { match ->
                MatchItem(match = match)
            }
        }
    }
}

private fun DateCategory.toDisplayName(): String {
    return when (this) {
        DateCategory.TODAY -> "Danas"
        DateCategory.TOMORROW -> "Sutra"
        DateCategory.WEEKEND -> "Vikend"
        DateCategory.NEXT_WEEK -> "Sledeća"
    }
}
