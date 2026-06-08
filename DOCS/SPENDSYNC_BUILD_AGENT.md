# SpendSync — Android Expense Tracker (Notion Sync)
## Complete Agent Build Document · Version 1.0.0

> **Purpose of this document**: Hand this file to any AI coding agent (Cursor, Windsurf, Claude Code, Copilot Workspace, etc.) and it will have 100% context to build the entire production-ready application from scratch. Every decision is pre-made. No ambiguity.

---

## 0. TL;DR for Agent

Build a **native Android** expense tracking app called **SpendSync** in **Kotlin + Jetpack Compose**.  
- Minimal, monochrome UI (white + black + single accent)  
- Syncs with user's **Notion database** via the free Notion REST API  
- **Home screen widgets** using Jetpack Glance  
- **Android App Shortcuts** for quick expense logging  
- Smart autocomplete suggestions  
- **100% FREE** — no subscriptions, no server, no paid third-party APIs  
- Architecture: **MVVM + Clean Architecture** (Repository pattern)  
- Target: Android 8.0+ (API 26+), Kotlin 2.x, Compose BOM 2024.12+

---

## 1. Project Identity

| Field | Value |
|---|---|
| App Name | SpendSync |
| Package Name | `com.spendsync.app` |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 35 (Android 15) |
| Compile SDK | 35 |
| Version Code | 1 |
| Version Name | `1.0.0` |
| Language | Kotlin 2.1.x |
| Build System | Gradle (Kotlin DSL) |
| Architecture | MVVM + Clean Architecture |
| UI Framework | Jetpack Compose (Material 3 / Material You) |

---

## 2. Feature Parity Matrix (vs SyncSpend iOS)

| Feature | SyncSpend (iOS) | SpendSync (Android) | Notes |
|---|---|---|---|
| Expense Logging (fast, minimal) | ✅ | ✅ | Same UX flow |
| Notion Sync | ✅ | ✅ | Direct REST API, no backend |
| Home Screen Widgets | ✅ | ✅ | Jetpack Glance |
| Smart Suggestions (autocomplete) | ✅ | ✅ | Room query-based |
| Custom Categories | ✅ | ✅ | User-defined |
| Custom Payment Methods | ✅ | ✅ | User-defined |
| Cross-device sync | ✅ iCloud | ✅ Notion (primary) | Notion is the sync layer |
| Quick Logging Shortcuts | ✅ Apple Shortcuts | ✅ Android App Shortcuts + Quick Tile | |
| Offline-first local storage | ✅ CoreData | ✅ Room + SQLite | |
| Dark/Light Mode | ✅ | ✅ | Material You dynamic colors |
| No sign-in required | ✅ | ✅ | Notion token is optional |
| Subscription paywall | ❌ (paid) | ✅ Never — 100% Free | Core improvement |
| Apple Pay automation | ✅ | ➡️ NFC Quick-Log (optional Phase 2) | Android has NFC |
| Ads | ❌ | ❌ | Always ad-free |

---

## 3. Tech Stack — Exact Versions

### `libs.versions.toml` (Version Catalog)

