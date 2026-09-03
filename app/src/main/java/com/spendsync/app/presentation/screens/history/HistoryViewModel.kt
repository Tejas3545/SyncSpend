package com.spendsync.app.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spendsync.app.domain.usecase.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.spendsync.app.domain.usecase.DeleteExpenseUseCase

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            getExpensesUseCase().collect { expenses ->
                _uiState.value = _uiState.value.copy(
                    expenses = expenses,
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
    
    fun deleteExpense(expense: com.spendsync.app.domain.model.Expense) {
        viewModelScope.launch {
            deleteExpenseUseCase(expense)
        }
    }
}

data class HistoryUiState(
    val expenses: List<com.spendsync.app.domain.model.Expense> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)
