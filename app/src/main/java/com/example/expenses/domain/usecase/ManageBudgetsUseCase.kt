package com.example.expenses.domain.usecase

import com.example.expenses.domain.model.Budget
import kotlinx.coroutines.flow.Flow

class ManageBudgetsUseCase(private val repository: ExpensesRepository) {
    suspend fun get(categoryId: Long, month: Int, year: Int): Budget? =
        repository.getBudget(categoryId, month, year)

    fun observe(month: Int, year: Int): Flow<List<Budget>> = repository.observeBudgets(month, year)

    suspend fun save(budget: Budget) = repository.upsertBudget(budget)

    suspend fun delete(budget: Budget) = repository.deleteBudget(budget)
}
