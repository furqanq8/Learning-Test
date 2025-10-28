package com.example.expenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import com.example.expenses.ui.ExpensesApp
import com.example.expenses.ui.theme.ExpensesTheme
import com.example.expenses.ui.viewmodel.ExpensesViewModel
import com.example.expenses.ui.viewmodel.ExpensesViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: ExpensesViewModel by viewModels {
        ExpensesViewModelFactory((application as ExpensesApplication).useCases)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        promptForAuthentication()
        setContent {
            val snackbarHost = remember { SnackbarHostState() }
            ExpensesTheme {
                ExpensesApp(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHost
                )
            }
        }
    }

    private fun promptForAuthentication() {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            == BiometricManager.BIOMETRIC_SUCCESS
        ) {
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.app_name))
                .setSubtitle(getString(R.string.pin_prompt_subtitle))
                .setNegativeButtonText(getString(R.string.use_pin))
                .build()
            val prompt = BiometricPrompt(this, mainExecutor,
                object : BiometricPrompt.AuthenticationCallback() {})
            prompt.authenticate(promptInfo)
        } else {
            viewModel.requirePinUnlock()
        }
    }
}
