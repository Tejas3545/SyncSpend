package com.spendsync.app.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("MMM dd")
    
    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }
    
    fun formatShortDate(date: LocalDate): String {
        return date.format(shortDateFormatter)
    }
    
    fun getDayName(date: LocalDate): String {
        return date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    /**
     * Converts any date string to a human-readable section header.
     * Input can be: "2026-06-06" (full date), "2026-06" (year-month), or day name
     */
    fun formatDateHeader(rawDate: String): String {
        return try {
            when {
                // Full date like "2026-06-06"
                rawDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> {
                    val date = LocalDate.parse(rawDate)
                    val today = LocalDate.now()
                    val yesterday = today.minusDays(1)
                    when (date) {
                        today     -> "Today"
                        yesterday -> "Yesterday"
                        else      -> date.format(
                            DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH)
                        ) // e.g. "Saturday, 6 June"
                    }
                }
                // Year-month like "2026-06"
                rawDate.matches(Regex("\\d{4}-\\d{2}")) -> {
                    val ym = YearMonth.parse(rawDate)
                    ym.format(
                        DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
                    ) // e.g. "June 2026"
                }
                else -> rawDate
            }
        } catch (e: Exception) {
            rawDate // fallback to raw string if parsing fails
        }
    }
}
