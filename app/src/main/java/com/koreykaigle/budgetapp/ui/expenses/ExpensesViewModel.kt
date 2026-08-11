package com.koreykaigle.budgetapp.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koreykaigle.budgetapp.data.BudgetRepository
import com.koreykaigle.budgetapp.data.CategoryType
import com.koreykaigle.budgetapp.data.OwnerType
import com.koreykaigle.budgetapp.data.entity.Category
import com.koreykaigle.budgetapp.data.entity.CustomField
import com.koreykaigle.budgetapp.data.entity.ExpenseEntry
import com.koreykaigle.budgetapp.ui.common.LineItemDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ExpenseRow(val entry: ExpenseEntry, val categoryName: String?)

class ExpensesViewModel(private val repo: BudgetRepository) : ViewModel() {

    val categories: StateFlow<List<Category>> = repo.categoriesByType(CategoryType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rows: StateFlow<List<ExpenseRow>> = combine(repo.expenses(), categories) { entries, cats ->
        val byId = cats.associateBy { it.id }
        entries.map { ExpenseRow(it, byId[it.categoryId]?.name) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun customFieldsFor(id: Long) = repo.customFieldsFor(OwnerType.EXPENSE, id)

    suspend fun draftFor(entry: ExpenseEntry): LineItemDraft {
        val fields = repo.customFieldsOnce(OwnerType.EXPENSE, entry.id).map { it.fieldName to it.fieldValue }
        return LineItemDraft(
            id = entry.id,
            name = entry.name,
            amount = entry.amount.toString(),
            categoryId = entry.categoryId,
            frequency = entry.frequency,
            notes = entry.notes ?: "",
            customFields = fields
        )
    }

    fun save(draft: LineItemDraft, newCategoryName: String?) {
        viewModelScope.launch {
            var categoryId = draft.categoryId
            if (newCategoryName != null) {
                categoryId = repo.upsertCategory(Category(name = newCategoryName, type = CategoryType.EXPENSE))
            }
            val amount = draft.amount.toDoubleOrNull() ?: return@launch
            val entry = ExpenseEntry(
                id = draft.id ?: 0,
                name = draft.name.trim(),
                amount = amount,
                categoryId = categoryId,
                frequency = draft.frequency,
                notes = draft.notes.ifBlank { null }
            )
            val newId = repo.upsertExpense(entry)
            val ownerId = if (draft.id != null) draft.id else newId
            repo.replaceCustomFields(OwnerType.EXPENSE, ownerId, draft.customFields)
        }
    }

    fun delete(entry: ExpenseEntry) {
        viewModelScope.launch { repo.deleteExpense(entry) }
    }
}
