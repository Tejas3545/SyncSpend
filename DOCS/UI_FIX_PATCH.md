# SpendSync — UI Fix Patch v1.1
## Give this ENTIRE file to Windsurf/Agent. Apply ALL changes below.

---

## PROBLEM SUMMARY

The app was built with Material You's default purple color scheme. Need to:
1. Replace ALL purple/lavender with strict monochrome (black + white only)
2. Add Inter font (closest free equivalent to iOS San Francisco font)
3. Change app icon dollar `$` → rupee `₹`
4. Fix date grouping format (`2026-06` → `June 2026`)
5. Fix FAB, chips, buttons — all must follow monochrome theme

---

## CHANGE 1 — `ui/theme/Color.kt`
> **Replace entire file content with this:**

```kotlin
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
```

---

## CHANGE 2 — `ui/theme/Theme.kt`
> **Replace entire file content with this:**

```kotlin
package com.spendsync.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ===================================================================
// IMPORTANT: dynamicColor is DISABLED intentionally.
// We use strict monochrome. Do NOT re-enable dynamic color.
// ===================================================================

private val LightColorScheme = lightColorScheme(
    primary              = Black,
    onPrimary            = White,
    primaryContainer     = LightGray,
    onPrimaryContainer   = Black,

    secondary            = DarkGray,
    onSecondary          = White,
    secondaryContainer   = LightGray,
    onSecondaryContainer = NearBlack,

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

    error                = Color(0xFFFF3B30),    // iOS red
    onError              = White,
)

private val DarkColorScheme = darkColorScheme(
    primary              = White,
    onPrimary            = Black,
    primaryContainer     = DarkSurface2,
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

    error                = Color(0xFFFF453A),    // iOS red dark
    onError              = White,
)

@Composable
fun SpendSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // dynamicColor intentionally NOT used — monochrome design system
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SpendSyncTypography,
        content = content
    )
}
```

---

## CHANGE 3 — `ui/theme/Type.kt`
> **Replace entire file content with this. Uses Inter font (iOS SF Pro equivalent — closest free match).**

### Step 3a — Download Inter font files first:

Download these 4 files from https://fonts.google.com/specimen/Inter and place them in `app/src/main/res/font/`:
- `inter_regular.ttf` (Weight 400)
- `inter_medium.ttf` (Weight 500)
- `inter_semibold.ttf` (Weight 600)
- `inter_bold.ttf` (Weight 700)

If you cannot download, use this fallback: add the Google Fonts dependency and use downloadable fonts.

### Step 3b — Add to `app/build.gradle.kts` dependencies:

```kotlin
// Google Fonts (for downloadable Inter font — no .ttf file needed)
implementation("androidx.compose.ui:ui-text-google-fonts:1.7.8")
```

### Step 3c — `Type.kt` content:

```kotlin
package com.spendsync.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.spendsync.app.R

// === Inter font via Google Fonts — mirrors iOS San Francisco feel ===
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

val InterFont = GoogleFont("Inter")

val InterFontFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Bold),
)

// ===================================================================
// Typography scale — mirrors iOS text styles exactly
// ===================================================================
val SpendSyncTypography = Typography(

    // Large titles (e.g. "$120.38" spending amount)
    displayLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp
    ),

    // Screen titles (e.g. "SpendSync", "Add Expense")
    headlineLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.3).sp
    ),

    headlineMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp
    ),

    // Section headers (e.g. "Latest", "Monday")
    titleLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.1).sp
    ),

    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    // Body text (expense names, descriptions)
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.1).sp
    ),

    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    // Captions (dates, labels)
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),

    // Chips, tags
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),

    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp
    ),
)
```

