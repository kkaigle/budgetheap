package com.koreykaigle.budgetapp.data.dao

import androidx.room.*
import com.koreykaigle.budgetapp.data.entity.IncomeEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income_entries ORDER BY date DESC")
    fun getAll(): Flow<List<IncomeEntry>>

    @Query("SELECT * FROM income_entries WHERE id = :id")
    suspend fun getById(id: Long): IncomeEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: IncomeEntry): Long

    @Delete
    suspend fun delete(entry: IncomeEntry)
}
