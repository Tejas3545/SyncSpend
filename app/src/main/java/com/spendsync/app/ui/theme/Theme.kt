package com.spendsync.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ===================================================================
// Design System: Monochrome with iOS Blue Accents
// ===================================================================

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

    background           = White,
    onBackground         = Black,

    surface              = White,
    onSurface            = Black,
    surfaceVariant       = OffWhite,
    onSurfaceVariant     = DarkGray,

    outline              = LightGray,
    outlineVariant       = Color(0xFFDDDDDD),

    error                = Color(0xFFFF3B30),
    onError              = White,
)

private val DarkColorScheme = darkColorScheme(
    primary              = AccentBlue,
    onPrimary            = White,
    primaryContainer     = AccentBlue.copy(alpha = 0.15f),
    onPrimaryContainer   = White,

    secondary            = DarkSubtext,
    onSecondary          = Black,
    secondaryContainer   = DarkSurface2,
    onSecondaryContainer = White,

    tertiary             = DarkSubtext,
    onTertiary           = Black,

    background           = DarkBg,
    onBackground         = DarkText,

    surface              = DarkSurface,
    onSurface            = DarkText,
    surfaceVariant       = DarkSurface2,
    onSurfaceVariant     = DarkSubtext,

    outline              = DarkSurface3,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SpendSyncTypography,
        content = content
    )
}
