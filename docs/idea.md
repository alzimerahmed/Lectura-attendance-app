# Lumera — Improvement Ideas from Competitor Research

Research basis: top attendance/planner apps — **RollCall** (Indian 75% rule specialist), **MyCollegeMate** (iOS, Live Activities + AI scanner), **ClassTrack** (offline OCR, persistent reminders), **Attenly** (assignments + leave planner), **MyStudyLife** (full student planner, cross-device sync, AI coach), **Class Timetable** (widgets, bell timer, grades).

Legend: ✅ already in Lumera · 🔶 partial · ❌ missing

---

## 1. Core Attendance & Bunk Math

| Idea | Status | Notes |
|---|---|---|
| Subject-wise % + overall % | ✅ | Per-unit tracking is already stronger than most rivals |
| Live safe-bunk counter per subject | ✅ | `AttendanceCalculator` |
| Recovery calculator ("attend N consecutive to reach 75%") | 🔶 | Shown in summary; make it a **Recovery Mode** with a plan + progress ring per subject |
| Risk alerts *before* you drop below target | ❌ | Predictive warning: "at current rate you'll fall below 75% by <date>" |
| What-if simulator | ❌ | Slider: "if I miss next 3, what's my %?" — instant answer before deciding to bunk |
| Per-subject custom target % | 🔶 | Global target exists; add per-subject override (colleges differ per subject) |
| Leave/bunk planner (plan a week of leave) | ❌ | Attenly-style: pick date range → shows impact on every subject |
| Minimum-attendance projection to semester end | ❌ | "If you attend everything from now, best possible % = X" |

## 2. Timetable & Scheduling

| Idea | Status | Notes |
|---|---|---|
| AI timetable OCR | ✅ | Gemini-based; see §7 for the key-friction problem |
| On-device offline OCR fallback | ❌ | ClassTrack's differentiator — ML Kit text recognition, no key needed |
| Rotating / Week A-B timetables | ❌ | MyStudyLife supports this; many colleges use it |
| Two-week (even/odd) cycles | ❌ | Class Timetable supports; easy add via `weekParity` on entries |
| Live "current class" bell timer with countdown | ❌ | Class Timetable's killer QoL — banner on Home: "ends in 23:14" |
| Timetable sharing with friends (no attendance data) | 🔶 | JSON export exists; make a clean share-sheet/link flow |
| Room/teacher per entry | ✅ | Overrides supported |
| Holidays & cancelled classes | ✅ | Holiday entity + CANCELLED status |

## 3. Notifications & Quick Marking

| Idea | Status | Notes |
|---|---|---|
| Class reminders (exact alarms) | ✅ | ReminderWorker + NotificationHelper |
| Mark attendance from notification actions | ✅ | Receiver-based present/absent actions |
| **Persistent reminder until marked** | ❌ | ClassTrack's standout: notification stays until logged |
| Lock-screen / quick-tile marking | ❌ | Android equivalent of MyCollegeMate's Live Activities: persistent notification with live countdown + actions |
| Home-screen **widgets** | ❌ | Big gap. Widget = today's classes + overall % + one-tap mark (Glance/WorkManager) |
| Post-class "did you attend?" follow-up nudge | ❌ | 15 min after class end, if unmarked |
| End-of-day digest | ❌ | "3 classes today, 2 unmarked" |

## 4. Beyond Attendance (Planner Expansion)

| Idea | Status | Notes |
|---|---|---|
| Assignment/deadline tracker per subject | ❌ | Attenly + MyStudyLife both have it; natural next feature |
| Exams & countdowns | ❌ | Exam entity + countdowns + "attendance must be ≥X to sit exam" warnings |
| Grades/GPA tracking | ❌ | Class Timetable / MyStudyLife feature |
| Subject notes & attachments (PDFs, links, photos) | ❌ | MyCollegeMate's most-loved feature |
| Pomodoro/focus timer | ❌ | MyStudyLife; low priority, crowded space |
| Teacher database (contacts, rooms) | 🔶 | Teacher name exists per subject; no directory |

## 5. Data, Sync & Platform

| Idea | Status | Notes |
|---|---|---|
| Local-first, no account | ✅ | Keep this as a privacy selling point |
| JSON/CSV backup export-import | ✅ | |
| **Automatic local backups** (daily, auto-rotate) | ❌ | Users lose data on reinstall — biggest real-world complaint |
| Cloud sync (Google Drive / WebDAV) | ❌ | MyCollegeMate uses iCloud; Android equivalent = Drive AppFolder |
| Cross-platform (web/desktop) | ❌ | Long-term: Compose Multiplatform |
| Google Calendar sync | ❌ | AttendMate differentiator; two-way push of class schedule |
| Auto-backup to user's own Google account | ❌ | Privacy-preserving alternative to building a backend |

## 6. Design System & UX

- ✅ Material 3, dynamic color, custom themes, dark mode — solid base.
- **Progress rings** (MyCollegeMate) for per-subject % instead of plain text — glanceable risk color (green/amber/red).
- **Dashboard-first Home**: hero card (overall % ring + bunk budget today) → current-class live card → today's list → risk strip of subjects near the threshold.
- **One-tap everything**: marking should never take >1 tap from Home or notification; add swipe gestures (swipe right = present, left = absent) with undo toast.
- Empty states & onboarding polish: sample-data preview mode, animated first-run.
- Motion: shared-element transitions from subject card → detail; springy progress rings.
- Accessibility: content descriptions, larger touch targets on marking buttons, TalkBack audit.
- App icon/theming: themed icon (Android 13+), Material You wallpaper-based accent (already partially supported).

## 7. Monetization & Growth (reference models)

- Class Timetable / MyStudyLife: free core + PRO subscription (unlimited entries, premium themes, widgets).
- Attenly: referral rewards unlock Pro.
- Lumera angle: keep attendance/bunk math free forever (it's the hook); Pro = cloud sync, widgets, themes, AI OCR without own key.

## 8. Technical / Architecture Improvements

- **DI**: replace manual DI with Hilt (testability, less boilerplate in NavGraph factories).
- **Repository layering**: split the god-repository (`AttendSmartlyRepository`, 600+ lines) into Subject/Timetable/Attendance/Holiday repositories.
- **Use case layer**: extract `AttendanceCalculator` interactions into use cases (CalculateBunkBudget, ProjectAttendance).
- **Testing**: unit tests exist for calculator/export — add DAO tests, ViewModel tests (Turbine), and Compose UI tests.
- **Crash reporting**: optional privacy-friendly telemetry (ACRA or Firebase Crashlytics, opt-in).
- **Gemini key friction**: proxy backend with free quota OR on-device OCR fallback (see §2) — biggest onboarding drop-off risk.

---

## Priority Shortlist (impact × effort)

1. **Home-screen widgets + persistent "mark now" notification** — biggest daily-use QoL gap.
2. **Recovery Mode + risk prediction alerts** — the emotional core of the 75% problem.
3. **On-device OCR fallback** — removes the Gemini-key onboarding wall.
4. **What-if bunk simulator + leave planner** — cheap to build on existing calculator, high delight.
5. **Auto-backup + Drive restore** — prevents the #1 support issue (data loss).
6. **Assignments & exams tracker** — turns Lumera from a tracker into a full student planner.
7. **Live bell timer + current-class hero card** — high perceived polish, low effort.
8. Hilt + repository split — before adding more features, to keep velocity.
