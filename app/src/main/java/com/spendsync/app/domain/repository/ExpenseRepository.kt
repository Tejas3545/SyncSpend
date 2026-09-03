package com.spendsync.app.domain.repository

import com.spendsync.app.domain.model.Expense
import com.spendsync.app.domain.model.SpendingSummary
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense)
    suspend fun deleteExpense(id: String)
    suspend fun getPendingNotionExpenses(): List<Expense>
    suspend fun getPendingGoogleExpenses(): List<Expense>
    suspend fun markNotionSynced(id: String, pageId: String)
    suspend fun markGoogleSynced(id: String)
    suspend fun recordSyncError(id: String, message: String)
    fun getSuggestions(prefix: String): Flow<List<Expense>>
    fun getSpendingSummary(): Flow<SpendingSummary>
}
