package com.example.expenses.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.expenses.data.local.converters.Converters
import com.example.expenses.data.local.dao.BudgetDao
import com.example.expenses.data.local.dao.CategoryDao
import com.example.expenses.data.local.dao.RecurringDao
import com.example.expenses.data.local.dao.TransactionDao
import com.example.expenses.data.local.entities.BudgetEntity
import com.example.expenses.data.local.entities.CategoryEntity
import com.example.expenses.data.local.entities.RecurringTransactionEntity
import com.example.expenses.data.local.entities.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        RecurringTransactionEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ExpensesDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun recurringDao(): RecurringDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: ExpensesDatabase? = null

        fun getInstance(context: Context): ExpensesDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): ExpensesDatabase =
            Room.databaseBuilder(
                context,
                ExpensesDatabase::class.java,
                "expenses.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}
