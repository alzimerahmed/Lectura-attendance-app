# Lumera — Implementation Plan

Turns `docs/idea.md` into an executable, phased plan. Each phase is shippable on its own (build passes, tests green, commit + push). Order chosen by impact × effort with architecture work placed before feature pile-up.

---

## Phase 0 — Foundation & Hygiene (do first, ~1–2 sessions)

**Goal:** make the codebase safe to build on. Everything else depends on this.

1. **Rebrand AttendSmartly → Lumera**
   - App name in `strings.xml`, `AndroidManifest.xml` label, notification channel names, `AttendSmartlyApplication` → `LumeraApplication`, `MainActivity` internals, notification IDs/work names.
   - Package rename `com.alzimerahmed.attendsmartly` → `com.alzimerahmed.lumera` (dirs, `build.gradle.kts` namespace/applicationId, manifest intent actions, proguard rules).
   - Rewrite `README.md` (remove dead links/badges to deleted docs, new name, new screenshots placeholder).
   - Keep DB name/scheme stable or add Room migration to avoid wiping user data on update.
2. **Verify build & tests**: `gradlew.bat assembleDebug` + `gradlew.bat test` (package rename from the ownership transfer has never been build-verified).
3. **Introduce Hilt** (replaces manual DI in `AttendSmartlyApplication` + ViewModel factories in `NavGraph`).
   - Add `ksp` + `hilt-navigation-compose`; annotate App, add `@HiltViewModel` to all 8 ViewModels, delete factory lambdas.
4. **Split the god repository** into `SubjectRepository`, `TimetableRepository`, `AttendanceRepository`, `HolidayRepository` (thin facades over existing DAOs; ViewModels inject what they need).
5. **CI**: update `.github/workflows/android.yml` to run `assembleDebug` + `test` on PRs; add signing secrets wiring for release builds (keystore base64 from `C:\Users\shadd\keystores`).

**Exit criteria:** app builds, tests pass, Hilt wired, 4 repositories, rebrand complete.

---

## Phase 1 — Daily-Use QoL: Widgets, Persistent Notifications, Bell Timer (highest impact)

1. **Current-class hero card + live bell timer** (Home)
   - New `CurrentClassMonitor` in HomeViewModel: derive "now" class from timetable + time; countdown ticker (`LaunchedEffect` + `delay(1s)`).
   - Hero card: subject, room, "ends in 23:14", one-tap Present/Absent.
2. **Persistent "mark now" notification** (ClassTrack-style)
   - Replace/augment `ReminderWorker` flow: when class starts, post an ongoing notification with live countdown (chronometer) + Present/Absent/Cancelled actions; auto-cancel on mark or class end.
   - Add "persistent until marked" setting toggle.
3. **Post-class follow-up nudge**
   - WorkManager one-shot scheduled at class end + 15 min: if session unmarked → nudge notification.
4. **Home-screen widgets** (Glance + WorkManager updates)
   - Widget A: today's class list with per-class mark buttons.
   - Widget B: overall % ring + today's bunk budget.
   - Update on data change (Room observer → `WorkManager` expedited refresh) and at day rollover.

**Exit criteria:** user can mark attendance without opening the app; countdown visible on Home, notification, and widget.

---

## Phase 2 — Bunk Intelligence: Recovery Mode, Prediction, What-If

1. **Extend `AttendanceCalculator`** (pure functions, unit-testable):
   - `bunkBudget(subject)` (exists) → also `recoveryPlan(subject, target)` → consecutive attends needed + date estimate.
   - `projectAttendance(subject, sessionsRemaining, attendRate)` → risk forecast ("below 75% by Mar 14 at current rate").
   - `simulate(units, hypothetical)` → what-if %.
2. **Recovery Mode UI**: subject below target → red banner + recovery plan card with progress ("7 more consecutive attends, 4 done").
3. **Risk strip on Home**: horizontal chips of subjects trending toward danger (color-coded).
4. **What-if simulator sheet**: bottom sheet with +/- steppers for attended/missed, live % preview, "apply" to actually mark.
5. **Leave planner**: date-range picker → per-subject impact table ("Math: 78% → 74% ⚠") before confirming.
6. **Per-subject target override**: add `targetAttendance` column to `SubjectEntity` (Room migration), calculator uses subject target ?? global.

