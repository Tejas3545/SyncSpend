package com.spendsync.app.data.repository

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

    private suspend fun getActiveDatabaseId(): String? = authDataStore.notionDatabaseId.first()

    override suspend fun syncUnsyncedExpenses() {
        val token = authDataStore.notionToken.first()
        if (token.isNullOrEmpty()) return

        val databaseId = getActiveDatabaseId()
        if (databaseId.isNullOrEmpty()) return
        
        val unsyncedExpenses = expenseRepository.getPendingNotionExpenses()
        
        unsyncedExpenses.forEach { expense ->
            val request = expense.toNotionCreateRequest(databaseId)
            val response = notionApiService.createPage(request)
            if (!response.isSuccessful) {
                val message = "Notion ${response.code()}: ${response.errorBody()?.string().orEmpty().take(120)}"
                expenseRepository.recordSyncError(expense.id, message)
                throw HttpException(response)
            }
            val pageId = response.body()?.id
                ?: throw IOException("Notion returned an empty page response")
            expenseRepository.markNotionSynced(expense.id, pageId)
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

    override suspend fun initializeWorkspaceDatabase(): Result<String> {
        val token = authDataStore.notionToken.first()
        if (token.isNullOrBlank()) return Result.failure(IOException("Connect Notion before creating a database."))

        val existingDatabaseId = getActiveDatabaseId()
        if (!existingDatabaseId.isNullOrBlank() && testConnection()) {
            return Result.success(existingDatabaseId)
        }

        return try {
            val searchResponse = notionApiService.search(
                mapOf(
                    "filter" to mapOf("property" to "object", "value" to "page"),
                    "page_size" to 1
                )
            )
            if (!searchResponse.isSuccessful) {
                return Result.failure(HttpException(searchResponse))
            }
            val parentPageId = searchResponse.body()?.results?.firstOrNull()?.id
                ?: return Result.failure(IOException("No shared Notion page found. Share one empty page with SyncSpend during Notion login."))

            val createResponse = notionApiService.createDatabase(buildExpenseDatabaseRequest(parentPageId))
            if (!createResponse.isSuccessful) {
                return Result.failure(HttpException(createResponse))
            }
            val databaseId = createResponse.body()?.id
                ?: return Result.failure(IOException("Notion returned an empty database response."))
            authDataStore.saveNotionAuth(token, databaseId, null)
            Result.success(databaseId)
        } catch (e: Exception) {
            Log.e("NotionRepository", "Failed to initialize Notion workspace", e)
            Result.failure(e)
        }
    }

    private fun buildExpenseDatabaseRequest(parentPageId: String): Map<String, Any> {
        val title = listOf(mapOf("type" to "text", "text" to mapOf("content" to "SyncSpend Expenses")))
        return mapOf(
            "parent" to mapOf("type" to "page_id", "page_id" to parentPageId),
            "title" to title,
            "properties" to mapOf(
                "Name" to mapOf("title" to emptyMap<String, Any>()),
                "Amount" to mapOf("number" to mapOf("format" to "number")),
                "Category" to mapOf("select" to mapOf("options" to emptyList<Any>())),
                "Payment" to mapOf("select" to mapOf("options" to emptyList<Any>())),
                "Date" to mapOf("date" to emptyMap<String, Any>())
            )
        )
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
