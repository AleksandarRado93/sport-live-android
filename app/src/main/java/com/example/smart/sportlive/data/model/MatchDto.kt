package com.example.smart.sportlive.data.model

import com.google.gson.annotations.SerializedName

data class MatchDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("homeTeam")
    val homeTeam: String,
    @SerializedName("awayTeam")
    val awayTeam: String,
    @SerializedName("homeTeamAvatar")
    val homeTeamAvatar: String?,
    @SerializedName("awayTeamAvatar")
    val awayTeamAvatar: String?,
    @SerializedName("date")
    val date: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("currentTime")
    val currentTime: String?,
    @SerializedName("result")
    val result: ResultDto?,
    @SerializedName("sportId")
    val sportId: Int,
    @SerializedName("competitionId")
    val competitionId: Int
)

data class ResultDto(
    @SerializedName("home")
    val home: Int,
    @SerializedName("away")
    val away: Int
)
