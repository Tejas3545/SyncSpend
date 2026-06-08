package com.spendsync.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.spendsync.app.data.local.db.entities.PaymentMethodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentMethodDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paymentMethod: PaymentMethodEntity)
    
    @Query("DELETE FROM payment_methods WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("SELECT * FROM payment_methods ORDER BY name ASC")
    fun getAllPaymentMethods(): Flow<List<PaymentMethodEntity>>
    
    @Query("SELECT * FROM payment_methods WHERE id = :id")
    suspend fun getPaymentMethodById(id: String): PaymentMethodEntity?
    
    @Query("SELECT * FROM payment_methods WHERE isDefault = 1")
    suspend fun getDefaultPaymentMethods(): List<PaymentMethodEntity>
}
