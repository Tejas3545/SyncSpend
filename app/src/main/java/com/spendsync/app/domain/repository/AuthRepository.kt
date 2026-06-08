package com.spendsync.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>
    val notionToken: Flow<String?>
    val notionDatabaseId: Flow<String?>
    
    suspend fun loginWithNotion(token: String): Result<Unit>
    suspend fun initializeGoogleSheetIfNeeded(account: android.accounts.Account?): Result<Unit>
    suspend fun logout()
}
