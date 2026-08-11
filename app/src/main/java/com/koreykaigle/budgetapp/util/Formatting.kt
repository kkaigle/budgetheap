package com.koreykaigle.budgetapp.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
private val monthFormat = SimpleDateFormat("MMM yyyy", Locale.US)

fun formatCurrency(amount: Double): String = currencyFormat.format(amount)

fun formatCurrencyCompact(amount: Double): String {
    val absAmount = kotlin.math.abs(amount)
    val sign = if (amount < 0) "-" else ""
    return when {
        absAmount >= 1_000_000 -> "$sign$${"%.1f".format(absAmount / 1_000_000)}M"
        absAmount >= 1_000 -> "$sign$${"%.1f".format(absAmount / 1_000)}K"
        else -> formatCurrency(amount)
    }
}

fun formatDate(millis: Long): String = dateFormat.format(Date(millis))

fun formatMonth(millis: Long): String = monthFormat.format(Date(millis))

fun formatPercent(fraction: Double): String = "${(fraction * 100).roundToInt()}%"
