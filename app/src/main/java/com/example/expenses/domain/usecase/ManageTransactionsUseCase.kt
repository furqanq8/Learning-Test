package com.example.expenses.domain.usecase

import com.example.expenses.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class ManageTransactionsUseCase(private val repository: ExpensesRepository) {
    fun observeAll(): Flow<List<Transaction>> = repository.observeTransactions()

    fun observeInRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>> =
        repository.observeTransactionsInRange(start, end)

    suspend fun get(id: Long): Transaction? = repository.getTransaction(id)

    suspend fun save(transaction: Transaction) {
        if (transaction.id == 0L) repository.addTransaction(transaction) else repository.updateTransaction(transaction)
    }

    suspend fun delete(transaction: Transaction) = repository.deleteTransaction(transaction)
}
