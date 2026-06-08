package com.spendsync.app.presentation.screens.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.spendsync.app.domain.model.Category
import com.spendsync.app.domain.model.Expense
import com.spendsync.app.domain.model.PaymentMethod
import com.spendsync.app.domain.repository.CategoryRepository
import com.spendsync.app.data.local.db.dao.PaymentMethodDao
import com.spendsync.app.domain.usecase.AddExpenseUseCase
import com.spendsync.app.domain.usecase.GetSuggestionsUseCase
import com.spendsync.app.worker.NotionSyncWorker
import com.spendsync.app.worker.GoogleSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val categoryRepository: CategoryRepository,
    private val getSuggestionsUseCase: GetSuggestionsUseCase,
    private val paymentMethodDao: PaymentMethodDao,
    private val workManager: WorkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadPaymentMethods()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    selectedCategory = categories.firstOrNull()
                )
            }
        }
    }

    private fun loadPaymentMethods() {
        viewModelScope.launch {
            paymentMethodDao.getAllPaymentMethods().collect { entities ->
                val methods = entities.map { PaymentMethod(it.id, it.name) }
                _uiState.value = _uiState.value.copy(
                    paymentMethods = methods,
                    selectedPaymentMethod = if (_uiState.value.selectedPaymentMethod == null) methods.firstOrNull() else _uiState.value.selectedPaymentMethod
                )
            }
        }
    }

    fun onKeyPressed(key: String) {
        val currentAmount = _uiState.value.amount
        val newAmount = when (key) {
            "backspace" -> {
                if (currentAmount.length > 1) currentAmount.dropLast(1) else "0"
            }
            "." -> {
                if (!currentAmount.contains(".")) currentAmount + "." else currentAmount
            }
            else -> {
                if (currentAmount == "0") key else currentAmount + key
            }
        }
        // Limit to 2 decimal places
        if (newAmount.contains(".") && newAmount.substringAfter(".").length > 2) {
            return
        }
        _uiState.value = _uiState.value.copy(amount = newAmount)
    }

    fun onAmountChanged(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
        loadSuggestions(name)
    }

    private fun loadSuggestions(prefix: String) {
        viewModelScope.launch {
            getSuggestionsUseCase(prefix).collect { suggestions ->
                _uiState.value = _uiState.value.copy(suggestions = suggestions)
            }
        }
    }

    fun onCategorySelected(category: Category) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun onPaymentMethodSelected(paymentMethod: PaymentMethod) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = paymentMethod)
    }

    fun onDateChanged(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun onSaveExpense() {
        val amount = _uiState.value.amount.toDoubleOrNull() ?: 0.0
        if (amount <= 0) return

        val finalName = _uiState.value.name.takeIf { it.isNotBlank() } ?: "Quick Add"
        val category = _uiState.value.selectedCategory ?: return

        val expense = Expense(
            id = UUID.randomUUID().toString(),
            name = finalName,
            amount = amount,
            category = category,
            paymentMethod = _uiState.value.selectedPaymentMethod,
            date = _uiState.value.date,
            isSynced = false,
            notionPageId = null
        )

        viewModelScope.launch {
            addExpenseUseCase(expense)
            // Trigger immediate sync
            workManager.enqueueUniqueWork(
                "one_time_notion_sync",
                androidx.work.ExistingWorkPolicy.REPLACE,
                NotionSyncWorker.buildOneTimeRequest()
            )
            workManager.enqueueUniqueWork(
                "one_time_google_sync",
                androidx.work.ExistingWorkPolicy.REPLACE,
                GoogleSyncWorker.buildOneTimeRequest()
            )
            _uiState.value = _uiState.value.copy(saveSuccess = true)
        }
    }

    fun onSuggestionSelected(suggestion: Expense) {
        _uiState.value = _uiState.value.copy(
            name = suggestion.name,
            amount = suggestion.amount.toString(),
            selectedCategory = suggestion.category,
            selectedPaymentMethod = suggestion.paymentMethod
        )
    }
}

data class AddExpenseUiState(
    val amount: String = "0",
    val name: String = "",
    val selectedCategory: Category? = null,
    val selectedPaymentMethod: PaymentMethod? = null,
    val date: LocalDate = LocalDate.now(),
    val categories: List<Category> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val suggestions: List<Expense> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)
