package com.example.expenses.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LockScreen(onUnlock: (String) -> Unit) {
    val pin = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enter PIN")
        OutlinedTextField(
            value = pin.value,
            onValueChange = { pin.value = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("PIN") }
        )
        Button(onClick = { onUnlock(pin.value) }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Unlock")
        }
    }
}
