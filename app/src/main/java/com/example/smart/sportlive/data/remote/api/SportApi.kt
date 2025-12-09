package com.example.smart.sportlive.data.remote.api

import com.example.smart.sportlive.data.model.CompetitionDto
import com.example.smart.sportlive.data.model.MatchDto
import com.example.smart.sportlive.data.model.SportDto
import retrofit2.http.GET

interface SportApi {

    @GET("sports")
    suspend fun getSports(): List<SportDto>

    @GET("competitions")
    suspend fun getCompetitions(): List<CompetitionDto>

    @GET("matches")
    suspend fun getMatches(): List<MatchDto>
}

