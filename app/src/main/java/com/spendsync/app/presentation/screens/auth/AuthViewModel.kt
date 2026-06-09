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

    fun buildNotionAuthUri(): Uri? {
        if (AppConfig.NOTION_OAUTH_CLIENT_ID.isBlank()) {
            _uiState.value = _uiState.value.copy(
                message = "Add your Notion OAuth client id in AppConfig before connecting Notion."
            )
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
        if (uri == null || uri.scheme != "syncspend" || uri.host != "notion-auth") return
        val error = uri.getQueryParameter("error")
        if (!error.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(message = "Notion login cancelled: $error")
            return
        }

        val accessToken = uri.getQueryParameter("access_token") ?: uri.getQueryParameter("token")
        val databaseId = uri.getQueryParameter("database_id")
        val code = uri.getQueryParameter("code")

        if (accessToken.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                message = if (code.isNullOrBlank()) {
                    "Notion did not return an access token."
                } else {
                    "Notion approved access. Route the OAuth code through your secure token-exchange backend, then redirect here with access_token."
                }
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWorking = true, message = null)
            authDataStore.saveNotionAuth(accessToken, databaseId.orEmpty(), null)
            val result = if (databaseId.isNullOrBlank()) {
                notionRepository.initializeWorkspaceDatabase()
            } else {
                Result.success(databaseId)
            }
            result.onSuccess {
                enqueue(NotionSyncWorker.WORK_NAME, NotionSyncWorker.buildOneTimeRequest())
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    message = "Notion connected. SyncSpend created your private expenses database."
                )
            }.onFailure {
                authDataStore.clearNotionAuth()
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    message = it.message ?: "Notion setup failed."
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
                        message = "Google Sheets connected. Your session will stay active until logout."
                    )
                }
                .onFailure {
                    authDataStore.clearGoogleAuth()
                    _uiState.value = _uiState.value.copy(
                        isWorking = false,
                        message = it.message ?: "Google login failed."
                    )
                }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
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
