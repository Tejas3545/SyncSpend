package com.spendsync.app.presentation.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.spendsync.app.data.repository.GoogleSheetsRepositoryImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showNotionDialog by remember { mutableStateOf(false) }
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

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { insets ->
        Column(
            Modifier.fillMaxSize().padding(insets).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            Text(
                "Your data lives on this phone first. Connect one or both services and SyncSpend will upload pending entries whenever the network returns.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SettingsLabel("SYNC DESTINATIONS")
            IntegrationCard(
                monogram = "G",
                monogramColor = Color(0xFF4285F4),
                title = "Google Sheets",
                subtitle = if (state.googleConnected) state.googleEmail else "Create a private SyncSpend spreadsheet",
                connected = state.googleConnected,
                onClick = {
                    if (state.googleConnected) viewModel.disconnectGoogle()
                    else googleLauncher.launch(googleClient.signInIntent)
                }
            )
            IntegrationCard(
                monogram = "N",
                monogramColor = MaterialTheme.colorScheme.onSurface,
                title = "Notion",
                subtitle = if (state.notionConnected) "Database • ${state.notionDatabaseId.take(8)}…" else "Send expenses to your Notion database",
                connected = state.notionConnected,
                onClick = {
                    if (state.notionConnected) viewModel.disconnectNotion() else showNotionDialog = true
                }
            )

            if (state.googleConnected || state.notionConnected) {
                Button(
                    onClick = viewModel::syncNow,
                    enabled = !state.isWorking,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Sync, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sync pending expenses", fontWeight = FontWeight.SemiBold)
                }
            }

            SettingsLabel("APPEARANCE")
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(Modifier.padding(5.dp)) {
                    listOf("system" to "System", "light" to "Light", "dark" to "Dark").forEach { (value, label) ->
                        val selected = state.theme == value
                        Surface(
                            modifier = Modifier.weight(1f).clickable { viewModel.onThemeChanged(value) },
                            color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
                            shape = RoundedCornerShape(14.dp),
                            shadowElevation = if (selected) 2.dp else 0.dp
                        ) {
                            Text(label, Modifier.padding(vertical = 11.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                        }
                    }
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.CloudDone, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Offline by default", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("Adding an expense never waits for the internet. Android WorkManager watches connectivity and retries cloud delivery safely in the background.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(30.dp))
        }
    }

    if (showNotionDialog) {
        NotionConnectDialog(
            working = state.isWorking,
            onDismiss = { showNotionDialog = false },
            onConnect = { token, databaseId ->
                viewModel.connectNotion(token, databaseId)
                showNotionDialog = false
            }
        )
    }
}

@Composable
private fun SettingsLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun IntegrationCard(
    monogram: String,
    monogramColor: Color,
    title: String,
    subtitle: String,
    connected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(MaterialTheme.colorScheme.surface, CircleShape), contentAlignment = Alignment.Center) {
                Text(monogram, color = monogramColor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(color = if (connected) Color(0xFF34C759).copy(alpha = .14f) else MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp)) {
                Text(if (connected) "Connected" else "Connect", Modifier.padding(horizontal = 11.dp, vertical = 7.dp), color = if (connected) Color(0xFF248A3D) else MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun NotionConnectDialog(working: Boolean, onDismiss: () -> Unit, onConnect: (String, String) -> Unit) {
    var token by remember { mutableStateOf("") }
    var databaseId by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Connect Notion") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Create a Notion integration, share your expense database with it, then paste the credentials below.", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(token, { token = it }, label = { Text("Integration token") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                OutlinedTextField(databaseId, { databaseId = it }, label = { Text("Database ID") }, singleLine = true)
                Text("Required properties: Name, Amount, Category, Payment, Date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = { TextButton(onClick = { onConnect(token, databaseId) }, enabled = !working && token.isNotBlank() && databaseId.isNotBlank()) { Text("Connect") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
