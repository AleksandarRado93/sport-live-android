package com.example.smart.sportlive.domain.util

sealed class Result<T> {
    data class Success<T>(val data: T, val source: Source? = null) : Result<T>()
    data class Error<T>(val message: String) : Result<T>()
}

enum class Source {
    CACHE,
    NETWORK
}

fun <T> Result<List<T>>.dataOrEmpty(): List<T> =
    if (this is Result.Success) data else emptyList()

fun <T> Result<T>.dataOrDefault(default: T): T =
    if (this is Result.Success) data else default

fun isFromCache(vararg results: Result<*>): Boolean =
    results.any { it is Result.Success && it.source == Source.CACHE }
