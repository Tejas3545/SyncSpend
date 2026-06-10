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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ReceiptLong
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PageDots(page = page, count = 4)
                Spacer(Modifier.height(28.dp))
                when (page) {
                    0 -> WelcomePage(onNext = { page = 1 })
                    1 -> FeaturePage(
                        eyebrow = "SHORTCUTS",
                        title = "Log expenses\nfrom anywhere.",
                        body = "Android app shortcuts open the complete expense form so amount, date, name, category, and payment method are all captured.",
                        icon = { ShortcutMock() },
                        button = "Continue",
                        onNext = { page = 2 }
                    )
                    2 -> FeaturePage(
                        eyebrow = "WIDGETS",
                        title = "Spending trends\nat a glance.",
                        body = "Track weekly, monthly, and yearly totals with clean monochrome cards that respect light and dark mode.",
                        icon = { WidgetMock() },
                        button = "Continue",
                        onNext = { page = 3 }
                    )
                    else -> LoginPanel(
                        state = state,
                        onGoogle = ::launchGooglePicker,
                        onNotion = {
                            viewModel.buildNotionAuthUri()?.let { uri ->
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        },
                        onContinue = onContinue
                    )
                }
                if (state.isWorking) {
                    Spacer(Modifier.height(18.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun PageDots(page: Int, count: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { index ->
            Box(
                Modifier
                    .size(width = if (index == page) 36.dp else 9.dp, height = 9.dp)
                    .background(
                        if (index == page) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
private fun WelcomePage(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        LiquidCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PreviewExpense("Coffee", "₹180.00")
                PreviewExpense("Groceries", "₹1,420.00")
                PreviewExpense("Petrol", "₹900.00")
            }
        }
        Icon(painterResource(R.drawable.ic_logo), contentDescription = null, modifier = Modifier.size(130.dp), tint = Color.Unspecified)
        Text("Welcome to\nSyncSpend", textAlign = TextAlign.Center, style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black, lineHeight = 42.sp))
        Text("Your simple, delightful way to track expenses in rupees.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        PrimaryAuthButton("Get Started", onNext)
    }
}

@Composable
private fun FeaturePage(eyebrow: String, title: String, body: String, icon: @Composable () -> Unit, button: String, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(26.dp)) {
        Text(eyebrow, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Black)
        icon()
        Text(title, textAlign = TextAlign.Center, style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black, lineHeight = 40.sp))
        Text(body, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
        PrimaryAuthButton(button, onNext)
    }
}

@Composable
private fun LoginPanel(state: AuthUiState, onGoogle: () -> Unit, onNotion: () -> Unit, onContinue: () -> Unit) {
    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Expense.\nTracking.\nSimplified.", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black, lineHeight = 42.sp))
        Text("Connect Google Sheets, Notion, or both. SyncSpend keeps your session active until you choose logout.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        LiquidCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                AuthProviderRow("Google Sheets", if (state.googleConnected) state.googleEmail else "Private spreadsheet sync", "G", Color(0xFF4285F4))
                Button(onClick = onGoogle, enabled = !state.isWorking, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(22.dp)) {
                    Text(if (state.googleConnected) "Choose another Google account" else "Continue with Google", fontWeight = FontWeight.Black)
                }
                AuthProviderRow("Notion", if (state.notionConnected) "Expenses database ready" else "Create your expenses database automatically", "N", MaterialTheme.colorScheme.onSurface)
                OutlinedButton(onClick = onNotion, enabled = !state.isWorking, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(22.dp)) {
                    Icon(painterResource(R.drawable.ic_notion), contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Unspecified)
                    Spacer(Modifier.size(8.dp))
                    Text(if (state.notionConnected) "Notion Connected" else "Continue with Notion", fontWeight = FontWeight.Bold)
                }
                if (state.googleConnected || state.notionConnected) {
                    PrimaryAuthButton("Start SyncSpend", onContinue)
                }
            }
        }
        Text("You can use either destination. Your expense entries remain private on your account and device.", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun LiquidCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(34.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)),
        shadowElevation = 18.dp,
        content = content
    )
}

@Composable
private fun PrimaryAuthButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(58.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground, contentColor = MaterialTheme.colorScheme.background)
    ) { Text(text, fontWeight = FontWeight.Black, fontSize = 18.sp) }
}

@Composable
private fun PreviewExpense(name: String, amount: String) {
    Surface(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .15f))) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).background(MaterialTheme.colorScheme.onBackground.copy(alpha = .10f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ReceiptLong, null, modifier = Modifier.size(23.dp))
            }
            Spacer(Modifier.size(14.dp))
            Text(name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text(amount, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ShortcutMock() {
    LiquidCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("New Expense", fontWeight = FontWeight.Bold)
            Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f), shape = RoundedCornerShape(22.dp)) {
                Text("₹16.99", Modifier.padding(18.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.10f), shape = RoundedCornerShape(18.dp)) { Text("Cancel", Modifier.padding(14.dp), textAlign = TextAlign.Center) }
                Surface(Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(18.dp)) { Text("Done", Modifier.padding(14.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.background, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun WidgetMock() {
    LiquidCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("This Week" to "₹120.38", "This Month" to "₹349.18", "This Year" to "₹1,865.18").forEach { (label, amount) ->
                Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f), shape = RoundedCornerShape(22.dp)) {
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BarChart, null)
                        Spacer(Modifier.size(12.dp))
                        Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(amount, fontWeight = FontWeight.Black)
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Default.DateRange, null)
                Spacer(Modifier.size(12.dp))
                Icon(Icons.Default.KeyboardArrowRight, null)
            }
        }
    }
}

@Composable
private fun AuthProviderRow(title: String, subtitle: String, monogram: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.size(46.dp).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(monogram, color = color, fontWeight = FontWeight.Black, fontSize = 22.sp) }
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
        if (subtitle.contains("ready") || subtitle.contains("@")) {
            Icon(Icons.Default.Check, null, tint = Color(0xFF34C759), modifier = Modifier.size(18.dp))
        }
    }
}
