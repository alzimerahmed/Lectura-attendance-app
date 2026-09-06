# Rule: Bug Fix & Debugging Discipline

**ALWAYS** apply the Bug Fix & Debugging skill and workflow when diagnosing and fixing bugs. Fix the root cause, not the symptom — use structured, evidence-driven diagnosis.

## Skill
`~/.codeium/windsurf/skills/bug-fix-debugging.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/debug.md` — invoke with `/debug`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/debugger.md` (parent: Quality Engineer)

## How to follow this rule:
1. When fixing a bug, invoke the `/debug` workflow
2. Follow the workflow steps in order: Reproduce → Gather Evidence → Isolate → Root Cause Analysis → Fix → Regression Test → Verify → Document & Prevent
3. Always reproduce the bug reliably before attempting a fix
4. Always fix the root cause, not the symptom — no downstream workarounds
5. Always add a regression test that reproduces the bug
6. Keep fixes minimal — single-line changes when sufficient, no scope creep
7. After fixing, check whether the bug class could exist elsewhere in the codebase
8. Recommend guardrails (linting rules, type safety, tests) to prevent the bug class

## When this rule applies:
- User reports a bug
- A test fails unexpectedly
- An error appears in production
- User asks to fix or debug something

## When this rule does NOT apply:
- Feature development (not a bug fix)
- User explicitly says to skip the debugging workflow
