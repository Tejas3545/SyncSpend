package com.spendsync.app.presentation.screens.auth

import android.accounts.Account
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.spendsync.app.AppConfig
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.domain.repository.AuthRepository
import com.spendsync.app.domain.repository.NotionRepository
import com.spendsync.app.worker.GoogleSyncWorker
import com.spendsync.app.worker.NotionSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URLEncoder
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val authRepository: AuthRepository,
    private val notionRepository: NotionRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                authDataStore.userEmail,
                authDataStore.googleSheetId,
                authDataStore.notionToken,
                authDataStore.notionDatabaseId
            ) { email, sheetId, token, databaseId ->
                _uiState.value.copy(
                    googleConnected = !email.isNullOrBlank() && !sheetId.isNullOrBlank(),
                    googleEmail = email.orEmpty(),
                    notionConnected = !token.isNullOrBlank() && !databaseId.isNullOrBlank()
                )
            }.collect { _uiState.value = it }
        }
    }

    fun connectNotion(token: String, databaseId: String) {
        if (token.isBlank()) {
            _uiState.value = _uiState.value.copy(message = "Please enter your Notion Integration Secret.")
            return
        }
        val cleanToken = token.trim()
        val cleanDb = com.spendsync.app.util.NotionUtils.extractDatabaseId(databaseId)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWorking = true, message = null)
            authDataStore.saveNotionAuth(cleanToken, cleanDb, null)
            val result = if (cleanDb.isNotBlank()) {
                val valid = notionRepository.testConnection()
                if (valid) Result.success(cleanDb)
                else Result.failure(Exception("Could not access that database. Make sure you shared it with the SyncSpend integration in Notion!"))
            } else {
                notionRepository.initializeWorkspaceDatabase()
            }
            result.onSuccess {
                enqueue(NotionSyncWorker.WORK_NAME, NotionSyncWorker.buildOneTimeRequest())
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    notionConnected = true,
                    message = "Notion connected! SyncSpend created your private expenses database."
                )
            }.onFailure { err ->
                authDataStore.clearNotionAuth()
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    notionConnected = false,
                    message = err.message ?: "Failed to connect to Notion."
                )
            }
        }
    }

    fun buildNotionAuthUri(): Uri? {
        if (AppConfig.NOTION_OAUTH_CLIENT_ID.isBlank()) {
            return null
        }
        val redirect = URLEncoder.encode(AppConfig.NOTION_REDIRECT_URI, "UTF-8")
        return Uri.parse(
            "https://api.notion.com/v1/oauth/authorize" +
                "?client_id=${AppConfig.NOTION_OAUTH_CLIENT_ID}" +
                "&response_type=code" +
                "&owner=user" +
                "&redirect_uri=$redirect"
        )
    }

    fun handleNotionRedirect(uri: Uri?) {
        if (uri == null || uri.scheme != "https" || uri.host != "syncspend" || uri.path != "/oauth") return
        val error = uri.getQueryParameter("error")
        if (!error.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(message = "Notion login cancelled: $error")
            return
        }

        val code = uri.getQueryParameter("code")
        if (code.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(message = "Notion did not return an authorization code.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWorking = true, message = null)
            
            // Step 1: Exchange code for access token
            val tokenResult = notionRepository.exchangeOAuthCode(code)
            tokenResult.onSuccess { token ->
                authDataStore.saveNotionAuth(token, "", null) // temporary save token without db

                // Step 2: Create/Init Database automatically
                val initResult = notionRepository.initializeWorkspaceDatabase()
                initResult.onSuccess {
                    enqueue(NotionSyncWorker.WORK_NAME, NotionSyncWorker.buildOneTimeRequest())
                    _uiState.value = _uiState.value.copy(
                        isWorking = false,
                        notionConnected = true,
                        message = "Notion connected! SyncSpend created your private expenses database."
                    )
                }.onFailure { err ->
                    authDataStore.clearNotionAuth()
                    _uiState.value = _uiState.value.copy(
                        isWorking = false,
                        notionConnected = false,
                        message = err.message ?: "Failed to set up Notion database."
                    )
                }
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    notionConnected = false,
                    message = err.message ?: "Failed to exchange Notion token."
                )
            }
        }
    }

    fun connectGoogle(account: Account, name: String?, email: String?, photoUrl: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWorking = true, message = null)
            authDataStore.saveGoogleInfo(name, email, photoUrl)
            authRepository.initializeGoogleSheetIfNeeded(account)
                .onSuccess {
                    enqueue(GoogleSyncWorker.WORK_NAME, GoogleSyncWorker.buildOneTimeRequest())
                    _uiState.value = _uiState.value.copy(
                        isWorking = false,
                        googleConnected = true,
                        message = "Google Sheets connected. Your session will stay active until logout."
                    )
                }
                .onFailure {
                    authDataStore.clearGoogleAuth()
                    _uiState.value = _uiState.value.copy(
                        isWorking = false,
                        googleConnected = false,
                        message = it.message ?: "Google login failed."
                    )
                }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun setErrorMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }

    private fun enqueue(name: String, request: androidx.work.OneTimeWorkRequest) {
        workManager.enqueueUniqueWork("${name}_login", ExistingWorkPolicy.REPLACE, request)
    }
}

data class AuthUiState(
    val isWorking: Boolean = false,
    val googleConnected: Boolean = false,
    val googleEmail: String = "",
    val notionConnected: Boolean = false,
    val message: String? = null
)
