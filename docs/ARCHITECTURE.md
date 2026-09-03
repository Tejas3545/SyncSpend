# SyncSpend Android Architecture

## 1. Overview
SyncSpend is a 100% native Android application built with Kotlin, Jetpack Compose, Room, WorkManager, and clean architecture principles. It operates on an offline-first foundation with zero cloud dependencies required for core functionality.

## 2. Architectural Layers

### Data Layer (`com.spendsync.app.data`)
- **Local (`data.local`)**:
  - `db/`: Room database (`ExpenseDatabase`), DAO interfaces (`ExpenseDao`), and entities (`ExpenseEntity`).
  - `datastore/`: Encrypted/Preferences DataStore for auth credentials (`AuthDataStore`) and app configuration (`SettingsDataStore`).
- **Remote (`data.remote`)**:
  - `notion/`: Retrofit API interfaces, authentication interceptor, and payload models for the Notion REST API v1.
- **Repository (`data.repository`)**:
  - Implementations of domain repository interfaces (`ExpenseRepositoryImpl`, `CategoryRepositoryImpl`, `GoogleSheetsRepositoryImpl`, `NotionRepositoryImpl`).

### Domain Layer (`com.spendsync.app.domain`)
- **Models (`domain.model`)**:
  - `Expense`, `Category`, `PaymentMethod`, `SpendingSummary`.
- **Repositories (`domain.repository`)**:
  - Pure Kotlin repository abstractions defining data contracts.
- **Use Cases (`domain.usecase`)**:
  - Single-responsibility business actions (`AddExpenseUseCase`, `DeleteExpenseUseCase`, `GetExpensesUseCase`, `GetSpendingSummaryUseCase`, `GetSuggestionsUseCase`, `SyncToNotionUseCase`).

### Presentation Layer (`com.spendsync.app.presentation`)
- **Navigation (`presentation.navigation`)**:
  - Type-safe Compose navigation routes (`Screen`) and navigation graph (`SpendSyncNavGraph`).
- **Screens (`presentation.screens`)**:
  - `home/`: Main dashboard, 7-day spending chart, grouped expenses, account switcher, and widget sheets.
  - `addexpense/`: Fast entry keypad, custom category picker, and smart suggestion chips.
  - `history/`: Searchable chronological expense log with live filtering.
  - `settings/`: Cloud synchronization management (Google Sheets & Notion), appearance theme switcher, and diagnostics.
  - `auth/`: Clean onboarding flow and account setup.

### Background Sync & Workers (`com.spendsync.app.worker`)
- `GoogleSyncWorker`: Periodic and on-demand drain queue to personal Google Sheets via Google Drive/Sheets API v4.
- `NotionSyncWorker`: Queue worker syncing local expenses to the user's private Notion database.

### Widgets & Shortcuts
- `widget/SyncSpendWidget.kt`: Android home screen widget built using Jetpack Glance.
- `service/QuickAddTileService.kt`: Quick Settings tile for instantaneous expense entry from any app.

## 3. UI Design System (`com.spendsync.app.ui.theme`)
- **Typography**: Apple San Francisco / Material Inter typography scale.
- **Theme**: True black `#0B0C10` AMOLED dark theme and crisp neutral `#F2F2F7` light theme.
- **Components**: Rounded corner surfaces (`RoundedCornerShape(20.dp)`), high-contrast borders, and Indian Rupee (`₹`) monetary representation.
