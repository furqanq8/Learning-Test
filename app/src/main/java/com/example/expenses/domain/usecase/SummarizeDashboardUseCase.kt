package com.example.expenses.domain.usecase

import com.example.expenses.domain.model.DashboardSummary
import kotlinx.datetime.LocalDate

class SummarizeDashboardUseCase(private val repository: ExpensesRepository) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate): DashboardSummary =
        repository.getDashboardSummary(start, end)
}
