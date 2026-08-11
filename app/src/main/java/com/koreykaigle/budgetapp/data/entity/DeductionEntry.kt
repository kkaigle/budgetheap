package com.koreykaigle.budgetapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.koreykaigle.budgetapp.data.Frequency

/**
 * Recurring paycheck deductions (taxes withheld, pre-tax retirement contributions,
 * insurance premiums, etc.) tracked separately from expenses so the health report
 * can show gross income -> deductions -> net (take-home) income -> expenses.
 */
@Entity(tableName = "deduction_entries")
data class DeductionEntry(
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
