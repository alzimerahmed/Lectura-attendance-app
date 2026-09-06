---
name: Bug Fix & Debugging Skill
description: Systematic, root-cause-driven methodology for diagnosing and fixing bugs — 2025-2026 practices with structured, evidence-driven diagnosis
version: 1.0.0
tags: [debugging, bug-fix, root-cause, diagnosis, troubleshooting]
---

# Bug Fix & Debugging Skill

## Purpose
This skill provides a systematic, root-cause-driven methodology for diagnosing and fixing bugs across any kind of web project. It reflects **modern 2025-2026 debugging practices** — not just trial-and-error but structured, evidence-driven diagnosis that finds and fixes the actual root cause.

## Core Philosophy

**Fix the root cause, not the symptom.** A workaround that suppresses the error without understanding it will lead to recurring bugs, technical debt, and fragile code. Always trace the bug to its origin before writing a fix.

**The #1 rule:** Reproduce reliably before fixing. If you can't reproduce it, you can't verify the fix. A bug you can't reproduce is a bug you can't fix.

---

## Part 1: Debugging Methodology

### 1.1 Reproducing the Bug Reliably
1. **Get exact steps:** Ask the user (or read the report) for the exact sequence of actions that triggered the bug
2. **Note the environment:** Browser, OS, device, screen size, network conditions, account state
3. **Reproduce locally:** Follow the exact steps — if it doesn't reproduce, try matching the environment more closely
4. **Minimize the reproduction:** Strip away unnecessary steps until you have the minimal sequence that triggers the bug
5. **Document the reproduction:** Write down the exact steps — you'll need them to verify the fix

### 1.2 Isolating the Failing Component
1. **Binary search approach:** If the system has multiple components, disable/enable halves to narrow down
2. **Check the network tab:** Is the API returning the wrong data? Is the request malformed?
3. **Check the console:** Are there JavaScript errors? Warnings? Unhandled promise rejections?
4. **Check the server logs:** Is there a server-side error? A stack trace?
5. **Check the database:** Is the data correct in the database? Is there a constraint violation?

### 1.3 Bisecting to Find the Introducing Change
```bash
# Find the commit that introduced the bug
git bisect start
git bisect bad          # Current commit is bad
git bisect good <hash>  # Last known good commit
# Git will check out a middle commit — test it
git bisect good         # or git bisect bad
# Repeat until git identifies the culprit commit
git bisect reset
```

### 1.4 Forming and Testing Hypotheses
1. **Form a hypothesis:** "The bug occurs because X happens when Y is true"
2. **Predict the outcome:** "If I change X to Z, the bug should disappear"
3. **Test the hypothesis:** Make the change and test with the reproduction steps
4. **If the hypothesis is wrong:** Discard it and form a new one — don't pile on changes
5. **If the hypothesis is right:** Verify the fix doesn't break anything else

### 1.5 Binary Search Through Git History
- Use `git bisect` when the bug appeared after a series of commits
- Mark the current state as `bad` and the last known working state as `good`
- Git will binary search through commits, checking out each for testing
- Automate with `git bisect run <test-command>` if a test can detect the bug

---

## Part 2: Root Cause Analysis

### 2.1 Distinguishing Symptoms from Causes
- **Symptom:** "The page shows a blank screen"
- **Cause:** "The API returns 500 because the database query times out because of a missing index on a foreign key"
- **Wrong fix:** "Add a loading spinner so the blank screen shows something"
- **Right fix:** "Add the missing index, optimize the query"

### 2.2 The 5-Whys Technique
1. Why does the page show a blank screen? → The API call fails
2. Why does the API call fail? → The server returns a 500 error
3. Why does the server return 500? → The database query times out
4. Why does the query time out? → It's doing a sequential scan on 1M rows
5. Why is it doing a sequential scan? → There's no index on the foreign key column

**Fix:** Add the index. **Prevention:** Add a CI check for unindexed foreign keys.

### 2.3 Tracing Data Flow Through the System
```
User Action → Event Handler → State Update → API Call → Server Route →
Service Layer → Database Query → Response → State Update → Re-render → UI
```
- Trace the data at each step — where does it become incorrect?
- Add logging/breakpoints at each boundary to inspect the data
- The bug is at the point where the data first becomes incorrect

### 2.4 Identifying the Layer Where the Bug Originates

