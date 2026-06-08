package com.spendsync.app.presentation.screens.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun onGoogleSignInSuccess(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                val email = account.email ?: ""
                val displayName = account.displayName ?: ""
                val profilePicUrl = account.photoUrl?.toString()
                
                authDataStore.saveGoogleInfo(displayName, email, profilePicUrl)
                authDataStore.setLoggedIn(true)
                
                // Here we will trigger the automated sheet creation
                authRepository.initializeGoogleSheetIfNeeded(account.account)
                
                _uiState.value = _uiState.value.copy(isLoading = false, isLoginSuccess = true)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error handling google sign in", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to process sign in")
            }
        }
    }

    fun onGoogleSignInFailure(errorMsg: String) {
        _uiState.value = _uiState.value.copy(isLoading = false, error = errorMsg)
    }

    fun onNotionOAuthClicked(context: Context) {
        // Updated with user's fresh Client ID and exact Redirect URI from the link
        val clientId = com.spendsync.app.AppConfig.NOTION_CLIENT_ID
        val redirectUri = com.spendsync.app.AppConfig.NOTION_REDIRECT_URI
        val authUrl = "https://api.notion.com/v1/oauth/authorize?client_id=$clientId&response_type=code&owner=user&redirect_uri=${Uri.encode(redirectUri)}"
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
        context.startActivity(intent)
    }

    fun onNotionLoginClicked(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.loginWithNotion(token)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isLoginSuccess = true)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val error: String? = null
)
