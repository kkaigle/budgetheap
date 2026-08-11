package com.koreykaigle.budgetapp.data.dao

import androidx.room.*
import com.koreykaigle.budgetapp.data.entity.DeductionEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DeductionDao {
    @Query("SELECT * FROM deduction_entries ORDER BY date DESC")
    fun getAll(): Flow<List<DeductionEntry>>

    @Query("SELECT * FROM deduction_entries WHERE id = :id")
    suspend fun getById(id: Long): DeductionEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: DeductionEntry): Long

    @Delete
    suspend fun delete(entry: DeductionEntry)
}
