package com.koreykaigle.budgetapp.ui.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koreykaigle.budgetapp.data.AccountKind
import com.koreykaigle.budgetapp.data.BudgetRepository
import com.koreykaigle.budgetapp.data.CategoryType
import com.koreykaigle.budgetapp.data.entity.Account
import com.koreykaigle.budgetapp.data.entity.BudgetTarget
import com.koreykaigle.budgetapp.data.entity.Category
import com.koreykaigle.budgetapp.data.entity.DeductionEntry
import com.koreykaigle.budgetapp.data.entity.ExpenseEntry
import com.koreykaigle.budgetapp.data.entity.IncomeEntry
import com.koreykaigle.budgetapp.util.monthlyAmount
import com.koreykaigle.budgetapp.util.toMonthlyFromPeriod
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CategoryBreakdown(val name: String, val monthlyAmount: Double)

data class BudgetAdherenceRow(
    val categoryName: String,
    val actual: Double,
    val target: Double,
    val overBudget: Boolean
)

data class HealthReport(
    val grossMonthlyIncome: Double = 0.0,
    val totalMonthlyDeductions: Double = 0.0,
    val netMonthlyIncome: Double = 0.0,
    val totalMonthlyExpenses: Double = 0.0,
    val monthlyCashFlow: Double = 0.0,
    val totalMonthlyContributions: Double = 0.0,
    val savingsRate: Double = 0.0,
    val totalAssets: Double = 0.0,
    val totalLiabilities: Double = 0.0,
    val netWorth: Double = 0.0,
    val assetAccounts: List<Account> = emptyList(),
    val liabilityAccounts: List<Account> = emptyList(),
    val expenseBreakdown: List<CategoryBreakdown> = emptyList(),
    val incomeBreakdown: List<CategoryBreakdown> = emptyList(),
    val budgetAdherence: List<BudgetAdherenceRow> = emptyList(),
    val hasAnyData: Boolean = false
)

private data class RawInputs(
    val income: List<IncomeEntry>,
    val expenses: List<ExpenseEntry>,
    val deductions: List<DeductionEntry>,
    val accounts: List<Account>,
    val categories: List<Category>
)

class HealthViewModel(repo: BudgetRepository) : ViewModel() {

    private val rawInputs = combine(
        repo.income(), repo.expenses(), repo.deductions(), repo.accounts(), repo.categories()
    ) { income, expenses, deductions, accounts, categories ->
        RawInputs(income, expenses, deductions, accounts, categories)
    }

    val report: StateFlow<HealthReport> = combine(rawInputs, repo.budgetTargets()) { inputs, targets ->
        buildReport(inputs, targets)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HealthReport())

    private fun buildReport(inputs: RawInputs, targets: List<BudgetTarget>): HealthReport {
        val (income, expenses, deductions, accounts, categories) = inputs
        val categoryById = categories.associateBy { it.id }

        val grossIncome = income.sumOf { monthlyAmount(it.amount, it.frequency) }
        val totalDeductions = deductions.sumOf { monthlyAmount(it.amount, it.frequency) }
        val netIncome = grossIncome - totalDeductions
        val totalExpenses = expenses.sumOf { monthlyAmount(it.amount, it.frequency) }
        val cashFlow = netIncome - totalExpenses

        val assetAccounts = accounts.filter { it.kind == AccountKind.ASSET }
        val liabilityAccounts = accounts.filter { it.kind == AccountKind.LIABILITY }
        val totalAssets = assetAccounts.sumOf { it.balance }
        val totalLiabilities = liabilityAccounts.sumOf { it.balance }
        val netWorth = totalAssets - totalLiabilities

        val totalContributions = assetAccounts.sumOf { monthlyAmount(it.contributionAmount ?: 0.0, it.contributionFrequency) }
        val savingsRate = if (netIncome > 0) (totalContributions / netIncome).coerceIn(0.0, 5.0) else 0.0

        val expenseBreakdown = expenses.groupBy { it.categoryId }
            .map { (catId, items) ->
                CategoryBreakdown(
                    name = categoryById[catId]?.name ?: "Uncategorized",
                    monthlyAmount = items.sumOf { monthlyAmount(it.amount, it.frequency) }
                )
            }
            .sortedByDescending { it.monthlyAmount }

        val incomeBreakdown = income.groupBy { it.categoryId }
            .map { (catId, items) ->
                CategoryBreakdown(
                    name = categoryById[catId]?.name ?: "Uncategorized",
                    monthlyAmount = items.sumOf { monthlyAmount(it.amount, it.frequency) }
                )
            }
            .sortedByDescending { it.monthlyAmount }

        val expenseActualByCategory = expenses.filter { it.categoryId != null }
            .groupBy { it.categoryId }
            .mapValues { (_, items) -> items.sumOf { monthlyAmount(it.amount, it.frequency) } }

        val budgetAdherence = targets.mapNotNull { target ->
            val category = categoryById[target.categoryId] ?: return@mapNotNull null
            if (category.type != CategoryType.EXPENSE) return@mapNotNull null
            val actual = expenseActualByCategory[target.categoryId] ?: 0.0
            val targetMonthly = toMonthlyFromPeriod(target.targetAmount, target.period)
            BudgetAdherenceRow(
                categoryName = category.name,
                actual = actual,
                target = targetMonthly,
                overBudget = actual > targetMonthly
            )
        }.sortedByDescending { it.actual / it.target.coerceAtLeast(0.01) }

        val hasAnyData = income.isNotEmpty() || expenses.isNotEmpty() || deductions.isNotEmpty() || accounts.isNotEmpty()

        return HealthReport(
            grossMonthlyIncome = grossIncome,
            totalMonthlyDeductions = totalDeductions,
            netMonthlyIncome = netIncome,
            totalMonthlyExpenses = totalExpenses,
            monthlyCashFlow = cashFlow,
            totalMonthlyContributions = totalContributions,
            savingsRate = savingsRate,
            totalAssets = totalAssets,
            totalLiabilities = totalLiabilities,
            netWorth = netWorth,
            assetAccounts = assetAccounts,
            liabilityAccounts = liabilityAccounts,
            expenseBreakdown = expenseBreakdown,
            incomeBreakdown = incomeBreakdown,
            budgetAdherence = budgetAdherence,
            hasAnyData = hasAnyData
        )
    }
}
