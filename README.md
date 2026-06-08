# SpendSync - Android Expense Tracker

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
app/
├── data/
│   ├── local/
│   │   ├── db/          # Room database, entities, DAOs
│   │   └── datastore/   # DataStore for settings
│   ├── remote/
│   │   └── notion/      # Notion API service, models
│   └── repository/      # Repository implementations
├── domain/
│   ├── model/           # Domain models
│   ├── repository/      # Repository interfaces
│   └── usecase/         # Use cases
├── presentation/
│   ├── navigation/      # Navigation graph
│   ├── screens/         # UI screens (Home, AddExpense, History, Settings)
│   └── components/      # Reusable UI components
├── widget/              # Jetpack Glance widget
├── worker/              # WorkManager background sync
├── di/                  # Hilt dependency injection modules
└── util/                # Utility classes
```

## Cost Analysis

This application is **100% free** for both developers and users:

| Item | Cost |
|------|------|
| Notion API | $0 (free tier) |
| Room (SQLite) | $0 (Jetpack library) |
| WorkManager | $0 (Jetpack library) |
| Jetpack Glance | $0 (Jetpack library) |
| Retrofit/OkHttp/Moshi | $0 (open source) |
| Vico charts | $0 (MIT license) |
| Hilt | $0 (Apache 2.0) |
| Backend server | $0 (not needed) |
| Firebase/cloud services | $0 (not used) |
| Play Store fee | $25 (one-time, for publishing) |

**Developer running cost: $0/month after initial $25 Play Store registration.**

## License

This project is open source and free to use. No commercial restrictions.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues or questions, please open an issue on the GitHub repository.

---

**SpendSync** - Track your spending, sync to Notion, stay free forever.
