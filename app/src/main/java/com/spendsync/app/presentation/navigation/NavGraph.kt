package com.spendsync.app.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.spendsync.app.presentation.screens.addexpense.AddExpenseScreen
import com.spendsync.app.presentation.screens.auth.AuthScreen
import com.spendsync.app.presentation.screens.history.HistoryScreen
import com.spendsync.app.presentation.screens.home.HomeScreen
import com.spendsync.app.presentation.screens.settings.SettingsScreen

@Composable
fun SpendSyncNavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,
    notionRedirectUri: Uri?,
    onAuthContinue: () -> Unit
) {
    val shouldHandleAuthRedirect = notionRedirectUri?.scheme == "syncspend" && notionRedirectUri.host == "notion-auth"
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn && !shouldHandleAuthRedirect) Screen.Home.route else Screen.Auth.route
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(notionRedirectUri = notionRedirectUri, onContinue = onAuthContinue)
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddExpense = {
                    navController.navigate(Screen.AddExpense.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
