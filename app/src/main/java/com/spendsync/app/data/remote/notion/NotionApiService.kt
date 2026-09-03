package com.spendsync.app.data.remote.notion

import com.spendsync.app.data.remote.notion.models.NotionCreatePageRequest
import com.spendsync.app.data.remote.notion.models.NotionDatabase
import com.spendsync.app.data.remote.notion.models.NotionPage
import com.spendsync.app.data.remote.notion.models.NotionQueryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.http.Headers

interface NotionApiService {

    @POST("databases/{databaseId}/query")
    suspend fun queryDatabase(
        @Path("databaseId") databaseId: String,
        @Body filter: Map<String, Any>? = null
    ): Response<NotionQueryResponse>

    @POST("pages")
    suspend fun createPage(
        @Body request: NotionCreatePageRequest
    ): Response<NotionPage>

    @PATCH("pages/{pageId}")
    suspend fun updatePage(
        @Path("pageId") pageId: String,
        @Body properties: Map<String, Any>
    ): Response<NotionPage>

    @PATCH("pages/{pageId}")
    suspend fun archivePage(
        @Path("pageId") pageId: String,
        @Body body: Map<String, Boolean> = mapOf("archived" to true)
    ): Response<NotionPage>

    @GET("databases/{databaseId}")
    suspend fun getDatabase(
        @Path("databaseId") databaseId: String
    ): Response<NotionDatabase>

    @PATCH("databases/{databaseId}")
    suspend fun updateDatabase(
        @Path("databaseId") databaseId: String,
        @Body body: Map<String, Any>
    ): Response<NotionDatabase>

    @POST("databases")
    suspend fun createDatabase(
        @Body body: Map<String, Any>
    ): Response<NotionDatabase>

    @POST("search")
    suspend fun search(
        @Body body: Map<String, Any>
    ): Response<NotionQueryResponse>

    @POST("oauth/token")
    @Headers("Content-Type: application/json")
    suspend fun exchangeToken(
        @Header("Authorization") basicAuth: String,
        @Body body: Map<String, String>
    ): Response<Map<String, Any>>
}
