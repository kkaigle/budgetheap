package com.koreykaigle.budgetapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.koreykaigle.budgetapp.data.CategoryType

/**
 * Fully user-defined categories. Nothing is preloaded — people build their own
 * category list for income, expenses, and deductions from scratch.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val colorHex: String? = null,
    val sortOrder: Int = 0
)
