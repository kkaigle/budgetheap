package com.koreykaigle.budgetapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.koreykaigle.budgetapp.data.Frequency

/**
 * A single expense line item. Same philosophy as [IncomeEntry]: only name + amount
 * are required, everything else is optional. [linkedAccountId] is reserved for a
 * future feature that lets an expense double as a contribution toward a savings
 * or retirement account.
 */
@Entity(tableName = "expense_entries")
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val categoryId: Long? = null,
    val frequency: Frequency? = null,
    val date: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val isActive: Boolean = true,
    val linkedAccountId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
