package com.example.smart.sportlive.data.mapper

import com.example.smart.sportlive.data.model.CompetitionDto
import com.example.smart.sportlive.data.model.MatchDto
import com.example.smart.sportlive.data.model.ResultDto
import com.example.smart.sportlive.data.model.SportDto
import com.example.smart.sportlive.domain.model.MatchStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MapperTest {

    @Test
    fun `toSport maps all fields correctly`() {
        val dto = SportDto(
            id = 1,
            name = "Football",
            sportIconUrl = "https://example.com/football.svg"
        )

        val result = dto.toSport()

        assertEquals(1, result.id)
        assertEquals("Football", result.name)
        assertEquals("https://example.com/football.svg", result.iconUrl)
    }

    @Test
    fun `toSport handles null iconUrl`() {
        val dto = SportDto(
            id = 2,
            name = "Basketball",
            sportIconUrl = null
        )

        val result = dto.toSport()

        assertEquals(2, result.id)
        assertEquals("Basketball", result.name)
        assertNull(result.iconUrl)
    }

    @Test
    fun `toCompetition maps all fields correctly`() {
        val dto = CompetitionDto(
            id = 10,
            sportId = 1,
            name = "Premier League",
            competitionIconUrl = "https://example.com/pl.svg"
        )

        val result = dto.toCompetition()

        assertEquals(10, result.id)
        assertEquals(1, result.sportId)
        assertEquals("Premier League", result.name)
        assertEquals("https://example.com/pl.svg", result.iconUrl)
    }

    @Test
    fun `toCompetition handles null iconUrl`() {
        val dto = CompetitionDto(
            id = 11,
            sportId = 2,
            name = "La Liga",
            competitionIconUrl = null
        )

        val result = dto.toCompetition()

        assertNull(result.iconUrl)
    }

    @Test
    fun `toResult maps scores correctly`() {
        val dto = ResultDto(home = 3, away = 1)

        val result = dto.toResult()

        assertEquals(3, result.home)
        assertEquals(1, result.away)
    }

    @Test
    fun `toResult handles zero scores`() {
        val dto = ResultDto(home = 0, away = 0)

        val result = dto.toResult()

        assertEquals(0, result.home)
        assertEquals(0, result.away)
    }

    @Test
    fun `toMatch maps LIVE status correctly`() {
        val dto = createMatchDto(status = "LIVE", currentTime = "45'")

        val result = dto.toMatch()

        assertEquals(MatchStatus.LIVE, result.status)
        assertEquals("45'", result.currentTime)
        assertNull(result.dateCategory) // Live matches don't have dateCategory
    }

    @Test
    fun `toMatch maps PRE_MATCH status correctly`() {
        val dto = createMatchDto(status = "PRE_MATCH", currentTime = null)

        val result = dto.toMatch()

        assertEquals(MatchStatus.PRE_MATCH, result.status)
        assertNull(result.currentTime)
    }

    @Test
    fun `toMatch maps unknown status to PRE_MATCH`() {
        val dto = createMatchDto(status = "UNKNOWN")

        val result = dto.toMatch()

        assertEquals(MatchStatus.PRE_MATCH, result.status)
    }

    @Test
    fun `toMatch maps result when present`() {
        val dto = createMatchDto(
            status = "LIVE",
            result = ResultDto(home = 2, away = 1)
        )

        val result = dto.toMatch()

        assertEquals(2, result.result?.home)
        assertEquals(1, result.result?.away)
    }

    @Test
    fun `toMatch handles null result`() {
        val dto = createMatchDto(result = null)

        val result = dto.toMatch()

        assertNull(result.result)
    }

    @Test
    fun `toMatch maps team information correctly`() {
        val dto = createMatchDto(
            homeTeam = "Arsenal",
            awayTeam = "Liverpool",
            homeTeamAvatar = "https://avatar.com/arsenal",
            awayTeamAvatar = "https://avatar.com/liverpool"
        )

        val result = dto.toMatch()

        assertEquals("Arsenal", result.homeTeam)
        assertEquals("Liverpool", result.awayTeam)
        assertEquals("https://avatar.com/arsenal", result.homeTeamAvatar)
        assertEquals("https://avatar.com/liverpool", result.awayTeamAvatar)
    }

    @Test
    fun `toMatches separates live and prematch correctly`() {
        val dtos = listOf(
            createMatchDto(id = 1, status = "LIVE"),
            createMatchDto(id = 2, status = "PRE_MATCH"),
            createMatchDto(id = 3, status = "LIVE"),
            createMatchDto(id = 4, status = "PRE_MATCH")
        )

        val result = dtos.toMatches()

        assertEquals(2, result.liveMatches.size)
        assertEquals(2, result.preMatches.size)
        assertEquals(listOf(1, 3), result.liveMatches.map { it.id })
        assertEquals(listOf(2, 4), result.preMatches.map { it.id })
    }

    @Test
    fun `toMatches handles empty list`() {
        val dtos = emptyList<MatchDto>()

        val result = dtos.toMatches()

        assertEquals(0, result.liveMatches.size)
        assertEquals(0, result.preMatches.size)
    }

    @Test
    fun `toMatches handles all live matches`() {
        val dtos = listOf(
            createMatchDto(id = 1, status = "LIVE"),
            createMatchDto(id = 2, status = "LIVE")
        )

        val result = dtos.toMatches()

        assertEquals(2, result.liveMatches.size)
        assertEquals(0, result.preMatches.size)
    }

    @Test
    fun `toMatches handles all prematch matches`() {
        val dtos = listOf(
            createMatchDto(id = 1, status = "PRE_MATCH"),
            createMatchDto(id = 2, status = "PRE_MATCH")
        )

        val result = dtos.toMatches()

        assertEquals(0, result.liveMatches.size)
        assertEquals(2, result.preMatches.size)
    }

    private fun createMatchDto(
        id: Int = 1,
        homeTeam: String = "Home Team",
        awayTeam: String = "Away Team",
        homeTeamAvatar: String? = null,
        awayTeamAvatar: String? = null,
        date: String = "2025-12-09 20:00",
        status: String = "PRE_MATCH",
        currentTime: String? = null,
        result: ResultDto? = null,
        sportId: Int = 1,
        competitionId: Int = 1
    ) = MatchDto(
        id = id,
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        homeTeamAvatar = homeTeamAvatar,
        awayTeamAvatar = awayTeamAvatar,
        date = date,
        status = status,
        currentTime = currentTime,
        result = result,
        sportId = sportId,
        competitionId = competitionId
    )
}

