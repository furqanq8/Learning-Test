package com.example.expenses.domain.model

import kotlinx.datetime.LocalDate

data class Transaction(
    val id: Long = 0,
    val type: TransactionType,
    val category: Category,
    val amountMinor: Long,
    val currency: String,
    val date: LocalDate,
    val tags: List<String>,
    val notes: String?,
    val receiptUri: String?,
    val isRecurringInstance: Boolean,
)
