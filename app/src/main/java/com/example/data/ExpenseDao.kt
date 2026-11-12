package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY isToday DESC, sortOrder ASC, timestamp DESC")
    fun getAllExpensesFlow(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses ORDER BY isToday DESC, sortOrder ASC, timestamp DESC")
    suspend fun getAllExpensesOnce(): List<ExpenseEntity>

    @Query("SELECT COUNT(*) FROM expenses")
    suspend fun getExpenseCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<ExpenseEntity>)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses")
    suspend fun clearAllExpenses()
}
