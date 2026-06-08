package com.spendsync.app.domain.usecase

import com.spendsync.app.domain.model.Expense
import com.spendsync.app.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        expenseRepository.addExpense(expense)
    }
}
