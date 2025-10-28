package com.example.expenses.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.expenses.domain.model.Transaction
import com.example.expenses.domain.model.TransactionType
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId"), Index("date")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val categoryId: Long,
    val amountMinor: Long,
    val currency: String,
    val date: LocalDate,
    val tags: List<String>,
    val notes: String?,
    val receiptUri: String?,
    val isRecurringInstance: Boolean
) {
    fun toDomain(category: CategoryEntity) = Transaction(
        id = id,
        type = type,
        category = category.toDomain(),
        amountMinor = amountMinor,
        currency = currency,
        date = date,
        tags = tags,
        notes = notes,
        receiptUri = receiptUri,
        isRecurringInstance = isRecurringInstance
    )

    companion object {
        fun fromDomain(transaction: Transaction) = TransactionEntity(
            id = transaction.id,
            type = transaction.type,
            categoryId = transaction.category.id,
            amountMinor = transaction.amountMinor,
            currency = transaction.currency,
            date = transaction.date,
            tags = transaction.tags,
            notes = transaction.notes,
            receiptUri = transaction.receiptUri,
            isRecurringInstance = transaction.isRecurringInstance
        )
    }
}
