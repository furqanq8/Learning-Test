package com.example.expenses.data.local.converters

import androidx.room.TypeConverter
import com.example.expenses.domain.model.TransactionType
import com.example.expenses.domain.model.CategoryType
import com.example.expenses.domain.model.RecurrenceInterval
import kotlinx.datetime.LocalDate

class Converters {
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromCategoryType(type: CategoryType): String = type.name

    @TypeConverter
    fun toCategoryType(value: String): CategoryType = CategoryType.valueOf(value)

    @TypeConverter
    fun fromRecurrence(interval: RecurrenceInterval): String = interval.name

    @TypeConverter
    fun toRecurrence(value: String): RecurrenceInterval = RecurrenceInterval.valueOf(value)

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)

    @TypeConverter
    fun fromTags(tags: List<String>): String = tags.joinToString(separator = "|")

    @TypeConverter
    fun toTags(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("|")
}
