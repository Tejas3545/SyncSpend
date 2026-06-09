package com.spendsync.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class AuthDataStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.authDataStore

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val NOTION_TOKEN = stringPreferencesKey("notion_token")
        private val NOTION_DATABASE_ID = stringPreferencesKey("notion_database_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_DP = stringPreferencesKey("user_dp")
        private val GOOGLE_SHEET_ID = stringPreferencesKey("google_sheet_id")
    }

    val notionToken: Flow<String?> = dataStore.data.map { it[NOTION_TOKEN] }
    val notionDatabaseId: Flow<String?> = dataStore.data.map { it[NOTION_DATABASE_ID] }
    val userName: Flow<String?> = dataStore.data.map { it[USER_NAME] }
    val userEmail: Flow<String?> = dataStore.data.map { it[USER_EMAIL] }
    val userDp: Flow<String?> = dataStore.data.map { it[USER_DP] }
    val googleSheetId: Flow<String?> = dataStore.data.map { it[GOOGLE_SHEET_ID] }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { it[IS_LOGGED_IN] ?: (it[NOTION_TOKEN] != null) }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit {
            it[IS_LOGGED_IN] = loggedIn
        }
    }

    suspend fun saveNotionAuth(token: String, databaseId: String, name: String?) {
        dataStore.edit {
            it[NOTION_TOKEN] = token
            it[NOTION_DATABASE_ID] = databaseId
            val hasGoogle = !it[USER_EMAIL].isNullOrBlank() && !it[GOOGLE_SHEET_ID].isNullOrBlank()
            it[IS_LOGGED_IN] = hasGoogle || (token.isNotBlank() && databaseId.isNotBlank())
            if (name != null) it[USER_NAME] = name
        }
    }

    suspend fun saveGoogleInfo(name: String?, email: String?, dpUrl: String?) {
        dataStore.edit {
            if (email != null && it[USER_EMAIL] != null && it[USER_EMAIL] != email) {
                it.remove(GOOGLE_SHEET_ID)
            }
            if (name != null) it[USER_NAME] = name
            if (email != null) it[USER_EMAIL] = email
            if (dpUrl != null) it[USER_DP] = dpUrl
        }
    }

    suspend fun saveGoogleSheetId(sheetId: String) {
        dataStore.edit {
            it[GOOGLE_SHEET_ID] = sheetId
            it[IS_LOGGED_IN] = sheetId.isNotBlank()
        }
    }

    suspend fun clearNotionAuth() {
        dataStore.edit {
            it.remove(NOTION_TOKEN)
            it.remove(NOTION_DATABASE_ID)
            val hasGoogle = !it[USER_EMAIL].isNullOrBlank() && !it[GOOGLE_SHEET_ID].isNullOrBlank()
            it[IS_LOGGED_IN] = hasGoogle
        }
    }

    suspend fun clearGoogleAuth() {
        dataStore.edit {
            it.remove(USER_NAME)
            it.remove(USER_EMAIL)
            it.remove(USER_DP)
            it.remove(GOOGLE_SHEET_ID)
            val hasNotion = !it[NOTION_TOKEN].isNullOrBlank() && !it[NOTION_DATABASE_ID].isNullOrBlank()
            it[IS_LOGGED_IN] = hasNotion
        }
    }

    suspend fun clearAuth() {
        dataStore.edit {
            it.clear()
        }
    }
}
