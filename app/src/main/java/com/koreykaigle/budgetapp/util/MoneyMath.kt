package com.koreykaigle.budgetapp.util

import com.koreykaigle.budgetapp.data.BudgetPeriod
import com.koreykaigle.budgetapp.data.Frequency

/**
 * Normalizes any recurring amount to a monthly figure so items with different
 * frequencies (weekly rent vs. annual insurance vs. a one-time purchase) can be
 * compared and summed on the same basis throughout the app.
 *
 * A null/one-time frequency is treated as a single occurrence that only counts
 * toward totals for the month it happened in -- callers decide that filtering;
 * this function just returns the amount unmodified for ONE_TIME/null.
 */
fun monthlyAmount(amount: Double, frequency: Frequency?): Double {
    val f = frequency ?: return amount
    return if (f == Frequency.ONE_TIME) amount else (amount * f.perYear) / 12.0
}

fun periodAmount(monthly: Double, period: BudgetPeriod): Double = when (period) {
    BudgetPeriod.WEEKLY -> monthly * 12.0 / 52.0
    BudgetPeriod.MONTHLY -> monthly
    BudgetPeriod.ANNUALLY -> monthly * 12.0
}

fun toMonthlyFromPeriod(amount: Double, period: BudgetPeriod): Double = when (period) {
    BudgetPeriod.WEEKLY -> amount * 52.0 / 12.0
    BudgetPeriod.MONTHLY -> amount
    BudgetPeriod.ANNUALLY -> amount / 12.0
}
