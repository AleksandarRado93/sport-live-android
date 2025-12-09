package com.example.smart.sportlive.data.mapper

import com.example.smart.sportlive.data.model.CompetitionDto
import com.example.smart.sportlive.domain.util.DateHelper
import com.example.smart.sportlive.data.model.MatchDto
import com.example.smart.sportlive.data.model.ResultDto
import com.example.smart.sportlive.data.model.SportDto
import com.example.smart.sportlive.domain.model.Competition
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.domain.model.Matches
import com.example.smart.sportlive.domain.model.MatchStatus
import com.example.smart.sportlive.domain.model.Result
import com.example.smart.sportlive.domain.model.Sport

fun SportDto.toSport(): Sport {
    return Sport(
        id = id,
        name = name,
        iconUrl = sportIconUrl
    )
}

fun CompetitionDto.toCompetition(): Competition {
    return Competition(
        id = id,
        sportId = sportId,
        name = name,
        iconUrl = competitionIconUrl
    )
}

fun MatchDto.toMatch(): Match {
    val matchStatus = if (status == "LIVE") MatchStatus.LIVE else MatchStatus.PRE_MATCH
    val dateCategory = if (matchStatus == MatchStatus.PRE_MATCH) {
        DateHelper.getDateCategory(date)
    } else null

    return Match(
        id = id,
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        homeTeamAvatar = homeTeamAvatar,
        awayTeamAvatar = awayTeamAvatar,
        date = date,
        status = matchStatus,
        currentTime = currentTime,
        result = result?.toResult(),
        sportId = sportId,
        competitionId = competitionId,
        dateCategory = dateCategory
    )
}

fun ResultDto.toResult(): Result {
    return Result(
        home = home,
        away = away
    )
}

fun List<MatchDto>.toMatches(): Matches {
    val allMatches = this.map { it.toMatch() }
    return Matches(
        liveMatches = allMatches.filter { it.status == MatchStatus.LIVE },
        preMatches = allMatches.filter { it.status == MatchStatus.PRE_MATCH }
    )
}
