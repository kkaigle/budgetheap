package com.koreykaigle.budgetapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(val route: String, val label: String, val icon: ImageVector) {
    data object Expenses : Destination("expenses", "Expenses", Icons.Outlined.ArrowCircleDown)
    data object Income : Destination("income", "Income", Icons.Outlined.ArrowCircleUp)
    data object Accounts : Destination("accounts", "Accounts", Icons.Filled.AccountBalance)
    data object Budget : Destination("budget", "Budget", Icons.Filled.Tune)
    data object Health : Destination("health", "Health", Icons.Filled.Favorite)

    companion object {
        val bottomNavItems = listOf(Expenses, Income, Accounts, Budget, Health)
    }
}
