package com.example.expenses.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val isCustom: Boolean = false
)
