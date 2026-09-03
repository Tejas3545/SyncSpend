package com.spendsync.app.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spendsync.app.domain.model.Expense
import com.spendsync.app.presentation.screens.home.components.AccountSwitcher
import com.spendsync.app.presentation.screens.home.components.WidgetsBottomSheet
import com.spendsync.app.ui.theme.LocalThemeIsDark
import com.spendsync.app.util.DateUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

@Composable
fun MainSpendingCard(
    uiState: HomeUiState,
    onPeriodChange: (Period) -> Unit
) {
    val isDark = LocalThemeIsDark.current
    var selectedDayIndex by remember { mutableIntStateOf(-1) }

    val amount = when (uiState.period) {
        Period.WEEK -> uiState.spendingSummary?.totalThisWeek ?: 0.0
        Period.MONTH -> uiState.spendingSummary?.totalThisMonth ?: 0.0
        Period.YEAR -> uiState.spendingSummary?.totalThisYear ?: 0.0
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row: Total Spending Title + Period Switcher Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Total Spending",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%,.2f", amount)}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Minimalist Period Switcher Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        Period.values().forEach { p ->
                            val isSelected = uiState.period == p
                            val label = when (p) {
                                Period.WEEK -> "Week"
                                Period.MONTH -> "Month"
                                Period.YEAR -> "Year"
                            }
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        onPeriodChange(p)
                                        selectedDayIndex = -1
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                                shadowElevation = if (isSelected) 1.dp else 0.dp
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 7-day Bar Chart with Guide Lines & Labels
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val today = LocalDate.now()
            val startOfWeek = today.minusDays(today.dayOfWeek.value % 7L)

            val weeklyValues = daysOfWeek.indices.map { dayOffset ->
                val date = startOfWeek.plusDays(dayOffset.toLong())
                uiState.spendingSummary?.dailyBreakdown?.get(date)?.toFloat() ?: 0f
            }

            val maxVal = (weeklyValues.maxOrNull() ?: 0f).coerceAtLeast(80f)
            val ceiling = (Math.ceil((maxVal / 20).toDouble()) * 20).toInt().coerceAtLeast(80)
            val guideLevels = listOf(ceiling, (ceiling * 3) / 4, (ceiling * 2) / 4, ceiling / 4)

            val barColor = if (isDark) Color.White else Color.Black
            val primaryColor = MaterialTheme.colorScheme.primary
            val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)

            // Selected Day Amount Banner if tapped
            if (selectedDayIndex in weeklyValues.indices) {
                val selectedAmount = weeklyValues[selectedDayIndex]
                val selectedDate = startOfWeek.plusDays(selectedDayIndex.toLong())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = "${daysOfWeek[selectedDayIndex]}: ₹${String.format(Locale.getDefault(), "%.2f", selectedAmount)}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                // Main Chart Canvas with 7 Columns & Gridlines
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val colWidth = size.width / 7f
                                val tappedIndex = (offset.x / colWidth).toInt().coerceIn(0, 6)
                                selectedDayIndex = if (selectedDayIndex == tappedIndex) -1 else tappedIndex
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val colWidth = width / 7f
                    val barWidth = 14.dp.toPx()

                    // Draw 4 Guide Lines
                    guideLevels.forEachIndexed { i, _ ->
                        val y = height * (i + 1) / 5f
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 0.5.dp.toPx()
                        )
                    }

                    // Draw Bottom Baseline
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, height),
                        end = Offset(width, height),
                        strokeWidth = 0.5.dp.toPx()
                    )

                    // Draw 7 Column Bars
                    weeklyValues.forEachIndexed { index, value ->
                        val centerX = (index * colWidth) + (colWidth / 2f)
                        val left = centerX - (barWidth / 2f)
                        val barHeight = if (ceiling > 0) (value / ceiling.toFloat()) * height else 0f
                        val top = height - barHeight.coerceAtLeast(if (value > 0f) 8.dp.toPx() else 0f)

                        // Light Track Pill
                        drawRoundRect(
                            color = trackColor,
                            topLeft = Offset(left, 0f),
                            size = Size(barWidth, height),
                            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                        )

                        // Active Value Bar
                        if (value > 0f) {
                            val activeColor = if (selectedDayIndex == index) primaryColor else barColor
                            drawRoundRect(
                                color = activeColor,
                                topLeft = Offset(left, top),
                                size = Size(barWidth, height - top),
                                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                            )
                        }
                    }
                }

                // Guide Scale Labels on the Right
                Column(
                    modifier = Modifier
                        .width(36.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    guideLevels.forEach { level ->
                        Text(
                            text = level.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.End
                        )
                    }
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Weekday Labels Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 36.dp)
            ) {
                daysOfWeek.forEachIndexed { index, label ->
                    val isToday = startOfWeek.plusDays(index.toLong()) == today
                    Text(
                        text = label,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedDayIndex = if (selectedDayIndex == index) -1 else index },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = if (isToday) FontWeight.Black else FontWeight.Medium,
                        color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseItemRow(
    expense: Expense,
    onDelete: (Expense) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Expense", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${expense.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(expense)
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDeleteConfirm = true }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon Tile
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = expenseIcon(expense.name, expense.category.name),
                    contentDescription = expense.category.name,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Name & Formatted Date
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            val formattedDate = try {
                expense.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
            } catch (e: Exception) {
                expense.date.toString()
            }
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Bold Amount in Rupee
        Text(
            text = "₹${String.format(Locale.getDefault(), "%,.2f", expense.amount)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun expenseIcon(name: String, category: String): ImageVector {
    val key = "${name.lowercase(Locale.getDefault())} ${category.lowercase(Locale.getDefault())}"
    return when {
        listOf("uber", "ola", "taxi", "cab", "car", "travel", "transport").any { it in key } -> Icons.Default.DirectionsCar
        listOf("petrol", "gas", "fuel").any { it in key } -> Icons.Default.LocalGasStation
        listOf("spotify", "music", "song").any { it in key } -> Icons.Default.MusicNote
        listOf("coffee", "cafe", "tea", "starbucks").any { it in key } -> Icons.Default.LocalCafe
        listOf("grocery", "groceries", "market", "basket", "supermarket").any { it in key } -> Icons.Default.ShoppingBasket
        listOf("dinner", "lunch", "breakfast", "food", "restaurant", "drinks", "burger", "pizza").any { it in key } -> Icons.Default.Restaurant
        listOf("movie", "cinema", "entertainment", "netflix").any { it in key } -> Icons.Default.Movie
        listOf("hospital", "doctor", "medicine", "pharmacy", "health").any { it in key } -> Icons.Default.LocalHospital
        listOf("flight", "airline", "plane", "trip").any { it in key } -> Icons.Default.Flight
        listOf("home", "rent", "house", "maintenance").any { it in key } -> Icons.Default.Home
        listOf("shopping", "cloth", "amazon", "flipkart").any { it in key } -> Icons.Default.ShoppingBag
        else -> Icons.Default.Category
    }
}
