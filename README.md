# Hacker Tracker

Android conference companion for schedules, maps, news, and more.

Companion apps: [iOS](https://github.com/junctor/hackertracker-ios) · [Web](https://github.com/junctor/hackertracker-web)

## Stack

- Kotlin, Jetpack Compose, Material 3
- Firebase (Firestore, Auth, Messaging, Crashlytics, Analytics, Remote Config)
- Koin, Coroutines, Navigation Compose, WorkManager

## Architecture

The app uses a pragmatic MVVM + repository layout across Gradle modules. Data flows like this:

```text
Compose screen → ViewModel → repository → :data interface
  → Firebase / Retrofit implementation → :core model → Flow → UI
```

- **`:core`** — shared domain models, utilities, and typed local storage. No project dependencies.
- **`:data`** — backend-neutral data-source interfaces, `UserSession`, and local bookmark implementations. Depends on `:core`.
- **`:ui`** — shared Compose screens and components used across features. Depends on `:core` and `:feature-glitch`.
- **`:app`** — composition root: DI aggregation, navigation, schedule/shell repositories and ViewModels, and screen wiring. Depends on every module.

User-facing feature modules typically own their repository, ViewModel, UI, and Koin module. Navigation graphs and Firebase/Retrofit bindings stay in `:app`.

### Dependency graph

```text
:core
├── :data
├── :feature-glitch ──→ :ui
├── :feature-play
├── :feature-analytics
├── :feature-reminder          (:core + :data)
├── :feature-retrofit          (:core + :data)
│
:feature-firebase              (:core + :data + :feature-play)
│
:data + :ui
├── :feature-locations
├── :feature-merch             (+ :feature-glitch)
├── :feature-documents
├── :feature-feedback
├── :feature-organizations
├── :feature-wifi
│
:app → all of the above
```

### Foundation

| Module | Role |
|--------|------|
| `core` | Domain models (`Conference`, `Event`, `Speaker`, …), shared state types, OkHttp client, notifications, typed stores (`MerchCartStore`, `UserPreferencesStore`, `ContentSyncStore`, `OfflineQueueStore`) |
| `data` | Data-source contracts (`ContentDataSource`, `LocationsDataSource`, …), `UserSession`, plus local bookmark stores |
| `ui` | Reusable Compose screens/components (schedule, home cards, maps chrome, settings, filters, privacy policy) and shared UI state types |

### Infrastructure (`feature-*`)

| Module | Role |
|--------|------|
| `feature-firebase` | Primary backend: Firestore data sources, DTO → core mapping, anonymous auth / conference session |
| `feature-retrofit` | HTTP maps download/cache (`MapsDataSource`) |
| `feature-analytics` | Analytics events and Remote Config flags |
| `feature-play` | In-app updates and Play Age Signals |
| `feature-reminder` | WorkManager-based event/feedback reminder notifications |
| `feature-glitch` | Visual/easter-egg effects consumed by `:ui` and `:feature-merch` |

### User-facing features

| Module | Role |
|--------|------|
| `feature-locations` | Location tree browser (repo, ViewModel, screen, Koin module) |
| `feature-merch` | Merch catalog, per-conference cart, and order-summary QR |
| `feature-documents` | Conference document list/detail (repo, ViewModel, screen) |
| `feature-feedback` | Feedback form UI, HTTP feedback submission, and content reporting |
| `feature-wifi` | Wi-Fi join helpers and screen (repo, ViewModel, Koin module) |
| `feature-organizations` | Org list/detail (repo, ViewModels, screens, Koin module) |

### Composition root (`app`)

`App` initializes Firebase, Timber, and Koin via `appModules()`. That list aggregates:

- **App modules** — `shellModule` (prefs/cart/bookmarks/analytics shell), `firebaseDataModule` (`:data` interfaces → Firebase/Retrofit), `scheduleModule` / `settingsModule` (schedule-shell repos and ViewModels), `playModule`
- **Feature modules** — `locationsModule`, `productsModule`, `organizationsModule`, `wifiModule`, `feedbackModule`, `documentsModule`, `reminderModule`

`MainActivity` owns the root `NavHost`; route wrappers in `:app` collect ViewModel state and pass it into `:ui` or feature screens. Feature modules do not register their own navigation graphs.

Schedule, search, home, news, FAQ, maps, speakers, and filters remain in `:app` (not extracted as feature modules).

## Requirements

- Android Studio Quail 2 (2026.1.2) or newer (AGP 9.3)
- JDK 17
- `minSdk` 26 / `targetSdk` 36 / `compileSdk` 37

Firebase config is not checked in. Copy the example and replace placeholders with values from the [Firebase console](https://console.firebase.google.com/) (Project settings → Your apps):

```bash
cp app/google-services.json.example app/google-services.json
```

Optional — content reporting endpoint (used by `:feature-feedback`; CI can inject the same via `REPORT_URL`):

```properties
# local.properties
REPORT_URL=https://your.endpoint/report
```

Without it, the build uses a placeholder URL and report submission will fail at runtime.

## Build

```bash
./gradlew :app:assembleDebug
```

Install on a device or emulator:

```bash
./gradlew :app:installDebug
```

## Testing

### Unit / CI

```bash
./gradlew ciCheck
```

Runs `ktlintCheck`, unit `test`, `:app:lintDebug`, and `:app:assembleDebug` (same as [CI](.github/workflows/ci.yml)).

### Maintainer smoke test

Manual checklist for DC34 → DC33 → TEST (home menu walk, conference switch, merch QR / cart isolation): [docs/SMOKE_TEST.md](docs/SMOKE_TEST.md).

TalkBack / content labels / custom control roles: [docs/ACCESSIBILITY.md](docs/ACCESSIBILITY.md).

Live-Firebase Compose instrumentation (device/emulator + `google-services.json` required; not PR-gated):

```bash
./gradlew :app:connectedDebugAndroidTest
```
