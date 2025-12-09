package com.example.smart.sportlive.di

import com.example.smart.sportlive.data.repository.SportRepositoryImpl
import com.example.smart.sportlive.domain.repository.SportRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSportRepository(
        sportRepositoryImpl: SportRepositoryImpl
    ): SportRepository
}