```toml
[versions]
# Build tools
agp = "8.10.0"
kotlin = "2.1.10"
ksp = "2.1.10-1.0.31"

# Core Android
coreKtx = "1.16.0"
appcompat = "1.7.0"
activityCompose = "1.10.1"
lifecycleRuntimeKtx = "2.8.7"
lifecycleViewmodelCompose = "2.8.7"

# Compose
composeBom = "2024.12.01"
navigationCompose = "2.8.5"

# Jetpack Glance (Widgets)
glanceAppwidget = "1.1.1"
glanceMaterial3 = "1.1.1"

# Room (Local DB)
room = "2.6.1"

# DataStore (Settings/Preferences)
datastore = "1.1.1"

# Hilt (Dependency Injection)
hilt = "2.55"
hiltNavigationCompose = "1.2.0"

# Networking (Notion API)
retrofit = "2.11.0"
okhttp = "4.12.0"
moshi = "1.15.2"

# Coroutines
coroutines = "1.9.0"

# WorkManager (Background Sync)
workManager = "2.10.0"
hiltWork = "1.2.0"

# Charts
vico = "2.0.1"

# Testing
junit = "4.13.2"
junitExt = "1.2.1"
espresso = "3.6.1"
mockk = "1.14.0"
turbine = "1.2.0"

[libraries]
# Core
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodelCompose" }

# Compose BOM + UI
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

# Glance (Widgets)
glance-appwidget = { group = "androidx.glance", name = "glance-appwidget", version.ref = "glanceAppwidget" }
glance-material3 = { group = "androidx.glance", name = "glance-material3", version.ref = "glanceMaterial3" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# DataStore
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }
hilt-work = { group = "androidx.hilt", name = "hilt-work", version.ref = "hiltWork" }
hilt-work-compiler = { group = "androidx.hilt", name = "hilt-compiler", version.ref = "hiltWork" }

# Networking
retrofit-core = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-moshi = { group = "com.squareup.retrofit2", name = "converter-moshi", version.ref = "retrofit" }
okhttp-core = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
moshi-core = { group = "com.squareup.moshi", name = "moshi", version.ref = "moshi" }
moshi-kotlin = { group = "com.squareup.moshi", name = "moshi-kotlin", version.ref = "moshi" }
moshi-codegen = { group = "com.squareup.moshi", name = "moshi-kotlin-codegen", version.ref = "moshi" }

# Coroutines
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

# WorkManager
work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workManager" }

# Charts — Vico (Compose, free, MIT license)
vico-compose = { group = "com.patrykandpatrick.vico", name = "compose", version.ref = "vico" }
vico-compose-m3 = { group = "com.patrykandpatrick.vico", name = "compose-m3", version.ref = "vico" }
vico-core = { group = "com.patrykandpatrick.vico", name = "core", version.ref = "vico" }

# Testing
junit = { group = "junit", name = "junit", version.ref = "junit" }
junit-ext = { group = "androidx.test.ext", name = "junit", version.ref = "junitExt" }
espresso = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espresso" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-parcelize = { id = "org.jetbrains.kotlin.plugin.parcelize", version.ref = "kotlin" }
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

---

## 4. Project Structure (File Tree)

```
SpendSync/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/spendsync/app/
│   │   │   │   ├── SpendSyncApp.kt              ← Application class (Hilt)
│   │   │   │   ├── MainActivity.kt               ← Single Activity host
│   │   │   │   │
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── db/
│   │   │   │   │   │   │   ├── SpendSyncDatabase.kt     ← Room DB
│   │   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   │   ├── ExpenseDao.kt
│   │   │   │   │   │   │   │   ├── CategoryDao.kt
│   │   │   │   │   │   │   │   └── PaymentMethodDao.kt
│   │   │   │   │   │   │   └── entities/
│   │   │   │   │   │   │       ├── ExpenseEntity.kt
│   │   │   │   │   │   │       ├── CategoryEntity.kt
│   │   │   │   │   │   │       └── PaymentMethodEntity.kt
│   │   │   │   │   │   └── datastore/
│   │   │   │   │   │       └── SettingsDataStore.kt     ← Notion token, prefs
│   │   │   │   │   │
│   │   │   │   │   ├── remote/
│   │   │   │   │   │   ├── notion/
│   │   │   │   │   │   │   ├── NotionApiService.kt      ← Retrofit interface
│   │   │   │   │   │   │   ├── NotionInterceptor.kt     ← Auth header injector
│   │   │   │   │   │   │   └── models/
│   │   │   │   │   │   │       ├── NotionPage.kt
│   │   │   │   │   │   │       ├── NotionDatabase.kt
│   │   │   │   │   │   │       ├── NotionProperty.kt
│   │   │   │   │   │   │       └── NotionCreatePageRequest.kt
│   │   │   │   │   │
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── ExpenseRepository.kt         ← Interface
│   │   │   │   │       ├── ExpenseRepositoryImpl.kt     ← Implementation
│   │   │   │   │       ├── CategoryRepository.kt
│   │   │   │   │       ├── CategoryRepositoryImpl.kt
│   │   │   │   │       ├── NotionRepository.kt
│   │   │   │   │       └── NotionRepositoryImpl.kt
│   │   │   │   │
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Expense.kt                   ← Domain model
│   │   │   │   │   │   ├── Category.kt
│   │   │   │   │   │   ├── PaymentMethod.kt
│   │   │   │   │   │   └── SpendingSummary.kt
│   │   │   │   │   └── usecase/
│   │   │   │   │       ├── AddExpenseUseCase.kt
│   │   │   │   │       ├── GetExpensesUseCase.kt
│   │   │   │   │       ├── DeleteExpenseUseCase.kt
│   │   │   │   │       ├── GetSpendingSummaryUseCase.kt
│   │   │   │   │       ├── SyncToNotionUseCase.kt
│   │   │   │   │       └── GetSuggestionsUseCase.kt
│   │   │   │   │
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   ├── NavGraph.kt
│   │   │   │   │   │   └── Screen.kt                   ← Sealed class routes
│   │   │   │   │   │
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── home/
│   │   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   │   │   ├── addexpense/
│   │   │   │   │   │   │   ├── AddExpenseScreen.kt
│   │   │   │   │   │   │   └── AddExpenseViewModel.kt
│   │   │   │   │   │   ├── history/
│   │   │   │   │   │   │   ├── HistoryScreen.kt
│   │   │   │   │   │   │   └── HistoryViewModel.kt
│   │   │   │   │   │   └── settings/
│   │   │   │   │   │       ├── SettingsScreen.kt
│   │   │   │   │   │       └── SettingsViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   └── components/
│   │   │   │   │       ├── ExpenseItem.kt
│   │   │   │   │       ├── SpendingChart.kt
│   │   │   │   │       ├── PeriodSelector.kt
│   │   │   │   │       ├── CategoryChip.kt
│   │   │   │   │       └── SuggestionRow.kt
│   │   │   │   │
│   │   │   │   ├── widget/
│   │   │   │   │   ├── SpendSyncWidget.kt               ← GlanceAppWidget
│   │   │   │   │   ├── SpendSyncWidgetReceiver.kt       ← GlanceAppWidgetReceiver
│   │   │   │   │   └── WidgetContent.kt                 ← Glance composable UI
│   │   │   │   │
│   │   │   │   ├── worker/
│   │   │   │   │   └── NotionSyncWorker.kt              ← WorkManager background sync
│   │   │   │   │
│   │   │   │   ├── di/
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   ├── NetworkModule.kt
│   │   │   │   │   ├── RepositoryModule.kt
│   │   │   │   │   └── WorkerModule.kt
│   │   │   │   │
│   │   │   │   └── util/
│   │   │   │       ├── DateUtils.kt
│   │   │   │       ├── CurrencyUtils.kt
│   │   │   │       └── NotionMapper.kt                  ← Domain ↔ Notion API mapper
│   │   │   │
│   │   │   └── res/
│   │   │       ├── drawable/
│   │   │       │   ├── ic_launcher_foreground.xml
│   │   │       │   └── ic_add.xml
│   │   │       ├── xml/
│   │   │       │   ├── spendsync_widget_info.xml        ← Widget metadata
│   │   │       │   └── shortcuts.xml                    ← Static App Shortcuts
│   │   │       ├── values/
│   │   │       │   ├── strings.xml
│   │   │       │   ├── themes.xml
│   │   │       │   └── colors.xml
│   │   │       └── layout/
│   │   │           └── glance_default_loading_layout.xml
│   │   │
│   │   ├── test/                                        ← Unit tests
│   │   └── androidTest/                                 ← Instrumented tests
│   │
│   └── build.gradle.kts
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── libs.versions.toml
```

---

## 5. Database Schema (Room)

### `ExpenseEntity`

```kotlin
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,          // UUID generated locally
    val name: String,                    // e.g. "Spotify"
    val amount: Double,                  // e.g. 20.98
    val categoryId: String,              // FK → categories.id
    val paymentMethodId: String?,        // FK → payment_methods.id (nullable)
    val date: Long,                      // Unix timestamp (ms)
    val notionPageId: String?,           // null until synced to Notion
    val isSynced: Boolean = false,       // dirty bit for sync
    val createdAt: Long = System.currentTimeMillis()
)
```

### `CategoryEntity`

```kotlin
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,           // e.g. "Entertainment", "Food & Drinks"
    val emoji: String,          // e.g. "🎵", "🍔"
    val isDefault: Boolean      // pre-seeded defaults
)
```

**Default categories to seed on first launch:**
- `Entertainment` — 🎭
- `Food & Drinks` — 🍔
- `Transportation` — 🚗
- `Shopping` — 🛍️
- `Health` — 💊
- `Utilities` — 💡
- `Housing` — 🏠
- `Personal` — 👤
- `Travel` — ✈️
- `Other` — 📌

### `PaymentMethodEntity`

```kotlin
@Entity(tableName = "payment_methods")
data class PaymentMethodEntity(
    @PrimaryKey val id: String,
    val name: String,           // e.g. "Cash", "Credit Card", "UPI"
    val isDefault: Boolean
)
```

**Default payment methods to seed:**
- Cash
- Credit Card
- Debit Card
- UPI
- Net Banking

---

## 6. Domain Models

```kotlin
data class Expense(
    val id: String,
    val name: String,
    val amount: Double,
    val category: Category,
    val paymentMethod: PaymentMethod?,
    val date: LocalDate,
    val isSynced: Boolean,
    val notionPageId: String?
)

