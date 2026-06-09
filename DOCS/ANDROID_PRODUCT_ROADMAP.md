# SyncSpend Android — Product, Privacy, and Build Roadmap

> **Purpose:** This is the source-of-truth brief for a coding agent building SyncSpend as a native Android expense tracker. It describes product behavior, architecture, acceptance criteria, integration constraints, and release phases.

## 1. Product promise

SyncSpend is a **native Android**, offline-first expense tracker focused on entering an expense in seconds. It may take product inspiration from minimalist finance applications, but it must not copy another developer's name, icon, screenshots, source code, proprietary assets, or pixel-identical trade dress.

The app must be:

- Free to use, with no history paywall, ads, analytics SDK, or subscription requirement.
- Fully usable without an account and without internet access.
- Private by design: the developer does not receive or host expense data.
- Optional-sync only: users may store a copy in their own Google Sheet or Notion workspace.
- Built as a proper native Android application using Kotlin and Jetpack Compose, not a WebView or website wrapper.

## 2. Non-negotiable privacy rules

1. Room on the device is the canonical database.
2. Creating, editing, deleting, filtering, and viewing all history must work offline.
3. Never send expenses to a developer-controlled server.
4. Never embed an API secret, Notion integration token, refresh token, signing key, or service-account key in source code or the APK.
5. Google/Notion connection is optional and must be initiated from Settings.
6. Clearly show what account is connected, what destination is used, last sync time, pending item count, and the disconnect action.
7. Disconnecting must not delete local history. Deleting local data must require explicit confirmation.
8. Logs and crash reports must not include merchant names, notes, amounts, tokens, or remote document identifiers.
9. Android backup remains disabled unless encrypted backup behavior is deliberately designed and disclosed.

## 3. MVP feature requirements

### 3.1 Home

- Current workspace label (default: `Personal`).
- Total spending for the selected period.
- Period selector: week, month, year, all time, and custom range.
- Simple spending chart with accessible text summary.
- Latest expenses grouped by day.
- Search by merchant/title, category, payment method, and note.
- One prominent add button.
- Empty state that lets the user add the first expense.

### 3.2 Fast expense entry

Required fields:

- Amount stored as integer minor units (for example cents), never `Double`.
- Currency.
- Title/merchant.
- Date and time.

Optional fields:

- Category.
- Payment method.
- Note.

Behavior:

- Numeric keyboard and locale-aware decimal parsing (`16.99` and `16,99`).
- Save in at most two focused actions after entering an amount.
- Suggestions from previous entries are calculated locally.
- Editing an old expense never creates a duplicate.
- Deletion offers undo.

### 3.3 History

- No one-month limit and no paid archive.
- Search, date range, category, payment method, amount range, and sync-state filters.
- Edit and delete.
- Export all or filtered data to CSV through Android's Storage Access Framework.
- Import from the app's documented CSV schema with a preview and duplicate handling.

### 3.4 Android-native convenience

- Home-screen widget for this week/month/year totals.
- Pinned shortcut and Quick Settings tile for adding an expense.
- App shortcut deep link into quick add.
- Material 3 dynamic color where available, plus light/dark/system themes.
- TalkBack labels, 48dp touch targets, scalable text, and meaningful chart summaries.

Do **not** promise automatic Google Pay transaction capture. Android does not provide a general public API that allows an unrelated expense app to read every tap-to-pay purchase. Quick Add shortcuts are the safe MVP alternative.

## 4. Local data design

Use Room as the single source of truth. Recommended tables:

### `expenses`

- `id: String` — locally generated UUID, stable across sync providers.
- `title: String`.
- `amountMinor: Long`.
- `currencyCode: String` (ISO 4217).
- `categoryId: String?`.
- `paymentMethodId: String?`.
- `note: String?`.
- `occurredAt: Instant` stored as epoch milliseconds.
- `createdAt`, `updatedAt`.
- `deletedAt: Instant?` for remote tombstones.
- `syncRevision: Long` incremented on every local mutation.

### `categories` and `payment_methods`

Stable UUID, display name, icon key, color key, ordering, and archived flag.

### `sync_destinations`

Provider, account label, remote container ID, enabled flag, last successful sync, and last error summary. Tokens belong in encrypted credential storage, not Room.

### `sync_queue`

Expense ID, provider, operation (`UPSERT`/`DELETE`), revision, attempts, next retry time, and sanitized error code. Add a unique key on `(expenseId, provider)` so repeated edits collapse into the newest operation.

## 5. Sync behavior

- Local writes commit immediately and enqueue sync work in the same Room transaction.
- WorkManager runs only when network is available and uses exponential backoff.
- UI never waits for cloud sync to save an expense.
- Every remote row/page includes the local UUID and revision.
- An operation is acknowledged only after the remote API confirms it.
- Use idempotent upserts to prevent duplicates after retries.
- Start with **one-way local-to-cloud backup**. Do not claim two-way sync until conflict behavior is implemented and tested.
- For later two-way sync, use last-write-wins only if timestamps are trustworthy; otherwise surface a conflict instead of silently discarding data.

## 6. Google Sheets integration (zero developer hosting cost)

Google OAuth client IDs are identifiers, not secrets. Configure an Android OAuth client in Google Cloud using the release package name and SHA-1/SHA-256 certificate fingerprints. Request the smallest practical scopes:

