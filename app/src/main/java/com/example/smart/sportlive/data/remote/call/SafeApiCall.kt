package com.example.smart.sportlive.data.remote.call

import retrofit2.HttpException
import java.io.IOException

sealed class ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error<T>(val message: String) : ApiResponse<T>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResponse<T> {
    return try {
        ApiResponse.Success(apiCall())
    } catch (e: HttpException) {
        ApiResponse.Error("HTTP error: ${e.code()}")
    } catch (e: IOException) {
        ApiResponse.Error("Network error: ${e.message}")
    } catch (e: Exception) {
        ApiResponse.Error(e.message ?: "Unknown error")
    }
}