### Step 3d — Add font certificate array (`res/values/font_certs.xml`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <array name="com_google_android_gms_fonts_certs">
        <item>@array/com_google_android_gms_fonts_certs_dev</item>
        <item>@array/com_google_android_gms_fonts_certs_prod</item>
    </array>

    <string-array name="com_google_android_gms_fonts_certs_dev">
        <item>
            MIIEqDCCA5CgAwIBAgIJANWFuGx90071MA0GCSqGSIb3DQEBBAUAMIGUMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzETMBEGA1UEChMKR29vZ2xlIEluYzEUMBIGA1UECxMLRW5naW5lZXJpbmcxFTATBgNVBAMTDGNzLmFuZHJvaWQuY29tMB4XDTE3MDYyNzIzMTYzN1oXDTQ3MDYyMDIzMTYzN1owgZQxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1TYW4gRnJhbmNpc2NvMRMwEQYDVQQKEwpHb29nbGUgSW5jMRQwEgYDVQQLEwtFbmdpbmVlcmluZzEVMBMGA1UEAxMMY3MuYW5kcm9pZC5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC9BnYvSMFoq4ynRskaQ97v4jfRFLXhHbL5OWGn3HAloCcYrn1Jii8gxKNblbf6DKE3B0ZyJCYl6ICcm7CQNI1pLSEr6N04LhSbmQjETjlHoIGF3+qGCCjk7LDSQ8OLVcLMwCVhJJ/3fSRKGF+5f8/lJ6K7hcn5WH5l7p5M5xXD5uJMn3c7HgNrjb8ZIeSMqFOTh7pMQEwxWK+qfXGJiEjTXsZC9NXD0XbZnA8zH8MFNqLDQm25bYKBn3bqdEWNaAElGc1I7QSEUuEcQ62ck22lBX8FxQhHLzIw7kJ9HdkdaLLwNqVK6YgR9TvFWZKZ8bN26LTq4RqbzEKD9AgMBAAGjggEaMIIBFjAdBgNVHQ4EFgQU0bTHRi8gJKI3O2DxGNzDU0FcVrEwgeMGA1UdIwSB2zCB2IAU0bTHRi8gJKI3O2DxGNzDU0FcVrGhgZqkgZcwgZQxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1TYW4gRnJhbmNpc2NvMRMwEQYDVQQKEwpHb29nbGUgSW5jMRQwEgYDVQQLEwtFbmdpbmVlcmluZzEVMBMGA1UEAxMMY3MuYW5kcm9pZC5jb22CCQDVhbhsfda+TTAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBBAUAA4IBAQBhFqoXE0Qrb3SikTfV9tB9lq3HtlK0n7xHKrHbU8x9hH5b98V6vSqXIHGGVQTtc07j8JBLU/7nSvlH0D0fKanFAz3dpHFMH9qM7+LU+wB4KG4FpGWNlXpTBHOoilG1mHQ8hnJk5vD+BFIK8CiEq1eLvMQhkDjEJE6VrdF4JqN7fXfP4B4Y9kAj8b2KKLyZ2ZgO9tRSqVEJbBmfQ8XoWFnP+rJKHhGKMYm6DlhxJlWPi5uTzJMDLT5oi8iDGf3i+kP0XhgTp
        </item>
    </string-array>

    <string-array name="com_google_android_gms_fonts_certs_prod">
        <item>
            MIIEQzCCAyugAwIBAgIJAMLgh0ZkSjCNMA0GCSqGSIb3DQEBBAUAMHQxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRQwEgYDVQQKEwtHb29nbGUgSW5jLjEQMA4GA1UECxMHQW5kcm9pZDEQMA4GA1UEAxMHQW5kcm9pZDAeFw0wODA4MjEyMzEzMzRaFw0zNjAxMDcyMzEzMzRaMHQxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRQwEgYDVQQKEwtHb29nbGUgSW5jLjEQMA4GA1UECxMHQW5kcm9pZDEQMA4GA1UEAxMHQW5kcm9pZDCCASAwDQYJKoZIhvcNAQEBBQADggENADCCAQgCggEBANbOLggKv+IxTdGNs8/TGFy0PTP6DHThvbbR24kT9ixcOd9W+EaBPWW+wPPkQxlXjOfAKbnIi8RPwgal3L1fgMY2ynMFx3T/J3p1bShqWFzJN64TuCfZGK9MhXDKGQRvWHDn9EMKMCKfLOWMkMmQP5m1m08G39N+1BVLP7bSw0EVHV6R9J0GAB7eORFv0D56XxIUEKdFCRBGAjnFkFsrn0Zi0Wm29kbHRbqGEEFMXDMEaWwxEQJEPCKmhKbxIjhE7Fj6AeaRQmtB3ew0vCBNpzBWKqUMFGT4HYr3Cys+MOXeGCNq2HKm+RLQfm4f3Ie14yC4E3D0+yBnRNX7kCAgED
        </item>
    </string-array>
</resources>
```

---

## CHANGE 4 — App Icon with ₹ (Rupee Symbol)

### `res/drawable/ic_launcher_foreground.xml`
> **Replace entire file content with this:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">

    <!-- Outer dark circle background -->
    <path
        android:fillColor="#000000"
        android:pathData="M54,54m-54,0a54,54 0,1 1,108 0a54,54 0,1 1,-108 0" />

    <!--
        Rupee Symbol ₹ — drawn as paths for crisp vector rendering
        Centered at 54,54 — white color on black background
    -->

    <!-- Top horizontal bar of ₹ -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M32,28 L76,28 L76,35 L32,35 Z" />

    <!-- Second horizontal bar of ₹ -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M32,40 L76,40 L76,47 L32,47 Z" />

    <!-- Vertical left stroke of ₹ -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M32,28 L39,28 L39,80 L32,80 Z" />

    <!-- Diagonal slash of ₹ (top-right to bottom-left) -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M68,47 L42,80 L35,80 L61,47 Z" />

    <!-- Right serif bump on top bar -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M54,28 Q76,28 76,40 Q76,47 54,47 L54,40 Q68,40 68,35 Q68,28 54,28 Z" />

</vector>
```

