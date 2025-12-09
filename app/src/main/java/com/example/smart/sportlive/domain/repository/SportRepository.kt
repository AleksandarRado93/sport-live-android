package com.example.smart.sportlive.domain.repository

import com.example.smart.sportlive.data.model.CompetitionDto
import com.example.smart.sportlive.data.model.MatchDto
import com.example.smart.sportlive.data.model.SportDto
import kotlinx.coroutines.flow.Flow

interface SportRepository {
    fun getSports(): Flow<List<SportDto>>
    fun getCompetitions(): Flow<List<CompetitionDto>>
    fun getMatches(): Flow<List<MatchDto>>
}
