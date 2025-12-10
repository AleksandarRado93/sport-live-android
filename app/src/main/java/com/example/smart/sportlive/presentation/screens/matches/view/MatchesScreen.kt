package com.example.smart.sportlive.presentation.screens.matches.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smart.sportlive.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smart.sportlive.domain.model.DateCategory
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.domain.model.Sport
import com.example.smart.sportlive.presentation.components.ErrorContent
import com.example.smart.sportlive.presentation.components.LoadingContent
import com.example.smart.sportlive.presentation.components.OfflineBanner
import com.example.smart.sportlive.presentation.components.SectionHeader
import com.example.smart.sportlive.presentation.components.SportIcon
import com.example.smart.sportlive.presentation.screens.matches.viewmodel.MatchesUiState
import com.example.smart.sportlive.presentation.util.translateSportName
import com.example.smart.sportlive.presentation.screens.matches.viewmodel.MatchesViewModel
import com.example.smart.sportlive.presentation.ui.theme.ChipBorder
import com.example.smart.sportlive.presentation.ui.theme.ChipUnselected
import com.example.smart.sportlive.presentation.ui.theme.GoldAccent
import com.example.smart.sportlive.presentation.ui.theme.TextPrimary
import com.example.smart.sportlive.presentation.ui.theme.TextSecondary
import com.example.smart.sportlive.presentation.ui.theme.spacing

@Composable
fun MatchesScreen(
    modifier: Modifier = Modifier,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is MatchesUiState.Loading -> LoadingContent()
            is MatchesUiState.Error -> ErrorContent(onRetry = viewModel::retry)
            is MatchesUiState.Success -> {
                if (state.isOffline) {
                    OfflineBanner()
                }
                
                MatchesContent(
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
    val spacing = MaterialTheme.spacing
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.default),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        // Sport tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                items(sports) { sport ->
                    val isSelected = sport.id == selectedSportId
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSportSelected(sport.id) },
                        label = { Text(translateSportName(sport.name)) },
                        leadingIcon = {
                            sport.iconUrl?.let { iconUrl ->
                                SportIcon(iconUrl = iconUrl)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = ChipBorder,
                            selectedBorderColor = GoldAccent
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = ChipUnselected,
                            labelColor = TextPrimary,
                            selectedContainerColor = GoldAccent,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }

        // Live matches section header
        item {
            SectionHeader(title = stringResource(R.string.section_live_matches))
        }

        if (liveMatches.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_live_matches),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            items(liveMatches) { match ->
                MatchItem(match = match, isLive = true)
            }
        }

        // Prematch section header
        item {
            Spacer(modifier = Modifier.height(spacing.small))
            SectionHeader(title = stringResource(R.string.section_prematch))
        }

        // Date category tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                items(DateCategory.entries) { category ->
                    FilterChip(
                        selected = category == selectedDateCategory,
                        onClick = { onDateCategorySelected(category) },
                        label = { Text(category.toDisplayName()) },
                        shape = RoundedCornerShape(8.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = category == selectedDateCategory,
                            borderColor = ChipBorder,
                            selectedBorderColor = GoldAccent
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = ChipUnselected,
                            labelColor = TextPrimary,
                            selectedContainerColor = GoldAccent,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }

        if (prematchMatches.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_available_matches),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            items(prematchMatches) { match ->
                MatchItem(match = match, isLive = false)
            }
        }
    }
}

@Composable
private fun DateCategory.toDisplayName(): String {
    return when (this) {
        DateCategory.TODAY -> stringResource(R.string.date_today)
        DateCategory.TOMORROW -> stringResource(R.string.date_tomorrow)
        DateCategory.WEEKEND -> stringResource(R.string.date_weekend)
        DateCategory.NEXT_WEEK -> stringResource(R.string.date_next_week)
    }
}
