package com.koreykaigle.budgetapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.koreykaigle.budgetapp.data.BudgetPeriod

/**
 * The slider value a user sets for one category: "I want to spend/earn at most/at
 * least this much per [period]". One row per category.
 */
@Entity(tableName = "budget_targets")
data class BudgetTarget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val targetAmount: Double,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val updatedAt: Long = System.currentTimeMillis()
)
