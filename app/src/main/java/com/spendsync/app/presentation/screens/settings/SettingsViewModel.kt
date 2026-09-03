package com.spendsync.app.presentation.screens.settings

import android.accounts.Account
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.spendsync.app.AppConfig
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.data.local.datastore.SettingsDataStore
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
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val authDataStore: AuthDataStore,
    private val authRepository: AuthRepository,
    private val notionRepository: NotionRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profileFlow = combine(authDataStore.userName, authDataStore.userDp) { name, dp -> name to dp }
            val authFlow = combine(
                authDataStore.notionToken,
                authDataStore.notionDatabaseId,
                authDataStore.userEmail,
                authDataStore.googleSheetId,
                profileFlow
            ) { notionToken, databaseId, email, sheetId, profile ->
                AuthSettingsSnapshot(notionToken, databaseId, email, sheetId, profile.first, profile.second)
            }
            combine(settingsDataStore.theme, authFlow) { theme, auth ->
                _uiState.value.copy(
                    theme = theme,
                    notionConnected = !auth.notionToken.isNullOrBlank() && !auth.databaseId.isNullOrBlank(),
                    notionDatabaseId = auth.databaseId.orEmpty(),
                    googleConnected = !auth.email.isNullOrBlank() && !auth.sheetId.isNullOrBlank(),
                    googleEmail = auth.email.orEmpty(),
                    googleSheetId = auth.sheetId.orEmpty(),
                    userName = auth.name.orEmpty(),
                    userDp = auth.dp.orEmpty()
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onThemeChanged(theme: String) = viewModelScope.launch { settingsDataStore.setTheme(theme) }

    fun buildNotionAuthUri(): Uri? {
        if (AppConfig.NOTION_OAUTH_CLIENT_ID.isBlank()) {
            _uiState.value = _uiState.value.copy(message = "Add your Notion OAuth client id in AppConfig before connecting Notion.")
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

    fun connectNotion(token: String, databaseId: String) {
        if (token.isBlank() || databaseId.isBlank()) {
            _uiState.value = _uiState.value.copy(message = "Enter both the Notion token and database ID.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWorking = true, message = null)
            authDataStore.saveNotionAuth(token.trim(), databaseId.trim(), null)
            val connected = notionRepository.testConnection()
            if (connected) {
                enqueue(NotionSyncWorker.WORK_NAME, NotionSyncWorker.buildOneTimeRequest())
                _uiState.value = _uiState.value.copy(isWorking = false, message = "Notion connected. Pending expenses will sync automatically.")
            } else {
                authDataStore.clearNotionAuth()
                _uiState.value = _uiState.value.copy(isWorking = false, message = "Could not access that Notion database. Check sharing, token, and ID.")
            }
        }
    }

    fun disconnectNotion() = viewModelScope.launch {
        authDataStore.clearNotionAuth()
        _uiState.value = _uiState.value.copy(message = "Notion disconnected. Expenses remain on this device.")
    }

    fun connectGoogle(account: Account, name: String?, email: String?, photoUrl: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWorking = true, message = null)
            authDataStore.saveGoogleInfo(name, email, photoUrl)
            authRepository.initializeGoogleSheetIfNeeded(account)
                .onSuccess {
                    enqueue(GoogleSyncWorker.WORK_NAME, GoogleSyncWorker.buildOneTimeRequest())
                    _uiState.value = _uiState.value.copy(isWorking = false, message = "Google Sheets connected. Offline entries will upload when online.")
                }
                .onFailure {
                    authDataStore.clearGoogleAuth()
                    _uiState.value = _uiState.value.copy(isWorking = false, message = it.message ?: "Google Sheets connection failed.")
                }
        }
    }

    fun disconnectGoogle() = viewModelScope.launch {
        authDataStore.clearGoogleAuth()
        _uiState.value = _uiState.value.copy(message = "Google Sheets disconnected. Existing sheet data is unchanged.")
    }

    fun syncNow() {
        enqueue(NotionSyncWorker.WORK_NAME, NotionSyncWorker.buildOneTimeRequest())
        enqueue(GoogleSyncWorker.WORK_NAME, GoogleSyncWorker.buildOneTimeRequest())
        _uiState.value = _uiState.value.copy(message = "Sync queued. It will run as soon as a network is available.")
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
        _uiState.value = _uiState.value.copy(message = "Logged out. Connect Google or Notion again to resume syncing.")
    }

    fun clearMessage() { _uiState.value = _uiState.value.copy(message = null) }

    private fun enqueue(name: String, request: androidx.work.OneTimeWorkRequest) {
        workManager.enqueueUniqueWork("${name}_manual", ExistingWorkPolicy.REPLACE, request)
    }
}

private data class AuthSettingsSnapshot(
    val notionToken: String?,
    val databaseId: String?,
    val email: String?,
    val sheetId: String?,
    val name: String?,
    val dp: String?
)

data class SettingsUiState(
    val theme: String = "system",
    val notionConnected: Boolean = false,
    val notionDatabaseId: String = "",
    val googleConnected: Boolean = false,
    val googleEmail: String = "",
    val googleSheetId: String = "",
    val userName: String = "",
    val userDp: String = "",
    val isWorking: Boolean = false,
    val message: String? = null
)
