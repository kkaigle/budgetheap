package com.koreykaigle.budgetapp.data.dao

import androidx.room.*
import com.koreykaigle.budgetapp.data.entity.ExpenseEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense_entries ORDER BY date DESC")
    fun getAll(): Flow<List<ExpenseEntry>>

    @Query("SELECT * FROM expense_entries WHERE id = :id")
    suspend fun getById(id: Long): ExpenseEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: ExpenseEntry): Long

    @Delete
    suspend fun delete(entry: ExpenseEntry)
}
