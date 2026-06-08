package com.spendsync.app.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object AddExpense : Screen("add_expense")
    object History : Screen("history")
    object Settings : Screen("settings")
}
