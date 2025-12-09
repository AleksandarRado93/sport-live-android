package com.example.smart.sportlive.domain.repository

import com.example.smart.sportlive.domain.model.Competition
import com.example.smart.sportlive.domain.model.Matches
import com.example.smart.sportlive.domain.model.Sport
import com.example.smart.sportlive.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface SportRepository {
    fun getSports(): Flow<Result<List<Sport>>>
    fun getCompetitions(): Flow<Result<List<Competition>>>
    fun getMatches(): Flow<Result<Matches>>
}
