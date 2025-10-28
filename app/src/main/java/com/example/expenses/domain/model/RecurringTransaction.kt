package com.example.expenses.domain.model

import kotlinx.datetime.LocalDate

enum class RecurrenceInterval {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

data class RecurringTransaction(
    val id: Long = 0,
    val baseTransactionId: Long,
    val nextExecution: LocalDate,
    val interval: RecurrenceInterval,
    val active: Boolean = true
)
