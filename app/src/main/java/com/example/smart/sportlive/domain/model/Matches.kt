package com.example.smart.sportlive.domain.model

data class Matches(
    val liveMatches: List<Match>,
    val preMatches: List<Match>
)

data class Match(
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamAvatar: String?,
    val awayTeamAvatar: String?,
    val date: String,
    val status: MatchStatus,
    val currentTime: String?,
    val result: Result?,
    val sportId: Int,
    val competitionId: Int,
    val dateCategory: DateCategory?,
    val competition: Competition? = null
)

data class Result(
    val home: Int,
    val away: Int
)

enum class MatchStatus {
    LIVE,
    PRE_MATCH
}

enum class DateCategory {
    TODAY,
    TOMORROW,
    WEEKEND,
    NEXT_WEEK
}