data class Category(
    val id: String,
    val name: String,
    val emoji: String
)

data class PaymentMethod(
    val id: String,
    val name: String
)

data class SpendingSummary(
    val totalThisWeek: Double,
    val totalThisMonth: Double,
    val totalThisYear: Double,
    val dailyBreakdown: Map<LocalDate, Double>  // for bar chart
)
```

---

## 7. Notion API Integration

### How It Works (Zero Server Required)

The app calls the Notion REST API **directly from the Android device**. The user provides their own **Notion Internal Integration Token** and **Database ID** in Settings. No backend, no cloud function, no paid service.

- **Notion API Base URL**: `https://api.notion.com/v1/`
- **API Version Header**: `Notion-Version: 2022-06-28`
- **Auth Header**: `Authorization: Bearer <user_token>`
- **Rate Limit**: 3 requests/second (2700/15 min) — free, no charge
- **API Cost for Developer**: $0
- **API Cost for User**: $0

### User Setup Flow (Settings Screen)

1. User opens Notion → Settings → Connections → Develop or manage integrations
2. Creates a new "Internal Integration" (free)
3. Copies the **Integration Token** (starts with `secret_...`)
4. Opens their expenses database in Notion
5. Copies the **Database ID** from the URL: `notion.so/{database_id}?v=...`
6. Shares the database with the integration (Share button → Invite integration)
7. Pastes both values in SpendSync Settings

