package com.example.smart.sportlive.data.model

import com.google.gson.annotations.SerializedName

data class CompetitionDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("sportId")
    val sportId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("competitionIconUrl")
    val competitionIconUrl: String?
)
