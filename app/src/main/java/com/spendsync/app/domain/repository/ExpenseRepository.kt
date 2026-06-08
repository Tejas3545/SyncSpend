package com.spendsync.app.domain.repository

import com.spendsync.app.domain.model.Expense
import com.spendsync.app.domain.model.SpendingSummary
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense)
    suspend fun deleteExpense(id: String)
    suspend fun getUnsyncedExpenses(): List<Expense>
    suspend fun markAsSynced(id: String, notionPageId: String)
    fun getSuggestions(prefix: String): Flow<List<Expense>>
    fun getSpendingSummary(): Flow<SpendingSummary>
}
