package com.spendsync.app.presentation.screens.auth

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.spendsync.app.presentation.components.NotionConnectDialog

@Composable
fun AuthScreen(
    notionRedirectUri: Uri?,
    onContinue: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    var page by remember { mutableIntStateOf(0) }
    
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

    fun launchGooglePicker() {
        googleClient.signOut().addOnCompleteListener {
            googleLauncher.launch(googleClient.signInIntent)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Indicator & Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    PageDots(page = page, count = 4)
                }

                // Middle Content Area - Fit to Frame
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = page,
                        transitionSpec = {
                            fadeIn() + slideInHorizontally { width -> if (targetState > initialState) width else -width } togetherWith
                                    fadeOut() + slideOutHorizontally { width -> if (targetState > initialState) -width else width }
                        },
                        label = "AuthPages"
                    ) { targetPage ->
                        when (targetPage) {
                            0 -> WelcomePage(onNext = { page = 1 })
                            1 -> FeaturePage(
                                eyebrow = "SHORTCUTS",
                                title = "Log expenses\nfrom anywhere.",
                                body = "Fast shortcut tile and widgets let you log expenses in seconds. Auto-suggestions fill in details from previous entries.",
                                icon = { ShortcutMock() },
                                buttonText = "Continue",
                                onNext = { page = 2 }
                            )
                            2 -> FeaturePage(
                                eyebrow = "WIDGETS",
                                title = "Spending trends\nat a glance.",
                                body = "Keep track of weekly, monthly, and yearly totals with clean monochrome cards right on your home screen.",
                                icon = { WidgetMock() },
                                buttonText = "Continue",
                                onNext = { page = 3 }
                            )
                            else -> LoginPanel(
                                state = state,
                                onGoogle = ::launchGooglePicker,
                                onNotion = {
                                    val uri = viewModel.buildNotionAuthUri()
                                    if (uri != null) {
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        context.startActivity(intent)
                                    }
                                },
                                onContinue = onContinue
                            )
                        }
                    }
                }

                // Footer Loading Indicator
                if (state.isWorking) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun PageDots(page: Int, count: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            Box(
                Modifier
                    .size(width = if (index == page) 32.dp else 8.dp, height = 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == page) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
private fun WelcomePage(onNext: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PreviewExpenseRow("Spotify", "Entertainment", "₹20.98")
                PreviewExpenseRow("Groceries", "Food & Drinks", "₹56.80")
                PreviewExpenseRow("Uber", "Transportation", "₹26.40")
            }
        }

        Icon(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Unspecified
        )

        Text(
            "Expense.\nTracking.\nSimplified.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Black,
                lineHeight = 36.sp,
                letterSpacing = (-1).sp
            )
        )

        Text(
            "Track expenses effortlessly. Auto-sync with Notion and Google with zero backend cost.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        PrimaryLiquidButton("Get Started", onNext)
    }
}

@Composable
private fun FeaturePage(
    eyebrow: String,
    title: String,
    body: String,
    icon: @Composable () -> Unit,
    buttonText: String,
    onNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            eyebrow,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = 2.sp
        )

        icon()

        Text(
            title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                lineHeight = 34.sp
            )
        )

        Text(
            body,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))

        PrimaryLiquidButton(buttonText, onNext)
    }
}

@Composable
private fun LoginPanel(
    state: AuthUiState,
    onGoogle: () -> Unit,
    onNotion: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Connect Your Sync Destinations",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black
            )
        )

        Text(
            "Expenses are saved directly to your phone. Connect Google, Notion, or use local offline mode.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AuthProviderItem(
                    title = "Google",
                    subtitle = if (state.googleConnected) state.googleEmail else "Private spreadsheet sync",
                    connected = state.googleConnected,
                    iconRes = R.drawable.ic_google_official,
                    onClick = onGoogle
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                AuthProviderItem(
                    title = "Notion",
                    subtitle = if (state.notionConnected) "Database connected" else "Auto-creates Notion expenses table",
                    connected = state.notionConnected,
                    iconRes = R.drawable.ic_notion_official,
                    onClick = onNotion
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        if (state.googleConnected || state.notionConnected) {
            PrimaryLiquidButton("Start SyncSpend", onContinue)
        }

        Text(
            "Zero cloud servers. Your financial data stays 100% private in your own accounts.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AuthProviderItem(
    title: String,
    subtitle: String,
    connected: Boolean,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
        Surface(
            color = if (connected) Color(0xFF34C759).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (connected) {
                    Icon(Icons.Default.Check, null, tint = Color(0xFF34C759), modifier = Modifier.size(14.dp))
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

@Composable
private fun LiquidGlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        content = content
    )
}

@Composable
private fun PrimaryLiquidButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.background
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun PreviewExpenseRow(name: String, category: String, amount: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ReceiptLong, null, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(amount, fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ShortcutMock() {
    LiquidGlassCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("What is the amount?", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Surface(
                Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Text(
                    "16.99",
                    Modifier.padding(14.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel", Modifier.padding(10.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium)
                }
                Surface(
                    Modifier.weight(1f),
                    color = Color(0xFF007AFF),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Done", Modifier.padding(10.dp), textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun WidgetMock() {
    LiquidGlassCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("This Week" to "₹120.38", "This Month" to "₹349.18", "This Year" to "₹1,865.18").forEach { (label, amount) ->
                Surface(
                    Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.BarChart, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        Text(amount, fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

