package com.example.smart.sportlive.domain.util

sealed class Result<T> {
    data class Success<T>(val data: T, val source: Source? = null) : Result<T>()
    data class Error<T>(val message: String) : Result<T>()
}

enum class Source {
    CACHE,
    NETWORK
}
