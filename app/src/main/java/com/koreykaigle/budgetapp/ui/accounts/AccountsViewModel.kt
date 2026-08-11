package com.koreykaigle.budgetapp.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koreykaigle.budgetapp.data.AccountKind
import com.koreykaigle.budgetapp.data.BudgetRepository
import com.koreykaigle.budgetapp.data.Frequency
import com.koreykaigle.budgetapp.data.OwnerType
import com.koreykaigle.budgetapp.data.entity.Account
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Draft used by the account editor. Only [name] is required -- balance defaults
 *  to zero and everything else (label, contribution, interest, notes) is optional,
 *  covering savings, retirement, investment, and debt accounts alike. */
data class AccountDraft(
    val id: Long? = null,
    val name: String = "",
    val kind: AccountKind = AccountKind.ASSET,
    val label: String = "",
    val balance: String = "",
    val contributionAmount: String = "",
    val contributionFrequency: Frequency? = null,
    val interestRatePct: String = "",
    val notes: String = "",
    val customFields: List<Pair<String, String>> = emptyList()
)

class AccountsViewModel(private val repo: BudgetRepository) : ViewModel() {

    val accounts: StateFlow<List<Account>> = repo.accounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun draftFor(account: Account): AccountDraft {
        val fields = repo.customFieldsOnce(OwnerType.ACCOUNT, account.id).map { it.fieldName to it.fieldValue }
        return AccountDraft(
            id = account.id,
            name = account.name,
            kind = account.kind,
            label = account.label ?: "",
            balance = account.balance.toString(),
            contributionAmount = account.contributionAmount?.toString() ?: "",
            contributionFrequency = account.contributionFrequency,
            interestRatePct = account.interestRatePct?.toString() ?: "",
            notes = account.notes ?: "",
            customFields = fields
        )
    }

    fun save(draft: AccountDraft) {
        viewModelScope.launch {
            val account = Account(
                id = draft.id ?: 0,
                name = draft.name.trim(),
                kind = draft.kind,
                label = draft.label.ifBlank { null },
                balance = draft.balance.toDoubleOrNull() ?: 0.0,
                contributionAmount = draft.contributionAmount.toDoubleOrNull(),
                contributionFrequency = draft.contributionFrequency,
                interestRatePct = draft.interestRatePct.toDoubleOrNull(),
                notes = draft.notes.ifBlank { null }
            )
            val newId = repo.upsertAccount(account)
            val ownerId = draft.id ?: newId
            repo.replaceCustomFields(OwnerType.ACCOUNT, ownerId, draft.customFields)
        }
    }

    fun delete(account: Account) {
        viewModelScope.launch { repo.deleteAccount(account) }
    }
}
