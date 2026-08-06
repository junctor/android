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

- **`:core`** — shared domain models and pure utilities. No project dependencies.
- **`:data`** — backend-neutral data-source interfaces, `UserSession`, local bookmark/prefs stores, and shared network client. Depends on `:core`.
- **`:ui`** — shared Compose screens and components used across features (including glitch visual effects). Depends on `:core` and `:data`.
- **`:app`** — composition root: DI aggregation, navigation, schedule/shell repositories and ViewModels, and screen wiring. Depends on every module.

User-facing feature modules typically own their repository, ViewModel, UI, and Koin module. Navigation graphs and Firebase/Retrofit bindings stay in `:app`.

### Dependency graph

```text
:core
├── :data
├── :ui
├── :infra-play
├── :infra-analytics
├── :infra-reminder          (:core + :data)
├── :infra-retrofit          (:core + :data)
│
:infra-firebase              (:core + :data + :infra-play)
│
:data + :ui
├── :feature-locations
├── :feature-merch
├── :feature-documents
├── :feature-feedback
├── :feature-organizations
├── :feature-wifi
├── :feature-maps
├── :feature-news
├── :feature-faq
├── :feature-speakers
├── :feature-settings
├── :feature-search
├── :feature-menu
│
:app → all of the above
```

### Module taxonomy

- **Foundation:** `:core`, `:data`, `:ui`
- **Infra (`infra-*`):** firebase, retrofit, play, analytics, reminder — Gradle module names are `infra-*`; Kotlin packages stay `com.advice.firebase`, `com.advice.retrofit`, `com.advice.play`, `com.advice.analytics`, `com.advice.reminder`
- **Product features (`feature-*`):** locations, merch, documents, feedback, organizations, wifi, maps, news, faq, speakers, settings, search, menu
- **Composition root:** `:app`

### Known debt

Identified and deliberately deferred; each needs its own scoped effort:

- **Namespace migration** — `applicationId` / some namespaces remain `com.shortstack.*` (not migrating in this epic). Merch is the exception: Kotlin package/namespace is `com.advice.merch` while catalog domain types stay `com.advice.core.local.products`.
- **Eager application-scope flows** — eight `SharingStarted.Eagerly` sites (`ContentRepository`, `FiltersRepository`, `FirebaseContentDataSource`, `FirebaseTagsDataSource`, `FirebaseNewsDataSource`, `FirebaseLocationsDataSource`, `FirebaseFeedbackDataSource`, `RetrofitMapsDataSource`) start Firestore listeners at process launch. Converting to `WhileSubscribed` needs a per-flow audit of background dependents (reminder sync, offline cache warming) first.
- **Mutable domain models** — `var isSelected` / `isBookmarked` / `isVisible` / `isExpanded` on `Conference`, `Tag`, `Type`, `Event`, `Location` in `:core` leak UI state into the domain layer; converting to `val` + `copy()` touches ~30 files.
- **Test gaps** — remaining untested surface is small: the trivial pass-through ViewModels (`ConferenceViewModel`, `SpeakersViewModel`, `SearchViewModel`) and the `Bundle`-building `MainViewModel` analytics methods (`onLinkOpen`, `onPause`, `onDestinationChanged`, `onPermissionRequest`), which would need Robolectric to cover on the JVM.

### Foundation

| Module | Role |
|--------|------|
| `core` | Domain models (`Conference`, `Event`, `Speaker`, …), audience policy, pure utilities (`TimeUtil`, `ToastManager`), preference catalog |
| `data` | Data-source contracts, `UserSession`, bookmark stores, OkHttp `Network` client, prefs stores (`UserPreferencesStore`, `ContentSyncStore`), offline queue DTOs |
| `ui` | Reusable Compose screens/components (schedule, home cards, maps chrome, settings, filters, privacy policy), glitch visual effects, and shared UI state types |

### Infrastructure (`infra-*`)

| Module | Role |
|--------|------|
| `infra-firebase` | Primary backend: Firestore data sources, DTO → core mapping, anonymous auth / conference session |
| `infra-retrofit` | HTTP maps download/cache (`MapsDataSource`) |
| `infra-analytics` | Analytics events and Remote Config flags |
| `infra-play` | In-app updates and Play Age Signals |
| `infra-reminder` | WorkManager-based event/feedback reminder notifications (`NotificationHelper`) |

### Product features (`feature-*`)

| Module | Role |
|--------|------|
| `feature-locations` | Location tree browser (repo, ViewModel, screen, Koin module) |
| `feature-merch` | Merch catalog (`com.advice.merch`), per-conference cart (`MerchCartStore`), and order-summary QR |
| `feature-documents` | Conference document list/detail (repo, ViewModel, screen) |
| `feature-feedback` | Feedback form UI, HTTP feedback/report submission, and offline queue |
| `feature-wifi` | Wi-Fi join helpers and screen (repo, ViewModel, Koin module) |
| `feature-organizations` | Org list/detail (repo, ViewModels, screens, Koin module) |
| `feature-maps` | Conference maps (repo, ViewModel, Koin module) |
| `feature-news` | News feed (repo, ViewModel, Koin module) |
| `feature-faq` | FAQ list (repo, ViewModel, Koin module) |
| `feature-speakers` | Speakers list/detail (repos, ViewModels, Koin module). The singular/plural repository pair (`SpeakerRepository` detail vs `SpeakersRepository` list) is intentional |
| `feature-settings` | Settings + privacy policy wiring (repo, ViewModel, Koin module) |
| `feature-search` | Cross-content search screen/repo |
| `feature-menu` | Nested conference menus |

### Composition root (`app`)

`App` initializes Firebase, Timber, and Koin via `appModules()`. That list aggregates:

- **App modules** — `shellModule` (scope/prefs/bookmarks/nav shell) and `scheduleModule` (schedule-shell repos and ViewModels)
- **Infra modules** — `firebaseDataModule` (`:data` interfaces → Firebase), `retrofitModule` (`MapsDataSource`), `analyticsModule`, `playModule`, `reminderModule` — each owned by its `infra-*` module
- **Feature modules** — `locationsModule`, `merchModule`, `organizationsModule`, `wifiModule`, `feedbackModule`, `documentsModule`, `mapsModule`, `newsModule`, `faqModule`, `speakersModule`, `searchModule`, `menuModule`, `settingsModule`

`MainActivity` owns the root `NavHost`; route wrappers in `:app` collect ViewModel state and pass it into `:ui` or feature screens. Feature modules do not register their own navigation graphs.

Home, schedule, filters, and content/event remain in `:app` (not extracted as feature modules).

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
