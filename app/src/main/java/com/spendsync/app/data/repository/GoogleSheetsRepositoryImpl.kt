package com.spendsync.app.data.repository

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.domain.repository.ExpenseRepository
import com.spendsync.app.domain.repository.GoogleSheetsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSheetsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authDataStore: AuthDataStore,
    private val expenseRepository: ExpenseRepository
) : GoogleSheetsRepository {

    override suspend fun syncUnsyncedExpenses() {
        val sheetId = authDataStore.googleSheetId.first()
        val email = authDataStore.userEmail.first()
        
        if (sheetId.isNullOrEmpty() || email.isNullOrEmpty()) {
            val notionToken = authDataStore.notionToken.first()
            if (notionToken.isNullOrEmpty()) {
                val unsyncedExpenses = expenseRepository.getUnsyncedExpenses()
                unsyncedExpenses.forEach {
                    expenseRepository.markAsSynced(it.id, "error: not_initialized")
                }
            }
            return
        }

        val unsyncedExpenses = expenseRepository.getUnsyncedExpenses()
        if (unsyncedExpenses.isEmpty()) return

        try {
            val credential = GoogleAccountCredential.usingOAuth2(
                context, 
                listOf("https://www.googleapis.com/auth/drive.file", "https://www.googleapis.com/auth/spreadsheets")
            )
            credential.selectedAccountName = email

            val transport = NetHttpTransport()
            val jsonFactory = GsonFactory.getDefaultInstance()

            val sheetsService = Sheets.Builder(
                transport, jsonFactory, credential
            ).setApplicationName("SyncSpend").build()

            val spreadsheet = sheetsService.spreadsheets().get(sheetId).execute()
            val sheetName = spreadsheet.sheets.first().properties.title

            // We append all unsynced expenses in one batch to save API calls
            val values = unsyncedExpenses.map { expense ->
                listOf(
                    expense.id,
                    expense.name,
                    expense.amount,
                    expense.category.name,
                    expense.paymentMethod?.name ?: "None",
                    expense.date.toString(),
                    "" // Time placeholder if any
                )
            }

            val body = ValueRange().setValues(values as List<List<Any>>?)
            
            val response = sheetsService.spreadsheets().values()
                .append(sheetId, "'$sheetName'!A:G", body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .execute()

            if (response != null) {
                // If successful, mark as synced.
                val range = response.updates?.updatedRange ?: "google_sheet_synced"
                unsyncedExpenses.forEach {
                    expenseRepository.markAsSynced(it.id, range)
                }
            }

        } catch (e: Exception) {
            Log.e("GoogleSheetsRepository", "Error syncing to Google Sheets", e)
            val msg = e.message?.take(30) ?: "unknown"
            unsyncedExpenses.forEach {
                expenseRepository.markAsSynced(it.id, "error: $msg")
            }
        }
    }
}
