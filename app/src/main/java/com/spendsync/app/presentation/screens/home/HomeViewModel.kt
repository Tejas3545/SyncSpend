package com.spendsync.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spendsync.app.domain.model.SpendingSummary
import com.spendsync.app.domain.usecase.GetExpensesUseCase
import com.spendsync.app.domain.usecase.GetSpendingSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

import com.spendsync.app.domain.usecase.DeleteExpenseUseCase

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getSpendingSummaryUseCase: GetSpendingSummaryUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getExpensesUseCase(),
                getSpendingSummaryUseCase()
            ) { expenses, summary ->
                _uiState.value = _uiState.value.copy(
                    groupedExpenses = groupExpensesByDate(expenses),
                    spendingSummary = summary,
                    isLoading = false
                )
            }.collect {}
        }
    }

    fun onPeriodChanged(period: Period) {
        _uiState.value = _uiState.value.copy(period = period)
    }

    private fun groupExpensesByDate(expenses: List<com.spendsync.app.domain.model.Expense>): Map<String, List<com.spendsync.app.domain.model.Expense>> {
        return expenses.groupBy { expense ->
            when (_uiState.value.period) {
                Period.WEEK -> expense.date.toString().substringBeforeLast("-")
                Period.MONTH -> expense.date.toString().substringBeforeLast("-")
                Period.YEAR -> expense.date.toString().substringBeforeLast("-")
            }
        }
    }

    fun deleteExpense(expense: com.spendsync.app.domain.model.Expense) {
        viewModelScope.launch {
            deleteExpenseUseCase(expense)
        }
    }
}

data class HomeUiState(
    val period: Period = Period.WEEK,
    val groupedExpenses: Map<String, List<com.spendsync.app.domain.model.Expense>> = emptyMap(),
    val spendingSummary: SpendingSummary? = null,
    val isLoading: Boolean = true
)

enum class Period { WEEK, MONTH, YEAR }
