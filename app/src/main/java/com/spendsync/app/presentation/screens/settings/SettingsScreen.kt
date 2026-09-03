package com.spendsync.app.presentation.screens.settings

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current

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
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black) },
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
            Modifier
                .fillMaxSize()
                .padding(insets)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
                Spacer(Modifier.height(4.dp))
                
                ProfileCard(state)

                Text(
                    "SyncSpend runs 100% serverless. Your expenses are stored on this phone first and synced directly with your personal integrations when online.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                SettingsSectionLabel("INTEGRATIONS")
                
                LiquidSettingsCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        StatusRow(
                            "Google Sheets", 
                            if (state.googleConnected) state.googleEmail else "Connect from login screen", 
                            state.googleConnected, 
                            "G"
                        )
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                        
                        StatusRow(
                            "Notion Database",
                            if (state.notionConnected) "Connected" else "Connect your Notion workspace",
                            state.notionConnected,
                            "N",
                            onAction = {
                                viewModel.buildNotionAuthUri()?.let { uri ->
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                }
                            }
                        )
                        
                        if (state.googleConnected || state.notionConnected) {
                            Spacer(Modifier.height(4.dp))
                            Button(
                                onClick = viewModel::syncNow,
                                enabled = !state.isWorking,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onBackground,
                                    contentColor = MaterialTheme.colorScheme.background
                                )
                            ) {
                                Icon(Icons.Default.Sync, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Sync Now", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                SettingsSectionLabel("APPEARANCE")
                
                LiquidSettingsCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        listOf("system" to "System", "light" to "Light", "dark" to "Dark").forEach { (value, label) ->
                            val selected = state.theme == value
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.onThemeChanged(value) },
                                color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
                                shape = RoundedCornerShape(14.dp),
                                border = if (selected) BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f)) else null,
                                shadowElevation = if (selected) 2.dp else 0.dp
                            ) {
                                Text(
                                    label,
                                    Modifier.padding(vertical = 10.dp),
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                LiquidSettingsCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.CloudDone, 
                            null, 
                            tint = Color(0xFF34C759),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Local & Privacy First", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "Adding expenses is always instant, even without network. Background synchronization handles rest.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = viewModel::logout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Logout and Disconnect", fontWeight = FontWeight.Bold)
                }
                
                Spacer(Modifier.height(32.dp))
            }
        }
    }

@Composable
private fun SettingsSectionLabel(text: String) {
    Text(
        text, 
        style = MaterialTheme.typography.labelMedium, 
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), 
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun LiquidSettingsCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        content = content
    )
}

@Composable
private fun ProfileCard(state: SettingsUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (state.userDp.isNotBlank()) {
                AsyncImage(
                    model = state.userDp,
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    Modifier
                        .size(52.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape), 
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (state.userName.ifBlank { state.googleEmail }.firstOrNull()?.uppercase() ?: "S"), 
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    state.userName.ifBlank { "SyncSpend User" }, 
                    fontWeight = FontWeight.Bold, 
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    state.googleEmail.ifBlank { "Offline Local Session" }, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun StatusRow(
    title: String, 
    subtitle: String, 
    connected: Boolean, 
    monogram: String, 
    onAction: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape), 
            contentAlignment = Alignment.Center
        ) {
            Text(
                monogram, 
                fontWeight = FontWeight.Black, 
                color = if (monogram == "G") Color(0xFF4285F4) else MaterialTheme.colorScheme.onSurface
            )
        }
        
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(
                subtitle, 
                style = MaterialTheme.typography.labelMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        
        Surface(
            modifier = if (!connected && onAction != null) Modifier.clickable { onAction() } else Modifier,
            color = if (connected) Color(0xFF34C759).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (connected) {
                    Icon(Icons.Default.Check, null, tint = Color(0xFF34C759), modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    if (connected) "Connected" else "Connect", 
                    color = if (connected) Color(0xFF248A3D) else MaterialTheme.colorScheme.onSurface, 
                    style = MaterialTheme.typography.labelSmall, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
