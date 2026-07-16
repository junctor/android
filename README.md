# Hacker Tracker

Android conference companion for schedules, maps, news, and more.

Companion apps: [iOS](https://github.com/junctor/hackertracker-ios) · [Web](https://github.com/junctor/hackertracker-web)

## Stack

- Kotlin, Jetpack Compose, Material 3
- Firebase (Firestore, Auth, Messaging, Crashlytics)
- Koin, Coroutines, Navigation Compose

## Architecture

The app uses a pragmatic MVVM + repository layout across Gradle modules. Data flows like this:

```text
Compose screen → ViewModel → repository → :data interface
  → Firebase / Retrofit implementation → :core model → Flow → UI
```

- **`:core`** — shared domain models, utilities, and networking primitives. No project dependencies.
- **`:data`** — backend-neutral data-source interfaces (and a few local bookmark implementations). Depends on `:core`.
- **`:ui`** — shared Compose screens and components used across features. Depends on `:core` and `:feature-glitch`.
- **`:app`** — composition root: DI, navigation, most repositories/ViewModels, and screen wiring. Depends on every module.

Feature isolation varies: some modules own repository + ViewModel + UI (`feature-merch`, `feature-locations`); others are mostly UI shells or infrastructure. Navigation and Koin bindings are centralized in `:app`.

### Dependency graph

```text
:core
├── :data
├── :feature-glitch ──→ :ui
├── :feature-play ──→ :feature-firebase
├── :feature-analytics
├── :feature-reminder
├── :feature-retrofit
│
:data + :ui
├── :feature-locations
├── :feature-merch
├── :feature-documents
├── :feature-feedback
├── :feature-organizations
├── :feature-wifi          (:core + :ui only)
│
:app → all of the above
```

### Foundation

| Module | Role |
|--------|------|
| `core` | Domain models (`Conference`, `Event`, `Speaker`, …), shared state types, OkHttp client, notifications, storage helpers |
| `data` | Data-source contracts (`ContentDataSource`, `LocationsDataSource`, …) plus local bookmark stores |
| `ui` | Reusable Compose screens/components (schedule, home cards, maps chrome, settings, filters) and shared UI state types |

### Infrastructure (`feature-*`)

| Module | Role |
|--------|------|
| `feature-firebase` | Primary backend: Firestore data sources, DTO → core mapping, anonymous auth / conference session |
| `feature-retrofit` | HTTP maps download/cache (`MapsDataSource`) |
| `feature-analytics` | Analytics events and Remote Config flags |
| `feature-play` | In-app updates and Play Age Signals |
| `feature-reminder` | WorkManager-based event/feedback reminder notifications |
| `feature-glitch` | Visual/easter-egg effects consumed by `:ui` |

### User-facing features

| Module | Role |
|--------|------|
| `feature-locations` | Location tree browser (repo, ViewModel, screen) |
| `feature-merch` | Merch catalog, cart, and order-summary QR |
| `feature-documents` | Conference document list/detail |
| `feature-feedback` | Feedback form UI and HTTP submission |
| `feature-wifi` | Wi-Fi join helpers and screen (ViewModel lives in `:app`) |
| `feature-organizations` | Org list/detail UI (repo/ViewModel live in `:app`) |

### Composition root (`app`)

`App` initializes Firebase, Timber, and Koin. `AppModule` binds every `:data` interface to a concrete Firebase/Retrofit implementation and registers repositories and ViewModels.

`MainActivity` owns the root `NavHost`; route wrappers in `:app` collect ViewModel state and pass it into `:ui` or feature screens. Feature modules do not register their own navigation graphs.

## Requirements

- Android Studio Ladybug or newer
- JDK 17
- `minSdk` 26 / `targetSdk` 36

Place `app/google-services.json` for Firebase before building.

## Build

```bash
./gradlew :app:assembleDebug
```

Install on a device or emulator:

```bash
./gradlew :app:installDebug
```
