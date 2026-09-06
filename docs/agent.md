# Agent Notes — Lumera

## Environment
- Windows, PowerShell. Workspace root = repo root (cloned in place).
- Remote: https://github.com/alzimerahmed/Lumera-attendance-app.git (owner: alzimer ahmed / alzimerahmed84@gmail.com)
- Grep tool returns false negatives in this workspace — use PowerShell `Select-String` instead.
- Long commands: run async, poll with command_status.

## Conventions
- File headers: `© alzimer ahmed — github.com/alzimerahmed84` GPL-3.0 notice in every .kt
- Package: `com.alzimerahmed.lumera`
- Never commit: *.jks, *.keystore, keystore.properties, .env
- Keystore lives at C:\Users\shadd\keystores\ (Lumera-release.jks, alias `lumera`)

## Verification
- Build: `java -cp gradle\wrapper\gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain assembleDebug` (the `&` in the folder path breaks gradlew.bat — use the jar directly)
- Tests: same command with `testDebugUnitTest`

## Session log
- 2026-09-06: Phase 2 DONE (local, NOT pushed) — commit 514e895: AttendanceCalculator extensions (recoveryPlan/projectAttendance/simulate + RecoveryPlan data class, projected-% bug caught by new tests and fixed), 3 new unit tests green, per-subject risk strip on Home (recovering = below target red, at-risk = within +5% amber, sorted worst-first, top 4), What-if simulator bottom sheet (attend/miss steppers, live projected %). DEFERRED: leave planner (date-range impact table) and per-subject target override UI (entity field already exists, UI edit pending). Note: per-subject targets already existed via SubjectEntity.targetPercentage.
- 2026-09-06: Phase 1 DONE (local, NOT pushed per user request) — commit 8b5270b: (1) live current-class hero card on Home (30s ticker, HomeViewModel.currentClass), (2) persistent in-progress notification with chronometer at class start (NotificationHelper.showOngoingClassNotification, receiver routes minutesBefore==0), (3) post-class unmarked nudge in ReminderWorker (ended >=15min + unmarked), (4) Glance home-screen widget (overall %, safe bunks, next class; LumeraWidgetReceiver + 30-min WorkManager refresh). Review fix: alarm request codes differentiated (sessionId*10+flag) so start alarm no longer overwrites reminder alarm. Build + tests green.
- 2026-09-06: Phase 0 DONE — rebrand to Lumera (package com.alzimerahmed.lumera, all classes/assets), Hilt DI (AppModule + @HiltViewModel x7 + hiltViewModel in NavGraph, manual factory deleted), build + unit tests green, CI verified (already runs test+assemble on PR, signed release via secrets), release alias default = lumera. DEFERRED: physical repository split (cross-domain logic in LumeraRepository needs integration tests first — do as standalone refactor in Phase 2+).
- 2026-09-06: ownership transfer, cleanup, keystore, competitor research → docs/idea.md; implementation plan → docs/IMPLEMENTATION_PLAN.md (8 phases). All docs live in docs/.
- Grep tool unreliable here — always use PowerShell Select-String.
