package com.example.smart.sportlive.presentation.screens.matches.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart.sportlive.domain.model.Competition
import com.example.smart.sportlive.domain.model.DateCategory
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.domain.model.Matches
import com.example.smart.sportlive.domain.model.Sport
import com.example.smart.sportlive.domain.model.withCompetition
import com.example.smart.sportlive.domain.usecase.GetCompetitionsUseCase
import com.example.smart.sportlive.domain.usecase.GetMatchesUseCase
import com.example.smart.sportlive.domain.usecase.GetSportsUseCase
import com.example.smart.sportlive.domain.util.Result
import com.example.smart.sportlive.domain.util.dataOrDefault
import com.example.smart.sportlive.domain.util.dataOrEmpty
import com.example.smart.sportlive.domain.util.isFromCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val getSportsUseCase: GetSportsUseCase,
    private val getCompetitionsUseCase: GetCompetitionsUseCase,
    private val getMatchesUseCase: GetMatchesUseCase
) : ViewModel() {

    private val selectedSportId = MutableStateFlow<Int?>(null)
    private val selectedDateCategory = MutableStateFlow(DateCategory.TODAY)
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    val uiState: StateFlow<MatchesUiState> = refreshTrigger
        .onStart { refreshTrigger.tryEmit(Unit) }
        .flatMapLatest {
            combine(
                getSportsUseCase(),
                getCompetitionsUseCase(),
                getMatchesUseCase(),
                selectedSportId,
                selectedDateCategory
            ) { sportsResult, competitionsResult, matchesResult, sportId, dateCategory ->
                val sports = sportsResult.dataOrEmpty()
                val competitions = competitionsResult.dataOrEmpty()
                val matches = matchesResult.dataOrDefault(
                    Matches(liveMatches = emptyList(), preMatches = emptyList())
                )

                val hasData = sports.isNotEmpty() ||
                        matches.liveMatches.isNotEmpty() || matches.preMatches.isNotEmpty()
                val failed = sportsResult is Result.Error && matchesResult is Result.Error

                when {
                    hasData -> buildSuccessState(
                        sports, competitions, matches,
                        sportId, dateCategory,
                        isOffline = isFromCache(sportsResult, matchesResult)
                    )

                    failed -> MatchesUiState.Error
                    else -> MatchesUiState.Loading
                }
            }.onStart { emit(MatchesUiState.Loading) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MatchesUiState.Loading
        )

    private fun buildSuccessState(
        sports: List<Sport>,
        competitions: List<Competition>,
        matches: Matches,
        sportId: Int?,
        dateCategory: DateCategory,
        isOffline: Boolean
    ): MatchesUiState.Success {
        val currentSportId = sportId ?: sports.firstOrNull()?.id
        val competitionsMap = competitions.associateBy { it.id }

        return MatchesUiState.Success(
            sports = sports,
            liveMatches = filterLiveMatches(matches.liveMatches, currentSportId, competitionsMap),
            prematchMatches = filterPrematchMatches(
                matches.preMatches,
                currentSportId,
                dateCategory,
                competitionsMap
            ),
            selectedSportId = currentSportId,
            selectedDateCategory = dateCategory,
            isOffline = isOffline
        )
    }

    private fun filterLiveMatches(
        matches: List<Match>,
        sportId: Int?,
        competitionsMap: Map<Int, Competition>
    ): List<Match> = matches
        .filter { sportId == null || it.sportId == sportId }
        .map { it.withCompetition(competitionsMap) }

    private fun filterPrematchMatches(
        matches: List<Match>,
        sportId: Int?,
        dateCategory: DateCategory,
        competitionsMap: Map<Int, Competition>
    ): List<Match> = matches
        .filter { (sportId == null || it.sportId == sportId) && it.dateCategory == dateCategory }
        .map { it.withCompetition(competitionsMap) }
        .sortedBy { it.date }

    fun onSportSelected(sportId: Int) {
        selectedSportId.value = sportId
        selectedDateCategory.value = DateCategory.TODAY
    }

    fun onDateCategorySelected(category: DateCategory) {
        selectedDateCategory.value = category
    }

    fun retry() {
        viewModelScope.launch { refreshTrigger.emit(Unit) }
    }
}

sealed class MatchesUiState {
    data object Loading : MatchesUiState()
    data class Success(
        val sports: List<Sport>,
        val liveMatches: List<Match>,
        val prematchMatches: List<Match>,
        val selectedSportId: Int?,
        val selectedDateCategory: DateCategory,
        val isOffline: Boolean = false
    ) : MatchesUiState()

    data object Error : MatchesUiState()
}
