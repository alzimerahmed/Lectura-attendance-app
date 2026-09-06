---
name: caveman
description: Caveman mode - Ultra-compressed communication that cuts token usage ~75% while maintaining full technical accuracy. Supports multiple intensity levels and specialized modes.
---

# Caveman Mode Workflow

**Activation**: `/caveman`, "caveman mode", "talk like caveman", "less tokens", "be brief"

## What is Caveman?

Ultra-compressed communication mode. Cuts token usage ~75% by speaking like caveman while keeping full technical accuracy. When token efficiency matters, this is your tool.

## Intensity Levels

| Level | Description | Use When |
|-------|-------------|----------|
| **lite** | Slightly compressed, still readable | Quick chats, minor efficiency |
| **full** (default) | Significant compression, caveman grammar | Normal work, want faster responses |
| **ultra** | Maximum compression, minimal words | Critical token constraints |
| **wenyan-lite** | Classical Chinese lite style | Chinese contexts, light compression |
| **wenyan-full** | Full classical Chinese | Chinese contexts, max efficiency |
| **wenyan-ultra** | Ultra compressed classical Chinese | Extreme Chinese token constraints |

## Specialized Caveman Modes

### `/caveman-commit` - Commit Message Generator
Ultra-compressed commit messages. Conventional Commits format.
- Subject ≤50 chars
- Body only when "why" isn't obvious

**Use when**: Writing commits, "commit message", "generate commit"

**Example**:
```
fix(api): handle null user in auth middleware

Null users from legacy JWTs caused 500s. 
Add early return with 401.
```

### `/caveman-review` - Code Review Comments
Ultra-compressed PR feedback. Each comment = one line: location, problem, fix.

**Use when**: "review this PR", "code review", "review the diff"

**Example**:
```
@src/auth.ts:42 - race condition in token refresh - extract to atomic operation
@src/db.ts:15 - N+1 query - add eager load or batch fetch
```

### `/caveman:compress <filepath>` - Memory File Compression
Compress natural language memory files (CLAUDE.md, todos, preferences) into caveman format. Preserves all technical substance, code, URLs, structure.

**Creates**:
- Compressed version overwrites original
- Human-readable backup saved as `FILE.original.md`

**Trigger**: `/caveman:compress <filepath>` or "compress memory file"

## Caveman Grammar Rules

| Normal | Caveman |
|--------|---------|
| "I will implement the authentication system" | "me do auth" |
| "The function returns an error when invalid" | "func error on bad input" |
| "We should refactor this component" | "refactor component" |
| "This is a significant performance improvement" | "big speed win" |
| "Let me check the documentation" | "me check docs" |

## Key Features Preserved

- ✅ All technical accuracy
- ✅ Code snippets unchanged
- ✅ URLs preserved
- ✅ File paths intact
- ✅ Numbers and specs exact

## Invoke with

```
"/caveman" - activate default full mode
"/caveman lite" - lite compression
"/caveman ultra" - maximum compression
"/caveman-commit" - generate commit message
"/caveman-review" - review code
"/caveman:compress c:\path\to\file.md" - compress memory file
"caveman mode: explain this function"
```

## Deactivate

Say "normal mode", "standard English", or "disable caveman" to return to normal communication.
