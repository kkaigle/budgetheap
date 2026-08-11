package com.koreykaigle.budgetapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.koreykaigle.budgetapp.data.OwnerType

/**
 * A completely free-form key/value attribute attached to any income, expense,
 * deduction, or account row. This is what makes the app "fully customizable" --
 * instead of a fixed set of extra columns, a user can add as many named fields
 * (e.g. "Vendor", "Payment method", "Due day", "Priority") as they want to any entry.
 */
@Entity(tableName = "custom_fields")
data class CustomField(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerType: OwnerType,
    val ownerId: Long,
    val fieldName: String,
    val fieldValue: String
)
