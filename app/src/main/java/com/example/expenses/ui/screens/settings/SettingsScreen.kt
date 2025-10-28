package com.example.expenses.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.menuAnchor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expenses.domain.model.Budget
import com.example.expenses.domain.model.Category
import com.example.expenses.domain.model.RecurrenceInterval
import com.example.expenses.domain.model.RecurringTransaction
import com.example.expenses.domain.model.TransactionType
import com.example.expenses.ui.viewmodel.ExpensesUiState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: ExpensesUiState,
    onCurrencyChange: (String) -> Unit,
    onBackup: (String) -> Unit,
    onRestore: (String) -> Unit,
    onToggleTheme: () -> Unit,
    onExport: (Int, Int, Set<String>, String) -> Unit,
    onBudgetSave: (Budget) -> Unit,
    onRecurringSave: (RecurringTransaction) -> Unit,
    onAddCategory: (String, TransactionType) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val selectedCurrency = remember { mutableStateOf(state.currency) }
    val backupPath = remember { mutableStateOf("") }
    val exportPath = remember { mutableStateOf("") }
    val exportYear = remember { mutableStateOf(2024) }
    val exportMonth = remember { mutableStateOf(1) }
    val selectedFormats = remember { mutableStateOf(setOf("csv", "xlsx", "pdf")) }

    val budgetCategoryExpanded = remember { mutableStateOf(false) }
    val budgetCategory = remember(state.expenseCategories) {
        mutableStateOf(state.expenseCategories.firstOrNull())
    }
    val budgetAmount = remember { mutableStateOf("0.0") }
    val budgetMonth = remember { mutableStateOf(exportMonth.value) }
    val budgetYear = remember { mutableStateOf(exportYear.value) }

    val recurringTransactionExpanded = remember { mutableStateOf(false) }
    val recurringTransaction = remember(state.transactions) {
        mutableStateOf(state.transactions.firstOrNull())
    }
    val recurringIntervalExpanded = remember { mutableStateOf(false) }
    val recurringInterval = remember { mutableStateOf(RecurrenceInterval.MONTHLY) }
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val newCategoryName = remember { mutableStateOf("") }
    val newCategoryTypeExpanded = remember { mutableStateOf(false) }
    val newCategoryType = remember { mutableStateOf(TransactionType.EXPENSE) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Settings", style = MaterialTheme.typography.titleLarge)
        ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = it }) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = selectedCurrency.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Currency") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) }
            )
            ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                listOf("KWD", "USD", "EUR", "GBP", "AED").forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            selectedCurrency.value = currency
                            onCurrencyChange(currency)
                            expanded.value = false
                        }
                    )
                }
            }
        }
        OutlinedTextField(
            value = backupPath.value,
            onValueChange = { backupPath.value = it },
            label = { Text("Backup file path") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Button(onClick = { onBackup(backupPath.value) }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Backup")
        }
        Button(onClick = { onRestore(backupPath.value) }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Restore")
        }
        Switch(
            checked = state.isDarkTheme,
            onCheckedChange = { onToggleTheme() },
            modifier = Modifier.padding(top = 16.dp)
        )
        OutlinedTextField(
            value = exportYear.value.toString(),
            onValueChange = { exportYear.value = it.toIntOrNull() ?: exportYear.value },
            label = { Text("Export Year") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        OutlinedTextField(
            value = exportMonth.value.toString(),
            onValueChange = { exportMonth.value = it.toIntOrNull()?.coerceIn(1, 12) ?: exportMonth.value },
            label = { Text("Export Month") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        OutlinedTextField(
            value = exportPath.value,
            onValueChange = { exportPath.value = it },
            label = { Text("Export Directory") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Button(
            onClick = { onExport(exportYear.value, exportMonth.value, selectedFormats.value, exportPath.value) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Export Summary")
        }
        Text("Custom Categories", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))
        OutlinedTextField(
            value = newCategoryName.value,
            onValueChange = { newCategoryName.value = it },
            label = { Text("Category Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        ExposedDropdownMenuBox(expanded = newCategoryTypeExpanded.value, onExpandedChange = { newCategoryTypeExpanded.value = it }) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                value = newCategoryType.value.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = newCategoryTypeExpanded.value) }
            )
            ExposedDropdownMenu(expanded = newCategoryTypeExpanded.value, onDismissRequest = { newCategoryTypeExpanded.value = false }) {
                TransactionType.values().forEach { type ->
                    DropdownMenuItem(text = { Text(type.name) }, onClick = {
                        newCategoryType.value = type
                        newCategoryTypeExpanded.value = false
                    })
                }
            }
        }
        Button(onClick = {
            if (newCategoryName.value.isNotBlank()) {
                onAddCategory(newCategoryName.value, newCategoryType.value)
                newCategoryName.value = ""
            }
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Add Category")
        }
        Text("Budgets", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))
        if (state.budgets.isEmpty()) {
            Text("No budgets yet", modifier = Modifier.padding(top = 4.dp))
        } else {
            state.budgets.forEach { budget ->
                Text(
                    text = "${budget.month}/${budget.year} - Category #${budget.categoryId}: ${budget.limitMinor / 100.0}",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        if (state.expenseCategories.isNotEmpty()) {
            ExposedDropdownMenuBox(expanded = budgetCategoryExpanded.value, onExpandedChange = { budgetCategoryExpanded.value = it }) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = budgetCategory.value?.name ?: "Select Category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Budget Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = budgetCategoryExpanded.value) }
                )
                ExposedDropdownMenu(expanded = budgetCategoryExpanded.value, onDismissRequest = { budgetCategoryExpanded.value = false }) {
                    state.expenseCategories.forEach { category: Category ->
                        DropdownMenuItem(text = { Text(category.name) }, onClick = {
                            budgetCategory.value = category
                            budgetCategoryExpanded.value = false
                        })
                    }
                }
            }
            OutlinedTextField(
                value = budgetAmount.value,
                onValueChange = { budgetAmount.value = it },
                label = { Text("Monthly Limit (${state.currency})") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            OutlinedTextField(
                value = budgetMonth.value.toString(),
                onValueChange = { budgetMonth.value = it.toIntOrNull()?.coerceIn(1, 12) ?: budgetMonth.value },
                label = { Text("Budget Month") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            OutlinedTextField(
                value = budgetYear.value.toString(),
                onValueChange = { budgetYear.value = it.toIntOrNull() ?: budgetYear.value },
                label = { Text("Budget Year") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Button(onClick = {
                val category = budgetCategory.value ?: return@Button
                val amountMinor = (budgetAmount.value.toDoubleOrNull() ?: 0.0).times(100).toLong()
                onBudgetSave(
                    Budget(
                        categoryId = category.id,
                        limitMinor = amountMinor,
                        month = budgetMonth.value,
                        year = budgetYear.value
                    )
                )
            }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Save Budget")
            }
        } else {
            Text("Add an expense category to create budgets.", modifier = Modifier.padding(top = 8.dp))
        }
        Text("Recurring Entries", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))
        if (state.transactions.isEmpty()) {
            Text("Add a transaction to schedule recurring entries.", modifier = Modifier.padding(top = 4.dp))
        } else {
            ExposedDropdownMenuBox(expanded = recurringTransactionExpanded.value, onExpandedChange = { recurringTransactionExpanded.value = it }) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = recurringTransaction.value?.category?.name ?: "Select Transaction",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Base Transaction") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurringTransactionExpanded.value) }
                )
                ExposedDropdownMenu(expanded = recurringTransactionExpanded.value, onDismissRequest = { recurringTransactionExpanded.value = false }) {
                    state.transactions.forEach { transaction ->
                        DropdownMenuItem(text = { Text("${transaction.category.name} - ${transaction.amountMinor / 100.0}") }, onClick = {
                            recurringTransaction.value = transaction
                            recurringTransactionExpanded.value = false
                        })
                    }
                }
            }
            ExposedDropdownMenuBox(expanded = recurringIntervalExpanded.value, onExpandedChange = { recurringIntervalExpanded.value = it }) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = recurringInterval.value.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Interval") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurringIntervalExpanded.value) }
                )
                ExposedDropdownMenu(expanded = recurringIntervalExpanded.value, onDismissRequest = { recurringIntervalExpanded.value = false }) {
                    RecurrenceInterval.values().forEach { interval ->
                        DropdownMenuItem(text = { Text(interval.name) }, onClick = {
                            recurringInterval.value = interval
                            recurringIntervalExpanded.value = false
                        })
                    }
                }
            }
            Button(onClick = {
                val transaction = recurringTransaction.value ?: return@Button
                onRecurringSave(
                    RecurringTransaction(
                        baseTransactionId = transaction.id,
                        nextExecution = today,
                        interval = recurringInterval.value,
                        active = true
                    )
                )
            }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Activate Recurring")
            }
        }
    }
}
