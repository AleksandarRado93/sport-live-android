package com.example.smart.sportlive.presentation.screens.matches.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.smart.sportlive.domain.model.DateCategory
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.domain.model.Sport
import com.example.smart.sportlive.presentation.components.ErrorContent
import com.example.smart.sportlive.presentation.components.LoadingContent
import com.example.smart.sportlive.presentation.screens.matches.viewmodel.MatchesUiState
import com.example.smart.sportlive.presentation.screens.matches.viewmodel.MatchesViewModel
import com.example.smart.sportlive.presentation.ui.theme.ChipBorder
import com.example.smart.sportlive.presentation.ui.theme.ChipUnselected
import com.example.smart.sportlive.presentation.ui.theme.GoldAccent
import com.example.smart.sportlive.presentation.ui.theme.TextPrimary
import com.example.smart.sportlive.presentation.ui.theme.TextSecondary

@Composable
fun MatchesScreen(
    modifier: Modifier = Modifier,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Sport tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sports) { sport ->
                    val isSelected = sport.id == selectedSportId
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSportSelected(sport.id) },
                        label = { Text(sport.name) },
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
            SectionHeader(title = "MEČEVI UŽIVO")
        }

        if (liveMatches.isEmpty()) {
            item {
                Text(
                    text = "Nema utakmica uživo",
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
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(title = "PREMATCH PONUDA")
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
                    text = "Nema dostupnih utakmica",
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
private fun SectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .background(GoldAccent, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}

@Composable
private fun SportIcon(iconUrl: String) {
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

private fun DateCategory.toDisplayName(): String {
    return when (this) {
        DateCategory.TODAY -> "Danas"
        DateCategory.TOMORROW -> "Sutra"
        DateCategory.WEEKEND -> "Vikend"
        DateCategory.NEXT_WEEK -> "Sledeća Nedelja"
    }
}
