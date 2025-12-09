package com.example.smart.sportlive.data.repository

import com.example.smart.sportlive.data.local.cache.FileCacheManager
import com.example.smart.sportlive.data.mapper.toCompetition
import com.example.smart.sportlive.data.mapper.toMatch
import com.example.smart.sportlive.data.mapper.toSport
import com.example.smart.sportlive.data.model.CompetitionDto
import com.example.smart.sportlive.data.model.MatchDto
import com.example.smart.sportlive.data.model.SportDto
import com.example.smart.sportlive.data.remote.call.ApiResponse
import com.example.smart.sportlive.data.remote.api.SportApi
import com.example.smart.sportlive.data.remote.call.safeApiCall
import com.example.smart.sportlive.domain.model.Competition
import com.example.smart.sportlive.domain.model.Match
import com.example.smart.sportlive.domain.model.Sport
import com.example.smart.sportlive.domain.repository.SportRepository
import com.example.smart.sportlive.domain.util.Result
import com.example.smart.sportlive.domain.util.Source
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository implementation using cache-first-then-network pattern.
 *
 * Flow:
 * 1. Emit cached data (source = null) if exists - initial data, API pending
 * 2. Fetch from API
 * 3. If API succeeds: save to cache, emit fresh data (source = NETWORK)
 * 4. If API fails + cache exists: emit cached data (source = CACHE) - stale
 * 5. If API fails + no cache: emit Error
 */
class SportRepositoryImpl @Inject constructor(
    private val sportApi: SportApi,
    private val cacheManager: FileCacheManager
) : SportRepository {

    override fun getSports(): Flow<Result<List<Sport>>> = flow {
        val cachedSports = cacheManager.readFromFile<List<SportDto>>(
            FileCacheManager.SPORTS_CACHE,
            object : TypeToken<List<SportDto>>() {}.type
        )

        if (!cachedSports.isNullOrEmpty()) {
            emit(Result.Success(cachedSports.map { it.toSport() }))
        }

        when (val response = safeApiCall { sportApi.getSports() }) {
            is ApiResponse.Success -> {
                cacheManager.saveToFile(FileCacheManager.SPORTS_CACHE, response.data)
                emit(Result.Success(response.data.map { it.toSport() }, Source.NETWORK))
            }
            is ApiResponse.Error -> {
                if (cachedSports.isNullOrEmpty()) {
                    emit(Result.Error(response.message))
                } else {
                    emit(Result.Success(cachedSports.map { it.toSport() }, Source.CACHE))
                }
            }
        }
    }

    override fun getCompetitions(): Flow<Result<List<Competition>>> = flow {
        val cachedCompetitions = cacheManager.readFromFile<List<CompetitionDto>>(
            FileCacheManager.COMPETITIONS_CACHE,
            object : TypeToken<List<CompetitionDto>>() {}.type
        )

        if (!cachedCompetitions.isNullOrEmpty()) {
            emit(Result.Success(cachedCompetitions.map { it.toCompetition() }))
        }

        when (val response = safeApiCall { sportApi.getCompetitions() }) {
            is ApiResponse.Success -> {
                cacheManager.saveToFile(FileCacheManager.COMPETITIONS_CACHE, response.data)
                emit(Result.Success(response.data.map { it.toCompetition() }, Source.NETWORK))
            }
            is ApiResponse.Error -> {
                if (cachedCompetitions.isNullOrEmpty()) {
                    emit(Result.Error(response.message))
                } else {
                    emit(Result.Success(cachedCompetitions.map { it.toCompetition() }, Source.CACHE))
                }
            }
        }
    }

    override fun getMatches(): Flow<Result<List<Match>>> = flow {
        val cachedMatches = cacheManager.readFromFile<List<MatchDto>>(
            FileCacheManager.MATCHES_CACHE,
            object : TypeToken<List<MatchDto>>() {}.type
        )

        if (!cachedMatches.isNullOrEmpty()) {
            emit(Result.Success(cachedMatches.map { it.toMatch() }))
        }

        when (val response = safeApiCall { sportApi.getMatches() }) {
            is ApiResponse.Success -> {
                cacheManager.saveToFile(FileCacheManager.MATCHES_CACHE, response.data)
                emit(Result.Success(response.data.map { it.toMatch() }, Source.NETWORK))
            }
            is ApiResponse.Error -> {
                if (cachedMatches.isNullOrEmpty()) {
                    emit(Result.Error(response.message))
                } else {
                    emit(Result.Success(cachedMatches.map { it.toMatch() }, Source.CACHE))
                }
            }
        }
    }
}
