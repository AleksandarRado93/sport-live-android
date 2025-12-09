package com.example.smart.sportlive.domain.usecase

import com.example.smart.sportlive.domain.model.Competition
import com.example.smart.sportlive.domain.repository.SportRepository
import com.example.smart.sportlive.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompetitionsUseCase @Inject constructor(
    private val repository: SportRepository
) {
    operator fun invoke(): Flow<Result<List<Competition>>> {
        return repository.getCompetitions()
    }
}