### `res/mipmap-*/ic_launcher.xml` (Adaptive Icon Background)
> Change the background to solid black. In `res/values/colors.xml`, ensure:

```xml
<color name="ic_launcher_background">#000000</color>
```

---

## CHANGE 5 — Fix Home Screen Date Format

### In `HomeScreen.kt` — find the date grouping header and fix this:

**FIND this pattern** (wherever you display grouped date headers like `"2026-06"`):
```kotlin
// BAD — raw ISO date
Text(text = dateKey)
```

**REPLACE with:**
```kotlin
// GOOD — human-readable format
Text(
    text = formatDateHeader(dateKey),
    style = MaterialTheme.typography.titleLarge,
    color = MaterialTheme.colorScheme.onBackground
)
```

**Add this helper function in `DateUtils.kt`:**

```kotlin
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Converts any date string to a human-readable section header.
 * Input can be: "2026-06-06" (full date), "2026-06" (year-month), or day name
 */
fun formatDateHeader(rawDate: String): String {
    return try {
        when {
            // Full date like "2026-06-06"
            rawDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> {
                val date = LocalDate.parse(rawDate)
                val today = LocalDate.now()
                val yesterday = today.minusDays(1)
                when (date) {
                    today     -> "Today"
                    yesterday -> "Yesterday"
                    else      -> date.format(
                        DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH)
                    ) // e.g. "Saturday, 6 June"
                }
            }
            // Year-month like "2026-06"
            rawDate.matches(Regex("\\d{4}-\\d{2}")) -> {
                val ym = YearMonth.parse(rawDate)
                ym.format(
                    DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
                ) // e.g. "June 2026"
            }
            else -> rawDate
        }
    } catch (e: Exception) {
        rawDate // fallback to raw string if parsing fails
    }
}
```

---

## CHANGE 6 — Home Screen UI Fixes (`HomeScreen.kt`)

Apply these targeted UI fixes:

### Fix 1 — Total Spending Card

```kotlin
// FIND the card and replace colors:
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,  // OffWhite / DarkSurface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // flat, no shadow — iOS style
) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text(
            text = "Total Spending",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "₹${String.format("%.2f", totalAmount)}",  // ₹ symbol
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "This Month",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

### Fix 2 — FAB (Floating Action Button)

```kotlin
// REPLACE FloatingActionButton with:
FloatingActionButton(
    onClick = onAddExpense,
    containerColor = MaterialTheme.colorScheme.primary,  // Black in light, White in dark
    contentColor   = MaterialTheme.colorScheme.onPrimary, // White in light, Black in dark
    shape = CircleShape,
    modifier = Modifier.size(56.dp)
) {
    Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Add Expense",
        modifier = Modifier.size(24.dp)
    )
}
```

---

## CHANGE 7 — Add Expense Screen UI Fixes (`AddExpenseScreen.kt`)

### Fix 1 — Amount Display Card

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    elevation = CardDefaults.cardElevation(0.dp)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "₹$displayAmount",   // ₹ symbol
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
```

### Fix 2 — Category Chips (selected = black, unselected = outlined)

```kotlin
// Replace chip implementation:
LazyRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(horizontal = 16.dp)
) {
    items(categories) { category ->
        val isSelected = selectedCategory?.id == category.id
        FilterChip(
            selected = isSelected,
            onClick  = { onCategorySelected(category) },
            label    = {
                Text(
                    text  = "${category.emoji} ${category.name}",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                // UNSELECTED state
                containerColor         = Color.Transparent,
                labelColor             = MaterialTheme.colorScheme.onBackground,
                // SELECTED state — solid black in light, solid white in dark
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor     = MaterialTheme.colorScheme.onPrimary,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled        = true,
                selected       = isSelected,
                borderColor    = MaterialTheme.colorScheme.outline,
                selectedBorderColor = MaterialTheme.colorScheme.primary,
                borderWidth    = 1.dp,
                selectedBorderWidth = 0.dp
            ),
            shape = RoundedCornerShape(20.dp)
        )
    }
}
```

### Fix 3 — Save Button (top right — accent blue when active, gray when inactive)

