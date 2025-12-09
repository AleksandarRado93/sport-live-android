package com.example.smart.sportlive.presentation.screens.matches.viewmodel

import app.cash.turbine.test
import com.example.smart.sportlive.domain.model.Competition
import com.example.smart.sportlive.domain.model.DateCategory
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.domain.model.MatchStatus
import com.example.smart.sportlive.domain.model.Matches
import com.example.smart.sportlive.domain.model.Sport
import com.example.smart.sportlive.domain.usecase.GetCompetitionsUseCase
import com.example.smart.sportlive.domain.usecase.GetMatchesUseCase
import com.example.smart.sportlive.domain.usecase.GetSportsUseCase
import com.example.smart.sportlive.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MatchesViewModelTest {

    private lateinit var getSportsUseCase: GetSportsUseCase
    private lateinit var getCompetitionsUseCase: GetCompetitionsUseCase
    private lateinit var getMatchesUseCase: GetMatchesUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSportsUseCase = mock()
        getCompetitionsUseCase = mock()
        getMatchesUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        whenever(getSportsUseCase()).thenReturn(flowOf())
        whenever(getCompetitionsUseCase()).thenReturn(flowOf())
        whenever(getMatchesUseCase()).thenReturn(flowOf())

        val viewModel = createViewModel()

        assertEquals(MatchesUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `emits Success when data is available`() = runTest {
        val sports = listOf(createSport(1, "Football"))
        val competitions = listOf(createCompetition(10, 1, "Premier League"))
        val matches = Matches(
            liveMatches = listOf(createMatch(1, sportId = 1, competitionId = 10, status = MatchStatus.LIVE)),
            preMatches = emptyList()
        )

        whenever(getSportsUseCase()).thenReturn(flowOf(Result.Success(sports)))
        whenever(getCompetitionsUseCase()).thenReturn(flowOf(Result.Success(competitions)))
        whenever(getMatchesUseCase()).thenReturn(flowOf(Result.Success(matches)))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1) // Skip Loading
            val state = awaitItem()

            assertTrue(state is MatchesUiState.Success)
            val success = state as MatchesUiState.Success
            assertEquals(1, success.sports.size)
            assertEquals(1, success.liveMatches.size)
            assertEquals("Premier League", success.liveMatches[0].competition?.name)
        }
    }

    @Test
    fun `emits Error when all requests fail`() = runTest {
        whenever(getSportsUseCase()).thenReturn(flowOf(Result.Error("Sports error")))
        whenever(getCompetitionsUseCase()).thenReturn(flowOf(Result.Error("Competitions error")))
        whenever(getMatchesUseCase()).thenReturn(flowOf(Result.Error("Matches error")))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1) // Skip Loading
            val state = awaitItem()

            assertTrue(state is MatchesUiState.Error)
        }
    }

    @Test
    fun `emits Success even when only sports succeed`() = runTest {
        val sports = listOf(createSport(1, "Football"))

        whenever(getSportsUseCase()).thenReturn(flowOf(Result.Success(sports)))
        whenever(getCompetitionsUseCase()).thenReturn(flowOf(Result.Error("error")))
        whenever(getMatchesUseCase()).thenReturn(flowOf(Result.Error("error")))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()

            assertTrue(state is MatchesUiState.Success)
            assertEquals(1, (state as MatchesUiState.Success).sports.size)
        }
    }

    @Test
    fun `filters live matches by selected sport`() = runTest {
        val sports = listOf(
            createSport(1, "Football"),
            createSport(2, "Basketball")
        )
        val matches = Matches(
            liveMatches = listOf(
                createMatch(1, sportId = 1, status = MatchStatus.LIVE),
                createMatch(2, sportId = 2, status = MatchStatus.LIVE),
                createMatch(3, sportId = 1, status = MatchStatus.LIVE)
            ),
            preMatches = emptyList()
        )

        whenever(getSportsUseCase()).thenReturn(flowOf(Result.Success(sports)))
        whenever(getCompetitionsUseCase()).thenReturn(flowOf(Result.Success(emptyList())))
        whenever(getMatchesUseCase()).thenReturn(flowOf(Result.Success(matches)))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            var state = awaitItem() as MatchesUiState.Success

            // Initially selects first sport (Football)
            assertEquals(1, state.selectedSportId)
            assertEquals(2, state.liveMatches.size) // 2 football matches

            // Select basketball
            viewModel.onSportSelected(2)
            state = awaitItem() as MatchesUiState.Success

            assertEquals(2, state.selectedSportId)
            assertEquals(1, state.liveMatches.size) // 1 basketball match
        }
    }

    @Test
    fun `filters prematch matches by date category`() = runTest {
        val sports = listOf(createSport(1, "Football"))
        val matches = Matches(
            liveMatches = emptyList(),
            preMatches = listOf(
                createMatch(1, sportId = 1, status = MatchStatus.PRE_MATCH, dateCategory = DateCategory.TODAY),
                createMatch(2, sportId = 1, status = MatchStatus.PRE_MATCH, dateCategory = DateCategory.TOMORROW),
                createMatch(3, sportId = 1, status = MatchStatus.PRE_MATCH, dateCategory = DateCategory.TODAY)
            )
        )

        whenever(getSportsUseCase()).thenReturn(flowOf(Result.Success(sports)))
        whenever(getCompetitionsUseCase()).thenReturn(flowOf(Result.Success(emptyList())))
        whenever(getMatchesUseCase()).thenReturn(flowOf(Result.Success(matches)))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            var state = awaitItem() as MatchesUiState.Success

            // Initially TODAY
            assertEquals(DateCategory.TODAY, state.selectedDateCategory)
            assertEquals(2, state.prematchMatches.size)

            // Select TOMORROW
            viewModel.onDateCategorySelected(DateCategory.TOMORROW)
            state = awaitItem() as MatchesUiState.Success

            assertEquals(DateCategory.TOMORROW, state.selectedDateCategory)
            assertEquals(1, state.prematchMatches.size)
        }
    }

    @Test
    fun `onSportSelected resets date category to TODAY`() = runTest {
        val sports = listOf(createSport(1, "Football"), createSport(2, "Basketball"))
        val matches = Matches(liveMatches = emptyList(), preMatches = emptyList())

        whenever(getSportsUseCase()).thenReturn(flowOf(Result.Success(sports)))
        whenever(getCompetitionsUseCase()).thenReturn(flowOf(Result.Success(emptyList())))
        whenever(getMatchesUseCase()).thenReturn(flowOf(Result.Success(matches)))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()

            // Change to TOMORROW
            viewModel.onDateCategorySelected(DateCategory.TOMORROW)
            var state = awaitItem() as MatchesUiState.Success
            assertEquals(DateCategory.TOMORROW, state.selectedDateCategory)

            // Select different sport - should reset to TODAY
            viewModel.onSportSelected(2)
            state = awaitItem() as MatchesUiState.Success

            assertEquals(2, state.selectedSportId)
            assertEquals(DateCategory.TODAY, state.selectedDateCategory)
        }
    }

    @Test
    fun `selects first sport by default when no sport selected`() = runTest {
        val sports = listOf(
            createSport(1, "Football"),
            createSport(2, "Basketball")
        )

        whenever(getSportsUseCase()).thenReturn(flowOf(Result.Success(sports)))
        whenever(getCompetitionsUseCase()).thenReturn(flowOf(Result.Success(emptyList())))
        whenever(getMatchesUseCase()).thenReturn(flowOf(Result.Success(Matches(emptyList(), emptyList()))))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem() as MatchesUiState.Success

            assertEquals(1, state.selectedSportId) // First sport selected
        }
    }

    private fun createViewModel() = MatchesViewModel(
        getSportsUseCase,
        getCompetitionsUseCase,
        getMatchesUseCase
    )

    private fun createSport(id: Int, name: String) = Sport(
        id = id,
        name = name,
        iconUrl = null
    )

    private fun createCompetition(id: Int, sportId: Int, name: String) = Competition(
        id = id,
        sportId = sportId,
        name = name,
        iconUrl = null
    )

    private fun createMatch(
        id: Int,
        sportId: Int = 1,
        competitionId: Int = 1,
        status: MatchStatus = MatchStatus.PRE_MATCH,
        dateCategory: DateCategory? = DateCategory.TODAY
    ) = Match(
        id = id,
        homeTeam = "Home $id",
        awayTeam = "Away $id",
        homeTeamAvatar = null,
        awayTeamAvatar = null,
        date = "2025-12-09 20:00",
        status = status,
        currentTime = if (status == MatchStatus.LIVE) "45'" else null,
        result = null,
        sportId = sportId,
        competitionId = competitionId,
        dateCategory = dateCategory
    )
}

