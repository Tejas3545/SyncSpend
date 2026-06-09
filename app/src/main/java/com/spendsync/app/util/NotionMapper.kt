package com.spendsync.app.util

import com.spendsync.app.data.local.db.entities.ExpenseEntity
import com.spendsync.app.data.remote.notion.models.NotionPage
import com.spendsync.app.domain.model.Expense
import java.time.LocalDate

object NotionMapper {
    
    fun NotionPage.toExpense(id: String): Expense {
        val name = properties["Name"]?.title?.firstOrNull()?.text?.content ?: ""
        val amount = properties["Amount"]?.number ?: 0.0
        val categoryName = properties["Category"]?.select?.name ?: "Other"
        val paymentName = properties["Payment"]?.select?.name
        val dateString = properties["Date"]?.date?.start ?: LocalDate.now().toString()
        val date = LocalDate.parse(dateString)
        
        return Expense(
            id = id,
            name = name,
            amount = amount,
            category = com.spendsync.app.domain.model.Category(
                id = categoryName,
                name = categoryName,
                emoji = "📌"
            ),
            paymentMethod = paymentName?.let {
                com.spendsync.app.domain.model.PaymentMethod(
                    id = it,
                    name = it
                )
            },
            date = date,
            isSynced = true,
            notionPageId = this.id,
            notionSynced = true
        )
    }
}