**Exit criteria:** calculator logic fully unit-tested; every bunk decision answerable in-app before acting.

---

## Phase 3 — Data Safety: Auto-Backup & Restore

1. **Automatic local backups**: WorkManager daily job → serialize via `ExportImportUtils` → keep last 7 in app-files dir; restore picker in Settings.
2. **Google Drive AppFolder sync** (opt-in): `Drive` REST via Play Services; upload on data change (debounced), restore on fresh install with one tap.
3. **Android Auto Backup rules**: `android:allowBackup` + `dataExtractionRules` for the Room DB (free win, often misconfigured).
4. Backup status card in Settings ("Last backup: 2h ago ✓").

**Exit criteria:** reinstalling the app never loses data without explicit user choice.

---

## Phase 4 — Onboarding Killer Fix: On-Device OCR Fallback

1. **ML Kit text recognition** (com.google.mlkit:text-recognition, on-device, free, no key).
2. **Two-tier OCR pipeline** in `TimetableOcrService`:
   - Tier 1: ML Kit extracts raw text grid on-device → heuristic parser (day/time/subject regexes) → preview.
   - Tier 2 (optional "enhance"): send ML Kit text (not the image) to Gemini for structuring — cheaper, still needs key but works without it.
3. Onboarding: OCR step no longer blocks on API key; show "Scan with AI (needs key)" vs "Scan offline" options.

**Exit criteria:** timetable import works with zero configuration.

---

## Phase 5 — Planner Expansion: Assignments & Exams

1. **Room migration v+1**: `assignments` (id, subjectId, title, dueDate, done, notes, attachmentUri) and `exams` (id, subjectId, title, date, syllabus, grade).
2. **Assignments screen**: per-subject + unified list, due-date reminders via existing NotificationHelper, overdue styling.
3. **Exams**: countdown cards on Home, "attendance below X% → exam eligibility warning" (ties into Phase 2 calculator).
4. **Subject attachments/notes**: notes field + SAF file picker storing URIs (persist permissions); links open via Custom Tabs.
5. New bottom-nav tab "Planner" (assignments + exams) or merge into Home sections — decide in design pass.

**Exit criteria:** Lumera covers attendance + planner; still local-first, no account.

---

## Phase 6 — Design System & Polish Pass

1. **Progress rings** for per-subject % (risk-colored green/amber/red) replacing text rows in Subjects/Analytics.
2. **Dashboard-first Home restructure**: hero (overall ring + bunk budget) → current class → risk chips → today list.
3. **Swipe gestures** on class rows (right = present, left = absent) + undo snackbar (reuse existing undo).
4. **Motion**: shared-element subject card → detail transition; spring animations on rings; consistent 150–250ms motion tokens.
5. **Themed app icon** (Android 13+ monochrome), Material You accent already supported.
6. **Accessibility audit**: TalkBack labels, 48dp touch targets, contrast checks.
7. **Empty states + onboarding polish**: illustration-led empty states, demo-data preview.

---

## Phase 7 — Growth & Distribution (optional/later)

- Play Store listing: privacy policy (recreate deleted PRIVACY.md content as a webpage), screenshots, feature graphic.
- Optional opt-in crash reporting (ACRA — privacy-friendly, no Google account).
- Referral/share: "share timetable with friends" flow (idea.md §7).
- GitHub Releases with signed APKs via updated workflow.

---

## Execution Rules

- One phase per branch/PR: `feature/phase-N-*`, commit per task, push to origin after green build.
- Room schema changes always via numbered migrations + migration tests; never destructive.
- New calculator logic lands with unit tests before UI.
- Update `docs/project.md` + `docs/agent.md` at the end of every phase.
- Verification per phase: `gradlew.bat assembleDebug`, `gradlew.bat test`, manual smoke test on device/emulator.

## Suggested Order & Rough Sizing

| Phase | Effort | Depends on |
|---|---|---|
| 0 Foundation | M | — |
| 1 QoL widgets/notifs | L | 0 |
| 2 Bunk intelligence | M | 0 |
| 3 Auto-backup | M | 0 |
| 4 On-device OCR | M | 0 |
| 5 Planner | L | 0 (2 recommended) |
| 6 Design polish | M | 1–2 |
| 7 Growth | S | all |
