package com.koreykaigle.budgetapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.koreykaigle.budgetapp.ui.accounts.AccountsScreen
import com.koreykaigle.budgetapp.ui.budget.BudgetScreen
import com.koreykaigle.budgetapp.ui.expenses.ExpensesScreen
import com.koreykaigle.budgetapp.ui.health.HealthScreen
import com.koreykaigle.budgetapp.ui.income.IncomeScreen

@Composable
fun BudgetNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                Destination.bottomNavItems.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Expenses.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Expenses.route) { ExpensesScreen() }
            composable(Destination.Income.route) { IncomeScreen() }
            composable(Destination.Accounts.route) { AccountsScreen() }
            composable(Destination.Budget.route) { BudgetScreen() }
            composable(Destination.Health.route) { HealthScreen() }
        }
    }
}
