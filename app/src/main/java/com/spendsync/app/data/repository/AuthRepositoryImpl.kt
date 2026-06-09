package com.spendsync.app.data.repository

import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.domain.repository.AuthRepository
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authDataStore: AuthDataStore
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = authDataStore.isLoggedIn
    
    override val notionToken: Flow<String?> = authDataStore.notionToken.map { 
        it.takeIf { t -> !t.isNullOrEmpty() }
    }
    
    override val notionDatabaseId: Flow<String?> = authDataStore.notionDatabaseId.map { 
        it.takeIf { d -> !d.isNullOrEmpty() }
    }


    override suspend fun initializeGoogleSheetIfNeeded(account: android.accounts.Account?): Result<Unit> {
        if (account == null) return Result.failure(Exception("No Google account provided"))
        
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val existingId = authDataStore.googleSheetId.first()
                if (!existingId.isNullOrEmpty()) {
                    return@withContext Result.success(Unit)
                }

                val credential = com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential.usingOAuth2(
                    context, 
                    listOf("https://www.googleapis.com/auth/drive.file", "https://www.googleapis.com/auth/spreadsheets")
                )
                credential.selectedAccount = account

                val transport = com.google.api.client.http.javanet.NetHttpTransport()
                val jsonFactory = com.google.api.client.json.gson.GsonFactory.getDefaultInstance()

                val sheetsService = com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential
                ).setApplicationName("SyncSpend").build()

                val spreadsheet = com.google.api.services.sheets.v4.model.Spreadsheet()
                    .setProperties(com.google.api.services.sheets.v4.model.SpreadsheetProperties().setTitle("SyncSpend Expenses"))
                
                val createdSpreadsheet = sheetsService.spreadsheets().create(spreadsheet).execute()
                val spreadsheetId = createdSpreadsheet.spreadsheetId
                val sheetName = createdSpreadsheet.sheets.first().properties.title

                val values = listOf(
                    listOf("ID", "Name", "Amount", "Category", "Payment Method", "Date", "Time")
                )
                val body = com.google.api.services.sheets.v4.model.ValueRange().setValues(values as List<List<Any>>?)
                
                sheetsService.spreadsheets().values()
                    .update(spreadsheetId, "'$sheetName'!A1:G1", body)
                    .setValueInputOption("RAW")
                    .execute()

                authDataStore.saveGoogleSheetId(spreadsheetId)
                
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("AuthRepository", "Failed to initialize Google Sheet", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun logout() {
        authDataStore.clearAuth()
        authDataStore.setLoggedIn(false)
    }
}
