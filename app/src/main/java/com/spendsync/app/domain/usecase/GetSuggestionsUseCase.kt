package com.spendsync.app.domain.usecase

import com.spendsync.app.domain.model.Expense
import com.spendsync.app.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSuggestionsUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    operator fun invoke(prefix: String): Flow<List<Expense>> {
        return expenseRepository.getSuggestions(prefix)
    }
}
