package com.koreykaigle.budgetapp.ui.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koreykaigle.budgetapp.ui.common.EmptyState
import com.koreykaigle.budgetapp.ui.common.SectionHeader
import com.koreykaigle.budgetapp.ui.common.repositoryViewModel
import com.koreykaigle.budgetapp.ui.theme.BrandPrimary
import com.koreykaigle.budgetapp.ui.theme.DangerColor
import com.koreykaigle.budgetapp.ui.theme.SuccessColor
import com.koreykaigle.budgetapp.util.formatCurrency
import com.koreykaigle.budgetapp.util.formatCurrencyCompact
import com.koreykaigle.budgetapp.util.formatPercent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen() {
    val viewModel = repositoryViewModel { HealthViewModel(it) }
    val report by viewModel.report.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Financial health") }) }) { padding ->
        if (!report.hasAnyData) {
            EmptyState(
                "Add some income, expenses, or accounts and your financial health report will build itself here.",
                modifier = Modifier.padding(padding)
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    SectionHeader("Overview")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        KpiCard(
                            title = "Net worth",
                            value = formatCurrencyCompact(report.netWorth),
                            valueColor = if (report.netWorth >= 0) SuccessColor else DangerColor,
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = "Monthly cash flow",
                            value = formatCurrencyCompact(report.monthlyCashFlow),
                            valueColor = if (report.monthlyCashFlow >= 0) SuccessColor else DangerColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    ) {
                        KpiCard(
                            title = "Savings rate",
                            value = formatPercent(report.savingsRate),
                            valueColor = BrandPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = "Net monthly income",
                            value = formatCurrencyCompact(report.netMonthlyIncome),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Column {
                    SectionHeader("Cash flow")
                    Card { Column(Modifier.padding(16.dp)) {
                        FlowRow("Gross income", report.grossMonthlyIncome)
                        FlowRow("− Deductions", -report.totalMonthlyDeductions)
                        Divider(Modifier.padding(vertical = 6.dp))
                        FlowRow("Net income", report.netMonthlyIncome, emphasize = true)
                        FlowRow("− Expenses", -report.totalMonthlyExpenses)
                        Divider(Modifier.padding(vertical = 6.dp))
                        FlowRow(
                            "Cash flow",
                            report.monthlyCashFlow,
                            emphasize = true,
                            tint = if (report.monthlyCashFlow >= 0) SuccessColor else DangerColor
                        )
                    } }
                }
            }

            item {
                Column {
                    SectionHeader("Net worth")
                    Card { Column(Modifier.padding(16.dp)) {
                        FlowRow("Total assets", report.totalAssets, tint = SuccessColor)
                        FlowRow("Total liabilities", -report.totalLiabilities, tint = DangerColor)
                        Divider(Modifier.padding(vertical = 6.dp))
                        FlowRow("Net worth", report.netWorth, emphasize = true)
                    } }
                }
            }

            if (report.expenseBreakdown.isNotEmpty()) {
                item {
                    Column {
                        SectionHeader("Where expenses go")
                        val max = report.expenseBreakdown.maxOf { it.monthlyAmount }
                        Card { Column(Modifier.padding(16.dp)) {
                            report.expenseBreakdown.forEach {
                                HorizontalBarRow(it.name, it.monthlyAmount, max, DangerColor)
                            }
                        } }
                    }
                }
            }

            if (report.incomeBreakdown.isNotEmpty()) {
                item {
                    Column {
                        SectionHeader("Where income comes from")
                        val max = report.incomeBreakdown.maxOf { it.monthlyAmount }
                        Card { Column(Modifier.padding(16.dp)) {
                            report.incomeBreakdown.forEach {
                                HorizontalBarRow(it.name, it.monthlyAmount, max, SuccessColor)
                            }
                        } }
                    }
                }
            }

            if (report.budgetAdherence.isNotEmpty()) {
                item {
                    Column {
                        SectionHeader("Budget adherence")
                        Card { Column(Modifier.padding(16.dp)) {
                            report.budgetAdherence.forEach { row ->
                                HorizontalBarRow(
                                    label = row.categoryName,
                                    amount = row.actual,
                                    maxAmount = maxOf(row.actual, row.target),
                                    barColor = if (row.overBudget) DangerColor else BrandPrimary
                                )
                                Text(
                                    "target ${formatCurrency(row.target)}${if (row.overBudget) "  ·  over budget" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (row.overBudget) DangerColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        } }
                    }
                }
            }

            if (report.assetAccounts.isNotEmpty() || report.liabilityAccounts.isNotEmpty()) {
                item {
                    Column {
                        SectionHeader("Accounts")
                        Card { Column(Modifier.padding(16.dp)) {
                            report.assetAccounts.forEach { FlowRow(it.name, it.balance, tint = SuccessColor) }
                            report.liabilityAccounts.forEach { FlowRow(it.name, -it.balance, tint = DangerColor) }
                        } }
                    }
                }
            }

            item {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 24.dp))
            }
        }
    }
}
