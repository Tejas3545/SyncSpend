package com.spendsync.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spendsync.app.data.local.db.entities.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM expenses ORDER BY date DESC, createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: String): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE notionSynced = 0 ORDER BY createdAt ASC")
    suspend fun getPendingNotionExpenses(): List<ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE googleSynced = 0 ORDER BY createdAt ASC")
    suspend fun getPendingGoogleExpenses(): List<ExpenseEntity>

    @Query("UPDATE expenses SET notionPageId = :pageId, notionSynced = 1, isSynced = 1, lastSyncError = NULL WHERE id = :id")
    suspend fun markNotionSynced(id: String, pageId: String)

    @Query("UPDATE expenses SET googleSynced = 1, isSynced = 1, lastSyncError = NULL WHERE id = :id")
    suspend fun markGoogleSynced(id: String)

    @Query("UPDATE expenses SET lastSyncError = :message WHERE id = :id")
    suspend fun recordSyncError(id: String, message: String)

    @Query("SELECT * FROM expenses WHERE name LIKE :prefix || '%' GROUP BY name ORDER BY createdAt DESC LIMIT 5")
    fun getFullSuggestions(prefix: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expenses")
    suspend fun getTotalSpending(): Double?

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startDate")
    suspend fun getTotalSpendingSince(startDate: Long): Double?
}
