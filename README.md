# SyncSpend - Android Expense Tracker

A 100% free, native Android expense tracking application with Notion sync. No subscriptions, no payments, no ads.

## Features

- **Expense Tracking**: Log expenses quickly with a clean, minimal UI
- **Notion Sync**: Sync your expenses to your personal Notion database (free API)
- **Home Screen Widget**: View your spending at a glance with Jetpack Glance
- **Smart Suggestions**: Autocomplete suggestions based on your expense history
- **Custom Categories**: Manage your own expense categories
- **Payment Methods**: Track how you pay (Cash, Credit Card, UPI, etc.)
- **Offline-First**: Works without internet, syncs when connected
- **Dark/Light Theme**: System default, light, or dark mode
- **App Shortcuts**: Quick access to add expenses from your home screen

## Tech Stack

- **Language**: Kotlin 2.1.10
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Local Database**: Room (SQLite)
- **Background Sync**: WorkManager
- **Networking**: Retrofit + OkHttp + Moshi
- **Widget**: Jetpack Glance
- **Charts**: Vico (MIT licensed)

## Requirements

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 35 (Android 15)
- JDK 17
- Minimum SDK: Android 8.0 (API 26)

## Building the Project

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run on an Android device or emulator

```bash
./gradlew assembleDebug
```

## Google Sheets + offline sync

1. Open **Settings → Google Sheets → Connect** and choose a Google account.
2. Approve Drive-file and Sheets access. SyncSpend creates a private `SyncSpend Expenses` spreadsheet owned by that account.
3. Add expenses normally, with or without a network connection. Every expense is committed to Room first.
4. When connectivity is available, WorkManager automatically drains the independent Google Sheets and Notion queues. Google rows are keyed by expense ID to prevent duplicate appends during retries.
5. You can connect Google Sheets, Notion, both, or neither. Disconnecting a destination never deletes local or previously uploaded data.

## Setting Up Notion Integration

To enable Notion sync:

1. Go to [Notion](https://www.notion.so) → Settings → Connections → Develop or manage integrations
2. Create a new "Internal Integration" (free)
3. Copy the **Integration Token** (starts with `secret_...`)
4. Create a new database in Notion with these properties:
   - `Name` (Title)
   - `Amount` (Number)
   - `Category` (Select)
   - `Payment` (Select)
   - `Date` (Date)
5. Copy the **Database ID** from the URL: `notion.so/{database_id}?v=...`
6. Share the database with your integration (Share button → Invite integration)
7. Open SpendSync → Settings → Notion Integration
8. Paste the Integration Token and Database ID
9. Tap "Test Connection" to verify
10. Tap "Save" to save your settings

## Project Structure

```
SyncSpend/
├── .github/
│   └── workflows/
│       └── android.yml        # Continuous Integration build workflow
├── app/                       # Android Application Module
│   ├── src/main/
│   │   ├── java/com/spendsync/app/
│   │   │   ├── data/          # Local (Room, DataStore) & Remote (Notion, Google Sheets)
│   │   │   ├── domain/        # Entities, Repository Interfaces & Use Cases
│   │   │   ├── presentation/  # Screens (Home, Add, History, Settings) & Components
│   │   │   ├── di/            # Hilt Dependency Injection Modules
│   │   │   ├── worker/        # WorkManager Background Sync Workers
│   │   │   ├── widget/        # Jetpack Glance Home Screen Widget
│   │   │   ├── ui/theme/      # Theme, Colors, Typography (High-contrast Dark/Light)
│   │   │   └── util/          # Currency & Date Formatters
│   │   └── res/               # Vector Drawables, Layouts, Strings & Fonts
│   └── build.gradle.kts       # App-level build configuration
├── art/                       # App branding, icons, and vector graphics
├── docs/                      # Technical documentation & product roadmap
│   ├── ARCHITECTURE.md        # Architecture overview & layer documentation
│   └── ROADMAP.md             # Product, privacy, and release roadmap
├── gradle/                    # Gradle wrapper and version catalog (libs.versions.toml)
├── build.gradle.kts           # Root build configuration
└── settings.gradle.kts        # Root project settings
```

## Cost Analysis

This application is **100% free** for both developers and users:

| Item | Cost |
|------|------|
| Notion API | $0 (free tier) |
| Google Sheets API | $0 (free tier) |
| Room (SQLite) | $0 (Jetpack library) |
| WorkManager | $0 (Jetpack library) |
| Jetpack Glance | $0 (Jetpack library) |
| Retrofit/OkHttp/Moshi | $0 (open source) |
| Vico charts | $0 (MIT license) |
| Hilt | $0 (Apache 2.0) |
| Backend server | $0 (serverless offline-first architecture) |
| Firebase/cloud services | $0 (not used) |

## Documentation

- [Architecture Overview](docs/ARCHITECTURE.md)
- [Product & Privacy Roadmap](docs/ROADMAP.md)

## License

This project is open source and free to use. No commercial restrictions.
