package com.spendsync.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.data.local.datastore.SettingsDataStore
import com.spendsync.app.presentation.navigation.Screen
import com.spendsync.app.presentation.navigation.SpendSyncNavGraph
import com.spendsync.app.ui.theme.SyncSpendTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    @Inject
    lateinit var authDataStore: AuthDataStore

    private var keepSplashOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        setContent {
            val themePreference by settingsDataStore.theme.collectAsState(initial = null)
            val isLoggedIn by authDataStore.isLoggedIn.collectAsState(initial = false)
            val notionRedirectUri = remember { mutableStateOf(intent?.data) }
            if (themePreference != null) {
                keepSplashOnScreen = false

                val darkTheme = when (themePreference) {
                    "light" -> false
                    "dark" -> true
                    else -> isSystemInDarkTheme()
                }

                SyncSpendTheme(darkTheme = darkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        LaunchedEffect(isLoggedIn) {
                            if (!isLoggedIn) {
                                navController.navigate(Screen.Auth.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                        LaunchedEffect(intent?.action, intent?.data) {
                            notionRedirectUri.value = intent?.data
                            val isAddExpenseIntent = intent?.action == "com.spendsync.app.ADD_EXPENSE" ||
                                (intent?.action == Intent.ACTION_VIEW &&
                                    intent?.data?.scheme == "syncspend" &&
                                    intent?.data?.host == "add_expense")
                            if (isAddExpenseIntent) {
                                navController.navigate(Screen.AddExpense.route)
                            }
                        }
                        SpendSyncNavGraph(
                            navController = navController,
                            isLoggedIn = isLoggedIn,
                            notionRedirectUri = notionRedirectUri.value,
                            onAuthContinue = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Auth.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
