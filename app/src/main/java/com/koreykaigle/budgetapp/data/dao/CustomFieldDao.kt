package com.koreykaigle.budgetapp.data.dao

import androidx.room.*
import com.koreykaigle.budgetapp.data.OwnerType
import com.koreykaigle.budgetapp.data.entity.CustomField
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomFieldDao {
    @Query("SELECT * FROM custom_fields")
    fun getAll(): Flow<List<CustomField>>

    @Query("SELECT * FROM custom_fields WHERE ownerType = :ownerType AND ownerId = :ownerId")
    fun getForOwner(ownerType: OwnerType, ownerId: Long): Flow<List<CustomField>>

    @Query("SELECT * FROM custom_fields WHERE ownerType = :ownerType AND ownerId = :ownerId")
    suspend fun getForOwnerOnce(ownerType: OwnerType, ownerId: Long): List<CustomField>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(field: CustomField): Long

    @Delete
    suspend fun delete(field: CustomField)

    @Query("DELETE FROM custom_fields WHERE ownerType = :ownerType AND ownerId = :ownerId")
    suspend fun deleteForOwner(ownerType: OwnerType, ownerId: Long)
}