| Layer | Signs | Tools |
|---|---|---|
| **UI** | Wrong rendering, layout broken, interaction doesn't work | DevTools Elements, React DevTools |
| **State** | State is stale, wrong, or not updating | React DevTools, Redux DevTools, console.log |
| **API** | Wrong request, wrong response, network error | DevTools Network tab, curl, Postman |
| **Server** | 500 error, wrong logic, unhandled case | Server logs, debugger, breakpoints |
| **Database** | Wrong data, missing data, constraint error | SQL client, EXPLAIN ANALYZE, query logs |
| **Config** | Wrong env var, wrong feature flag, wrong config | env inspection, config validation |
| **Infra** | DNS, SSL, CDN, load balancer, proxy issues | curl -v, dig, traceroute, server logs |

---

## Part 3: Common Bug Categories

### 3.1 State Management Bugs
- **Stale closures:** useEffect/useCallback capturing old state — fix with proper dependency arrays or refs
- **Race conditions:** Multiple async operations updating the same state — use AbortController or queue
- **Zombie children:** Child component updating state after unmount — use cleanup functions and abort signals
- **State desync:** Local state out of sync with server state — use server state library (React Query, SWR)
- **Context propagation:** Context value changes not reaching consumers — check reference equality

### 3.2 Rendering Bugs
- **Key warnings:** Duplicate or missing keys cause incorrect rendering — use stable unique IDs
- **Effect dependency issues:** Missing dependencies cause stale data — use exhaustive-deps rule
- **Hydration mismatches:** Server-rendered HTML differs from client — avoid `Date.now()`, `Math.random()`, `window` in render
- **Layout shift:** Content jumps during load — set explicit dimensions, use skeleton placeholders
- **CSS specificity wars:** Styles not applying due to specificity — use DevTools to inspect cascade

### 3.3 API Bugs
- **Serialization:** Date objects serialized as strings, BigInt lost in JSON — use proper serialization
- **Deserialization:** API returns string but code expects number — validate and transform at boundary
- **Status code handling:** Treating 4xx as success, ignoring 5xx — check response.ok or status code
- **Timeout:** Request hangs indefinitely — set timeout and handle timeout error
- **Pagination:** Off-by-one in offset/limit, cursor pagination broken — test edge cases

### 3.4 Database Bugs
- **Transaction isolation:** Read committed vs repeatable read — non-repeatable reads, phantom reads
- **Constraint violations:** Unique, foreign key, check constraints — handle gracefully
- **Migration drift:** Schema in code doesn't match database — run migrations, check migration status
- **Connection pool exhaustion:** Too many connections — check pool config, look for connection leaks
- **Deadlocks:** Two transactions waiting on each other — order operations consistently

### 3.5 CSS/Layout Bugs
- **Box model:** padding/border not accounted for — use `box-sizing: border-box`
- **Stacking contexts:** z-index not working — parent has `transform`, `opacity`, or `filter` creating new context
- **Flexbox edge cases:** `flex: 1` with `min-width: auto` causing overflow — set `min-width: 0`
- **Grid edge cases:** Implicit tracks, `auto` vs `1fr`, `minmax` behavior
- **Mobile viewport:** `100vh` includes browser chrome — use `100dvh` or `100svh`

---

## Part 4: Fix Discipline

### 4.1 Minimal Upstream Fixes Over Downstream Workarounds
- **Wrong:** Adding a null check downstream to handle a case that shouldn't happen upstream
- **Right:** Fix the upstream code so it never sends null in the first place
- **Wrong:** Catching and ignoring an error to prevent a crash
- **Right:** Fix the code that's causing the error

### 4.2 Single-Line Changes When Sufficient
- Don't refactor surrounding code while fixing a bug — that's a separate PR
- If the fix is a single line (e.g., `<` → `<=`), just change that line
- Resist the urge to "clean up" while fixing — it makes the fix harder to review

### 4.3 No Over-Engineering
- Don't add abstractions, patterns, or complexity to fix a simple bug
- Don't add error handling for hypothetical future scenarios
- Fix the bug in the simplest way that addresses the root cause

### 4.4 Preserving Existing Tests
- Don't modify existing tests to make them pass with the bug
- Don't delete tests that are failing because of the bug — fix the bug
- Don't weaken test assertions to accommodate the bug

### 4.5 Adding Regression Tests for the Fixed Bug
- Write a test that reproduces the bug (fails before the fix, passes after)
- Place it next to related tests
- Test the exact scenario that was broken
- Include edge cases around the fix

