package com.koreykaigle.budgetapp.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koreykaigle.budgetapp.data.BudgetRepository
import com.koreykaigle.budgetapp.data.CategoryType
import com.koreykaigle.budgetapp.data.OwnerType
import com.koreykaigle.budgetapp.data.entity.Category
import com.koreykaigle.budgetapp.data.entity.DeductionEntry
import com.koreykaigle.budgetapp.ui.common.LineItemDraft
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DeductionRow(val entry: DeductionEntry, val categoryName: String?)

/** Paycheck deductions -- taxes withheld, pre-tax retirement, insurance premiums --
 *  tracked separately from expenses so gross-to-net income math is accurate. */
class DeductionsViewModel(private val repo: BudgetRepository) : ViewModel() {

    val categories: StateFlow<List<Category>> = repo.categoriesByType(CategoryType.DEDUCTION)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rows: StateFlow<List<DeductionRow>> = combine(repo.deductions(), categories) { entries, cats ->
        val byId = cats.associateBy { it.id }
        entries.map { DeductionRow(it, byId[it.categoryId]?.name) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun draftFor(entry: DeductionEntry): LineItemDraft {
        val fields = repo.customFieldsOnce(OwnerType.DEDUCTION, entry.id).map { it.fieldName to it.fieldValue }
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
                categoryId = repo.upsertCategory(Category(name = newCategoryName, type = CategoryType.DEDUCTION))
            }
            val amount = draft.amount.toDoubleOrNull() ?: return@launch
            val entry = DeductionEntry(
                id = draft.id ?: 0,
                name = draft.name.trim(),
                amount = amount,
                categoryId = categoryId,
                frequency = draft.frequency,
                notes = draft.notes.ifBlank { null }
            )
            val newId = repo.upsertDeduction(entry)
            val ownerId = draft.id ?: newId
            repo.replaceCustomFields(OwnerType.DEDUCTION, ownerId, draft.customFields)
        }
    }

    fun delete(entry: DeductionEntry) {
        viewModelScope.launch { repo.deleteDeduction(entry) }
    }
}