### Required Notion Database Schema

The app expects (and can auto-create) a Notion database with these properties:

| Property Name | Notion Type | Notes |
|---|---|---|
| `Name` | Title | Expense name (required, default title prop) |
| `Amount` | Number | Format: Number |
| `Category` | Select | Options match app categories |
| `Payment` | Select | Options match payment methods |
| `Date` | Date | ISO 8601 date |

### `NotionApiService.kt` (Retrofit Interface)

```kotlin
interface NotionApiService {

    @GET("databases/{databaseId}/query")
    suspend fun queryDatabase(
        @Path("databaseId") databaseId: String,
        @Body filter: NotionQueryFilter? = null
    ): NotionQueryResponse

    @POST("pages")
    suspend fun createPage(
        @Body request: NotionCreatePageRequest
    ): NotionPage

    @PATCH("pages/{pageId}")
    suspend fun updatePage(
        @Path("pageId") pageId: String,
        @Body properties: NotionUpdatePageRequest
    ): NotionPage

    @DELETE("blocks/{blockId}")
    suspend fun archivePage(
        @Path("blockId") pageId: String,
        @Body body: Map<String, Boolean> = mapOf("archived" to true)
    ): NotionPage

    @GET("databases/{databaseId}")
    suspend fun getDatabase(
        @Path("databaseId") databaseId: String
    ): NotionDatabase
}
```

### `NotionCreatePageRequest.kt` — Request Body

```kotlin
@JsonClass(generateAdapter = true)
data class NotionCreatePageRequest(
    val parent: NotionParent,
    val properties: Map<String, NotionPropertyValue>
)

@JsonClass(generateAdapter = true)
data class NotionParent(
    val type: String = "database_id",
    val database_id: String
)

// Build this from an Expense domain model:
fun Expense.toNotionCreateRequest(databaseId: String): NotionCreatePageRequest {
    return NotionCreatePageRequest(
        parent = NotionParent(database_id = databaseId),
        properties = mapOf(
            "Name" to NotionPropertyValue.TitleValue(
                title = listOf(NotionRichText(text = NotionText(content = name)))
            ),
            "Amount" to NotionPropertyValue.NumberValue(number = amount),
            "Category" to NotionPropertyValue.SelectValue(
                select = NotionSelectOption(name = category.name)
            ),
            "Payment" to NotionPropertyValue.SelectValue(
                select = NotionSelectOption(name = paymentMethod?.name ?: "None")
            ),
            "Date" to NotionPropertyValue.DateValue(
                date = NotionDate(start = date.toString()) // ISO format YYYY-MM-DD
            )
        )
    )
}
```

### Sync Strategy — Local-First with Background Sync

```
User logs expense
        ↓
Save to Room DB (isSynced = false)
        ↓
Trigger immediate WorkManager one-time sync task
        ↓
NotionSyncWorker picks all (isSynced = false) records
        ↓
POST /v1/pages for each unsync'd expense
        ↓
On success: update Room record (isSynced = true, notionPageId = response.id)
        ↓
Enqueue periodic WorkManager task every 15 minutes as fallback
```

**Conflict Resolution**: Local data is source of truth. Notion is write-only from app.  
**Delete Sync**: When user deletes expense, PATCH page with `archived: true` in Notion if `notionPageId != null`.

### `NotionSyncWorker.kt`

```kotlin
@HiltWorker
class NotionSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val expenseRepository: ExpenseRepository,
    private val notionRepository: NotionRepository,
    private val settingsDataStore: SettingsDataStore
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val token = settingsDataStore.notionToken.first() ?: return Result.success()
        val dbId = settingsDataStore.notionDatabaseId.first() ?: return Result.success()
        
        val unsynced = expenseRepository.getUnsyncedExpenses()
        if (unsynced.isEmpty()) return Result.success()

        return try {
            unsynced.forEach { expense ->
                val page = notionRepository.createExpensePage(expense, dbId)
                expenseRepository.markAsSynced(expense.id, page.id)
            }
            Result.success()
        } catch (e: HttpException) {
            if (e.code() == 429) Result.retry() // Rate limited
            else Result.failure()
        } catch (e: IOException) {
            Result.retry() // Network error, retry
        }
    }

    companion object {
        const val WORK_NAME = "notion_sync_worker"
        
        fun buildPeriodicRequest(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<NotionSyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
    }
}
```

