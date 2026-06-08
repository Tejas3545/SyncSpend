package com.spendsync.app.domain.usecase

import com.spendsync.app.domain.repository.ExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val notionRepository: com.spendsync.app.domain.repository.NotionRepository
) {
    suspend operator fun invoke(expense: com.spendsync.app.domain.model.Expense) {
        if (!expense.notionPageId.isNullOrEmpty() && expense.notionPageId != "error") {
            notionRepository.deleteExpense(expense.notionPageId)
        }
        expenseRepository.deleteExpense(expense.id)
    }
}
