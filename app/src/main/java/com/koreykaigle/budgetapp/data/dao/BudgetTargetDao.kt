package com.koreykaigle.budgetapp.data.dao

import androidx.room.*
import com.koreykaigle.budgetapp.data.entity.BudgetTarget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetTargetDao {
    @Query("SELECT * FROM budget_targets")
    fun getAll(): Flow<List<BudgetTarget>>

    @Query("SELECT * FROM budget_targets WHERE categoryId = :categoryId LIMIT 1")
    suspend fun getForCategory(categoryId: Long): BudgetTarget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(target: BudgetTarget): Long

    @Delete
    suspend fun delete(target: BudgetTarget)

    @Query("DELETE FROM budget_targets WHERE categoryId = :categoryId")
    suspend fun deleteForCategory(categoryId: Long)
}
