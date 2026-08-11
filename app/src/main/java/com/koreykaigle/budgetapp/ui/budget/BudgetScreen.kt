package com.koreykaigle.budgetapp.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koreykaigle.budgetapp.data.CategoryType
import com.koreykaigle.budgetapp.ui.common.EmptyState
import com.koreykaigle.budgetapp.ui.common.repositoryViewModel
import com.koreykaigle.budgetapp.util.formatCurrency
import kotlin.math.ceil
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen() {
    val viewModel = repositoryViewModel { BudgetViewModel(it) }
    val rows by viewModel.rows.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Budget targets (monthly)") }) }) { padding ->
        if (rows.isEmpty()) {
            EmptyState(
                "Create categories on the Expenses or Income tab first, then set a monthly target here.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rows, key = { it.category.id }) { row ->
                    BudgetSliderCard(
                        row = row,
                        onTargetChange = { newTarget -> viewModel.setTarget(row.category.id, newTarget, row.targetId) }
                    )
                }
                item { androidx.compose.foundation.layout.Box(Modifier.padding(bottom = 24.dp)) }
            }
        }
    }
}

@Composable
private fun BudgetSliderCard(row: BudgetRow, onTargetChange: (Double) -> Unit) {
    val niceMax = remember(row.actualMonthly, row.targetMonthly) {
        val base = max(row.actualMonthly, row.targetMonthly ?: 0.0) * 1.5
        max(200.0, ceil(base / 100.0) * 100.0)
    }
    var sliderValue by remember(row.targetMonthly, niceMax) {
        mutableFloatStateOf((row.targetMonthly ?: 0.0).toFloat().coerceIn(0f, niceMax.toFloat()))
    }
    LaunchedEffect(row.targetMonthly, niceMax) {
        sliderValue = (row.targetMonthly ?: 0.0).toFloat().coerceIn(0f, niceMax.toFloat())
    }

    val over = row.targetMonthly != null && row.category.type == CategoryType.EXPENSE && row.actualMonthly > row.targetMonthly
    val progressColor = if (over) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(row.category.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(row.category.type.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "Planned: ${formatCurrency(row.actualMonthly)}  ·  Target: ${row.targetMonthly?.let { formatCurrency(it) } ?: "not set"}",
                style = MaterialTheme.typography.bodyMedium,
                color = progressColor
            )
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = { onTargetChange(sliderValue.toDouble()) },
                valueRange = 0f..niceMax.toFloat()
            )
            Text(
                "Slider target: ${formatCurrency(sliderValue.toDouble())} / month",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
