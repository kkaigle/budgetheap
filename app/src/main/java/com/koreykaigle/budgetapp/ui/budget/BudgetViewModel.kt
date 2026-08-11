package com.koreykaigle.budgetapp.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koreykaigle.budgetapp.data.BudgetPeriod
import com.koreykaigle.budgetapp.data.BudgetRepository
import com.koreykaigle.budgetapp.data.CategoryType
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
import kotlinx.coroutines.launch

/** One row per category the user has actually created: how much is planned
 *  (actual monthly total from their entries) vs. the slider target they set. */
data class BudgetRow(
    val category: Category,
    val actualMonthly: Double,
    val targetMonthly: Double?,
    val targetId: Long?
)

class BudgetViewModel(private val repo: BudgetRepository) : ViewModel() {

    val rows: StateFlow<List<BudgetRow>> = combine(
        repo.categories(),
        repo.income(),
        repo.expenses(),
        repo.deductions(),
        repo.budgetTargets()
    ) { categories: List<Category>, income: List<IncomeEntry>, expenses: List<ExpenseEntry>, deductions: List<DeductionEntry>, targets: List<BudgetTarget> ->
        val targetByCategory = targets.associateBy { it.categoryId }

        categories.sortedBy { it.name }.map { category ->
            val actual = when (category.type) {
                CategoryType.INCOME -> income.filter { it.categoryId == category.id }
                    .sumOf { monthlyAmount(it.amount, it.frequency) }
                CategoryType.EXPENSE -> expenses.filter { it.categoryId == category.id }
                    .sumOf { monthlyAmount(it.amount, it.frequency) }
                CategoryType.DEDUCTION -> deductions.filter { it.categoryId == category.id }
                    .sumOf { monthlyAmount(it.amount, it.frequency) }
            }
            val target = targetByCategory[category.id]
            BudgetRow(
                category = category,
                actualMonthly = actual,
                targetMonthly = target?.let { toMonthlyFromPeriod(it.targetAmount, it.period) },
                targetId = target?.id
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTarget(categoryId: Long, monthlyAmount: Double, existingTargetId: Long?) {
        viewModelScope.launch {
            repo.upsertBudgetTarget(
                BudgetTarget(
                    id = existingTargetId ?: 0,
                    categoryId = categoryId,
                    targetAmount = monthlyAmount,
                    period = BudgetPeriod.MONTHLY
                )
            )
        }
    }

    fun clearTarget(categoryId: Long) {
        viewModelScope.launch { repo.deleteBudgetTargetForCategory(categoryId) }
    }
}
