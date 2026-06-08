package com.spendsync.app.data.remote.notion

import com.spendsync.app.AppConfig
import com.spendsync.app.data.local.datastore.AuthDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class NotionInterceptor @Inject constructor(
    private val authDataStore: AuthDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Get token from DataStore (OAuth)
        val token = runBlocking(Dispatchers.IO) { authDataStore.notionToken.first() }
        
        val requestBuilder = chain.request().newBuilder()
            .addHeader("Notion-Version", "2022-06-28")
            .addHeader("Content-Type", "application/json")
            
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        return chain.proceed(requestBuilder.build())
    }
}
