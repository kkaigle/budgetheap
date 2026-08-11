package com.koreykaigle.budgetapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.koreykaigle.budgetapp.data.dao.AccountDao
import com.koreykaigle.budgetapp.data.dao.BudgetTargetDao
import com.koreykaigle.budgetapp.data.dao.CategoryDao
import com.koreykaigle.budgetapp.data.dao.CustomFieldDao
import com.koreykaigle.budgetapp.data.dao.DeductionDao
import com.koreykaigle.budgetapp.data.dao.ExpenseDao
import com.koreykaigle.budgetapp.data.dao.IncomeDao
import com.koreykaigle.budgetapp.data.entity.Account
import com.koreykaigle.budgetapp.data.entity.BudgetTarget
import com.koreykaigle.budgetapp.data.entity.Category
import com.koreykaigle.budgetapp.data.entity.CustomField
import com.koreykaigle.budgetapp.data.entity.DeductionEntry
import com.koreykaigle.budgetapp.data.entity.ExpenseEntry
import com.koreykaigle.budgetapp.data.entity.IncomeEntry

@Database(
    entities = [
        Category::class,
        IncomeEntry::class,
        ExpenseEntry::class,
        DeductionEntry::class,
        Account::class,
        BudgetTarget::class,
        CustomField::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun deductionDao(): DeductionDao
    abstract fun accountDao(): AccountDao
    abstract fun budgetTargetDao(): BudgetTargetDao
    abstract fun customFieldDao(): CustomFieldDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_app.db"
                )
                    // Early-stage app: fall back to rebuilding the DB on schema bumps
                    // rather than requiring hand-written migrations for every change.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
