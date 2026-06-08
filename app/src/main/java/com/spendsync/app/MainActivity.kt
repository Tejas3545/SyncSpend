package com.spendsync.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import com.spendsync.app.data.local.datastore.AuthDataStore
import com.spendsync.app.data.local.datastore.SettingsDataStore
import com.spendsync.app.presentation.navigation.SpendSyncNavGraph
import com.spendsync.app.presentation.navigation.Screen
import com.spendsync.app.ui.theme.SyncSpendTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    @Inject
    lateinit var authDataStore: AuthDataStore

    @Inject
    lateinit var authRepository: com.spendsync.app.domain.repository.AuthRepository

    private var keepSplashOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
        
        setContent {
            val themePreference by settingsDataStore.theme.collectAsState(initial = null)
            val isLoggedInState by authDataStore.isLoggedIn.collectAsState(initial = null)

            if (themePreference != null && isLoggedInState != null) {
                keepSplashOnScreen = false
            }

            if (themePreference != null && isLoggedInState != null) {
                val isSystemDark = isSystemInDarkTheme()
                val darkTheme = when (themePreference) {
                    "light" -> false
                    "dark" -> true
                    else -> isSystemDark
                }

                SyncSpendTheme(darkTheme = darkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        
                        LaunchedEffect(intent) {
                            val action = intent?.action
                            val data = intent?.data
                            if (action == Intent.ACTION_VIEW && data != null) {
                                // Handle BOTH custom scheme and HTTPS style redirect
                                val isNotionOauth = (data.scheme == "syncspend" && data.host == "oauth") ||
                                                  (data.scheme == "https" && data.host == "syncspend")
                                
                                if (isNotionOauth) {
                                    val code = data.getQueryParameter("code")
                                    if (code != null) {
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            authRepository.loginWithNotion(code)
                                        }
                                    }
                                } else if (data.scheme == "syncspend" && data.host == "add_expense") {
                                    navController.navigate(Screen.AddExpense.route)
                                }
                            } else if (action == "com.spendsync.app.ADD_EXPENSE") {
                                navController.navigate(Screen.AddExpense.route)
                            }
                        }
                        
                        SpendSyncNavGraph(
                            navController = navController,
                            isLoggedIn = isLoggedInState ?: false
                        )
                    }
                }
            }
        }
    }
}
