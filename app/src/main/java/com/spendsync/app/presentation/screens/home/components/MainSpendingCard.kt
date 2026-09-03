package com.spendsync.app.presentation.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendsync.app.presentation.screens.home.HomeUiState
import com.spendsync.app.presentation.screens.home.Period
import com.spendsync.app.ui.theme.LocalThemeIsDark
import java.time.LocalDate
import java.util.Locale

@Composable
fun MainSpendingCard(
    uiState: HomeUiState,
    onPeriodChange: (Period) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalThemeIsDark.current
    var selectedDayIndex by remember { mutableIntStateOf(-1) }

    val amount = when (uiState.period) {
        Period.WEEK -> uiState.spendingSummary?.totalThisWeek ?: 0.0
        Period.MONTH -> uiState.spendingSummary?.totalThisMonth ?: 0.0
        Period.YEAR -> uiState.spendingSummary?.totalThisYear ?: 0.0
    }

    Surface(
        modifier = modifier
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
