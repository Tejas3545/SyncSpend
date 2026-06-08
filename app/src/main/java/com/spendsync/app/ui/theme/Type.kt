package com.spendsync.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.spendsync.app.R

val SfProFontFamily = FontFamily(
    Font(R.font.sf_pro_regular, weight = FontWeight.Normal),
    Font(R.font.sf_pro_regular, weight = FontWeight.Medium), // fallback
    Font(R.font.sf_pro_bold, weight = FontWeight.SemiBold), // fallback
    Font(R.font.sf_pro_bold, weight = FontWeight.Bold)
)

// ===================================================================
// Typography scale — mirrors iOS text styles exactly
// ===================================================================
val SpendSyncTypography = Typography(

    // Large titles (e.g. "$120.38" spending amount)
    displayLarge = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp
    ),

    // Screen titles (e.g. "SpendSync", "Add Expense")
    headlineLarge = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.3).sp
    ),

    headlineMedium = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp
    ),

    // Section headers (e.g. "Latest", "Monday")
    titleLarge = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.1).sp
    ),

    titleMedium = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    // Body text (expense names, descriptions)
    bodyLarge = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.1).sp
    ),

    bodyMedium = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    // Captions (dates, labels)
    bodySmall = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),

    // Chips, tags
    labelLarge = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    labelMedium = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),

    labelSmall = TextStyle(
        fontFamily = SfProFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp
    ),
)
