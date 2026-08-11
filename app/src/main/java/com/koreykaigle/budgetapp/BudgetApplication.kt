package com.koreykaigle.budgetapp

import android.app.Application
import com.koreykaigle.budgetapp.data.AppDatabase
import com.koreykaigle.budgetapp.data.BudgetRepository

class BudgetApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: BudgetRepository by lazy { BudgetRepository(database) }
}
