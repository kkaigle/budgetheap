package com.koreykaigle.budgetapp.data.dao

import androidx.room.*
import com.koreykaigle.budgetapp.data.CategoryType
import com.koreykaigle.budgetapp.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY sortOrder ASC, name ASC")
    fun getByType(type: CategoryType): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: Category): Long

    @Delete
    suspend fun delete(category: Category)
}
