package com.koreykaigle.budgetapp.data.dao

import androidx.room.*
import com.koreykaigle.budgetapp.data.entity.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY name ASC")
    fun getAll(): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): Account?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(account: Account): Long

    @Delete
    suspend fun delete(account: Account)
}
