package com.example.expenses.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expenses.ui.navigation.NavDestination
import com.example.expenses.ui.screens.LockScreen
import com.example.expenses.ui.screens.dashboard.DashboardScreen
import com.example.expenses.ui.screens.detail.TransactionDetailScreen
import com.example.expenses.ui.screens.settings.SettingsScreen
import com.example.expenses.ui.screens.transactions.TransactionsScreen
import com.example.expenses.ui.viewmodel.ExpensesViewModel
import com.example.expenses.ui.viewmodel.LockState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesApp(viewModel: ExpensesViewModel, snackbarHostState: SnackbarHostState) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val lockState by viewModel.lockState.collectAsState()

    if (lockState is LockState.Locked) {
        LockScreen(onUnlock = viewModel::unlockWithPin)
        return
    }

    val destinations = listOf(
        NavDestination.Dashboard,
        NavDestination.Transactions,
        NavDestination.Settings
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                destinations.forEach { destination ->
                    val icon = when (destination) {
                        NavDestination.Dashboard -> Icons.Default.Dashboard
                        NavDestination.Transactions -> Icons.Default.List
                        NavDestination.Settings -> Icons.Default.Settings
                        else -> Icons.Default.Dashboard
                    }
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            if (currentRoute != destination.route) {
                                navController.navigate(destination.route) {
                                    launchSingleTop = true
                                    popUpTo(NavDestination.Dashboard.route)
                                }
                            }
                        },
                        icon = { Icon(imageVector = icon, contentDescription = destination.route) },
                        label = { Text(destination.route.substringBefore("/")) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            ExpensesNavHost(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
private fun ExpensesNavHost(navController: NavHostController, viewModel: ExpensesViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    NavHost(navController = navController, startDestination = NavDestination.Dashboard.route) {
        composable(NavDestination.Dashboard.route) {
            uiState.dashboardSummary?.let { summary ->
                DashboardScreen(
                    state = uiState,
                    summary = summary,
                    onFilter = viewModel::filterByRange
                )
            }
        }
        composable(NavDestination.Transactions.route) {
            TransactionsScreen(
                state = uiState,
                onSearch = viewModel::search,
                onDelete = viewModel::deleteTransaction
            )
        }
        composable(NavDestination.Settings.route) {
            SettingsScreen(
                state = uiState,
                onCurrencyChange = viewModel::setCurrency,
                onBackup = viewModel::backup,
                onRestore = viewModel::restore,
                onToggleTheme = viewModel::toggleTheme,
                onExport = viewModel::exportSummary,
                onBudgetSave = viewModel::saveBudget,
                onRecurringSave = viewModel::saveRecurring,
                onAddCategory = viewModel::saveCategory
            )
        }
        composable(NavDestination.TransactionDetail.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("transactionId")?.toLongOrNull()
            val transaction = uiState.transactions.firstOrNull { it.id == id }
            if (transaction != null) {
                TransactionDetailScreen(
                    transaction = transaction,
                    currency = uiState.currency,
                    onSave = viewModel::saveTransaction
                )
            }
        }
    }
}
