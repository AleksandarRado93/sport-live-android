package com.example.smart.sportlive.data.local.cache

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileCacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    companion object {
        const val SPORTS_CACHE = "sports.json"
        const val COMPETITIONS_CACHE = "competitions.json"
        const val MATCHES_CACHE = "matches.json"
    }

    fun <T> saveToFile(filename: String, data: T) {
        try {
            val file = File(context.filesDir, filename)
            val json = gson.toJson(data)
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun <T> readFromFile(filename: String, type: Type): T? {
        return try {
            val file = File(context.filesDir, filename)
            if (!file.exists()) return null

            val json = file.readText()
            if (json.isBlank()) return null

            gson.fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

