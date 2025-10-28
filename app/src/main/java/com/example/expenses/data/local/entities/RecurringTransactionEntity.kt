package com.example.expenses.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expenses.domain.model.RecurrenceInterval
import com.example.expenses.domain.model.RecurringTransaction
import kotlinx.datetime.LocalDate

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val baseTransactionId: Long,
    val nextExecution: LocalDate,
    val interval: RecurrenceInterval,
    val active: Boolean
) {
    fun toDomain() = RecurringTransaction(
        id = id,
        baseTransactionId = baseTransactionId,
        nextExecution = nextExecution,
        interval = interval,
        active = active
    )

    companion object {
        fun fromDomain(recurring: RecurringTransaction) = RecurringTransactionEntity(
            id = recurring.id,
            baseTransactionId = recurring.baseTransactionId,
            nextExecution = recurring.nextExecution,
            interval = recurring.interval,
            active = recurring.active
        )
    }
}
