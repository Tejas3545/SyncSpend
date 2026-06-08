package com.spendsync.app.di

import android.content.Context
import androidx.room.Room
import com.spendsync.app.data.local.db.SyncSpendDatabase
import com.spendsync.app.data.local.db.dao.CategoryDao
import com.spendsync.app.data.local.db.dao.ExpenseDao
import com.spendsync.app.data.local.db.dao.PaymentMethodDao
import com.spendsync.app.data.local.db.entities.CategoryEntity
import com.spendsync.app.data.local.db.entities.PaymentMethodEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SyncSpendDatabase {
        val db = Room.databaseBuilder(context, SyncSpendDatabase::class.java, "syncspend.db")
            .fallbackToDestructiveMigration()
            .build()
        
        // Seed default data
        CoroutineScope(Dispatchers.IO).launch {
            seedDefaultData(db)
        }
        
        return db
    }

    private suspend fun seedDefaultData(db: SyncSpendDatabase) {
        val categoryDao = db.categoryDao()
        val paymentMethodDao = db.paymentMethodDao()
        
        // Seed default categories
        val defaultCategories = listOf(
            CategoryEntity("1", "Entertainment", "🎭", true),
            CategoryEntity("2", "Food & Drinks", "🍔", true),
            CategoryEntity("3", "Transportation", "🚗", true),
            CategoryEntity("4", "Shopping", "🛍️", true),
            CategoryEntity("5", "Health", "💊", true),
            CategoryEntity("6", "Utilities", "💡", true),
            CategoryEntity("7", "Housing", "🏠", true),
            CategoryEntity("8", "Personal", "👤", true),
            CategoryEntity("9", "Travel", "✈️", true),
            CategoryEntity("10", "Other", "📌", true)
        )
        
        val existingCategories = categoryDao.getDefaultCategories()
        if (existingCategories.isEmpty()) {
            defaultCategories.forEach { categoryDao.insert(it) }
        }
        
        // Seed default payment methods
        val defaultPaymentMethods = listOf(
            PaymentMethodEntity("1", "Cash", true),
            PaymentMethodEntity("2", "Credit Card", true),
            PaymentMethodEntity("3", "Debit Card", true),
            PaymentMethodEntity("4", "UPI", true),
            PaymentMethodEntity("5", "Net Banking", true)
        )
        
        val existingPaymentMethods = paymentMethodDao.getDefaultPaymentMethods()
        if (existingPaymentMethods.isEmpty()) {
            defaultPaymentMethods.forEach { paymentMethodDao.insert(it) }
        }
    }

    @Provides
    fun provideExpenseDao(db: SyncSpendDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideCategoryDao(db: SyncSpendDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun providePaymentMethodDao(db: SyncSpendDatabase): PaymentMethodDao = db.paymentMethodDao()
}
