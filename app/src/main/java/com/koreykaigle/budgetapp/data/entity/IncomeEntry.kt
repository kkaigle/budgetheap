package com.koreykaigle.budgetapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.koreykaigle.budgetapp.data.Frequency

/**
 * A single income line item. Only [name] and [amount] are required to save a row —
 * category, frequency, date, and notes are all optional so people aren't forced
 * into a schema that doesn't fit how they earn money.
 */
@Entity(tableName = "income_entries")
data class IncomeEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val categoryId: Long? = null,
    val frequency: Frequency? = null,
    val date: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
