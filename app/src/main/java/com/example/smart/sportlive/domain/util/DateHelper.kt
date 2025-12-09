package com.example.smart.sportlive.domain.util

import com.example.smart.sportlive.domain.model.DateCategory
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateHelper {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun getDateCategory(dateString: String): DateCategory? {
        return try {
            val matchDate = LocalDateTime.parse(dateString, formatter).toLocalDate()
            val today = LocalDate.now()

            when {
                matchDate.isBefore(today) -> null // Past matches have no category
                matchDate == today -> DateCategory.TODAY
                matchDate == today.plusDays(1) -> DateCategory.TOMORROW
                isUpcomingWeekend(matchDate, today) -> DateCategory.WEEKEND
                matchDate.isBefore(today.plusDays(8)) -> DateCategory.NEXT_WEEK
                else -> DateCategory.NEXT_WEEK // Fallback for dates further out
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun isUpcomingWeekend(matchDate: LocalDate, today: LocalDate): Boolean {
        val dayOfWeek = matchDate.dayOfWeek
        val isWeekendDay = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
        
        // Check if it's a weekend day within the next 7 days
        return isWeekendDay && !matchDate.isBefore(today) && matchDate.isBefore(today.plusDays(8))
    }
}

