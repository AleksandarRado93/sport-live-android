package com.example.smart.sportlive.presentation.screens.matches.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart.sportlive.domain.model.DateCategory
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.domain.model.Matches
import com.example.smart.sportlive.domain.model.Sport
import com.example.smart.sportlive.domain.usecase.GetCompetitionsUseCase
import com.example.smart.sportlive.domain.usecase.GetMatchesUseCase
import com.example.smart.sportlive.domain.usecase.GetSportsUseCase
import com.example.smart.sportlive.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    getSportsUseCase: GetSportsUseCase,
    getCompetitionsUseCase: GetCompetitionsUseCase,
    getMatchesUseCase: GetMatchesUseCase
) : ViewModel() {

    private val selectedSportId = MutableStateFlow<Int?>(null)
    private val selectedDateCategory = MutableStateFlow(DateCategory.TODAY)

    val uiState: StateFlow<MatchesUiState> = combine(
        getSportsUseCase(),
        getCompetitionsUseCase(),
        getMatchesUseCase(),
        selectedSportId,
        selectedDateCategory
    ) { sportsResult, competitionsResult, matchesResult, sportId, dateCategory ->

        val sports =
            if (sportsResult is Result.Success) sportsResult.data
            else emptyList()

        val competitions =
            if (competitionsResult is Result.Success) competitionsResult.data
            else emptyList()

        val matches =
            if (matchesResult is Result.Success) matchesResult.data
            else Matches(emptyList(), emptyList())

        val hasData = sports.isNotEmpty() ||
                matches.liveMatches.isNotEmpty() || matches.preMatches.isNotEmpty()

        val allFailed = sportsResult is Result.Error && matchesResult is Result.Error

        val currentSportId = sportId ?: sports.firstOrNull()?.id

        when {
            hasData -> {
                val competitionsMap = competitions.associateBy { it.id }

                val filteredLive = matches.liveMatches
                    .filter { match -> currentSportId == null || match.sportId == currentSportId }
                    .map { match -> match.copy(competition = competitionsMap[match.competitionId]) }

                val filteredPrematch = matches.preMatches
                    .filter { match ->
                        (currentSportId == null || match.sportId == currentSportId) &&
                        match.dateCategory == dateCategory
                    }
                    .map { match -> match.copy(competition = competitionsMap[match.competitionId]) }

                MatchesUiState.Success(
                    sports = sports,
                    liveMatches = filteredLive,
                    prematchMatches = filteredPrematch,
                    selectedSportId = currentSportId,
                    selectedDateCategory = dateCategory
                )
            }
            allFailed -> MatchesUiState.Error
            else -> MatchesUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MatchesUiState.Loading
    )

    fun onSportSelected(sportId: Int) {
        selectedSportId.value = sportId
        selectedDateCategory.value = DateCategory.TODAY
    }

    fun onDateCategorySelected(category: DateCategory) {
        selectedDateCategory.value = category
    }
}

sealed class MatchesUiState {
    data object Loading : MatchesUiState()
    data class Success(
        val sports: List<Sport>,
        val liveMatches: List<Match>,
        val prematchMatches: List<Match>,
        val selectedSportId: Int?,
        val selectedDateCategory: DateCategory
    ) : MatchesUiState()

    data object Error : MatchesUiState()
}
