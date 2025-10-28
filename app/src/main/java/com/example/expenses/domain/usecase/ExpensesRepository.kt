package com.example.expenses.domain.usecase

import com.example.expenses.domain.model.Budget
import com.example.expenses.domain.model.Category
import com.example.expenses.domain.model.DashboardSummary
import com.example.expenses.domain.model.RecurringTransaction
import com.example.expenses.domain.model.Transaction
import com.example.expenses.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ExpensesRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    fun observeTransactionsInRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>>
    suspend fun getTransaction(id: Long): Transaction?
    suspend fun addTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    fun searchTransactions(query: String): Flow<List<Transaction>>

    fun observeCategories(type: TransactionType): Flow<List<Category>>
    suspend fun upsertCategory(category: Category): Long
    suspend fun deleteCategory(category: Category)

    fun observeRecurring(): Flow<List<RecurringTransaction>>
    suspend fun upsertRecurring(recurring: RecurringTransaction): Long
    suspend fun deleteRecurring(recurring: RecurringTransaction)

    suspend fun getBudget(categoryId: Long, month: Int, year: Int): Budget?
    fun observeBudgets(month: Int, year: Int): Flow<List<Budget>>
    suspend fun upsertBudget(budget: Budget): Long
    suspend fun deleteBudget(budget: Budget)

    suspend fun getDashboardSummary(start: LocalDate, end: LocalDate): DashboardSummary

    suspend fun getDefaultCurrency(): String
    suspend fun setDefaultCurrency(currency: String)
    suspend fun isPinRequired(): Boolean
    suspend fun setPinCode(pin: String)
    suspend fun validatePin(pin: String): Boolean

    suspend fun backupDatabase(uri: String)
    suspend fun restoreDatabase(uri: String)
    suspend fun exportMonthlySummary(year: Int, month: Int, formats: Set<String>, uri: String)
}
