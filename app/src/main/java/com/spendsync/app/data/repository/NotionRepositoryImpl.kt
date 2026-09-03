package com.spendsync.app.data.repository

import android.util.Log
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.data.remote.notion.NotionApiService
import com.spendsync.app.data.remote.notion.models.*
import com.spendsync.app.domain.model.Expense
import com.spendsync.app.domain.repository.ExpenseRepository
import com.spendsync.app.domain.repository.NotionRepository
import com.spendsync.app.util.NotionUtils
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class NotionRepositoryImpl @Inject constructor(
    private val notionApiService: NotionApiService,
    private val expenseRepository: ExpenseRepository,
    private val authDataStore: AuthDataStore
) : NotionRepository {

    private suspend fun getActiveDatabaseId(): String? {
        val rawId = authDataStore.notionDatabaseId.first()
        return if (!rawId.isNullOrBlank()) NotionUtils.extractDatabaseId(rawId) else null
    }

    override suspend fun syncUnsyncedExpenses() {
        val token = authDataStore.notionToken.first()
        if (token.isNullOrEmpty()) return

        var databaseId = getActiveDatabaseId()
        if (databaseId.isNullOrEmpty()) {
            val initResult = initializeWorkspaceDatabase()
            if (initResult.isSuccess) {
                databaseId = initResult.getOrNull()
            }
        }
        if (databaseId.isNullOrEmpty()) return

        val cleanDbId = NotionUtils.extractDatabaseId(databaseId)

        // Ensure Notion database has Amount, Category, Payment, and Date columns
        ensureDatabaseColumns(cleanDbId)

        val unsyncedExpenses = expenseRepository.getPendingNotionExpenses()

        unsyncedExpenses.forEach { expense ->
            val request = expense.toNotionCreateRequest(cleanDbId)
            var response = notionApiService.createPage(request)
            
            // If Notion rejected because the primary title column is named "Title" instead of "Name"
            if (!response.isSuccessful) {
                val errorMsg = response.errorBody()?.string().orEmpty()
                Log.w("NotionRepository", "Notion createPage initial attempt failed: $errorMsg")
                
                if (errorMsg.contains("Name is not a property that exists", ignoreCase = true) ||
                    errorMsg.contains("property_not_found", ignoreCase = true) ||
                    errorMsg.contains("Title", ignoreCase = true)
                ) {
                    val fallbackRequest = NotionCreatePageRequest(
                        parent = NotionParent(database_id = cleanDbId),
                        properties = mapOf(
                            "Title" to NotionPropertyValue(
                                title = listOf(NotionRichText(text = NotionText(content = expense.name)))
                            ),
                            "Amount" to NotionPropertyValue(number = expense.amount),
                            "Category" to NotionPropertyValue(select = NotionSelectOption(name = expense.category.name)),
                            "Payment" to NotionPropertyValue(select = NotionSelectOption(name = expense.paymentMethod?.name ?: "None")),
                            "Date" to NotionPropertyValue(date = NotionDate(start = expense.date.toString()))
                        )
                    )
                    response = notionApiService.createPage(fallbackRequest)
                }

                if (!response.isSuccessful) {
                    val finalError = "Notion ${response.code()}: ${errorMsg.take(120)}"
                    expenseRepository.recordSyncError(expense.id, finalError)
                    throw HttpException(response)
                }
            }

            val pageId = response.body()?.id
                ?: throw IOException("Notion returned an empty page response")
            expenseRepository.markNotionSynced(expense.id, pageId)
            Log.d("NotionRepository", "Successfully synced expense ${expense.name} to Notion page $pageId")
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
        val cleanDbId = NotionUtils.extractDatabaseId(databaseId)

        return try {
            val response = notionApiService.getDatabase(cleanDbId)
            if (response.isSuccessful) {
                ensureDatabaseColumns(cleanDbId)
                true
            } else {
                false
            }
        } catch (e: Exception) {
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
            // 1. Check if user already shared an existing database (e.g., "my expenses")
            val dbSearch = notionApiService.search(
                mapOf(
                    "filter" to mapOf("property" to "object", "value" to "database"),
                    "page_size" to 5
                )
            )
            val sharedDbId = if (dbSearch.isSuccessful) {
                dbSearch.body()?.results?.firstOrNull()?.id
            } else null

            if (!sharedDbId.isNullOrBlank()) {
                val cleanDbId = NotionUtils.extractDatabaseId(sharedDbId)
                ensureDatabaseColumns(cleanDbId)
                authDataStore.saveNotionAuth(token, cleanDbId, null)
                return Result.success(cleanDbId)
            }

            // 2. Look for any shared page to create the 'SyncSpend Expenses' database
            val pageSearch = notionApiService.search(
                mapOf(
                    "filter" to mapOf("property" to "object", "value" to "page"),
                    "page_size" to 5
                )
            )
            if (!pageSearch.isSuccessful) {
                val errorBody = pageSearch.errorBody()?.string() ?: "Status ${pageSearch.code()}"
                return Result.failure(IOException("Notion search failed: $errorBody"))
            }
            val parentPageId = pageSearch.body()?.results?.firstOrNull()?.id
                ?: return Result.failure(IOException("No shared Notion page or database found. In Notion, open your expenses page/database, click '...' at the top right -> 'Connections' -> select 'SyncSpend', then retry!"))

            val createResponse = notionApiService.createDatabase(buildExpenseDatabaseRequest(parentPageId))
            if (!createResponse.isSuccessful) {
                val errorMsg = createResponse.errorBody()?.string() ?: "Status ${createResponse.code()}"
                return Result.failure(IOException("Failed to create Notion database: $errorMsg"))
            }
            val rawId = createResponse.body()?.id
                ?: return Result.failure(IOException("Notion returned an empty database response."))
            val databaseId = NotionUtils.extractDatabaseId(rawId)
            authDataStore.saveNotionAuth(token, databaseId, null)
            Result.success(databaseId)
        } catch (e: Exception) {
            Log.e("NotionRepository", "Failed to initialize Notion workspace", e)
            Result.failure(e)
        }
    }

    private suspend fun ensureDatabaseColumns(databaseId: String) {
        try {
            val patchBody = mapOf(
                "properties" to mapOf(
                    "Amount" to mapOf("number" to mapOf("format" to "number")),
                    "Category" to mapOf("select" to mapOf("options" to emptyList<Any>())),
                    "Payment" to mapOf("select" to mapOf("options" to emptyList<Any>())),
                    "Date" to mapOf("date" to emptyMap<String, Any>())
                )
            )
            val response = notionApiService.updateDatabase(databaseId, patchBody)
            if (response.isSuccessful) {
                Log.d("NotionRepository", "Database columns verified and updated on Notion database $databaseId")
            } else {
                Log.w("NotionRepository", "Schema update returned ${response.code()}: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.w("NotionRepository", "Failed to patch database columns", e)
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

    override suspend fun exchangeOAuthCode(code: String): Result<String> {
        return try {
            val credentials = "${com.spendsync.app.AppConfig.NOTION_OAUTH_CLIENT_ID}:${com.spendsync.app.AppConfig.NOTION_OAUTH_CLIENT_SECRET}"
            val basicAuth = "Basic " + android.util.Base64.encodeToString(credentials.toByteArray(), android.util.Base64.NO_WRAP)

            val body = mapOf(
                "grant_type" to "authorization_code",
                "code" to code,
                "redirect_uri" to com.spendsync.app.AppConfig.NOTION_REDIRECT_URI
            )

            val response = notionApiService.exchangeToken(basicAuth, body)
            if (response.isSuccessful) {
                val token = response.body()?.get("access_token") as? String
                if (token.isNullOrBlank()) {
                    Result.failure(IOException("Notion token response was missing access_token"))
                } else {
                    Result.success(token)
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Status ${response.code()}"
                Result.failure(IOException("OAuth token exchange failed: $errorMsg"))
            }
        } catch (e: Exception) {
            Log.e("NotionRepository", "Failed to exchange OAuth code", e)
            Result.failure(e)
        }
    }
}