### 4.6 Avoiding Scope Creep During a Fix
- If you notice other issues while fixing, note them as TODOs or separate issues
- Don't fix "while I'm here" bugs in the same change — separate PR
- Keep the diff minimal and focused on the one bug

---

## Part 5: Verification

### 5.1 Confirming the Fix Resolves the Original Symptom
1. Run the exact reproduction steps from Part 1.1
2. Verify the bug no longer occurs
3. Test with variations of the reproduction (different inputs, different order)

### 5.2 Checking for Side Effects
1. Run the existing test suite — `npm test` / `pnpm test`
2. Test related functionality that shares the same code path
3. Check for new console errors or warnings
4. Test the fix in different browsers/devices if applicable

### 5.3 Running Existing Test Suite
- Run the full test suite, not just tests related to the fix
- If any tests fail, investigate — the fix may have broken something
- Don't skip or disable failing tests — fix them or revert the fix

### 5.4 Manual Verification of Related Flows
- Test the happy path through the affected code
- Test error paths through the affected code
- Test edge cases around the fix boundary
- Test with different user roles/permissions if applicable

### 5.5 Testing Edge Cases Around the Fix
- What happens at the boundary of the fix?
- What if the input is empty? Null? Maximum size?
- What if the operation is called concurrently?
- What if the network is slow or unreliable?

---

## Part 6: Error Reading & Interpretation

### 6.1 Reading Stack Traces
```
TypeError: Cannot read properties of undefined (reading 'map')
    at UserList (UserList.tsx:23:18)
    at renderWithHooks (react-dom.js:14985:18)
    at updateFunctionComponent (react-dom.js:17356:20)
```
- **Start from the top:** The error type and message tell you what went wrong
- **Find your code:** Look for file names you recognize in the stack
- **Read the line numbers:** Go to the exact file and line
- **Trace backwards:** Each stack frame shows what called what

### 6.2 Decoding Minified Errors
- Use source maps to decode: DevTools automatically uses source maps if available
- In production: ensure source maps are uploaded to error tracking (Sentry, Bugsnag)
- `Error: t is not a function` → minified variable, need source map to identify

### 6.3 Interpreting Browser Console Errors
- **Red errors:** Must fix — actual errors that break functionality
- **Yellow warnings:** Should fix — deprecation notices, React warnings, performance issues
- **CORS errors:** Check server CORS configuration and request headers
- **CSP violations:** Content Security Policy blocking resources — update CSP headers
- **Mixed content:** HTTPS page loading HTTP resources — upgrade to HTTPS

### 6.4 Analyzing Network Failures
- **Status codes:** 4xx = client error, 5xx = server error, 0 = network/CORS failure
- **Request inspection:** Check URL, method, headers, body — is the request correct?
- **Response inspection:** Check status, headers, body — is the response what you expect?
- **Timing:** Is the request slow? Check DNS, connection, TTFB, content download
- **Failed requests:** Check if it's a CORS issue, auth issue, or server error

### 6.5 Reading Server Logs
- **Timestamp:** When did the error occur? Correlate with user actions
- **Request ID:** Trace a single request through the system
- **Stack trace:** Same as client stack traces — find your code, read the line
- **Context:** What was the user doing? What was the input?
- **Level:** ERROR (must investigate), WARN (should investigate), INFO (context)

### 6.6 Understanding Error Codes
- **HTTP status codes:** 400 (bad request), 401 (unauth), 403 (forbidden), 404 (not found), 409 (conflict), 422 (unprocessable), 429 (rate limited), 500 (server error), 502 (bad gateway), 503 (unavailable), 504 (timeout)
- **Database error codes:** PostgreSQL (23505 = unique violation, 23503 = FK violation, 23502 = not-null violation, 40001 = serialization failure)
- **Node.js error codes:** ENOENT (file not found), EACCES (permission denied), ECONNREFUSED (connection refused), ETIMEDOUT (timeout)

---

## Part 7: Tooling

### 7.1 Browser DevTools

| Tab | What It's For |
|---|---|
| **Elements** | Inspect DOM, CSS, layout — modify live to test fixes |
| **Console** | Run JS, see errors/warnings, log values, interact with page |
| **Network** | Inspect requests/responses, headers, timing, status codes |
| **Sources** | Set breakpoints, step through code, inspect variables, call stack |
| **Performance** | Record execution, find slow functions, layout thrashing |
| **Memory** | Heap snapshots, allocation timelines, memory leak detection |
| **Application** | localStorage, sessionStorage, IndexedDB, cookies, service workers |

