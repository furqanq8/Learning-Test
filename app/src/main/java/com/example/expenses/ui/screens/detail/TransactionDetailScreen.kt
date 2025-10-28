package com.example.expenses.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expenses.domain.model.Transaction

@Composable
fun TransactionDetailScreen(
    transaction: Transaction,
    currency: String,
    onSave: (Transaction) -> Unit
) {
    val amount = remember { mutableStateOf((transaction.amountMinor / 100.0).toString()) }
    val notes = remember { mutableStateOf(transaction.notes ?: "") }
    val tags = remember { mutableStateOf(transaction.tags.joinToString(",")) }
    val receipt = remember { mutableStateOf(transaction.receiptUri.orEmpty()) }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Edit Transaction", modifier = Modifier.padding(bottom = 16.dp))
        OutlinedTextField(
            value = amount.value,
            onValueChange = { amount.value = it },
            label = { Text("Amount ($currency)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = notes.value,
            onValueChange = { notes.value = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = tags.value,
            onValueChange = { tags.value = it },
            label = { Text("Tags (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = receipt.value,
            onValueChange = { receipt.value = it },
            label = { Text("Receipt URI") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val updated = transaction.copy(
                    amountMinor = (amount.value.toDoubleOrNull() ?: 0.0).times(100).toLong(),
                    notes = notes.value,
                    tags = tags.value.split(',').map { it.trim() }.filter { it.isNotBlank() },
                    receiptUri = receipt.value.ifBlank { null }
                )
                onSave(updated)
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Save")
        }
    }
}
