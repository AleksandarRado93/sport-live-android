package com.example.smart.sportlive.presentation.screens.matches.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart.sportlive.domain.model.Competition
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.domain.model.Sport
import com.example.smart.sportlive.domain.usecase.GetCompetitionsUseCase
import com.example.smart.sportlive.domain.usecase.GetMatchesUseCase
import com.example.smart.sportlive.domain.usecase.GetSportsUseCase
import com.example.smart.sportlive.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val uiState: StateFlow<MatchesUiState> = combine(
        getSportsUseCase(),
        getCompetitionsUseCase(),
        getMatchesUseCase()
    ) { sportsResult, competitionsResult, matchesResult ->

        val sports =
            if (sportsResult is Result.Success) sportsResult.data
            else emptyList()

        val competitions =
            if (competitionsResult is Result.Success) competitionsResult.data
            else emptyList()

        val matches =
            if (matchesResult is Result.Success) matchesResult.data
            else emptyList()

        val hasData = sports.isNotEmpty() || competitions.isNotEmpty() || matches.isNotEmpty()
        val allFailed = sportsResult is Result.Error
                && competitionsResult is Result.Error
                && matchesResult is Result.Error

        when {
            hasData -> MatchesUiState.Success(sports, competitions, matches)
            allFailed -> MatchesUiState.Error
            else -> MatchesUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MatchesUiState.Loading
    )
}

sealed class MatchesUiState {
    data object Loading : MatchesUiState()
    data class Success(
        val sports: List<Sport>,
        val competitions: List<Competition>,
        val matches: List<Match>
    ) : MatchesUiState()

    data object Error : MatchesUiState()
}

