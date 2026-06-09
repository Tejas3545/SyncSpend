package com.spendsync.app.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spendsync.app.util.DateUtils
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
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("SyncSpend", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                actions = {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(if (isSearchActive) Icons.Default.Close else Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddExpense,
                containerColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(16.dp)
                    .size(60.dp)
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Add Expense",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background)) {
            // Background Accent
            Box(
                modifier = Modifier
                    .size(500.dp)
                    .offset(x = (-200).dp, y = (-200).dp)
                    .background(Brush.radialGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), Color.Transparent)))
            )

            Column(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        placeholder = { Text("Search expenses...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        ),
                        singleLine = true
                    )
                }

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
                    ) {
                        item {
                            SpendingSummaryCard(
                                totalSpending = uiState.spendingSummary?.totalThisMonth ?: 0.0,
                                period = "This Month",
                                chartData = uiState.spendingSummary?.dailyBreakdown ?: emptyMap()
                            )
                        }

                        item {
                            SpendingTrendsRow(uiState.spendingSummary)
                        }

                        uiState.groupedExpenses.forEach { (date, expenses) ->
                            val filteredExpenses = expenses.filter { 
                                it.name.contains(searchQuery, ignoreCase = true) || 
                                it.category.name.contains(searchQuery, ignoreCase = true) 
                            }
                            
                            if (filteredExpenses.isNotEmpty()) {
                                item {
                                    val headerDate = try { java.time.LocalDate.parse(date) } catch (e: Exception) { null }
                                    Text(
                                        text = if (headerDate == java.time.LocalDate.now()) "Latest" else DateUtils.formatDateHeader(date),
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                item {
                                    Surface(
                                        modifier = Modifier
                                            .padding(horizontal = 20.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(24.dp)),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                    ) {
                                        Column {
                                            filteredExpenses.forEachIndexed { index, expense ->
                                                ExpenseItem(
                                                    expense = expense,
                                                    onDelete = { viewModel.deleteExpense(it) }
                                                )
                                                if (index < filteredExpenses.size - 1) {
                                                    HorizontalDivider(
                                                        modifier = Modifier.padding(start = 72.dp),
                                                        thickness = 0.5.dp,
                                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
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

@Composable
fun SpendingTrendsRow(summary: com.spendsync.app.domain.model.SpendingSummary?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TrendCard("This Week", summary?.totalThisWeek ?: 0.0, Modifier.weight(1f))
        TrendCard("This Month", summary?.totalThisMonth ?: 0.0, Modifier.weight(1f))
        TrendCard("This Year", summary?.totalThisYear ?: 0.0, Modifier.weight(1f))
    }
}

@Composable
fun TrendCard(label: String, amount: Double, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(100.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "₹${String.format(java.util.Locale.getDefault(), "%,.0f", amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SpendingSummaryCard(
    totalSpending: Double,
    period: String,
    chartData: Map<java.time.LocalDate, Double>
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Total Spending",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "₹${String.format(java.util.Locale.getDefault(), "%,.2f", totalSpending)}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = period,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Minimal iOS-style stick chart with soft grid lines.
            val chartEntries = chartData.toSortedMap().values.toList().takeLast(7).map { it.toFloat() }
            val displayEntries = if (chartEntries.isEmpty()) List(7) { 0f } else chartEntries
            val barColor = MaterialTheme.colorScheme.onSurface
            val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.10f)
            val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .padding(vertical = 8.dp)
            ) {
                val maxVal = displayEntries.maxOrNull() ?: 1f
                val safeMax = if (maxVal == 0f) 1f else maxVal
                val barWidth = 18.dp.toPx()
                val totalBarsWidth = barWidth * displayEntries.size
                val spacing = if (displayEntries.size > 1) {
                    (size.width - totalBarsWidth).coerceAtLeast(0f) / (displayEntries.size - 1)
                } else {
                    0f
                }

                repeat(4) { index ->
                    val y = size.height * (index + 1) / 5f
                    drawLine(
                        color = gridColor,
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                displayEntries.forEachIndexed { index, value ->
                    val x = index * (barWidth + spacing)
                    val normalizedHeight = if (value > 0f) (value / safeMax) * size.height else 0f
                    val barHeight = normalizedHeight.coerceAtLeast(if (value > 0f) 12.dp.toPx() else 0f)
                    val y = size.height - barHeight
                    drawRoundRect(
                        color = trackColor,
                        topLeft = androidx.compose.ui.geometry.Offset(x, 0f),
                        size = androidx.compose.ui.geometry.Size(barWidth, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2, barWidth / 2)
                    )
                    if (value > 0f) {
                        drawRoundRect(
                            color = barColor,
                            topLeft = androidx.compose.ui.geometry.Offset(x, y),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2, barWidth / 2)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Insight Card
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Smart Insight",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Your spending is trending down this week.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseItem(
    expense: com.spendsync.app.domain.model.Expense,
    onDelete: ((com.spendsync.app.domain.model.Expense) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = expense.category.emoji,
                    fontSize = 22.sp
                )
            }

            Column {
                Text(
                    text  = expense.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text  = expense.category.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    expense.paymentMethod?.let {
                        Text(
                            text = " • ${it.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text  = "₹${String.format(java.util.Locale.getDefault(), "%,.2f", expense.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            val syncLabel = when {
                expense.notionSynced && expense.googleSynced -> "Google + Notion"
                expense.notionSynced -> "Notion"
                expense.googleSynced -> "Google Sheets"
                expense.lastSyncError != null -> "Waiting to retry"
                else -> "Saved offline"
            }
            Text(
                text = syncLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (expense.isSynced) Color(0xFF34C759) else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (onDelete != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete Expense",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onDelete(expense) }
                )
            }
        }
    }
}
