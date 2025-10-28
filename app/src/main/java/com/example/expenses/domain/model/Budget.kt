package com.example.expenses.domain.model

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val limitMinor: Long,
    val month: Int,
    val year: Int
)
