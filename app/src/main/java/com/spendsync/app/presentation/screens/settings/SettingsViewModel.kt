package com.spendsync.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.data.local.datastore.SettingsDataStore
import com.spendsync.app.domain.repository.NotionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val authDataStore: AuthDataStore,
    private val notionRepository: NotionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        testNotionConnection()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsDataStore.theme.collect { theme ->
                _uiState.value = _uiState.value.copy(theme = theme)
            }
        }
        viewModelScope.launch {
            authDataStore.userName.collect { name ->
                _uiState.value = _uiState.value.copy(userName = name)
            }
        }
        viewModelScope.launch {
            authDataStore.userEmail.collect { email ->
                _uiState.value = _uiState.value.copy(userEmail = email)
            }
        }
        viewModelScope.launch {
            authDataStore.userDp.collect { dp ->
                _uiState.value = _uiState.value.copy(userDp = dp)
            }
        }
    }

    fun onThemeChanged(theme: String) {
        viewModelScope.launch {
            settingsDataStore.setTheme(theme)
        }
    }

    fun testNotionConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTestingConnection = true)
            val isConnected = notionRepository.testConnection()
            _uiState.value = _uiState.value.copy(
                isTestingConnection = false,
                notionConnected = isConnected
            )
        }
    }

    fun syncAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            notionRepository.syncUnsyncedExpenses()
            _uiState.value = _uiState.value.copy(isSyncing = false)
            // Re-test connection just in case
            testNotionConnection()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authDataStore.clearAuth()
            authDataStore.setLoggedIn(false)
        }
    }
}

data class SettingsUiState(
    val theme: String = "system",
    val notionConnected: Boolean? = null,
    val isTestingConnection: Boolean = false,
    val isSyncing: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null,
    val userDp: String? = null
)
