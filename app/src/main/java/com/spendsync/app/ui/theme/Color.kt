package com.spendsync.app.ui.theme

import androidx.compose.ui.graphics.Color

// === MONOCHROME PALETTE — NO PURPLE, NO DYNAMIC COLOR ===

// Light Mode
val White         = Color(0xFFFFFFFF)
val OffWhite      = Color(0xFFF5F5F5)
val LightGray     = Color(0xFFEEEEEE)
val MediumGray    = Color(0xFF999999)
val DarkGray      = Color(0xFF444444)
val NearBlack     = Color(0xFF1A1A1A)
val Black         = Color(0xFF000000)

// Dark Mode surfaces (iOS-inspired)
val DarkBg        = Color(0xFF000000)
val DarkSurface   = Color(0xFF1C1C1E)   // iOS systemBackground dark
val DarkSurface2  = Color(0xFF2C2C2E)   // iOS secondarySystemBackground dark
val DarkSurface3  = Color(0xFF3A3A3C)   // iOS tertiarySystemBackground dark
val DarkText      = Color(0xFFFFFFFF)
val DarkSubtext   = Color(0xFF8E8E93)   // iOS secondaryLabel dark

// Single accent — used ONLY for interactive elements (Save button, selected chip)
val AccentBlue    = Color(0xFF007AFF)   // iOS system blue — exact match
