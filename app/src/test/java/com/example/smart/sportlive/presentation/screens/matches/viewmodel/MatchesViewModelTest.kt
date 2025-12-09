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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    private val testDispatcher = UnconfinedTestDispatcher()

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
            // Skip Loading states until we get Success
            var state = awaitItem()
            while (state is MatchesUiState.Loading) {
                state = awaitItem()
            }

            assertTrue(state is MatchesUiState.Success)
            val success = state as MatchesUiState.Success
            assertEquals(1, success.sports.size)
            assertEquals(1, success.liveMatches.size)
            assertEquals("Premier League", success.liveMatches[0].competition?.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Error when all requests fail`() = runTest {
        whenever(getSportsUseCase()).thenReturn(flowOf(Result.Error("Sports error")))
        whenever(getCompetitionsUseCase()).thenReturn(flowOf(Result.Error("Competitions error")))
        whenever(getMatchesUseCase()).thenReturn(flowOf(Result.Error("Matches error")))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state is MatchesUiState.Loading) {
                state = awaitItem()
            }

            assertTrue(state is MatchesUiState.Error)
            cancelAndIgnoreRemainingEvents()
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
            var state = awaitItem()
            while (state is MatchesUiState.Loading) {
                state = awaitItem()
            }

            assertTrue(state is MatchesUiState.Success)
            assertEquals(1, (state as MatchesUiState.Success).sports.size)
            cancelAndIgnoreRemainingEvents()
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
            var state = awaitItem()
            while (state is MatchesUiState.Loading) {
                state = awaitItem()
            }

            // Initially selects first sport (Football)
            var success = state as MatchesUiState.Success
            assertEquals(1, success.selectedSportId)
            assertEquals(2, success.liveMatches.size) // 2 football matches

            // Select basketball
            viewModel.onSportSelected(2)
            success = awaitItem() as MatchesUiState.Success

            assertEquals(2, success.selectedSportId)
            assertEquals(1, success.liveMatches.size) // 1 basketball match
            cancelAndIgnoreRemainingEvents()
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
            var state = awaitItem()
            while (state is MatchesUiState.Loading) {
                state = awaitItem()
            }

            // Initially TODAY
            var success = state as MatchesUiState.Success
            assertEquals(DateCategory.TODAY, success.selectedDateCategory)
            assertEquals(2, success.prematchMatches.size)

            // Select TOMORROW
            viewModel.onDateCategorySelected(DateCategory.TOMORROW)
            success = awaitItem() as MatchesUiState.Success

            assertEquals(DateCategory.TOMORROW, success.selectedDateCategory)
            assertEquals(1, success.prematchMatches.size)
            cancelAndIgnoreRemainingEvents()
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
            var state = awaitItem()
            while (state is MatchesUiState.Loading) {
                state = awaitItem()
            }

            // Change to TOMORROW
            viewModel.onDateCategorySelected(DateCategory.TOMORROW)
            var success = awaitItem() as MatchesUiState.Success
            assertEquals(DateCategory.TOMORROW, success.selectedDateCategory)

            // Select different sport - should reset to TODAY
            // This updates two state flows, so we might get intermediate emissions
            viewModel.onSportSelected(2)
            
            // Wait until we get the final state with both sportId=2 and dateCategory=TODAY
            success = awaitItem() as MatchesUiState.Success
            while (success.selectedSportId != 2 || success.selectedDateCategory != DateCategory.TODAY) {
                success = awaitItem() as MatchesUiState.Success
            }

            assertEquals(2, success.selectedSportId)
            assertEquals(DateCategory.TODAY, success.selectedDateCategory)
            cancelAndIgnoreRemainingEvents()
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
            var state = awaitItem()
            while (state is MatchesUiState.Loading) {
                state = awaitItem()
            }

            val success = state as MatchesUiState.Success
            assertEquals(1, success.selectedSportId) // First sport selected
            cancelAndIgnoreRemainingEvents()
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
