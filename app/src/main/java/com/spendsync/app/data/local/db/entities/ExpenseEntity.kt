package com.spendsync.app.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val amount: Double,
    val categoryId: String,
    val paymentMethodId: String?,
    val date: Long,
    val notionPageId: String?,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
