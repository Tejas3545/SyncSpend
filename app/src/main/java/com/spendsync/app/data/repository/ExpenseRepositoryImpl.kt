package com.spendsync.app.data.repository

import com.spendsync.app.data.local.db.dao.CategoryDao
import com.spendsync.app.data.local.db.dao.ExpenseDao
import com.spendsync.app.data.local.db.dao.PaymentMethodDao
import com.spendsync.app.data.local.db.entities.ExpenseEntity
import com.spendsync.app.domain.model.Category
import com.spendsync.app.domain.model.Expense
import com.spendsync.app.domain.model.PaymentMethod
import com.spendsync.app.domain.model.SpendingSummary
import com.spendsync.app.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val paymentMethodDao: PaymentMethodDao
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addExpense(expense: Expense) {
        val entity = expense.toEntity()
        expenseDao.insert(entity)
    }

    override suspend fun deleteExpense(id: String) {
        expenseDao.deleteById(id)
    }

    override suspend fun getUnsyncedExpenses(): List<Expense> {
        return expenseDao.getUnsyncedExpenses().map { it.toDomainModel() }
    }

    override suspend fun markAsSynced(id: String, notionPageId: String) {
        expenseDao.markAsSynced(id, notionPageId)
    }

    override fun getSuggestions(prefix: String): Flow<List<Expense>> {
        return expenseDao.getFullSuggestions(prefix).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSpendingSummary(): Flow<SpendingSummary> {
        return expenseDao.getAllExpenses().map { entities ->
            val now = LocalDate.now()
            val weekStart = now.minusDays(now.dayOfWeek.value.toLong() - 1)
            val monthStart = now.withDayOfMonth(1)
            val yearStart = now.withDayOfYear(1)

            val expenses = entities.map {
                it.amount to LocalDate.ofEpochDay(it.date / (24 * 60 * 60 * 1000))
            }

            val totalThisWeek = expenses
                .filter { it.second >= weekStart }
                .sumOf { it.first }

            val totalThisMonth = expenses
                .filter { it.second >= monthStart }
                .sumOf { it.first }

            val totalThisYear = expenses
                .filter { it.second >= yearStart }
                .sumOf { it.first }

            val dailyBreakdown = expenses
                .groupBy { it.second }
                .mapValues { it.value.sumOf { expense -> expense.first } }

            SpendingSummary(
                totalThisWeek = totalThisWeek,
                totalThisMonth = totalThisMonth,
                totalThisYear = totalThisYear,
                dailyBreakdown = dailyBreakdown
            )
        }
    }

    private suspend fun ExpenseEntity.toDomainModel(): Expense {
        val categoryEntity = categoryDao.getCategoryById(categoryId)
        val paymentMethodEntity = paymentMethodId?.let { paymentMethodDao.getPaymentMethodById(it) }

        return Expense(
            id = id,
            name = name,
            amount = amount,
            category = Category(
                id = categoryEntity?.id ?: categoryId,
                name = categoryEntity?.name ?: "Unknown",
                emoji = categoryEntity?.emoji ?: "❓"
            ),
            paymentMethod = paymentMethodEntity?.let {
                PaymentMethod(id = it.id, name = it.name)
            },
            date = LocalDate.ofEpochDay(date / (24 * 60 * 60 * 1000)),
            isSynced = isSynced,
            notionPageId = notionPageId
        )
    }

    private fun Expense.toEntity(): ExpenseEntity {
        return ExpenseEntity(
            id = id,
            name = name,
            amount = amount,
            categoryId = category.id,
            paymentMethodId = paymentMethod?.id,
            date = date.toEpochDay() * 24 * 60 * 60 * 1000,
            notionPageId = notionPageId,
            isSynced = isSynced
        )
    }
}
