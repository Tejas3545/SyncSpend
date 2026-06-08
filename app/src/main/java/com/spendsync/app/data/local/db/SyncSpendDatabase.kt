package com.spendsync.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spendsync.app.data.local.db.dao.CategoryDao
import com.spendsync.app.data.local.db.dao.ExpenseDao
import com.spendsync.app.data.local.db.dao.PaymentMethodDao
import com.spendsync.app.data.local.db.entities.CategoryEntity
import com.spendsync.app.data.local.db.entities.ExpenseEntity
import com.spendsync.app.data.local.db.entities.PaymentMethodEntity

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        PaymentMethodEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SyncSpendDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun paymentMethodDao(): PaymentMethodDao
}