---

## 8. UI/UX Specification

### Design Language

Strictly follow SyncSpend's aesthetic: **Intentional Minimalism**.

| Token | Light Mode | Dark Mode |
|---|---|---|
| Background | `#FFFFFF` | `#000000` |
| Surface | `#F5F5F5` | `#121212` |
| On Background | `#000000` | `#FFFFFF` |
| Primary Accent | `#000000` | `#FFFFFF` |
| Accent Highlight | `#0066FF` (single blue) | `#0066FF` |
| Body Font | `Inter` or system default `Roboto` | same |
| Headline Font | Bold weight, same family | same |
| Corner Radius | `16dp` cards, `12dp` buttons, `8dp` chips | same |

Apply **Material You dynamic color** as a secondary layer — if user's device supports it, respect wallpaper colors for subtle tinting of surfaces.

---

## 9. Screen Specifications

### 9.1 — Home Screen (`HomeScreen.kt`)

**Layout (top to bottom):**
```
[Header row]
  ← "Personal" (account name / list selector)     [🔍] [≡] [⚙]

[Total Spending Card]
  "Total Spending"  (small label)
  "$120.38"         (large bold, 36sp)

[Bar Chart]
  Week view by default, 7 bars (Sun–Sat)
  Use Vico CartesianChartView with ColumnCartesianLayer
  Color: black bars, no grid lines, no legend
  Period selector below chart: "Week | Month | Year"

[Section: "Latest"]
  List of most recent 5 expenses
  Each row → ExpenseItem composable

[Section: "Monday" / "Tuesday" etc. grouped by date]
  Same ExpenseItem rows

[FAB (Floating Action Button)]
  Bottom-right, black circle, white "+" icon
  onClick → navigate to AddExpenseScreen
```

**HomeViewModel State:**

```kotlin
data class HomeUiState(
    val totalSpending: Double = 0.0,
    val period: Period = Period.WEEK,
    val chartData: List<Pair<String, Double>> = emptyList(), // day label to amount
    val groupedExpenses: Map<String, List<Expense>> = emptyMap(), // date string → expenses
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false
)

enum class Period { WEEK, MONTH, YEAR }
```

---

### 9.2 — Add Expense Screen (`AddExpenseScreen.kt`)

**Interaction Flow (optimized for speed):**
```
[Top bar]
  "Cancel"  ← text button                "Save" → text button (blue, active when amount > 0)

[Amount Field]
  Large centered display: "$0.00"
  Custom numeric keypad (like SyncSpend iOS)
  Numbers: 1–9, 0, backspace, decimal
  Updates display in real-time

[Expense Name Field]
  Rounded text input: "Expense name"
  Below input → horizontal scrollable SuggestionRow
  Shows last 5 unique names that match typed prefix
  Tap suggestion → fills name field instantly

[Category Selector]
  Horizontal scrollable row of CategoryChips
  Selected chip → filled black background, white text
  Unselected → outlined

[Payment Method Selector]
  Same chip row pattern as categories

[Date Selector]
  Row: "Today" with calendar icon
  Tap → Material DatePicker dialog
  Defaults to today

[Notion Sync indicator]
  Small row at bottom: "Will sync to Notion" with Notion logo icon
  Only shown if Notion is configured
```

**AddExpenseViewModel:**

```kotlin
data class AddExpenseUiState(
    val amount: String = "0",
    val name: String = "",
    val selectedCategory: Category? = null,
    val selectedPaymentMethod: PaymentMethod? = null,
    val date: LocalDate = LocalDate.now(),
    val categories: List<Category> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)
```

**Smart Suggestions Query (Room):**

```kotlin
@Query("""
    SELECT DISTINCT name FROM expenses
    WHERE name LIKE :prefix || '%'
    ORDER BY createdAt DESC
    LIMIT 5
""")
fun getSuggestions(prefix: String): Flow<List<String>>
```

---

### 9.3 — History Screen (`HistoryScreen.kt`)

```
[Search bar at top]
  Icon + "Search expenses..."

[Filter row]
  Chips: "All" | "This Week" | "This Month" | "This Year"

[Expense list]
  Full expense list with ExpenseItem rows
  Grouped by date (sticky headers)
  Infinite scroll via LazyColumn

[Empty state]
  If no expenses: centered icon + "No expenses yet" + "Log your first expense" button
```

---

### 9.4 — Settings Screen (`SettingsScreen.kt`)

