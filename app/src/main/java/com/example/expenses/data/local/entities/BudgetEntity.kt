package com.example.expenses.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expenses.domain.model.Budget

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val limitMinor: Long,
    val month: Int,
    val year: Int
) {
    fun toDomain() = Budget(id, categoryId, limitMinor, month, year)

    companion object {
        fun fromDomain(budget: Budget) = BudgetEntity(
            id = budget.id,
            categoryId = budget.categoryId,
            limitMinor = budget.limitMinor,
            month = budget.month,
            year = budget.year
        )
    }
}
