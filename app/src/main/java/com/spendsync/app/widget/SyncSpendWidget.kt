package com.spendsync.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.spendsync.app.domain.repository.ExpenseRepository
import com.spendsync.app.util.CurrencyUtils
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.util.Locale

class SyncSpendWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetEntryPoint {
        fun expenseRepository(): ExpenseRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val repository = entryPoint.expenseRepository()
        val summary = repository.getSpendingSummary().first()

        provideContent {
            WidgetContent(
                totalThisWeek = summary.totalThisWeek,
                totalThisMonth = summary.totalThisMonth,
                totalThisYear = summary.totalThisYear
            )
        }
    }
}

class SyncSpendWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SyncSpendWidget()
}

@Composable
fun WidgetContent(
    totalThisWeek: Double,
    totalThisMonth: Double,
    totalThisYear: Double
) {
    GlanceTheme {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "WIDGETS",
                style = TextStyle(
                    color = ColorProvider(androidx.compose.ui.graphics.Color.Gray),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Spending Trends at a Glance",
                style = TextStyle(
                    color = ColorProvider(androidx.compose.ui.graphics.Color.Black),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.padding(bottom = 12.dp)
            )

            SummaryItem(label = "This Week", amount = totalThisWeek)
            Spacer(GlanceModifier.height(8.dp))
            SummaryItem(label = "This Month", amount = totalThisMonth)
            Spacer(GlanceModifier.height(8.dp))
            SummaryItem(label = "This Year", amount = totalThisYear)
        }
    }
}

@Composable
fun SummaryItem(label: String, amount: Double) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(60.dp)
            .background(androidx.compose.ui.graphics.Color(0xFFF5F5F5))
            .cornerRadius(16.dp)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = TextStyle(
                    color = ColorProvider(androidx.compose.ui.graphics.Color.Gray),
                    fontSize = 12.sp
                )
            )
            Text(
                text = "₹${String.format(Locale.getDefault(), "%,.2f", amount)}",
                style = TextStyle(
                    color = ColorProvider(androidx.compose.ui.graphics.Color.Black),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
