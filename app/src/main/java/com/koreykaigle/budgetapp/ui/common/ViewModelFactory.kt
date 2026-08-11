package com.koreykaigle.budgetapp.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.koreykaigle.budgetapp.BudgetApplication
import com.koreykaigle.budgetapp.data.BudgetRepository

/** Small manual-DI factory so every screen's ViewModel gets the shared repository
 *  without pulling in a DI framework. */
class SimpleViewModelFactory(
    private val repository: BudgetRepository,
    private val creator: (BudgetRepository) -> ViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = creator(repository) as T
}

@Composable
inline fun <reified T : ViewModel> repositoryViewModel(noinline creator: (BudgetRepository) -> T): T {
    val app = LocalContext.current.applicationContext as BudgetApplication
    return viewModel(factory = SimpleViewModelFactory(app.repository, creator))
}