```kotlin
TextButton(
    onClick  = onSave,
    enabled  = amount.isNotEmpty() && amount != "0",
    colors   = ButtonDefaults.textButtonColors(
        contentColor         = AccentBlue,          // Blue when active (iOS style)
        disabledContentColor = MediumGray            // Gray when disabled
    )
) {
    Text(
        text  = "Save",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}
```

### Fix 4 — Cancel Button

```kotlin
TextButton(
    onClick = onCancel,
    colors  = ButtonDefaults.textButtonColors(
        contentColor = AccentBlue    // iOS blue
    )
) {
    Text(
        text  = "Cancel",
        style = MaterialTheme.typography.titleMedium
    )
}
```

### Fix 5 — Expense Name Text Field

```kotlin
OutlinedTextField(
    value         = name,
    onValueChange = onNameChange,
    placeholder   = {
        Text(
            "Expense name",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    },
    modifier      = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
    shape         = RoundedCornerShape(12.dp),
    colors        = OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = MaterialTheme.colorScheme.onBackground,  // Black border when focused
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,         // Light gray when unfocused
        focusedTextColor     = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor   = MaterialTheme.colorScheme.onBackground,
        cursorColor          = AccentBlue,
        focusedContainerColor   = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
    ),
    singleLine = true,
    textStyle  = MaterialTheme.typography.bodyLarge
)
```

---

## CHANGE 8 — Settings Screen UI Fixes (`SettingsScreen.kt`)

### Fix — Buttons (replace purple with monochrome)

```kotlin
// Test Connection button
Button(
    onClick = onTestConnection,
    colors  = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,  // Black
        contentColor   = MaterialTheme.colorScheme.onPrimary // White
    ),
    shape  = RoundedCornerShape(12.dp)
) {
    Text("Test Connection", style = MaterialTheme.typography.labelLarge)
}

// Save button
Button(
    onClick = onSave,
    colors  = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor   = MaterialTheme.colorScheme.onPrimary
    ),
    shape = RoundedCornerShape(12.dp)
) {
    Text("Save", style = MaterialTheme.typography.labelLarge)
}
```

### Fix — Theme selector chips

```kotlin
// FIND theme selector row — replace chip colors same as category chips:
// System Default | Light | Dark
// Selected = black fill, unselected = outlined
```

---

## CHANGE 9 — `ExpenseItem.kt` Composable Fix

Replace the expense row with this clean iOS-style layout:

```kotlin
@Composable
fun ExpenseItem(
    expense: Expense,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: emoji + name + category
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Emoji in rounded square
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = expense.category.emoji,
                        fontSize = 20.sp
                    )
                }

                Column {
                    Text(
                        text  = expense.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text  = expense.category.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right: amount + sync indicator
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text  = "₹${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (!expense.isSynced) {
                    Text(
                        text  = "⏳",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
```

---

## CHANGE 10 — Currency Symbol

**Global find-and-replace across ALL Kotlin files:**

```
FIND:    "${"
REPLACE: "₹${"

FIND:    "\$"  (where used as currency prefix in strings)
REPLACE: "₹"

FIND:    text = "$$amount"
REPLACE: text = "₹$amount"
```

Also update `strings.xml`:
```xml
<string name="currency_symbol">₹</string>
```

---

## CHANGE 11 — `strings.xml` App Name Display Fix

The app name on the home screen AppBar must be shown. Ensure in `HomeScreen.kt`:

```kotlin
// Top bar — ensure this exists:
TopAppBar(
    title = {
        Text(
            text  = stringResource(R.string.app_name),  // "SpendSync"
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    },
    actions = {
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    },
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,  // White/Black
        titleContentColor = MaterialTheme.colorScheme.onBackground,
    )
)
```

---

## SUMMARY — Tell Windsurf SWE-1.6 exactly this:

```
Apply the UI_FIX_PATCH.md file. Specifically:
1. Replace Color.kt with the monochrome palette — remove ALL purple
2. Replace Theme.kt — disable dynamicColor completely, use custom black/white scheme
3. Replace Type.kt — add Inter font via Google Fonts provider
4. Add font_certs.xml to res/values/
5. Add ui-text-google-fonts dependency to app/build.gradle.kts
6. Update ic_launcher_foreground.xml with ₹ rupee symbol
7. Set ic_launcher_background to #000000
8. Fix all Button, Chip, Card colors to use colorScheme.primary (black/white)
9. Fix FAB color to colorScheme.primary
10. Replace all $ with ₹ currency symbol
11. Fix date grouping format using the formatDateHeader() function
12. Ensure TopAppBar shows "SpendSync" title

Do NOT use dynamicColor anywhere. Do NOT use purple. The theme is strictly monochrome black-and-white with AccentBlue (#007AFF) only for interactive text (Save, Cancel buttons).
```

---

*Patch applies on top of existing generated code. No full rebuild needed.*
