package com.spendsync.app.domain.repository

interface GoogleSheetsRepository {
    suspend fun syncUnsyncedExpenses()
}
