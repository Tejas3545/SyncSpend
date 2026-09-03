package com.spendsync.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.domain.model.Expense
import com.spendsync.app.domain.model.SpendingSummary
import com.spendsync.app.domain.usecase.DeleteExpenseUseCase
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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getSpendingSummaryUseCase: GetSpendingSummaryUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allExpenses: List<Expense> = emptyList()

    init {
        loadData()
        observeAuth()
    }

    private fun observeAuth() {
        viewModelScope.launch {
            combine(
                authDataStore.googleSheetId,
                authDataStore.notionToken
            ) { googleId, notionToken ->
                _uiState.value = _uiState.value.copy(
                    isGoogleConnected = !googleId.isNullOrBlank(),
                    isNotionConnected = !notionToken.isNullOrBlank()
                )
            }.collect {}
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getExpensesUseCase(),
                getSpendingSummaryUseCase()
            ) { expenses, summary ->
                allExpenses = expenses
                filterAndUpdate(expenses, summary)
            }.collect {}
        }
    }

    private fun filterAndUpdate(expenses: List<Expense>, summary: SpendingSummary?) {
        val query = _uiState.value.searchQuery.trim()
        val filtered = if (query.isEmpty()) {
            expenses
        } else {
            expenses.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.category.name.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(
            groupedExpenses = groupExpensesByDate(filtered),
            spendingSummary = summary,
            isLoading = false
        )
    }

    fun onPeriodChanged(period: Period) {
        _uiState.value = _uiState.value.copy(period = period)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterAndUpdate(allExpenses, _uiState.value.spendingSummary)
    }

    fun onAccountSelected(account: String) {
        _uiState.value = _uiState.value.copy(currentAccount = account)
    }

    fun onAddAccount(newAccount: String) {
        val updated = (_uiState.value.accounts + newAccount).distinct()
        _uiState.value = _uiState.value.copy(
            accounts = updated,
            currentAccount = newAccount
        )
    }

    private fun groupExpensesByDate(expenses: List<Expense>): Map<String, List<Expense>> {
        return expenses
            .sortedByDescending { it.date }
            .groupBy { expense -> expense.date.toString() }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            deleteExpenseUseCase(expense)
        }
    }
}

data class HomeUiState(
    val currentAccount: String = "Personal",
    val accounts: List<String> = listOf("Personal", "Business", "Joint"),
    val period: Period = Period.WEEK,
    val searchQuery: String = "",
    val isGoogleConnected: Boolean = false,
    val isNotionConnected: Boolean = false,
    val groupedExpenses: Map<String, List<Expense>> = emptyMap(),
    val spendingSummary: SpendingSummary? = null,
    val isLoading: Boolean = true
)

enum class Period { WEEK, MONTH, YEAR }
