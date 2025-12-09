package com.example.smart.sportlive.domain.model

data class Match(
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamAvatar: String?,
    val awayTeamAvatar: String?,
    val date: String,
    val status: String,
    val currentTime: String?,
    val result: Result?,
    val sportId: Int,
    val competitionId: Int
)

data class Result(
    val home: Int,
    val away: Int
)
