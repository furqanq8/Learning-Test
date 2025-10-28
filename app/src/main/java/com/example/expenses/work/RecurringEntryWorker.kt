package com.example.expenses.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.expenses.data.local.ExpensesDatabase
import com.example.expenses.data.repository.ExpensesRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.TimeUnit

class RecurringEntryWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = ExpensesDatabase.getInstance(applicationContext)
        val repository = ExpensesRepositoryImpl(
            database.transactionDao(),
            database.categoryDao(),
            database.recurringDao(),
            database.budgetDao(),
            applicationContext
        )
        val recurringEntries = repository.observeRecurring().first()
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        recurringEntries.filter { it.active && it.nextExecution <= today }.forEach { recurring ->
            val base = repository.getTransaction(recurring.baseTransactionId) ?: return@forEach
            val newTransaction = base.copy(
                id = 0,
                isRecurringInstance = true,
                date = today
            )
            repository.addTransaction(newTransaction)
            val nextDate = when (recurring.interval) {
                com.example.expenses.domain.model.RecurrenceInterval.DAILY -> recurring.nextExecution.plus(1, DateTimeUnit.DAY)
                com.example.expenses.domain.model.RecurrenceInterval.WEEKLY -> recurring.nextExecution.plus(1, DateTimeUnit.WEEK)
                com.example.expenses.domain.model.RecurrenceInterval.MONTHLY -> recurring.nextExecution.plus(1, DateTimeUnit.MONTH)
                com.example.expenses.domain.model.RecurrenceInterval.YEARLY -> recurring.nextExecution.plus(1, DateTimeUnit.YEAR)
            }
            repository.upsertRecurring(recurring.copy(nextExecution = nextDate))
        }
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "recurring_entry_worker"

        fun enqueue(context: Context) {
            val workManager = WorkManager.getInstance(context)
            val workRequest = PeriodicWorkRequestBuilder<RecurringEntryWorker>(24, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()
            workManager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest)
        }
    }
}
