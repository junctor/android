# Maintainer smoke test

Manual and automated checklist for verifying the Hacker Tracker Android app against live Firebase data. This mirrors the maintainer workflow: walk every home-menu screen, switch conferences and confirm data swaps, and when possible verify the merch cart QR payload.

## Conferences (pinned)

| Order | Target | Role |
| ----- | ------ | ---- |
| 1 | **DC34** | Primary full pass (chrome + every menu item) |
| 2 | **DC33** | Switch + data-integrity pass |
| 3 | **TEST** | Switch + data-integrity pass |

Resolve in the Home conference selector by name/code (case-insensitive). Firebase may show variants such as `DEFCON34`, `DEF CON 34`, or `TEST2026`.

| Target | Match heuristic |
| ------ | --------------- |
| DC34 | name/code contains `DC34`, `DEFCON34`, or `DEF CON 34` |
| DC33 | name/code contains `DC33`, `DEFCON33`, or `DEF CON 33` |
| TEST | name/code contains `TEST`, and is not DC34/DC33 |

## Prerequisites

- Debug build with a valid `app/google-services.json`
- Device or emulator with network access to Firestore
- Prefer a clean install or known-good preferred conference
- For instrumentation: turn off animations (Compose idle hangs on maps/loaders otherwise)

```bash
./gradlew :app:installDebug

adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
```

## Phase A — Fixed chrome (on DC34)

Open the Home left panel (hamburger on Schedule if the app opens on Schedule).

- [ ] **Home:** conference selector shows a title; header/logo/news/menu finish loading (not stuck on Loading/Error)
- [ ] **Schedule (center):** day list / events load; open one event detail, then back
- [ ] **Filters (right):** tag filters render
- [ ] **Maps** (bottom bar): map loads or empty-state for this conference; no crash
- [ ] **Search:** search field works; results are empty or conference-scoped; no crash
- [ ] **Settings:** opens; timezone label matches the conference

Bottom bar is shown while the Home (left) panel is active.

## Phase B — Home menu walk (full on DC34)

Menus are Firebase-driven (`homeMenuId`). Skip section headings and dividers.

- [ ] List every navigable home-menu row
- [ ] Open each item; for nested `menu` entries, open the submenu and walk its children
- [ ] Each destination leaves Loading within a reasonable time and is not a permanent Error
- [ ] Content looks like **DC34** (not another conference)

| Menu function | Pass criteria |
| ------------- | ------------- |
| `schedule` / `schedule_bookmark` | Filtered or bookmarked schedule loads |
| `people` | Speakers list loads |
| `locations` | Location tree/list loads |
| `products` | Merch catalog loads |
| `news` | News list loads |
| `faq` | FAQ list loads |
| `organizations` | Org list loads |
| `content` | Content/event list loads |
| `document` | Document body renders |
| `maps` / `search` | Same as chrome |
| wifi (if present) | Wi-Fi screen opens |

## Phase C — Conference switch

### Switch to DC33

- [ ] Home selector title becomes the DC33 conference
- [ ] Home menu items change for DC33
- [ ] Schedule events are not DC34’s
- [ ] Maps reset for DC33 (or empty)
- [ ] Search / speakers / locations / news / FAQ / orgs / merch reflect DC33
- [ ] Merch cart is not DC34’s cart (cart is per-conference)

Spot-check chrome + a few menu items (not necessarily every nested leaf).

### Switch to TEST

Repeat the same checks for TEST.

## Phase D — Cart + QR (when possible)

Only if the active conference has merch with `enable_merch_cart` (prefer **DC33**, then DC34/TEST):

1. Open Products → choose an in-stock product → select a variant if required → **Add to cart** (qty 1) → Summary
2. Confirm a QR is shown (not the empty-cart placeholder / Glitch logo)
3. **Decode the QR image** (not only the content description). Payload must be compact order format:

```text
1:{conferenceId}:A{versionCode}:{variantId}:{qty}[;{variantId}:{qty}...]
```

Validation:

- Payload on the QR node content description `QR Code: {payload}` parses as compact order (`parseCompactOrderData`)
- That payload round-trips through production `generateQRCode` + ZXing decode (same encoder the Image uses)
- Version is `1`; platform is `A` + digits (Android `versionCode`)
- Cart lines match: after add, a single `{variantId}:1`; after increasing qty, same variant with `:2`
- **Remove all cart lines** → QR node is gone; empty copy (“Nothing in your list”) is shown

Pattern: `^1:\d+:A\d+:(\d+:\d+)(;\d+:\d+)*$` (see [ht-qrcode](https://github.com/junctor/ht-qrcode/)).

If no pinned conference supports cart, mark **N/A**.

## Instrumentation

Compose instrumentation under `app/src/androidTest` automates the same phases against live Firebase.

```bash
# All smoke tests (device/emulator required)
./gradlew :app:connectedDebugAndroidTest

# Single class
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.advice.schedule.smoke.ConferenceSwitchSmokeTest
```

Requires network + `google-services.json`. These tests are **not** part of default PR CI.

Locators use **accessibility** and visible text only (no `testTag`s):

| Element | Locator |
| ------- | ------- |
| Conference chrome | `contentDescription = "Conference selector"` |
| Conference row | `"Conference ${name}"` |
| Schedule hamburger | `"Menu"` |
| Bottom nav | `"Schedule"` / `"Maps"` / `"Search"` / `"Settings"` |
| Home / nested menu row | `"Home menu: ${label}"` |
| Product tile | `"Product ${label}"` |
| Add to cart | Button text `"Add to list"` |
| Cart summary | Text `"View List"` (substring) |
| Merch QR | `"QR Code:"` substring (payload in CD) |
| Qty / remove | `"Increase quantity"` / `"Remove from cart"` |

| Class | Phase |
| ----- | ----- |
| `ConferenceChromeSmokeTest` | A on DC34 (selector + bottom bar present) |
| `HomeMenuWalkSmokeTest` | B on DC34 (menu rows listed; full walk is manual) |
| `ConferenceSwitchSmokeTest` | C DC34 → DC33 → TEST |
| `MerchQrSmokeTest` | D: decode QR, compact order ↔ cart, clear cart removes QR |

## Recording results

| Phase | Conference | Result (pass / fail / N/A) | Notes |
| ----- | ---------- | -------------------------- | ----- |
| A Chrome | DC34 | | |
| B Menu walk | DC34 | | |
| D Cart QR | | | |
| C Switch | DC33 | | |
| C Switch | TEST | | |
