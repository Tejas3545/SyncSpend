package com.spendsync.app.presentation.screens.auth

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.spendsync.app.R
import com.spendsync.app.data.repository.GoogleSheetsRepositoryImpl

@Composable
fun AuthScreen(
    notionRedirectUri: Uri?,
    onContinue: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val googleOptions = remember {
        val scopes = GoogleSheetsRepositoryImpl.GOOGLE_SCOPES.map(::Scope)
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(scopes.first(), *scopes.drop(1).toTypedArray())
            .build()
    }
    val googleClient = remember { GoogleSignIn.getClient(context, googleOptions) }
    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        GoogleSignIn.getSignedInAccountFromIntent(result.data)
            .addOnSuccessListener { account ->
                account.account?.let {
                    viewModel.connectGoogle(it, account.displayName, account.email, account.photoUrl?.toString())
                }
            }
            .addOnFailureListener { viewModel.clearMessage() }
    }

    LaunchedEffect(notionRedirectUri) { viewModel.handleNotionRedirect(notionRedirectUri) }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFF7F7F8), MaterialTheme.colorScheme.background)
                    )
                )
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    "Expense.\nTracking.\nSimplified.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black, lineHeight = 42.sp),
                    color = Color.Black
                )
                Text(
                    "Connect Google Sheets, Notion, or both. SyncSpend keeps your session active until you choose logout.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF5F6368)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White.copy(alpha = 0.72f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f)),
                    shadowElevation = 18.dp
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        AuthProviderRow("Google Sheets", if (state.googleConnected) state.googleEmail else "Private spreadsheet sync", "G", Color(0xFF4285F4))
                        Button(
                            onClick = { googleLauncher.launch(googleClient.signInIntent) },
                            enabled = !state.isWorking,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(if (state.googleConnected) "Google Connected" else "Continue with Google", fontWeight = FontWeight.Bold)
                        }

                        AuthProviderRow("Notion", if (state.notionConnected) "Expenses database ready" else "Create your expenses database automatically", "N", Color.Black)
                        OutlinedButton(
                            onClick = {
                                viewModel.buildNotionAuthUri()?.let { uri ->
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                }
                            },
                            enabled = !state.isWorking,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Icon(painterResource(R.drawable.ic_notion), contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(8.dp))
                            Text(if (state.notionConnected) "Notion Connected" else "Continue with Notion", fontWeight = FontWeight.Bold)
                        }

                        if (state.googleConnected || state.notionConnected) {
                            Button(
                                onClick = onContinue,
                                enabled = !state.isWorking,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                            ) {
                                Text("Start SyncSpend", fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                if (state.isWorking) CircularProgressIndicator()

                Text(
                    "You can use either destination. Your expense entries remain private on your account and device.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6E6E73)
                )
            }
        }
    }
}

@Composable
private fun AuthProviderRow(title: String, subtitle: String, monogram: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.size(42.dp).background(Color(0xFFF4F4F5), CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(monogram, color = color, fontWeight = FontWeight.Black, fontSize = 22.sp) }
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = Color(0xFF6E6E73), style = MaterialTheme.typography.bodySmall)
        }
    }
}
