# Agent Notes — Lumera

## Environment
- Windows, PowerShell. Workspace root = repo root (cloned in place).
- Remote: https://github.com/alzimerahmed/Lumera-attendance-app.git (owner: alzimer ahmed / alzimerahmed84@gmail.com)
- Grep tool returns false negatives in this workspace — use PowerShell `Select-String` instead.
- Long commands: run async, poll with command_status.

## Conventions
- File headers: `© alzimer ahmed — github.com/alzimerahmed84` GPL-3.0 notice in every .kt
- Package: `com.alzimerahmed.lumera` (rename to Lumera pending)
- Never commit: *.jks, *.keystore, keystore.properties, .env
- Keystore lives at C:\Users\shadd\keystores\ (Lumera-release.jks, alias `lumera`)

## Verification
- Build: `gradlew.bat assembleDebug` (not yet run — package rename unverified by a build)
- Tests: `gradlew.bat test`

## Session log
- 2026-09-06: ownership transfer, cleanup, keystore, competitor research → docs/idea.md; implementation plan → docs/IMPLEMENTATION_PLAN.md (8 phases, Phase 0 = rebrand + Hilt + repo split + build verification). All docs live in docs/.
- Grep tool unreliable here — always use PowerShell Select-String.
