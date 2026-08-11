package com.koreykaigle.budgetapp.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koreykaigle.budgetapp.data.AccountKind
import com.koreykaigle.budgetapp.data.entity.Account
import com.koreykaigle.budgetapp.data.entity.DeductionEntry
import com.koreykaigle.budgetapp.ui.common.EmptyState
import com.koreykaigle.budgetapp.ui.common.LineItemCard
import com.koreykaigle.budgetapp.ui.common.LineItemDraft
import com.koreykaigle.budgetapp.ui.common.LineItemEditorSheet
import com.koreykaigle.budgetapp.ui.common.repositoryViewModel
import com.koreykaigle.budgetapp.util.formatCurrency
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen() {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Accounts", "Deductions")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Accounts") })
                TabRow(selectedTabIndex = tabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(title) })
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (tabIndex == 0) AccountsList() else DeductionsList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountsList() {
    val viewModel = repositoryViewModel { AccountsViewModel(it) }
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var editingDraft by remember { mutableStateOf<AccountDraft?>(null) }
    var editingAccount by remember { mutableStateOf<Account?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (accounts.isEmpty()) {
            EmptyState("No accounts yet. Track savings, retirement, investments, or debts here.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Box(Modifier.padding(top = 8.dp)) }
                items(accounts, key = { it.id }) { account ->
                    val signedBalance = if (account.kind == AccountKind.LIABILITY) -account.balance else account.balance
                    val subtitle = listOfNotNull(
                        account.label,
                        if (account.kind == AccountKind.ASSET) "Asset" else "Liability"
                    ).joinToString(" · ")
                    LineItemCard(
                        name = account.name,
                        amount = signedBalance,
                        subtitle = subtitle,
                        onClick = {
                            scope.launch {
                                editingAccount = account
                                editingDraft = viewModel.draftFor(account)
                                showEditor = true
                            }
                        }
                    )
                }
                item { Box(Modifier.padding(bottom = 80.dp)) }
            }
        }

        FloatingActionButton(
            onClick = {
                editingAccount = null
                editingDraft = AccountDraft()
                showEditor = true
            },
            modifier = Modifier
                .padding(20.dp)
                .align(androidx.compose.ui.Alignment.BottomEnd)
        ) { Icon(Icons.Filled.Add, contentDescription = "Add account") }
    }

    if (showEditor && editingDraft != null) {
        AccountEditorSheet(
            initial = editingDraft!!,
            onDismiss = { showEditor = false },
            onSave = { draft -> viewModel.save(draft); showEditor = false },
            onDelete = editingAccount?.let { acc -> { viewModel.delete(acc); showEditor = false } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeductionsList() {
    val viewModel = repositoryViewModel { DeductionsViewModel(it) }
    val rows by viewModel.rows.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var editingDraft by remember { mutableStateOf<LineItemDraft?>(null) }
    var editingEntry by remember { mutableStateOf<DeductionEntry?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    val total = rows.sumOf { it.entry.amount }

    Box(modifier = Modifier.fillMaxSize()) {
        if (rows.isEmpty()) {
            EmptyState("No deductions yet. Add taxes, insurance, or pre-tax retirement withheld from your paycheck.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Text("Total deductions: ${formatCurrency(total)}", modifier = Modifier.padding(vertical = 8.dp)) }
                items(rows, key = { it.entry.id }) { row ->
                    val subtitle = listOfNotNull(row.categoryName, row.entry.frequency?.label).joinToString(" · ")
                    LineItemCard(
                        name = row.entry.name,
                        amount = row.entry.amount,
                        subtitle = subtitle.ifBlank { null },
                        onClick = {
                            scope.launch {
                                editingEntry = row.entry
                                editingDraft = viewModel.draftFor(row.entry)
                                showEditor = true
                            }
                        }
                    )
                }
                item { Box(Modifier.padding(bottom = 80.dp)) }
            }
        }

        FloatingActionButton(
            onClick = {
                editingEntry = null
                editingDraft = LineItemDraft()
                showEditor = true
            },
            modifier = Modifier
                .padding(20.dp)
                .align(androidx.compose.ui.Alignment.BottomEnd)
        ) { Icon(Icons.Filled.Add, contentDescription = "Add deduction") }
    }

    if (showEditor && editingDraft != null) {
        LineItemEditorSheet(
            title = if (editingEntry == null) "Add deduction" else "Edit deduction",
            categories = categories,
            initial = editingDraft!!,
            onDismiss = { showEditor = false },
            onSave = { draft, newCategoryName ->
                viewModel.save(draft, newCategoryName)
                showEditor = false
            },
            onDelete = editingEntry?.let { entry -> { viewModel.delete(entry); showEditor = false } }
        )
    }
}
