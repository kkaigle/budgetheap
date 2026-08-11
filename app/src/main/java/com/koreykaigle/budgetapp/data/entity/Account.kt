package com.koreykaigle.budgetapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.koreykaigle.budgetapp.data.AccountKind
import com.koreykaigle.budgetapp.data.Frequency

/**
 * Balance-bearing accounts: savings, retirement (401k/IRA/pension), investments,
 * debts, loans, etc. [kind] decides whether the balance adds to or subtracts from
 * net worth; [label] is a free-text field so people can call it whatever they want
 * ("Roth IRA", "Emergency Fund", "Car Loan"...).
 */
@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val kind: AccountKind,
    val label: String? = null,
    val balance: Double = 0.0,
    val contributionAmount: Double? = null,
    val contributionFrequency: Frequency? = null,
    val interestRatePct: Double? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
