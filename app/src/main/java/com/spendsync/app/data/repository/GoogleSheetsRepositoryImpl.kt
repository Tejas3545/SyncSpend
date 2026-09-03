package com.spendsync.app.data.repository

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.domain.repository.ExpenseRepository
import com.spendsync.app.domain.repository.GoogleSheetsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSheetsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authDataStore: AuthDataStore,
    private val expenseRepository: ExpenseRepository
) : GoogleSheetsRepository {

    override suspend fun syncUnsyncedExpenses() = withContext(Dispatchers.IO) {
        val sheetId = authDataStore.googleSheetId.first() ?: return@withContext
        val email = authDataStore.userEmail.first() ?: return@withContext
        val pending = expenseRepository.getPendingGoogleExpenses()
        if (pending.isEmpty()) return@withContext

        val credential = GoogleAccountCredential.usingOAuth2(context, GOOGLE_SCOPES).apply {
            selectedAccountName = email
        }
        val service = Sheets.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
            .setApplicationName("SyncSpend")
            .build()
        try {
            val sheetName = service.spreadsheets().get(sheetId).execute().sheets.first().properties.title
            val existingIds = service.spreadsheets().values()
                .get(sheetId, "'$sheetName'!A2:A")
                .execute()
                .getValues()
                .orEmpty()
                .mapNotNull { row -> row.firstOrNull()?.toString() }
                .toSet()
            val newExpenses = pending.filterNot { it.id in existingIds }
            if (newExpenses.isNotEmpty()) {
                val values: List<List<Any>> = newExpenses.map { expense ->
                    listOf(
                        expense.id,
                        expense.name,
                        expense.amount,
                        expense.category.name,
                        expense.paymentMethod?.name ?: "None",
                        expense.date.toString(),
                        "SyncSpend"
                    )
                }
                service.spreadsheets().values()
                    .append(sheetId, "'$sheetName'!A:G", ValueRange().setValues(values))
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute()
            }
            pending.forEach { expenseRepository.markGoogleSynced(it.id) }
        } catch (error: Exception) {
            val message = "Google Sheets: ${error.message.orEmpty().take(120)}"
            pending.forEach { expenseRepository.recordSyncError(it.id, message) }
            if (error is IOException) throw error
            throw IOException(message, error)
        }
    }

    companion object {
        val GOOGLE_SCOPES = listOf(
            "https://www.googleapis.com/auth/drive.file",
            "https://www.googleapis.com/auth/spreadsheets"
        )
    }
}
