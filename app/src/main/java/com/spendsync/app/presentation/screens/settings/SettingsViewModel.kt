package com.spendsync.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.data.local.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    authDataStore: AuthDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataStore.theme.collect { theme ->
                _uiState.value = _uiState.value.copy(theme = theme)
            }
        }
        viewModelScope.launch {
            authDataStore.notionToken.collect { token ->
                _uiState.value = _uiState.value.copy(notionConnected = !token.isNullOrBlank())
            }
        }
    }

    fun onThemeChanged(theme: String) {
        viewModelScope.launch {
            settingsDataStore.setTheme(theme)
        }
    }
}

data class SettingsUiState(
    val theme: String = "system",
    val notionConnected: Boolean = false
)