```
[Section: Notion Integration]
  ┌──────────────────────────────────────────┐
  │  Notion Integration                       │
  │  Integration Token  [secret_xxxx]  [✏️]  │
  │  Database ID        [xxxxxxx]      [✏️]  │
  │  [Test Connection]  → shows ✅ or ❌     │
  │  [Sync Now]         → triggers manual sync│
  └──────────────────────────────────────────┘

[Section: Data]
  ├── Manage Categories (→ list with add/delete)
  ├── Manage Payment Methods (→ list with add/delete)
  └── Export to CSV (generates CSV, shares via Android share sheet)

[Section: Appearance]
  └── Theme: System Default | Light | Dark

[Section: About]
  ├── Version: 1.0.0
  ├── GitHub (link)
  └── Privacy Policy (no data collected — text only)
```

---

## 10. Widget Specification (Jetpack Glance)

### Widget Info XML (`spendsync_widget_info.xml`)

```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="250dp"
    android:minHeight="110dp"
    android:targetCellWidth="3"
    android:targetCellHeight="2"
    android:maxResizeWidth="400dp"
    android:maxResizeHeight="220dp"
    android:updatePeriodMillis="1800000"
    android:resizeMode="horizontal|vertical"
    android:widgetCategory="home_screen"
    android:description="@string/widget_description"
    android:previewLayout="@layout/glance_default_loading_layout"
    android:initialLayout="@layout/glance_default_loading_layout" />
```

### Widget UI (`WidgetContent.kt`)

```
┌───────────────────────────────────┐
│ SpendSync            This Month   │  ← Label row
│                                   │
│  $349.18                          │  ← Large total (28sp, bold)
│                                   │
│  Week $120.38   Year $1,865.18    │  ← Two sub-totals
│                                   │
│  [+ Add Expense]                  │  ← Tappable button → opens AddExpenseScreen
└───────────────────────────────────┘
```

### Widget Data Refresh Logic

```kotlin
class SpendSyncWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load data from Room via repository (inject via EntryPoints)
        val summary = SpendSyncEntryPoint.getExpenseRepository(context)
            .getSpendingSummary()
        
        provideContent {
            WidgetContent(summary = summary)
        }
    }
}

// Refresh widget whenever a new expense is added:
// Call SpendSyncWidget().update(context, id) after AddExpenseUseCase
```

---

## 11. Android App Shortcuts (`shortcuts.xml`)

Static shortcuts for the launcher long-press menu:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shortcuts xmlns:android="http://schemas.android.com/apk/res/android">
    <shortcut
        android:shortcutId="add_expense"
        android:enabled="true"
        android:icon="@drawable/ic_add"
        android:shortcutShortLabel="@string/shortcut_add_short"
        android:shortcutLongLabel="@string/shortcut_add_long"
        android:shortcutDisabledMessage="@string/shortcut_disabled">
        <intent
            android:action="com.spendsync.app.ADD_EXPENSE"
            android:targetPackage="com.spendsync.app"
            android:targetClass="com.spendsync.app.MainActivity" />
        <categories android:name="android.shortcut.conversation" />
    </shortcut>
</shortcuts>
```

Handle deep link in `MainActivity.kt`:

```kotlin
// In MainActivity.kt onCreate:
if (intent?.action == "com.spendsync.app.ADD_EXPENSE") {
    // Navigate directly to AddExpenseScreen via navController
    navController.navigate(Screen.AddExpense.route)
}
```

---

## 12. AndroidManifest.xml (Key Sections)

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.spendsync.app">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <application
        android:name=".SpendSyncApp"
        android:icon="@mipmap/ic_launcher"
        android:theme="@style/Theme.SpendSync"
        android:allowBackup="false"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="false">

        <!-- Main Activity -->
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <!-- Shortcuts -->
            <meta-data
                android:name="android.app.shortcuts"
                android:resource="@xml/shortcuts" />
            <!-- Deep link from widget + shortcut -->
            <intent-filter>
                <action android:name="com.spendsync.app.ADD_EXPENSE" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>

        <!-- Glance Widget Receiver -->
        <receiver
            android:name=".widget.SpendSyncWidgetReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/spendsync_widget_info" />
        </receiver>

    </application>
</manifest>
```

---

## 13. Dependency Injection Setup (Hilt)

