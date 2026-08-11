package com.koreykaigle.budgetapp.ui.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koreykaigle.budgetapp.data.entity.ExpenseEntry
import com.koreykaigle.budgetapp.ui.common.EmptyState
import com.koreykaigle.budgetapp.ui.common.LineItemCard
import com.koreykaigle.budgetapp.ui.common.LineItemDraft
import com.koreykaigle.budgetapp.ui.common.LineItemEditorSheet
import com.koreykaigle.budgetapp.ui.common.rememberReceiptScanner
import com.koreykaigle.budgetapp.ui.common.repositoryViewModel
import com.koreykaigle.budgetapp.util.formatCurrency
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen() {
    val viewModel = repositoryViewModel { ExpensesViewModel(it) }
    val rows by viewModel.rows.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var editingDraft by remember { mutableStateOf<LineItemDraft?>(null) }
    var editingEntry by remember { mutableStateOf<ExpenseEntry?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    val total = rows.sumOf { it.entry.amount }

    val scanReceipt = rememberReceiptScanner { result ->
        editingEntry = null
        editingDraft = LineItemDraft(
            name = result.suggestedName,
            amount = result.suggestedAmount,
            notes = if (result.rawText.isNotBlank()) "Scanned text (double-check the amount above):\n${result.rawText.take(400)}" else ""
        )
        showEditor = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expenses · ${formatCurrency(total)}") },
                actions = {
                    IconButton(onClick = scanReceipt) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = "Scan a receipt")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingEntry = null
                editingDraft = LineItemDraft()
                showEditor = true
            }) { Icon(Icons.Filled.Add, contentDescription = "Add expense") }
        }
    ) { padding ->
        if (rows.isEmpty()) {
            EmptyState("No expenses yet. Tap + to add your first one.", modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Box(Modifier.padding(top = 8.dp)) }
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
    }

    if (showEditor && editingDraft != null) {
        LineItemEditorSheet(
            title = if (editingEntry == null) "Add expense" else "Edit expense",
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
