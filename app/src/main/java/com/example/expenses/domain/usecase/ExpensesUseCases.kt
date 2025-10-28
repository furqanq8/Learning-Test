package com.example.expenses.domain.usecase

data class ExpensesUseCases(
    val manageTransactions: ManageTransactionsUseCase,
    val manageCategories: ManageCategoriesUseCase,
    val manageRecurringEntries: ManageRecurringEntriesUseCase,
    val manageBudgets: ManageBudgetsUseCase,
    val managePreferences: ManageUserPreferencesUseCase,
    val searchTransactions: SearchTransactionsUseCase,
    val summarizeDashboard: SummarizeDashboardUseCase,
    val exportMonthlySummary: ExportMonthlySummaryUseCase
)
