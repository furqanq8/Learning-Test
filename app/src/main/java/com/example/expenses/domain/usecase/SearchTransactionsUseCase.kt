package com.example.expenses.domain.usecase

import com.example.expenses.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

class SearchTransactionsUseCase(private val repository: ExpensesRepository) {
    operator fun invoke(query: String): Flow<List<Transaction>> = repository.searchTransactions(query)
}
