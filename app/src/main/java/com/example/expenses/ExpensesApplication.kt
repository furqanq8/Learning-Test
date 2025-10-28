package com.example.expenses

import android.app.Application
import androidx.work.Configuration
import com.example.expenses.data.local.ExpensesDatabase
import com.example.expenses.data.repository.ExpensesRepositoryImpl
import com.example.expenses.domain.usecase.ExpensesUseCases
import com.example.expenses.domain.usecase.ExportMonthlySummaryUseCase
import com.example.expenses.domain.usecase.ManageBudgetsUseCase
import com.example.expenses.domain.usecase.ManageCategoriesUseCase
import com.example.expenses.domain.usecase.ManageRecurringEntriesUseCase
import com.example.expenses.domain.usecase.ManageTransactionsUseCase
import com.example.expenses.domain.usecase.ManageUserPreferencesUseCase
import com.example.expenses.domain.usecase.SearchTransactionsUseCase
import com.example.expenses.domain.usecase.SummarizeDashboardUseCase
import com.example.expenses.work.RecurringEntryWorker

class ExpensesApplication : Application(), Configuration.Provider {

    lateinit var repository: ExpensesRepositoryImpl
        private set
    lateinit var useCases: ExpensesUseCases
        private set

    override fun onCreate() {
        super.onCreate()
        val database = ExpensesDatabase.getInstance(this)
        repository = ExpensesRepositoryImpl(
            database.transactionDao(),
            database.categoryDao(),
            database.recurringDao(),
            database.budgetDao(),
            this
        )
        useCases = ExpensesUseCases(
            manageTransactions = ManageTransactionsUseCase(repository),
            manageCategories = ManageCategoriesUseCase(repository),
            manageRecurringEntries = ManageRecurringEntriesUseCase(repository),
            manageBudgets = ManageBudgetsUseCase(repository),
            managePreferences = ManageUserPreferencesUseCase(repository),
            searchTransactions = SearchTransactionsUseCase(repository),
            summarizeDashboard = SummarizeDashboardUseCase(repository),
            exportMonthlySummary = ExportMonthlySummaryUseCase(repository)
        )
        RecurringEntryWorker.enqueue(this)
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
