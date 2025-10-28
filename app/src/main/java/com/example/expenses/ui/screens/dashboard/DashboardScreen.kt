package com.example.expenses.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expenses.domain.model.CategoryTotal
import com.example.expenses.domain.model.DashboardSummary
import com.example.expenses.domain.model.MonthlySummary
import com.example.expenses.domain.util.MoneyFormatter
import com.example.expenses.ui.viewmodel.ExpensesUiState
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

@Composable
fun DashboardScreen(
    state: ExpensesUiState,
    summary: DashboardSummary,
    onFilter: (LocalDate, LocalDate) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    val start = today.minus(DateTimeUnit.DayBased(30))
                    onFilter(start, today)
                }) { Text("30D") }
                Button(onClick = {
                    val start = today.minus(DateTimeUnit.MonthBased(3))
                    onFilter(start, today)
                }, modifier = Modifier.padding(start = 8.dp)) { Text("3M") }
                Button(onClick = {
                    val start = today.minus(DateTimeUnit.YearBased(1))
                    onFilter(start, today)
                }, modifier = Modifier.padding(start = 8.dp)) { Text("1Y") }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Balance", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = MoneyFormatter.format(summary.balanceMinor, state.currency),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        item {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        items(summary.recentTransactions) { transaction ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(transaction.category.name, fontWeight = FontWeight.Medium)
                    Text(transaction.date.toString(), style = MaterialTheme.typography.bodySmall)
                }
                Text(MoneyFormatter.format(transaction.amountMinor, transaction.currency))
            }
        }
        item {
            Text(
                text = "Monthly Summaries",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        items(summary.monthlySummaries) { monthlySummary ->
            MonthlySummaryCard(summary = monthlySummary, currency = state.currency)
        }
    }
}

@Composable
private fun MonthlySummaryCard(summary: MonthlySummary, currency: String) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${summary.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${summary.year}", fontWeight = FontWeight.Bold)
            Text("Income: ${MoneyFormatter.format(summary.totalIncomeMinor, currency)}")
            Text("Expenses: ${MoneyFormatter.format(summary.totalExpenseMinor, currency)}")
            Text("By Category", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            PieChart(summary.categoryTotals)
            Text("By Income Type", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            PieChart(summary.incomeTypeTotals)
        }
    }
}

@Composable
private fun PieChart(data: List<CategoryTotal>) {
    val total = data.sumOf { it.amountMinor }.takeIf { it > 0 } ?: return
    val colors = listOf(
        Color(0xFF1ABC9C),
        Color(0xFF3498DB),
        Color(0xFFE74C3C),
        Color(0xFFF1C40F),
        Color(0xFF9B59B6),
        Color(0xFF2ECC71)
    )
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .height(180.dp)) {
        var startAngle = 0f
        data.forEachIndexed { index, item ->
            val sweep = item.amountMinor.toFloat() / total * 360f
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true
            )
            startAngle += sweep
        }
    }
}
