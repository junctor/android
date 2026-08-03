# Accessibility (TalkBack / assistive tech)

Checklist and inventory for content labels and native vs custom Compose controls. Complements [SMOKE_TEST.md](SMOKE_TEST.md) (smoke tests use `contentDescription` as locators; this doc is for real assistive-tech quality).

## Acceptance criteria

For every interactive control:

| Check | Pass if |
| ----- | ------- |
| Label | TalkBack announces a short, action-oriented name (or adjacent visible text is merged) |
| Role | Announces as button / toggle / checkbox / radio / tab / link as appropriate |
| State | Selected / checked / expanded is announced when relevant |
| Focus | One focus stop per logical control (no nested duplicate clickables) |
| Gestures | Drag/pinch UIs also expose equivalent buttons or custom accessibility actions |

Decorative images/icons may use `contentDescription = null` only when a parent already labels the control, or the image is non-informative.

Prefer Material3 controls (`IconButton`, `IconToggleButton`, `Switch`, `RadioButton`, `NavigationBarItem`). For custom clickables, use `semantics(mergeDescendants = true) { role = …; contentDescription = … }` (see `ReportLink`).

## Maintainer TalkBack pass

Debug build, animations off (same as smoke). Enable TalkBack. Prefer **DC34** chrome plus one deep feature (merch or feedback).

- [ ] **Chrome:** conference selector labeled; expand/collapse announced; bottom bar Schedule / Maps / Search / Settings
- [ ] **Panels:** open home / filters / close without relying only on drag (custom actions or Menu / clear affordances)
- [ ] **Schedule:** day chips announce selection; event row; bookmark add/remove
- [ ] **Filters:** clear filters labeled; category selected state announced
- [ ] **Search:** clear search labeled when query non-empty
- [ ] **Settings:** preference switches — single focus stop, checked state
- [ ] **FAQ / Locations:** expand/collapse — single focus stop; location status not color-only
- [ ] **Merch:** product tile; qty +/− / remove; options button; cart remove; QR announces as QR (payload may remain in CD for smoke decode)
- [ ] **Feedback:** radio/checkbox rows — one stop, label + state
- [ ] **Maps / PDF:** zoom in / out / fit labeled; pinch not the only path

Record unlabeled nodes, wrong roles, duplicate stops, color-only meaning, gesture-only traps.

## Static inventory (severity)

Remediated in the accessibility audit pass unless noted. Re-scan when adding UI.

### P0 — Unlabeled or missing interactive labels (fixed)

| Finding | Location | Fix |
| ------- | -------- | --- |
| Bookmark toggle icon `null` | `BookmarkButton.kt` | Add/Remove bookmark CD |
| Search clear `null` | `SearchBar.kt` | Clear search CD |
| Filter clear `null` | `FilterScreen.kt` | Clear filters CD |
| FAQ dual clickables | `FreqAskedQuestion.kt` | Single merged button + expand state |
| Location dual clickables / color-only status | `LocationView.kt` | Merged label includes status; single clickable |
| Back CD `"back"` | `BackButton.kt` | `Back` string resource |

### P1 — Non-native types / wrong roles / duplicate focus (fixed)

| Finding | Location | Fix |
| ------- | -------- | --- |
| Custom 32.dp icon button | `feature-merch/.../IconButton.kt` | Material `IconButton`, 48.dp |
| Custom qty buttons; delete said decrease | `feature-merch/.../QuantityAdjuster.kt` | Material buttons; remove CD when qty=1 |
| Day chips visual selection only | `DaySelectorView.kt` | `Role.Tab` + `selected` |
| Filter category visual selection only | `Category.kt` | `Role.Checkbox` + `selected` |
| Row + Switch dual toggles | `SwitchPreference`, `PromoSwitch` | `toggleable` + `onCheckedChange = null` |
| Row + RadioButton dual focus | `SelectOneItem`, `VariantRow` | Row click + `onClick = null` on radio |
| Checkbox not merged with label | `MultiSelectItem.kt` | `toggleable` row |
| Gesture panels without a11y actions | `app/.../OverlappingPanelsView.kt` | Custom actions + gutter CD |
| Menu icon CD = resource key | `MenuIcon.kt` | Decorative `null` (parent labels) |

### P2 — Weak / noisy labels (mostly fixed)

| Finding | Location | Notes |
| ------- | -------- | ----- |
| Generic logo/image/info | orgs, merch, cards, gallery | Decorative `null` or indexed gallery CD |
| QR CD dumps full payload | `feature-merch/.../QRCodeImage.kt` | Intentionally kept `QR Code: {payload}` for smoke decode |
| Easter-egg chicken CD | `RubberButton.kt` | Play rubber chicken sound |
| Pinch/zoom gestures | `ZoomableGestures.kt` | Buttons already labeled on PDF/maps |
| Hardcoded English a11y strings | some chrome locators | Prefer `stringResource` when touching; smoke locators stay stable |

### Already in good shape (reference)

| Pattern | Location |
| ------- | -------- |
| `mergeDescendants` + `Role.Button` + label | `ui/.../ReportLink.kt` |
| Chrome / menu / product smoke locators | `app/.../Home.kt`, `ui/.../HomeScreen.kt`, `feature-merch/.../ProductSquare.kt`, `ui/.../ConferenceSelector.kt` |
| PDF zoom CD on buttons, icons null | `ui/.../PdfDisplay.kt` |
| Shared `Image` requires non-null CD | `ui/.../Image.kt` |

## PR habit

- Material control **or** explicit `Role` + label
- No new unlabeled icon-only buttons
- No new parent+child double-clickables (`clickable` row + inner `Switch`/`RadioButton`/`IconButton` with its own click)
- Keep smoke locator strings stable (`Schedule`, `Home menu: …`, `Product …`, `QR Code:`, `Increase quantity`, …) or update smoke docs + tests together

## Automated guards

- Root [`lint.xml`](../lint.xml) — Android accessibility-related issues at Warning (View lint; Compose gaps remain)
- Instrumentation: `com.advice.schedule.accessibility.AccessibilitySemanticsTest` — asserts labels on BookmarkButton, SearchBar clear, QuantityAdjuster (device/emulator; not PR CI by default)
