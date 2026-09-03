package com.spendsync.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalThemeIsDark = staticCompositionLocalOf { false }

// Clean, high-contrast Material 3 light color scheme
private val LightColorScheme = lightColorScheme(
    primary              = AccentBlue,
    onPrimary            = White,
    primaryContainer     = AccentBlue.copy(alpha = 0.1f),
    onPrimaryContainer   = AccentBlue,

    secondary            = NearBlack,
    onSecondary          = White,
    secondaryContainer   = LightGray,
    onSecondaryContainer = Black,

    tertiary             = MediumGray,
    onTertiary           = White,

    background           = Color(0xFFF2F2F7), // Neutral light substrate
    onBackground         = Black,

    surface              = White,
    onSurface            = Black,
    surfaceVariant       = Color(0xFFE5E5EA),
    onSurfaceVariant     = Color(0xFF8E8E93),

    outline              = Color(0xFFE5E5EA),
    outlineVariant       = Color(0xFFD1D1D6),

    error                = Color(0xFFFF3B30),
    onError              = White,
)

// Deep AMOLED dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary              = AccentBlue,
    onPrimary            = White,
    primaryContainer     = AccentBlue.copy(alpha = 0.2f),
    onPrimaryContainer   = White,

    secondary            = DarkSubtext,
    onSecondary          = Black,
    secondaryContainer   = DarkSurface2,
    onSecondaryContainer = White,

    tertiary             = DarkSubtext,
    onTertiary           = Black,

    background           = Color(0xFF0B0C10), // True deep dark
    onBackground         = DarkText,

    surface              = Color(0xFF18191D), // Refined solid card
    onSurface            = DarkText,
    surfaceVariant       = DarkSurface2,
    onSurfaceVariant     = Color(0xFF98989D),

    outline              = Color(0xFF2C2C2E),
    outlineVariant       = DarkSurface2,

    error                = Color(0xFFFF453A),
    onError              = White,
)

@Composable
fun SyncSpendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalThemeIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SpendSyncTypography,
            content = content
        )
    }
}
