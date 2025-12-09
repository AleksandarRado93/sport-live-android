package com.example.smart.sportlive.domain.usecase

import com.example.smart.sportlive.domain.model.Sport
import com.example.smart.sportlive.domain.repository.SportRepository
import com.example.smart.sportlive.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSportsUseCase @Inject constructor(
    private val repository: SportRepository
) {
    operator fun invoke(): Flow<Result<List<Sport>>> {
        return repository.getSports()
    }
}

