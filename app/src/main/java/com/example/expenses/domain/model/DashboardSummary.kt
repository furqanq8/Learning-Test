package com.example.expenses.domain.model

import kotlinx.datetime.Month

data class CategoryTotal(
    val category: Category,
    val amountMinor: Long
)

data class IncomeTypeTotal(
    val category: Category,
    val amountMinor: Long
)

data class MonthlySummary(
    val month: Month,
    val year: Int,
    val totalIncomeMinor: Long,
    val totalExpenseMinor: Long,
    val categoryTotals: List<CategoryTotal>,
    val incomeTypeTotals: List<IncomeTypeTotal>
)

data class DashboardSummary(
    val balanceMinor: Long,
    val recentTransactions: List<Transaction>,
    val monthlySummaries: List<MonthlySummary>
)
