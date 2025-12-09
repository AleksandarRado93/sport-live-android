package com.example.smart.sportlive.domain.repository

import com.example.smart.sportlive.domain.model.Competition
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.domain.model.Sport
import kotlinx.coroutines.flow.Flow

interface SportRepository {
    fun getSports(): Flow<List<Sport>>
    fun getCompetitions(): Flow<List<Competition>>
    fun getMatches(): Flow<List<Match>>
}
