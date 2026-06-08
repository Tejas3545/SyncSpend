# Authentication Setup Guide (Updated 2026 UI)

This guide provides the latest steps for the 2026 Notion and Google portals to get your Client IDs.

---

## 1. Notion OAuth Setup (2026 UI)

In the 2026 Developer Platform, "Distribution" settings only appear for **Public Connections**.

### A. Create a Public Connection
1. Go to the [Notion Developer Portal](https://app.notion.com/developers).
2. **DO NOT** click on your existing connection if it is internal.
3. In the left sidebar, click **Get started**.
4. Look for the **"Build a public integration"** or **"Workspace-scoped OAuth"** template.
5. Click **Create public connection**.
6. Give it a name: `SyncSpend Public`.

### B. Reveal the Distribution Tab
If you are editing an existing connection and the **Distribution** tab is missing:
1. Go to the **Configuration** tab.
2. Fill in the **Description** field (Mandatory for public apps).
3. Fill in the **Developer Website** and **Support Email**.
4. Scroll to the bottom and look for **"Connection Type"** or **"Public Distribution"**.
5. Toggle it to **Public**.
6. A new tab called **Marketplace** or **Distribution** will appear at the top.

### C. Get Credentials
1. Click the new **Distribution** (or **Authentication**) tab.
2. **Redirect URIs**: Add `syncspend://oauth`.
3. **Client ID**: Copy the **OAuth Client ID**.
4. **Client Secret**: Click "Show" and copy the **OAuth Client Secret**.
5. Paste the **Client ID** into `LoginViewModel.kt`.

---

## 2. Google Sign-In Setup

### A. Create a Project
1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Click **New Project** and name it `SyncSpend`.
3. Go to **APIs & Services > Credentials**.

### B. Generate SHA-1
1. In Android Studio Terminal, run:
   ```bash
   ./gradlew signingReport
   ```
2. Copy the **SHA1** from the `debug` variant.

### C. Create IDs
1. Click **Create Credentials > OAuth client ID**.
2. **Android ID**: Select "Android", enter your package `com.spendsync.app`, and paste the SHA-1.
3. **Web ID**: Create another one for "Web application".
4. Copy the **Web Client ID** and paste it into `LoginViewModel.kt`.

---

## 3. What to paste where? (Confusion Fix)

I see you have a **Notion Token** and a **Database ID**. Here is where they go:

### A. If you want to use the "Login with Notion" button:
1. Follow **Section 1** in this file to get a **Public Client ID**.
2. Paste that **Client ID** into `LoginViewModel.kt` here:
   ```kotlin
   val clientId = "PASTE_CLIENT_ID_HERE"
   ```
3. When you click the button in the app, it will automatically handle the token and create the database for you.

### B. If you already have a Token and Database ID (Manual Setup):
If you don't want to use the login button and just want to paste your existing keys:
1. Open **`AppConfig.kt`** in Android Studio.
2. Paste your token and database ID there:
   ```kotlin
   const val NOTION_TOKEN = "your_secret_token_here"
   const val NOTION_DATABASE_ID = "your_database_id_here"
   ```
3. The app will use these if you are logged in.

---

## 4. Code Update Checklist

Open `LoginViewModel.kt` and ensure these lines are updated:

```kotlin
// Google
.setServerClientId("PASTE_GOOGLE_WEB_CLIENT_ID_HERE")

// Notion
val clientId = "PASTE_NOTION_CLIENT_ID_HERE"
```
