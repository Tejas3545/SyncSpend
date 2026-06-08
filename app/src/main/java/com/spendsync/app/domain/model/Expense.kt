package com.spendsync.app.domain.model

import java.time.LocalDate

data class Expense(
    val id: String,
    val name: String,
    val amount: Double,
    val category: Category,
    val paymentMethod: PaymentMethod?,
    val date: LocalDate,
    val isSynced: Boolean,
    val notionPageId: String?
)
