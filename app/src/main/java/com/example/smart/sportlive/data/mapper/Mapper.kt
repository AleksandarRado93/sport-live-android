package com.example.smart.sportlive.data.mapper

import com.example.smart.sportlive.data.model.CompetitionDto
import com.example.smart.sportlive.data.model.MatchDto
import com.example.smart.sportlive.data.model.ResultDto
import com.example.smart.sportlive.data.model.SportDto
import com.example.smart.sportlive.domain.model.Competition
import com.example.smart.sportlive.domain.model.Match
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
    return Match(
        id = id,
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        homeTeamAvatar = homeTeamAvatar,
        awayTeamAvatar = awayTeamAvatar,
        date = date,
        status = status,
        currentTime = currentTime,
        result = result?.toResult(),
        sportId = sportId,
        competitionId = competitionId
    )
}

fun ResultDto.toResult(): Result {
    return Result(
        home = home,
        away = away
    )
}

