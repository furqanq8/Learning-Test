package com.example.expenses.domain.usecase

import com.example.expenses.domain.model.Category
import com.example.expenses.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

class ManageCategoriesUseCase(private val repository: ExpensesRepository) {
    fun observeByType(type: TransactionType): Flow<List<Category>> = repository.observeCategories(type)

    suspend fun save(category: Category) = repository.upsertCategory(category)

    suspend fun delete(category: Category) = repository.deleteCategory(category)
}
