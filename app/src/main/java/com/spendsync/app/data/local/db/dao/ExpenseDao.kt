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
    
    @Query("SELECT * FROM expenses WHERE isSynced = 0")
    suspend fun getUnsyncedExpenses(): List<ExpenseEntity>
    
    @Query("UPDATE expenses SET notionPageId = :notionPageId, isSynced = CASE WHEN :notionPageId = 'error' THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun markAsSynced(id: String, notionPageId: String)
    
    @Query("SELECT * FROM expenses WHERE name LIKE :prefix || '%' GROUP BY name ORDER BY createdAt DESC LIMIT 5")
    fun getFullSuggestions(prefix: String): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>
    
    @Query("SELECT SUM(amount) FROM expenses")
    suspend fun getTotalSpending(): Double?
    
    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startDate")
    suspend fun getTotalSpendingSince(startDate: Long): Double?
}
