package com.spendsync.app.domain.repository

import com.spendsync.app.domain.model.Expense

interface NotionRepository {
    suspend fun syncUnsyncedExpenses()
    suspend fun deleteExpense(notionPageId: String): Boolean
    suspend fun testConnection(): Boolean
    suspend fun initializeWorkspaceDatabase(): Result<String>
}
