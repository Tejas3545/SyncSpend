package com.spendsync.app.data.repository

import com.spendsync.app.AppConfig
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.data.remote.notion.models.*
import com.spendsync.app.data.remote.notion.NotionApiService
import com.spendsync.app.domain.model.Expense
import com.spendsync.app.domain.repository.ExpenseRepository
import com.spendsync.app.domain.repository.NotionRepository
import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NotionRepositoryImpl @Inject constructor(
    private val notionApiService: NotionApiService,
    private val expenseRepository: ExpenseRepository,
    private val authDataStore: AuthDataStore
) : NotionRepository {

    private suspend fun getActiveDatabaseId(): String? {
        val storeId = authDataStore.notionDatabaseId.first()
        return if (!storeId.isNullOrEmpty()) storeId else AppConfig.NOTION_DATABASE_ID
    }

    override suspend fun syncUnsyncedExpenses() {
        val token = authDataStore.notionToken.first()
        if (token.isNullOrEmpty()) return

        val databaseId = getActiveDatabaseId()
        if (databaseId.isNullOrEmpty()) return
        
        val unsyncedExpenses = expenseRepository.getUnsyncedExpenses()
        
        unsyncedExpenses.forEach { expense ->
            try {
                val request = expense.toNotionCreateRequest(databaseId)
                val response = notionApiService.createPage(request)
                
                if (response.isSuccessful || response.code() == 200) {
                    val body = response.body()
                    if (body != null) {
                        expenseRepository.markAsSynced(expense.id, body.id)
                    }
                } else {
                    Log.e("NotionRepository", "Notion sync failed for ${expense.name}: ${response.code()} ${response.errorBody()?.string()}")
                    expenseRepository.markAsSynced(expense.id, "error")
                }
            } catch (e: Exception) {
                Log.e("NotionRepository", "Error syncing", e)
                expenseRepository.markAsSynced(expense.id, "error")
            }
        }
    }

    override suspend fun deleteExpense(notionPageId: String): Boolean {
        if (notionPageId.isEmpty() || notionPageId == "error") return false
        return try {
            val response = notionApiService.archivePage(notionPageId)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("NotionRepository", "Error deleting expense", e)
            false
        }
    }

    override suspend fun testConnection(): Boolean {
        val databaseId = getActiveDatabaseId()
        if (databaseId.isNullOrEmpty()) return false
        
        return try {
            val response = notionApiService.getDatabase(databaseId)
            response.isSuccessful
        } catch (e: HttpException) {
            false
        } catch (e: IOException) {
            false
        }
    }

    private fun Expense.toNotionCreateRequest(databaseId: String): NotionCreatePageRequest {
        return NotionCreatePageRequest(
            parent = NotionParent(database_id = databaseId),
            properties = mapOf(
                "Name" to NotionPropertyValue(
                    title = listOf(NotionRichText(text = NotionText(content = name)))
                ),
                "Amount" to NotionPropertyValue(number = amount),
                "Category" to NotionPropertyValue(
                    select = NotionSelectOption(name = category.name)
                ),
                "Payment" to NotionPropertyValue(
                    select = NotionSelectOption(name = paymentMethod?.name ?: "None")
                ),
                "Date" to NotionPropertyValue(
                    date = NotionDate(start = date.toString())
                )
            )
        )
    }
}
