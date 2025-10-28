package com.example.expenses.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses.domain.model.Budget
import com.example.expenses.domain.model.Category
import com.example.expenses.domain.model.CategoryType
import com.example.expenses.domain.model.DashboardSummary
import com.example.expenses.domain.model.RecurringTransaction
import com.example.expenses.domain.model.Transaction
import com.example.expenses.domain.model.TransactionType
import com.example.expenses.domain.usecase.ExpensesUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.monthNumber
import kotlinx.datetime.toLocalDateTime

sealed interface LockState {
    object Locked : LockState
    object Unlocked : LockState
}

data class ExpensesUiState(
    val transactions: List<Transaction> = emptyList(),
    val dashboardSummary: DashboardSummary? = null,
    val incomeCategories: List<Category> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val budgets: List<Budget> = emptyList(),
    val recurring: List<RecurringTransaction> = emptyList(),
    val currency: String = "KWD",
    val searchResults: List<Transaction> = emptyList(),
    val isDarkTheme: Boolean = false,
    val activeFilter: Pair<LocalDate, LocalDate>? = null
)

class ExpensesViewModel(private val useCases: ExpensesUseCases) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()

    private val _lockState = MutableStateFlow<LockState>(LockState.Locked)
    val lockState: StateFlow<LockState> = _lockState.asStateFlow()

    private var filterJob: Job? = null
    private var searchJob: Job? = null

    init {
        observeData()
    }

    private fun observeData() {
        val end = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val start = end.minus(DateTimeUnit.MonthBased(1))
        val defaultRange = start to end
        _uiState.update { it.copy(activeFilter = defaultRange) }
        viewModelScope.launch {
            useCases.manageTransactions.observeAll().collect { transactions ->
                val filter = _uiState.value.activeFilter ?: defaultRange
                val summary = useCases.summarizeDashboard(filter.first, filter.second)
                _uiState.update {
                    it.copy(
                        transactions = transactions,
                        dashboardSummary = summary
                    )
                }
            }
        }
        viewModelScope.launch {
            useCases.manageCategories.observeByType(TransactionType.INCOME).collect { income ->
                _uiState.update { it.copy(incomeCategories = income) }
            }
        }
        viewModelScope.launch {
            useCases.manageCategories.observeByType(TransactionType.EXPENSE).collect { expense ->
                _uiState.update { it.copy(expenseCategories = expense) }
            }
        }
        viewModelScope.launch {
            useCases.manageRecurringEntries.observeActive().collect { recurring ->
                _uiState.update { it.copy(recurring = recurring) }
            }
        }
        viewModelScope.launch {
            val month = end.monthNumber
            val year = end.year
            useCases.manageBudgets.observe(month, year).collect { budgets ->
                _uiState.update { it.copy(budgets = budgets) }
            }
        }
        viewModelScope.launch {
            val currency = useCases.managePreferences.getCurrency()
            _uiState.update { it.copy(currency = currency) }
        }
    }

    fun unlockWithPin(pin: String) {
        viewModelScope.launch {
            val success = useCases.managePreferences.validatePin(pin)
            if (success) {
                _lockState.value = LockState.Unlocked
            }
        }
    }

    fun requirePinUnlock() {
        viewModelScope.launch {
            if (!useCases.managePreferences.isPinRequired()) {
                _lockState.value = LockState.Unlocked
            }
        }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun filterByRange(start: LocalDate, end: LocalDate) {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            useCases.manageTransactions.observeInRange(start, end).collect { filtered ->
                val summary = useCases.summarizeDashboard(start, end)
                _uiState.update {
                    it.copy(
                        transactions = filtered,
                        dashboardSummary = summary,
                        activeFilter = start to end
                    )
                }
            }
        }
    }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            useCases.searchTransactions(query).collect { results ->
                _uiState.update { it.copy(searchResults = results) }
            }
        }
    }

    fun saveTransaction(transaction: Transaction) {
        viewModelScope.launch {
            useCases.manageTransactions.save(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            useCases.manageTransactions.delete(transaction)
        }
    }

    fun saveCategory(name: String, type: TransactionType) {
        viewModelScope.launch {
            useCases.manageCategories.save(
                Category(name = name, type = CategoryType.valueOf(type.name), isCustom = true)
            )
        }
    }

    fun saveBudget(budget: Budget) {
        viewModelScope.launch {
            useCases.manageBudgets.save(budget)
        }
    }

    fun saveRecurring(recurring: RecurringTransaction) {
        viewModelScope.launch {
            useCases.manageRecurringEntries.save(recurring)
        }
    }

    fun exportSummary(year: Int, month: Int, formats: Set<String>, uri: String) {
        viewModelScope.launch {
            useCases.exportMonthlySummary(year, month, formats, uri)
        }
    }

    fun setCurrency(currency: String) {
        viewModelScope.launch {
            useCases.managePreferences.setCurrency(currency)
            _uiState.update { it.copy(currency = currency) }
        }
    }

    fun backup(uri: String) {
        viewModelScope.launch {
            useCases.managePreferences.backup(uri)
        }
    }

    fun restore(uri: String) {
        viewModelScope.launch {
            useCases.managePreferences.restore(uri)
        }
    }
}
