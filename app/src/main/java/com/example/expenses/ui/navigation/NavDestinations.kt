package com.example.expenses.ui.navigation

sealed class NavDestination(val route: String) {
    object Dashboard : NavDestination("dashboard")
    object Transactions : NavDestination("transactions")
    object TransactionDetail : NavDestination("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: Long) = "transaction_detail/$transactionId"
    }
    object Settings : NavDestination("settings")
    object Lock : NavDestination("lock")
}
