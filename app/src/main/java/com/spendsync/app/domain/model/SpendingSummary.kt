package com.spendsync.app.domain.model

import java.time.LocalDate

data class SpendingSummary(
    val totalThisWeek: Double,
    val totalThisMonth: Double,
    val totalThisYear: Double,
    val dailyBreakdown: Map<LocalDate, Double>
)
