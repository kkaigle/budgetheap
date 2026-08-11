package com.koreykaigle.budgetapp.data

import com.koreykaigle.budgetapp.data.entity.Account
import com.koreykaigle.budgetapp.data.entity.BudgetTarget
import com.koreykaigle.budgetapp.data.entity.Category
import com.koreykaigle.budgetapp.data.entity.CustomField
import com.koreykaigle.budgetapp.data.entity.DeductionEntry
import com.koreykaigle.budgetapp.data.entity.ExpenseEntry
import com.koreykaigle.budgetapp.data.entity.IncomeEntry
import kotlinx.coroutines.flow.Flow

/**
 * Single access point for every table. Thin on purpose -- aggregation/health-math
 * lives in the view models that combine these flows, so the storage layer stays
 * simple and each screen can decide how to interpret the data.
 */
class BudgetRepository(private val db: AppDatabase) {

    // Categories
    fun categories(): Flow<List<Category>> = db.categoryDao().getAll()
    fun categoriesByType(type: CategoryType): Flow<List<Category>> = db.categoryDao().getByType(type)
    suspend fun upsertCategory(category: Category): Long = db.categoryDao().upsert(category)
    suspend fun deleteCategory(category: Category) = db.categoryDao().delete(category)

    // Income
    fun income(): Flow<List<IncomeEntry>> = db.incomeDao().getAll()
    suspend fun upsertIncome(entry: IncomeEntry): Long = db.incomeDao().upsert(entry)
    suspend fun deleteIncome(entry: IncomeEntry) = db.incomeDao().delete(entry)

    // Expenses
    fun expenses(): Flow<List<ExpenseEntry>> = db.expenseDao().getAll()
    suspend fun upsertExpense(entry: ExpenseEntry): Long = db.expenseDao().upsert(entry)
    suspend fun deleteExpense(entry: ExpenseEntry) = db.expenseDao().delete(entry)

    // Deductions
    fun deductions(): Flow<List<DeductionEntry>> = db.deductionDao().getAll()
    suspend fun upsertDeduction(entry: DeductionEntry): Long = db.deductionDao().upsert(entry)
    suspend fun deleteDeduction(entry: DeductionEntry) = db.deductionDao().delete(entry)

    // Accounts
    fun accounts(): Flow<List<Account>> = db.accountDao().getAll()
    suspend fun upsertAccount(account: Account): Long = db.accountDao().upsert(account)
    suspend fun deleteAccount(account: Account) = db.accountDao().delete(account)

    // Budget targets
    fun budgetTargets(): Flow<List<BudgetTarget>> = db.budgetTargetDao().getAll()
    suspend fun upsertBudgetTarget(target: BudgetTarget): Long = db.budgetTargetDao().upsert(target)
    suspend fun deleteBudgetTargetForCategory(categoryId: Long) = db.budgetTargetDao().deleteForCategory(categoryId)

    // Custom fields
    fun customFieldsFor(ownerType: OwnerType, ownerId: Long): Flow<List<CustomField>> =
        db.customFieldDao().getForOwner(ownerType, ownerId)
    suspend fun customFieldsOnce(ownerType: OwnerType, ownerId: Long): List<CustomField> =
        db.customFieldDao().getForOwnerOnce(ownerType, ownerId)
    suspend fun upsertCustomField(field: CustomField): Long = db.customFieldDao().upsert(field)
    suspend fun deleteCustomField(field: CustomField) = db.customFieldDao().delete(field)
    suspend fun replaceCustomFields(ownerType: OwnerType, ownerId: Long, fields: List<Pair<String, String>>) {
        db.customFieldDao().deleteForOwner(ownerType, ownerId)
        fields.filter { it.first.isNotBlank() }.forEach { (name, value) ->
            db.customFieldDao().upsert(CustomField(ownerType = ownerType, ownerId = ownerId, fieldName = name, fieldValue = value))
        }
    }
}
