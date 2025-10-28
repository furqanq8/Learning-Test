package com.example.expenses.domain.usecase

import com.example.expenses.domain.model.RecurringTransaction
import kotlinx.coroutines.flow.Flow

class ManageRecurringEntriesUseCase(private val repository: ExpensesRepository) {
    fun observeActive(): Flow<List<RecurringTransaction>> = repository.observeRecurring()

    suspend fun save(recurring: RecurringTransaction) = repository.upsertRecurring(recurring)

    suspend fun delete(recurring: RecurringTransaction) = repository.deleteRecurring(recurring)
}
