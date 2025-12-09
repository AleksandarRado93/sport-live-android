package com.example.smart.sportlive.data.repository

import com.example.smart.sportlive.data.local.cache.FileCacheManager
import com.example.smart.sportlive.data.model.CompetitionDto
import com.example.smart.sportlive.data.model.MatchDto
import com.example.smart.sportlive.data.model.SportDto
import com.example.smart.sportlive.data.remote.api.SportApi
import com.example.smart.sportlive.domain.repository.SportRepository
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository implementation using cache-first-then-network pattern.
 *
 * Flow:
 * 1. Emit cached data from file (if exists) for immediate UI update
 * 2. Fetch fresh data from API
 * 3. Save API response to file cache
 * 4. Emit fresh data to update UI
 */
 
class SportRepositoryImpl @Inject constructor(
    private val sportApi: SportApi,
    private val cacheManager: FileCacheManager
) : SportRepository {

    override fun getSports(): Flow<List<SportDto>> = flow {
        val cachedSports = cacheManager.readFromFile<List<SportDto>>(
            FileCacheManager.SPORTS_CACHE,
            object : TypeToken<List<SportDto>>() {}.type
        )

        if (!cachedSports.isNullOrEmpty()) {
            emit(cachedSports)
        }

        val remoteSports = sportApi.getSports()

        cacheManager.saveToFile(FileCacheManager.SPORTS_CACHE, remoteSports)

        emit(remoteSports)
    }

    override fun getCompetitions(): Flow<List<CompetitionDto>> = flow {
        val cachedCompetitions = cacheManager.readFromFile<List<CompetitionDto>>(
            FileCacheManager.COMPETITIONS_CACHE,
            object : TypeToken<List<CompetitionDto>>() {}.type
        )

        if (!cachedCompetitions.isNullOrEmpty()) {
            emit(cachedCompetitions)
        }

        val remoteCompetitions = sportApi.getCompetitions()

        cacheManager.saveToFile(FileCacheManager.COMPETITIONS_CACHE, remoteCompetitions)

        emit(remoteCompetitions)
    }

    override fun getMatches(): Flow<List<MatchDto>> = flow {
        val cachedMatches = cacheManager.readFromFile<List<MatchDto>>(
            FileCacheManager.MATCHES_CACHE,
            object : TypeToken<List<MatchDto>>() {}.type
        )

        if (!cachedMatches.isNullOrEmpty()) {
            emit(cachedMatches)
        }

        val remoteMatches = sportApi.getMatches()

        cacheManager.saveToFile(FileCacheManager.MATCHES_CACHE, remoteMatches)

        emit(remoteMatches)
    }
}

