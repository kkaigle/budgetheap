package com.koreykaigle.budgetapp.ui.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koreykaigle.budgetapp.data.BudgetRepository
import com.koreykaigle.budgetapp.data.CategoryType
import com.koreykaigle.budgetapp.data.OwnerType
import com.koreykaigle.budgetapp.data.entity.Category
import com.koreykaigle.budgetapp.data.entity.IncomeEntry
import com.koreykaigle.budgetapp.ui.common.LineItemDraft
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class IncomeRow(val entry: IncomeEntry, val categoryName: String?)

class IncomeViewModel(private val repo: BudgetRepository) : ViewModel() {

    val categories: StateFlow<List<Category>> = repo.categoriesByType(CategoryType.INCOME)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rows: StateFlow<List<IncomeRow>> = combine(repo.income(), categories) { entries, cats ->
        val byId = cats.associateBy { it.id }
        entries.map { IncomeRow(it, byId[it.categoryId]?.name) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun draftFor(entry: IncomeEntry): LineItemDraft {
        val fields = repo.customFieldsOnce(OwnerType.INCOME, entry.id).map { it.fieldName to it.fieldValue }
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
                categoryId = repo.upsertCategory(Category(name = newCategoryName, type = CategoryType.INCOME))
            }
            val amount = draft.amount.toDoubleOrNull() ?: return@launch
            val entry = IncomeEntry(
                id = draft.id ?: 0,
                name = draft.name.trim(),
                amount = amount,
                categoryId = categoryId,
                frequency = draft.frequency,
                notes = draft.notes.ifBlank { null }
            )
            val newId = repo.upsertIncome(entry)
            val ownerId = draft.id ?: newId
            repo.replaceCustomFields(OwnerType.INCOME, ownerId, draft.customFields)
        }
    }

    fun delete(entry: IncomeEntry) {
        viewModelScope.launch { repo.deleteIncome(entry) }
    }
}
