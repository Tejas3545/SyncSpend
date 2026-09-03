package com.spendsync.app.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spendsync.app.presentation.screens.home.components.AccountSwitcher
import com.spendsync.app.presentation.screens.home.components.ExpenseItemRow
import com.spendsync.app.presentation.screens.home.components.MainSpendingCard
import com.spendsync.app.presentation.screens.home.components.WidgetsBottomSheet
import com.spendsync.app.ui.theme.LocalThemeIsDark
import com.spendsync.app.util.DateUtils
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = LocalThemeIsDark.current
    var isSearchActive by remember { mutableStateOf(false) }
    var showWidgetsBottomSheet by remember { mutableStateOf(false) }

    if (showWidgetsBottomSheet) {
        WidgetsBottomSheet(
            summary = uiState.spendingSummary,
            isGoogleConnected = uiState.isGoogleConnected,
            isNotionConnected = uiState.isNotionConnected,
            onOpenSettings = {
                showWidgetsBottomSheet = false
                onNavigateToSettings()
            },
            onDismiss = { showWidgetsBottomSheet = false }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Brand Logo Tile ("SS")
                        Surface(
                            modifier = Modifier.size(34.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) Color.White else Color.Black,
                            contentColor = if (isDark) Color.Black else Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "SS",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.5).sp
                                )
                            }
                        }

                        // Account Switcher dropdown
                        AccountSwitcher(
                            currentAccount = uiState.currentAccount,
                            accounts = uiState.accounts,
                            onSelectAccount = viewModel::onAccountSelected,
                            onAddAccount = viewModel::onAddAccount
                        )
                    }
                },
                actions = {
                    // Search Button
                    IconButton(
                        onClick = { isSearchActive = !isSearchActive },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(19.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Widgets & Spending Trends Button
                    IconButton(
                        onClick = { showWidgetsBottomSheet = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Widgets & Spending Trends",
                            modifier = Modifier.size(19.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Settings Button
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(19.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            // Circle FAB (+ Button)
            FloatingActionButton(
                onClick = onNavigateToAddExpense,
                containerColor = if (isDark) Color.White else Color.Black,
                contentColor = if (isDark) Color.Black else Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .padding(bottom = 12.dp, end = 8.dp)
                    .size(54.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Expandable Search Input
            AnimatedVisibility(
                visible = isSearchActive,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    placeholder = {
                        Text(
                            "Search expenses...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
                ) {
                    // Main Spending Card with Period Switcher & Guide Lines
                    item {
                        MainSpendingCard(
                            uiState = uiState,
                            onPeriodChange = viewModel::onPeriodChanged
                        )
                    }

                    // Grouped Expenses Feed
                    if (uiState.groupedExpenses.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp, start = 24.dp, end = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "No Expenses",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Tap + to log an expense",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        uiState.groupedExpenses.forEach { (dateStr, expenses) ->
                            if (expenses.isNotEmpty()) {
                                item {
                                    val headerDate = try { LocalDate.parse(dateStr) } catch (e: Exception) { null }
                                    val isToday = headerDate == LocalDate.now()
                                    val yesterday = LocalDate.now().minusDays(1)
                                    val isYesterday = headerDate == yesterday

                                    val headerTitle = when {
                                        isToday -> "Latest"
                                        isYesterday -> yesterday.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
                                        else -> DateUtils.formatDateHeader(dateStr)
                                    }

                                    Text(
                                        text = headerTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 8.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                item {
                                    // Unified Rounded Card for Expense Group
                                    Surface(
                                        modifier = Modifier
                                            .padding(horizontal = 20.dp)
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                                    ) {
                                        Column {
                                            expenses.forEachIndexed { index, expense ->
                                                ExpenseItemRow(
                                                    expense = expense,
                                                    onDelete = { viewModel.deleteExpense(it) }
                                                )
                                                if (index < expenses.size - 1) {
                                                    HorizontalDivider(
                                                        modifier = Modifier.padding(start = 68.dp),
                                                        thickness = 0.5.dp,
                                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
