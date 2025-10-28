package com.example.expenses.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expenses.data.local.entities.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface RecurringDao {
    @Query("SELECT * FROM recurring_transactions WHERE active = 1")
    suspend fun getActive(): List<RecurringTransactionEntity>

    @Query("SELECT * FROM recurring_transactions WHERE active = 1")
    fun observeActive(): Flow<List<RecurringTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecurringTransactionEntity): Long

    @Update
    suspend fun update(entity: RecurringTransactionEntity)

    @Delete
    suspend fun delete(entity: RecurringTransactionEntity)

    @Query("UPDATE recurring_transactions SET nextExecution = :nextExecution WHERE id = :id")
    suspend fun updateNextExecution(id: Long, nextExecution: LocalDate)
}
