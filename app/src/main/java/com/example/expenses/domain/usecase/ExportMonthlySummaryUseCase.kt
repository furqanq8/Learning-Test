package com.example.expenses.domain.usecase

class ExportMonthlySummaryUseCase(private val repository: ExpensesRepository) {
    suspend operator fun invoke(year: Int, month: Int, formats: Set<String>, uri: String) {
        repository.exportMonthlySummary(year, month, formats, uri)
    }
}