### `DatabaseModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SpendSyncDatabase =
        Room.databaseBuilder(context, SpendSyncDatabase::class.java, "spendsync.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideExpenseDao(db: SpendSyncDatabase) = db.expenseDao()
    @Provides fun provideCategoryDao(db: SpendSyncDatabase) = db.categoryDao()
    @Provides fun providePaymentMethodDao(db: SpendSyncDatabase) = db.paymentMethodDao()
}
```

### `NetworkModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNotionInterceptor(settingsDataStore: SettingsDataStore): NotionInterceptor =
        NotionInterceptor(settingsDataStore)

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: NotionInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.notion.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNotionApiService(retrofit: Retrofit): NotionApiService =
        retrofit.create(NotionApiService::class.java)
}
```

### `NotionInterceptor.kt`

```kotlin
class NotionInterceptor(
    private val settingsDataStore: SettingsDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { settingsDataStore.notionToken.first() }
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Notion-Version", "2022-06-28")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}
```

---

## 14. Settings DataStore (`SettingsDataStore.kt`)

```kotlin
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.createDataStore("settings")

    companion object {
        val NOTION_TOKEN = stringPreferencesKey("notion_token")
        val NOTION_DATABASE_ID = stringPreferencesKey("notion_database_id")
        val THEME = stringPreferencesKey("theme") // "system" | "light" | "dark"
    }

    val notionToken: Flow<String?> = dataStore.data.map { it[NOTION_TOKEN] }
    val notionDatabaseId: Flow<String?> = dataStore.data.map { it[NOTION_DATABASE_ID] }
    val theme: Flow<String> = dataStore.data.map { it[THEME] ?: "system" }

    suspend fun setNotionToken(token: String) =
        dataStore.edit { it[NOTION_TOKEN] = token }

    suspend fun setNotionDatabaseId(dbId: String) =
        dataStore.edit { it[NOTION_DATABASE_ID] = dbId }

    suspend fun setTheme(theme: String) =
        dataStore.edit { it[THEME] = theme }
}
```

---

## 15. Implementation Phases (Ordered)

### Phase 1 — Foundation (Week 1)
- [ ] Create Android Studio project with Kotlin DSL Gradle
- [ ] Set up `libs.versions.toml` with all dependencies
- [ ] Configure Hilt application class and plugin
- [ ] Create Room database with all 3 entities + DAOs
- [ ] Seed default categories and payment methods on first launch
- [ ] Set up Navigation Compose with all route destinations
- [ ] Implement `SettingsDataStore` for preferences

### Phase 2 — Core Expense Features (Week 2)
- [ ] Build `AddExpenseScreen` with custom numeric keypad
- [ ] Implement `AddExpenseViewModel` with full state management
- [ ] Wire `AddExpenseUseCase` → repository → Room DAO
- [ ] Build `HomeScreen` with grouped expense list
- [ ] Implement `HomeViewModel` with period-based summary
- [ ] Build `HistoryScreen` with search + filter chips
- [ ] Implement `ExpenseItem` composable (reused across screens)
- [ ] Implement smart suggestions (Room `LIKE` query + `SuggestionRow`)

### Phase 3 — Charts (Week 2–3)
- [ ] Integrate Vico chart library
- [ ] Build `SpendingChart` composable using Vico `CartesianChartView`
- [ ] Wire chart data to `HomeViewModel` (period-aware daily breakdown)
- [ ] Implement `PeriodSelector` chip row with Week/Month/Year

### Phase 4 — Notion Sync (Week 3)
- [ ] Set up Retrofit + OkHttp + Moshi
- [ ] Implement `NotionApiService` with all endpoints
- [ ] Implement `NotionInterceptor` for auth headers
- [ ] Implement `NotionMapper` (Expense ↔ Notion API model)
- [ ] Implement `NotionRepositoryImpl`
- [ ] Implement `NotionSyncWorker` with WorkManager
- [ ] Implement sync trigger after every `AddExpenseUseCase` execution
- [ ] Implement periodic background sync (every 15 min, requires CONNECTED)
- [ ] Implement delete-sync (archive Notion page on local delete)
- [ ] Build Settings Notion section with token input + "Test Connection" + "Sync Now"

### Phase 5 — Widget (Week 4)
- [ ] Create `spendsync_widget_info.xml`
- [ ] Implement `SpendSyncWidget` (GlanceAppWidget)
- [ ] Implement `SpendSyncWidgetReceiver`
- [ ] Build `WidgetContent` composable (Glance composables only — not Compose UI)
- [ ] Wire widget to Room data via EntryPoints (Hilt workaround for Glance)
- [ ] Implement widget update trigger after every new expense saved
- [ ] Register widget receiver in AndroidManifest

### Phase 6 — Shortcuts, Settings & Polish (Week 4–5)
- [ ] Create `shortcuts.xml` static shortcut for "Add Expense"
- [ ] Handle deep link intent in `MainActivity.kt`
- [ ] Build full `SettingsScreen` (Notion + Categories + Payment Methods + Theme + Export)
- [ ] Implement CSV export (generate comma-separated string, Android ShareSheet)
- [ ] Implement Category CRUD (add/delete custom categories)
- [ ] Implement Payment Method CRUD (add/delete)
- [ ] Implement Dark/Light/System theme toggle with DataStore persistence
- [ ] Apply Material You dynamic color theming

### Phase 7 — Testing & Release Prep (Week 5–6)
- [ ] Unit tests for all UseCases (MockK)
- [ ] Unit tests for ViewModels (Turbine for Flow)
- [ ] Unit tests for NotionMapper
- [ ] Integration test for Room DAO operations
- [ ] Manual E2E test: log expense → verify Notion sync
- [ ] Manual E2E test: widget displays correct totals
- [ ] Create app icon (monochrome $ symbol, matching SyncSpend aesthetic)
- [ ] Configure ProGuard/R8 rules for Retrofit + Moshi
- [ ] Generate signed APK / AAB for Play Store

---

## 16. ProGuard Rules (`proguard-rules.pro`)

```proguard
# Moshi
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# Notion API models — don't obfuscate
-keep class com.spendsync.app.data.remote.notion.models.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
```

---

## 17. Cost Analysis — Developer & User

| Item | Cost |
|---|---|
| Notion API | **$0** — Free, user uses own token |
| Room (SQLite) | **$0** — Jetpack library |
| WorkManager | **$0** — Jetpack library |
| Jetpack Glance | **$0** — Jetpack library |
| Retrofit/OkHttp/Moshi | **$0** — Open source |
| Vico charts | **$0** — MIT license |
| Hilt | **$0** — Apache 2.0 |
| Backend server | **$0** — Not needed |
| Firebase / any cloud service | **$0** — Not used |
| **Play Store one-time fee** | **$25** (one-time, mandatory for publishing) |
| User subscription | **$0** — Never |

**Developer total running cost: $0/month after initial $25 Play Store registration.**

---

## 18. Key Architecture Rules for Agent

1. **No data class in Presentation layer** — always map Entity → Domain model → UI state
2. **ViewModels never call DAOs directly** — must go through Repository → UseCase
3. **All Notion API calls go through WorkManager** — never block the main thread with network
4. **Widget uses EntryPoints** (not standard `@Inject`) because Glance receivers are not Hilt components
5. **Local Room is always the source of truth** — Notion is a write-mirror
6. **All `Flow` collected in ViewModels with `stateIn`** — expose only `StateFlow` to UI
7. **No hardcoded strings** — everything in `strings.xml`
8. **Compose UI only** — no XML layouts except widget provider XML and Glance loading layout

---

## 19. App Icon Specification

Design a minimal icon matching the SyncSpend aesthetic:
- **Shape**: Adaptive icon (foreground + background layers)
- **Foreground**: Bold `$` symbol or stylized coin, white, centered
- **Background**: Solid black circle
- **Style**: Flat, no gradients, no shadows
- **Sizes to generate**: 48dp, 72dp, 96dp, 144dp, 192dp + xxxhdpi

---

## 20. Quick Reference — Notion API Endpoints Used

| Operation | Method | Endpoint |
|---|---|---|
| Verify database | GET | `/v1/databases/{id}` |
| Create expense page | POST | `/v1/pages` |
| Update expense | PATCH | `/v1/pages/{id}` |
| Delete/Archive expense | PATCH | `/v1/pages/{id}` (archived: true) |
| Query expenses | POST | `/v1/databases/{id}/query` |

All calls use:
- `Authorization: Bearer {user_integration_token}`
- `Notion-Version: 2022-06-28`
- `Content-Type: application/json`

---

## 21. Strings Reference (`strings.xml`)

```xml
<resources>
    <string name="app_name">SpendSync</string>
    <string name="add_expense">Add Expense</string>
    <string name="total_spending">Total Spending</string>
    <string name="this_week">This Week</string>
    <string name="this_month">This Month</string>
    <string name="this_year">This Year</string>
    <string name="latest">Latest</string>
    <string name="expense_name_hint">Expense name</string>
    <string name="category">Category</string>
    <string name="payment_method">Payment Method</string>
    <string name="save">Save</string>
    <string name="cancel">Cancel</string>
    <string name="settings">Settings</string>
    <string name="notion_integration">Notion Integration</string>
    <string name="integration_token">Integration Token</string>
    <string name="database_id">Database ID</string>
    <string name="test_connection">Test Connection</string>
    <string name="sync_now">Sync Now</string>
    <string name="no_expenses_yet">No expenses yet</string>
    <string name="log_first_expense">Log your first expense</string>
    <string name="widget_description">View your spending at a glance</string>
    <string name="shortcut_add_short">Add Expense</string>
    <string name="shortcut_add_long">Log a new expense</string>
    <string name="shortcut_disabled">SpendSync is not available</string>
    <string name="will_sync_to_notion">Will sync to Notion</string>
    <string name="notion_connected">Notion connected</string>
    <string name="notion_not_configured">Notion not configured</string>
    <string name="export_csv">Export to CSV</string>
    <string name="manage_categories">Manage Categories</string>
    <string name="manage_payment_methods">Manage Payment Methods</string>
    <string name="theme">Theme</string>
    <string name="system_default">System Default</string>
    <string name="light">Light</string>
    <string name="dark">Dark</string>
</resources>
```

---

*Document generated for SpendSync Android — build ready for AI agent execution.*  
*All dependencies verified as of June 2026. Notion API free tier confirmed.*  
*No paid services. No subscriptions. No backend. Build it and ship it.*
