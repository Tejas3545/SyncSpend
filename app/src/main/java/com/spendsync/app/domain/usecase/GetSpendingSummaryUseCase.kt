package com.spendsync.app.domain.usecase

import com.spendsync.app.domain.model.SpendingSummary
import com.spendsync.app.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSpendingSummaryUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    operator fun invoke(): Flow<SpendingSummary> {
        return expenseRepository.getSpendingSummary()
    }
}
