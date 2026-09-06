# Lumera — Project Notes

## What it is
Android attendance tracker (formerly AttendSmartly, cloned from agupta07505/AttendSmartly, now owned by alzimer ahmed). Package: `com.alzimerahmed.attendsmartly`. Repo: github.com/alzimerahmed/Lumera-attendance-app.

## Stack
Kotlin, Jetpack Compose (M3), Room, DataStore, WorkManager + exact alarms, Navigation Compose, Gemini OCR (user-supplied key). Manual DI via Application class. Min API 24.

## Structure
- `data/` Room entities (Subject, TimetableEntry, AttendanceSession, AttendanceUnit, Holiday), DAOs, `AttendSmartlyRepository`, DataStore prefs, `data/remote/gemini/TimetableOcrService`
- `domain/` `AttendanceCalculator`, models
- `ui/` NavGraph + screens (home, timetable, subjects, subjectdetails, analytics, history, settings, onboarding), theme
- `worker/` ReminderWorker (15-min scan → exact alarms), `receiver/` notification actions
- `util/` DateUtils, ExportImportUtils (JSON/CSV), DemoDataGenerator, GitHubUpdateChecker

## Key design decisions
- Attendance is per-unit within a session (partial attendance supported)
- Timetable edits are date-versioned (startDate/endDate) to preserve history
- Gemini API key is per-user (entered in Settings); no backend
- Undo via in-memory unit snapshot

## Ownership work done (2026-09-06)
- Cloned detached from origin; remote = alzimerahmed/Lumera-attendance-app
- All authorship rewritten in git history (filter-branch) to alzimer ahmed <alzimerahmed84@gmail.com>
- Package renamed com.agupta07505.* → com.alzimerahmed.* (dirs, gradle, manifest, proguard)
- Deleted: old tags, AttendSmartly.jks/base64, metadata.json, .idea, 5W2H.md, ROADMAP.md, all .md except README + LICENSE
- New keystore: C:\Users\shadd\keystores\Lumera-release.jks (alias lumera, creds in Lumera-keystore.properties) — outside repo, gitignored

## Next
- idea.md written (competitor research: RollCall, MyCollegeMate, ClassTrack, Attenly, MyStudyLife, Class Timetable)
- Pending: implementation plan for idea.md items; app rename AttendSmartly → Lumera; README refresh