### 7.2 Debugging Proxies
- **Charles Proxy / Proxyman:** Intercept and modify HTTP requests/responses
- **mitmproxy:** Command-line proxy for API debugging
- Use to: simulate API errors, modify responses, test error handling

### 7.3 Log Analysis
- **Structured logs:** Search by request ID, user ID, timestamp
- **Log levels:** Filter by ERROR/WARN to find issues quickly
- **Correlation IDs:** Trace a request across multiple services
- **Tools:** Datadog, Loki, ELK, CloudWatch Logs Insights

### 7.4 Database Inspection
- **SQL client:** DBeaver, TablePlus, pgAdmin — run queries, inspect data
- **EXPLAIN ANALYZE:** Understand query execution plans
- **Active queries:** `pg_stat_activity` — see what's running, what's blocked
- **Locks:** `pg_locks` — identify deadlocks and blocking

### 7.5 Feature Flag Toggling for Debugging in Production
- Toggle off a problematic feature without deploying
- Toggle on debug logging for a specific user
- Gradually roll out a fix to verify it works in production
- Tools: LaunchDarkly, GrowthBook, PostHog, custom flags

---

## Part 8: Post-Fix Practices

### 8.1 Documenting the Bug and Fix
- **Bug description:** What was the symptom? How to reproduce?
- **Root cause:** What was actually wrong? Which layer? Which code?
- **Fix:** What was changed? Why this approach?
- **Verification:** How was the fix verified? What tests were added?
- **Format:** PR description, commit message, or issue comment

### 8.2 Updating Relevant Documentation
- If the bug was caused by a documentation gap, update the docs
- If the fix changes behavior, update API docs, README, or inline docs
- If the fix adds a new constraint or requirement, document it

### 8.3 Adding Monitoring/Alerting for the Failure Mode
- If the bug caused user-facing errors, add error tracking (Sentry, Bugsnag)
- If the bug was a performance issue, add metrics/dashboards
- If the bug was a data issue, add data validation checks
- Set up alerts to catch recurrence early

### 8.4 Considering Whether the Bug Class Could Exist Elsewhere
- Search the codebase for similar patterns
- If the bug was a missing null check, are there similar unchecked accesses?
- If the bug was a race condition, are there similar async patterns?
- Create a linting rule or code review checklist item to prevent the class

---

## Part 9: Prevention

### 9.1 Identifying Systemic Causes
- **Missing tests:** Was the bug in untested code? → Add test coverage requirements
- **Unclear APIs:** Was the bug from misunderstanding an API? → Improve API docs/types
- **Complex state:** Was the bug from tangled state management? → Simplify or use state machine
- **Manual processes:** Was the bug from a manual step? → Automate
- **Type safety gaps:** Was the bug from a type error? → Enable stricter TypeScript, add Zod validation

### 9.2 Recommending Guardrails
- **Type safety:** Enable `strict` mode, `noUncheckedIndexedAccess`, branded types for IDs
- **Runtime validation:** Zod/Valibot at API boundaries
- **Linting rules:** ESLint rules for common bug patterns (no-floating-promises, no-unsafe-assignment)
- **Pre-commit hooks:** Type check, lint, test before commit
- **CI gates:** Full test suite, type check, lint on PR

### 9.3 Suggesting Architectural Improvements
- **Make invalid states unrepresentable:** Use discriminated unions instead of optional fields
- **Reduce shared mutable state:** Use immutability, pure functions, server state libraries
- **Simplify complex flows:** Use state machines (XState) for multi-step processes
- **Add error boundaries:** Catch errors at component boundaries, show fallback UI
- **Improve observability:** Structured logging, error tracking, metrics from day one

---

## Execution Instructions for Cascade

When this skill is activated for debugging:

1. **Reproduce first** — never start fixing without a reliable reproduction
2. **Read the error** — stack trace, console, network, logs — gather all evidence
3. **Trace the data flow** — find where the data first becomes incorrect
4. **Form a hypothesis** — predict what's wrong and what the fix should be
5. **Test the hypothesis** — make the minimal change and test with reproduction steps
6. **Fix the root cause** — not the symptom, not the downstream effect
7. **Keep the fix minimal** — single-line if sufficient, no scope creep
8. **Add a regression test** — test that reproduces the bug, passes with the fix
9. **Verify** — run reproduction steps, run test suite, check for side effects
10. **Document** — what was the bug, what was the root cause, what was the fix
11. **Prevent** — could this bug class exist elsewhere? Add guardrails