- `drive.file` to create and manage files created by this app.
- `spreadsheets` to write rows in the created spreadsheet.

On first connection:

1. User chooses a Google account and grants consent.
2. Create `SyncSpend Expenses` in that user's Drive.
3. Create a schema/version metadata sheet and an `Expenses` sheet.
4. Store only the spreadsheet ID locally.
5. Upsert by local expense UUID rather than blindly appending.

Recommended columns:

`ID, Revision, Deleted, Title, AmountMinor, Currency, Category, PaymentMethod, Note, OccurredAtUtc, UpdatedAtUtc`

Google API quotas can be used without per-user subscription fees for normal personal-app usage, but quota and OAuth verification requirements must be checked before Play Store release. Never use a service account for personal user sheets and never ship a service-account JSON key.

## 7. Notion integration constraints

A public Notion OAuth flow requires exchanging an authorization code using a client secret. A mobile APK cannot keep that secret confidential. Therefore choose one of these explicit modes:

### Mode A — zero-backend personal build (MVP)

The user creates their own Notion internal integration, shares a parent page with it, and pastes the integration token plus parent page ID into SyncSpend. Store the token with Android Keystore-backed encrypted storage. The app creates an `Expenses` data source/database beneath that page and stores its ID locally.

This mode has no developer server cost, but setup is less friendly and must include step-by-step help and a token warning.

### Mode B — public Play Store OAuth (future)

Use a minimal HTTPS token-exchange service that keeps the Notion client secret server-side. The service must not receive expenses; it only handles OAuth credentials. This is not literally zero-infrastructure, although low-volume serverless free tiers may reduce cost. Publish a privacy policy and document token handling and deletion.

Do not embed the Notion client secret in the Android app. Do not describe OAuth as secure if the secret is compiled into the APK.

Notion schema:

- `Name` (title)
- `Amount Minor` (number)
- `Currency` (rich text/select)
- `Category` (select)
- `Payment Method` (select)
- `Date` (date)
- `Note` (rich text)
- `SyncSpend ID` (rich text, unique in app logic)
- `Revision` (number)
- `Deleted` (checkbox)

## 8. Security and configuration

- Commit no credentials. Add credential files, `local.properties`, signing files, databases, `.gradle`, and build outputs to `.gitignore`.
- Treat every value in `BuildConfig`, resources, native libraries, and the APK as public.
- Rotate any credential that was ever committed; deleting it in a later commit does not remove it from Git history.
- Use HTTPS only and disable cleartext traffic.
- Prefer Credential Manager / Google Identity for Google sign-in.
- Use Android Keystore-backed encrypted storage for user tokens. Validate the currently supported AndroidX security approach before implementation because library recommendations can change.
- Add dependency and secret scanning in CI.

## 9. Delivery phases

### Phase 0 — repository and privacy baseline

- Offline launch with no sign-in gate.
- Remove committed secrets and generated build artifacts.
- Add `.gitignore`, CI, privacy architecture, and a release checklist.
- Add unit tests around currency parsing and Room operations.

### Phase 1 — reliable offline MVP

- Complete add/edit/delete/history/search/filter flows.
- Correct integer money model and migrations.
- CSV import/export.
- Accessibility pass and process-death/state restoration tests.

### Phase 2 — Android convenience

- Widget, shortcut, and Quick Settings tile.
- Local smart suggestions.
- Notification-free background queue infrastructure.

### Phase 3 — Google Sheets backup

- Optional account connection.
- Automatic spreadsheet creation.
- Idempotent upload, deletion tombstones, retry UI, and disconnect.

### Phase 4 — Notion personal integration

- User-supplied token and parent page.
- Automatic database creation and validation.
- Idempotent upload and troubleshooting UI.

### Phase 5 — Play Store readiness

- Independent branding and store assets.
- Privacy policy, Data safety form, content rating, accessibility checks.
- Signed App Bundle, internal/closed testing, crash-free validation, and staged rollout.
- Decide whether public Notion OAuth justifies a credential-only backend.

## 10. Definition of done

A release candidate is not done until all of the following pass:

- Fresh install can add and find expenses in airplane mode without signing in.
- All history remains available across month/year boundaries for free.
- Process death and device restart do not lose committed entries.
- Amount calculations are exact for supported currencies.
- Google/Notion failures never block local saves and never duplicate entries after retry.
- Disconnect preserves local data.
- No secret scanner finding exists in tracked files or the built APK.
- Unit, Room migration, Compose UI, and WorkManager integration tests pass.
- TalkBack can complete the primary add-expense flow.
- The app has original branding and does not imply affiliation with the iOS developer.

## 11. Agent execution rules

When this document is given to a coding agent, the agent must:

1. Audit existing behavior before rewriting working features.
2. Make small, buildable commits by phase.
3. Never add a mandatory account gate.
4. Never add a developer-hosted expense database.
5. Never invent support for payment transaction capture.
6. Never commit credentials or use fake production links.
7. Add tests for every sync retry, mapping, or migration change.
8. State clearly which behavior is complete, partial, mocked, or deferred.
9. Preserve local user data through every schema migration.
10. Stop and document the blocker if a requested integration cannot be implemented securely under the zero-backend constraint.
