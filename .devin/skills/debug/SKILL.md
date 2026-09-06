---
name: debug
description: Systematic, root-cause-driven debugging workflow — reproduce, diagnose, fix, verify, and prevent bugs
---

# Bug Fix & Debugging Workflow

This workflow applies the **Bug Fix & Debugging Skill** (`~/.codeium/windsurf/skills/bug-fix-debugging.md`) to systematically diagnose and fix bugs by finding and addressing the root cause.

## When to Run
- When the user reports a bug
- When a test fails unexpectedly
- When an error appears in production
- When the user says `/debug` or asks to fix a bug

---

## Step 1: Reproduce the Bug

1. Get exact reproduction steps from the user or bug report
2. Note the environment: browser, OS, device, screen size, network, account state
3. Follow the exact steps to reproduce locally
4. If it doesn't reproduce, try matching the environment more closely
5. Minimize the reproduction — strip away unnecessary steps
6. Document the exact minimal reproduction steps

## Step 2: Gather Evidence

1. **Browser console:** Check for errors, warnings, unhandled promises
2. **Network tab:** Check API requests/responses, status codes, timing
3. **Server logs:** Check for errors, stack traces, request context
4. **Database:** Check if data is correct, constraints, query performance
5. **Stack trace:** Read it — find your code files, go to the exact lines

## Step 3: Isolate the Failing Component

1. Trace the data flow: User Action → Event Handler → State → API → Server → Database → Response → UI
2. Find where the data first becomes incorrect
3. Use binary search if multiple components are involved
4. Identify the layer: UI, State, API, Server, Database, Config, or Infra

## Step 4: Perform Root Cause Analysis

1. Distinguish symptom from cause — "blank screen" is a symptom, "missing index" is a cause
2. Apply 5-Whys technique to drill down to the root cause
3. Trace data flow through the system to find where it breaks
4. Form a hypothesis: "The bug occurs because X happens when Y is true"
5. If using git bisect: `git bisect start` → `git bisect bad` → `git bisect good <hash>`

## Step 5: Implement the Fix

1. Fix the root cause, not the symptom
2. Make the minimal change necessary — single-line if sufficient
3. Don't refactor surrounding code — that's a separate PR
4. Don't over-engineer — simplest fix that addresses the root cause
5. Don't add workarounds downstream — fix upstream
6. Preserve existing tests — don't modify them to accommodate the bug
7. Avoid scope creep — note other issues as separate TODOs/issues

## Step 6: Add Regression Test

1. Write a test that reproduces the bug (fails before fix, passes after)
2. Place it next to related tests
3. Test the exact scenario that was broken
4. Include edge cases around the fix boundary

## Step 7: Verify the Fix

1. Run the exact reproduction steps — bug should no longer occur
2. Run the full test suite — no regressions
3. Test related functionality that shares the same code path
4. Check for new console errors or warnings
5. Test edge cases: empty input, null, max size, concurrent operations
6. Test in different browsers/devices if applicable

## Step 8: Document & Prevent

1. **Document the bug and fix:**
   - What was the symptom? How to reproduce?
   - What was the root cause? Which layer? Which code?
   - What was changed? Why this approach?

2. **Update documentation** if the fix changes behavior

3. **Add monitoring/alerting** for the failure mode to catch recurrence

4. **Check for the bug class elsewhere:**
   - Search codebase for similar patterns
   - If it was a missing null check, find similar unchecked accesses
   - If it was a race condition, find similar async patterns

5. **Recommend guardrails:**
   - Linting rules to catch the pattern
   - Type safety improvements
   - Test coverage requirements
   - Architectural improvements to eliminate the bug class
