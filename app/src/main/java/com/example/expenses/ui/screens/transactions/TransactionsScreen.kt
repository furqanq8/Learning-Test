package com.example.expenses.ui.screens.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expenses.domain.model.Transaction
import com.example.expenses.domain.util.MoneyFormatter
import com.example.expenses.ui.viewmodel.ExpensesUiState

@Composable
fun TransactionsScreen(
    state: ExpensesUiState,
    onSearch: (String) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    val query = remember { mutableStateOf("") }
    val transactions = if (query.value.isBlank()) state.transactions else state.searchResults
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = query.value,
            onValueChange = {
                query.value = it
                onSearch(it)
            },
            label = { Text("Search notes or tags") },
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(transactions) { transaction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(transaction.category.name, fontWeight = FontWeight.Medium)
                        Text(transaction.date.toString(), style = MaterialTheme.typography.bodySmall)
                        Text(transaction.tags.joinToString(), style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(MoneyFormatter.format(transaction.amountMinor, transaction.currency))
                        IconButton(onClick = { onDelete(transaction) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}
