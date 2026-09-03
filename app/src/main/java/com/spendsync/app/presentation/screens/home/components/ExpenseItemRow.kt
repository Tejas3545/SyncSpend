package com.spendsync.app.presentation.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.spendsync.app.domain.model.Expense
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ExpenseItemRow(
    expense: Expense,
    onDelete: (Expense) -> Unit,
    modifier: Modifier = Modifier
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
        modifier = modifier
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

fun expenseIcon(name: String, category: String): ImageVector {
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
