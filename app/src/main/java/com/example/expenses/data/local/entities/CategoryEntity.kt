package com.example.expenses.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expenses.domain.model.Category
import com.example.expenses.domain.model.CategoryType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val isCustom: Boolean = false
) {
    fun toDomain() = Category(id = id, name = name, type = type, isCustom = isCustom)

    companion object {
        fun fromDomain(category: Category) = CategoryEntity(
            id = category.id,
            name = category.name,
            type = category.type,
            isCustom = category.isCustom
        )
    }
}
