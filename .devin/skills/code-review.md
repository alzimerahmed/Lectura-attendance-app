---
name: Code Review Skill
description: Comprehensive, systematic methodology for reviewing code quality, correctness, security, performance, and maintainability — 2025-2026 standards with deep structural and logical analysis
version: 1.0.0
tags: [code-review, quality, security, performance, correctness, maintainability]
---

# Code Review Skill

## Purpose
This skill provides a comprehensive, systematic methodology for reviewing code quality, correctness, security, performance, and maintainability across any kind of web project. It reflects **modern 2025-2026 code review standards** — not just linting checklists but deep structural and logical analysis that catches bugs automated tools miss.

## Core Philosophy

**Code review is the highest-leverage quality gate.** Automated tools catch ~20% of issues. Human review catches the rest: logic errors, architectural drift, security holes, performance traps, and maintainability decay. Every review should be thorough, constructive, and prioritized.

**The #1 rule:** Review the code that was written, not the code you wish was written. Focus on what matters: correctness, security, and clarity. Nitpicks go last.

---

## Part 1: Correctness & Logic

### 1.1 Race Conditions
- **Shared mutable state:** Any state accessed by multiple async flows (promises, callbacks, event handlers) without synchronization
- **Check-then-act:** `if (user) { user.save() }` — what if user is deleted between check and save?
- **TOCTOU (Time-of-check to time-of-use):** File existence checks followed by file operations
- **Concurrent writes:** Multiple requests updating the same record without optimistic locking
- **Stale closures:** React useEffect/useCallback capturing outdated state or props
- **Event handler races:** Rapid clicks triggering duplicate operations (use debounce or disable)

### 1.2 Off-by-One Errors
- Loop boundaries: `<` vs `<=`, `0` vs `1` start index
- Array slicing: `arr.slice(0, n)` vs `arr.slice(0, n+1)`
- Pagination: `offset = (page - 1) * limit` — check edge cases at page 0, page 1, last page
- Date/time boundaries: inclusive vs exclusive end dates
- String indexing: substring, substr, slice differences

### 1.3 Null/Undefined Handling
- **Missing null checks:** Accessing `.property` on potentially null/undefined without optional chaining
- **Null vs undefined:** Different semantics in JavaScript — check for both when appropriate
- **Default values:** `value ?? defaultValue` (nullish coalescing) vs `value || defaultValue` (falsy check — catches 0, "", false)
- **Optional chaining:** `user?.address?.city` — but verify the chain doesn't hide legitimate errors
- **Array access:** `arr[0]` when arr could be empty — use `arr[0]` with guard or `arr.at(0)`

### 1.4 Edge Cases
- **Empty inputs:** Empty arrays, empty strings, empty objects, null collections
- **Single-element edge case:** Loops, sorting, pagination with 1 item
- **Maximum inputs:** Very large arrays, very long strings, deep nesting
- **Unicode:** Multi-byte characters, emoji, RTL text in string operations
- **Time zones:** Date operations across time zones, DST transitions
- **Negative numbers:** In array indexing, pagination, math operations
- **Type coercion:** `0 == ""`, `null == undefined`, `"0" == 0` — use strict equality `===`

### 1.5 Incorrect State Transitions
- State machines without exhaustive checks — what happens in unhandled states?
- Status enums with invalid transitions (e.g., `cancelled` → `active`)
- Loading states: loading → error → loading (without clearing error)
- Auth states: logged out → loading → error (stuck in error state)
- Use discriminated unions to make invalid states unrepresentable

### 1.6 Type Mismatches
- String vs number in comparisons, API responses, URL params
- `parseInt` without radix: `parseInt("08")` — always pass radix 10
- `Number()` vs `parseInt()` vs `parseFloat()` — different behavior with empty strings, null, undefined
- Date parsing: `new Date("2024-01-01")` vs `new Date(2024, 0, 1)` — different time zone behavior
- JSON.parse without try/catch on untrusted input

### 1.7 Unhandled Promises
- **Missing await:** `doSomething()` without `await` — promise floats, errors swallowed
- **Unhandled rejections:** `.then()` without `.catch()` — unhandled promise rejection crashes Node.js
- **Async error boundaries:** React error boundaries don't catch async errors — need explicit try/catch
- **Promise.all vs Promise.allSettled:** `all` rejects on first failure, `allSettled` waits for all
- **Race conditions in async:** Multiple awaits where order matters but isn't enforced

### 1.8 Floating-Point Precision
- `0.1 + 0.2 !== 0.3` — use integer cents for money, or a decimal library
- `toFixed()` returns a string, not a number — `Number((0.1 + 0.2).toFixed(2))`
- Comparisons: use epsilon comparison `Math.abs(a - b) < Number.EPSILON`
- `NaN` propagation: `NaN === NaN` is false — use `Number.isNaN()`
- `Infinity` from division by zero — check before dividing

---

## Part 2: Security Review

### 2.1 Injection Vulnerabilities

| Type | Detection | Prevention |
|---|---|---|
| **SQL Injection** | String-concatenated queries, template literals in SQL, raw query builders with user input | Parameterized queries, ORM methods, prepared statements |
| **XSS** | `dangerouslySetInnerHTML`, `innerHTML`, `document.write`, unescaped user input in templates | Context-aware output encoding, textContent, React's default escaping, CSP |
| **Command Injection** | `exec()`, `execSync()` with user input, shell commands with string concatenation | `execFile()` with array arguments, avoid shell, sanitize input |
| **Path Traversal** | `../` in file paths, user input in `fs.readFile`, `path.join` with user input | `path.resolve()` + verify within allowed directory, allowlist approach |
| **SSRF** | User-provided URLs fetched server-side, webhook URLs, image proxy endpoints | Allowlist domains, block internal IPs (127.0.0.0/8, 10.0.0.0/8, 169.254.0.0/16), validate URL scheme |
| **LDAP Injection** | User input in LDAP queries | LDAP escaping, parameterized LDAP queries |
| **NoSQL Injection** | User input as query objects (`{ $gt: "" }`) | Validate and sanitize input, use schema validation |
| **Template Injection** | User input in template engines (Handlebars, EJS) | Use safe templating, sandboxed templates |

### 2.2 Auth/Authz Bypasses
- **Missing authorization checks:** Every API endpoint must verify the user is authenticated AND authorized
- **IDOR (Insecure Direct Object Reference):** `GET /api/users/123` — verify user 123 belongs to the requester
- **Horizontal privilege escalation:** User A accessing user B's resources
- **Vertical privilege escalation:** Regular user accessing admin endpoints
- **Missing role checks:** Admin-only operations without role verification
- **JWT issues:** `algorithm: none`, accepting unsigned tokens, not verifying issuer/audience
- **Session fixation:** Not regenerating session ID after login
- **Missing CSRF protection:** State-changing operations without CSRF tokens

### 2.3 Insecure Dependencies
- Run `npm audit` / `pnpm audit` — check for known vulnerabilities
- Review new dependencies for: maintenance status, security history, transitive deps
- Check for typosquatting (e.g., `reactt` instead of `react`)
- Review lockfile changes — unexpected version bumps
- Use `npm ls` to inspect dependency tree

### 2.4 Secrets in Code
- **Hardcoded secrets:** API keys, passwords, tokens in source code
- **Secrets in client code:** Any secret shipped to the browser is public
- **Secrets in URLs:** `https://api.example.com?api_key=secret` — logged in server logs, browser history
- **Secrets in git history:** Even if removed, still in git history — use `git-secrets` or `TruffleHog`
- **Environment variables in client bundles:** `VITE_` or `NEXT_PUBLIC_` prefixed secrets are exposed
- **Check:** `.env` files in `.gitignore`, secrets only in server-side code, secrets manager for production

### 2.5 Insecure Crypto Usage
- **Weak algorithms:** MD5, SHA1 for passwords — use bcrypt, argon2, scrypt
- **Math.random for security:** Use `crypto.getRandomValues()` or `crypto.randomUUID()`
- **Hardcoded IVs:** AES with static IV — use random IV per encryption
- **ECB mode:** Identical plaintext blocks produce identical ciphertext — use GCM or CBC
- **Short key lengths:** RSA < 2048 bits, AES < 128 bits
- **Custom crypto:** Never implement crypto yourself — use established libraries

### 2.6 Missing Input Validation
- **No validation at API boundary:** Every API endpoint must validate input
- **Trust of client-side validation:** Server must re-validate everything
- **Missing type checks:** `typeof` / schema validation on incoming data
- **Missing length limits:** Unbounded strings, arrays — DoS vector
- **Missing range checks:** Negative numbers, out-of-range dates
- **Use Zod, Valibot, or similar for schema validation at boundaries**

### 2.7 CORS Misconfigurations
- `Access-Control-Allow-Origin: *` with `credentials: true` — **critical vulnerability**
- Reflecting any origin without allowlist — allows any site to make authenticated requests
- `Access-Control-Allow-Headers: *` — overly permissive
- Missing `Access-Control-Allow-Credentials` when cookies are needed
- Preflight caching too long (`Access-Control-Max-Age: 86400`)

### 2.8 OWASP Top 10 Coverage

| # | Category | Key Checks |
|---|---|---|
| A01 | Broken Access Control | IDOR, missing authz checks, privilege escalation |
| A02 | Cryptographic Failures | Weak algorithms, hardcoded secrets, insecure TLS |
| A03 | Injection | SQL, NoSQL, XSS, command, LDAP injection |
| A04 | Insecure Design | Missing threat modeling, no rate limiting, no security layers |
| A05 | Security Misconfiguration | Default credentials, verbose errors, open S3 buckets |
| A06 | Vulnerable Components | Outdated deps, known CVEs, typosquatting |
| A07 | Auth Failures | Weak passwords, no MFA, credential stuffing, session fixation |
| A08 | Software/Data Integrity | Unsigned updates, insecure deserialization, CI/CD security |
| A09 | Logging/Monitoring Failures | Missing audit logs, no alerting, no incident response |
| A10 | SSRF | Unvalidated URLs, internal network access from server |

---

## Part 3: Performance

### 3.1 N+1 Queries
- **Detection:** Database queries inside loops, lazy-loading relations in a loop
- **ORM:** `users.map(u => u.posts)` triggers N queries — use eager loading (`include`, `preload`, `with`)
- **Fix:** Batch fetch with a single query using `WHERE IN`, or use join + eager loading
- **GraphQL:** N+1 in resolvers — use DataLoader to batch requests

### 3.2 Unnecessary Re-renders (React)
- **New object/array references:** `{ foo: bar }` or `[...arr]` in render creates new reference every render
- **Inline functions:** `onClick={() => handleClick()}` creates new function every render
- **Missing memoization:** `useMemo` / `useCallback` for expensive computations and stable references
- **Context overuse:** Context value changes cause all consumers to re-render — split contexts
- **State in wrong component:** State too high causes unnecessary re-renders of siblings
- **Key prop:** Missing or non-unique keys cause unnecessary DOM operations

### 3.3 Memory Leaks
- **Uncleared intervals/timeouts:** `setInterval` without `clearInterval` in cleanup
- **Event listeners:** `addEventListener` without `removeEventListener` in cleanup
- **WebSocket connections:** Not closing on unmount
- **Subscriptions:** Not unsubscribing from observables/stores
- **Closures holding references:** Large objects captured in long-lived closures
- **Detached DOM nodes:** Removed from DOM but still referenced in JS

### 3.4 Unbounded Loops
- `while (true)` without clear exit condition
- Loops over user-controlled data without size limits
- Recursive functions without depth limits — stack overflow
- `Array.prototype.map` over very large arrays — consider chunking or streaming

### 3.5 Missing Indexes
- Foreign keys without indexes — join performance
- Filter columns without indexes — WHERE clause performance
- Sort columns without indexes — ORDER BY performance
- Check with `EXPLAIN ANALYZE` — look for sequential scans on large tables

### 3.6 Large Bundle Contributions
- **Heavy imports:** `import _ from 'lodash'` instead of `import debounce from 'lodash/debounce'`
- **Missing tree-shaking:** CJS imports, side-effectful modules
- **Large dependencies:** moment.js (use date-fns/dayjs), entire icon libraries (import specific icons)
- **Missing code splitting:** Everything in one bundle — use route-based or component-based splitting
- **Polyfills:** Including polyfills for browsers that don't need them

### 3.7 Blocking Operations
- **Synchronous I/O:** `fs.readFileSync`, `execSync` in request handlers — blocks event loop
- **Heavy computation on main thread:** Large data processing — use Web Workers
- **Long-running database queries:** Without pagination — fetch all records
- **JSON.parse on large payloads:** Can block main thread — consider streaming parsers

### 3.8 Missing Pagination/Caching
- **List endpoints without pagination:** `GET /api/items` returning all records
- **Missing cache headers:** Static assets without `Cache-Control`, `ETag`
- **Repeated expensive computations:** Same calculation multiple times — memoize
- **Missing CDN:** Serving static assets from origin instead of CDN
- **No query caching:** Same database query repeated — use query cache or materialized view

---

## Part 4: Architecture & Design

### 4.1 Separation of Concerns Violations
- **Business logic in controllers:** Route handlers containing business logic — extract to service layer
- **HTTP concerns in services:** Services returning HTTP status codes — services should be transport-agnostic
- **Data access in controllers:** Direct database queries in route handlers — use repository pattern
- **UI logic in data layer:** Formatting/display logic in API responses — keep in presentation layer

### 4.2 Tight Coupling
- **Direct imports of concrete classes:** Should depend on interfaces/abstractions
- **Shared mutable state:** Modules sharing global state — use dependency injection
- **Hardcoded dependencies:** `new Stripe()` in business logic — inject the client
- **Cross-module imports:** Module A importing from Module B's internal implementation

### 4.3 Circular Dependencies
- Module A imports Module B, Module B imports Module A — runtime undefined errors
- Detect with: `madge --circular` or ESLint `import/no-cycle` rule
- Fix by: extracting shared code to a third module, using dependency inversion

### 4.4 Leaky Abstractions
- **ORM errors leaking to clients:** Catching database errors and returning them directly
- **Internal table names in API responses:** `users_table` field names in JSON
- **Implementation details in interfaces:** Exposing cache keys, internal IDs, file paths
- **Framework-specific types in domain logic:** Express `Request` in service layer

### 4.5 God Objects
- Classes/modules > 500 lines or > 20 methods or > 10 dependencies
- Single component handling: data fetching, state management, business logic, and rendering
- Fix by: extracting responsibilities into separate modules/components

### 4.6 SOLID Principles Violations
- **SRP:** A class/function doing more than one thing
- **OCP:** Need to modify existing code to add new behavior — use strategy/plugin pattern
- **LSP:** Subclass breaking parent's contract
- **ISP:** Forcing clients to depend on methods they don't use — split interfaces
- **DIP:** High-level modules depending on low-level modules — depend on abstractions

### 4.7 Inconsistent Patterns
- **Mixed data fetching:** Some routes use React Query, others use useEffect + fetch
- **Mixed styling:** Some components use CSS Modules, others use Tailwind, others use styled-components
- **Mixed error handling:** Some endpoints return error objects, others throw, others return null
- **Mixed API patterns:** Some endpoints REST, others RPC-style — be consistent

---

## Part 5: Code Style & Readability

### 5.1 Naming Conventions
- **Descriptive names:** `data` → `userProfile`, `handleClick` → `handleDeleteUser`
- **Boolean naming:** `isVisible` not `visible`, `hasPermission` not `permission`
- **Function naming:** Verbs for actions (`fetchUsers`), nouns for values (`userList`)
- **Constant naming:** `MAX_RETRY_COUNT` not `maxRetry` for true constants
- **Consistent casing:** camelCase for JS/TS variables, PascalCase for components/types, UPPER_SNAKE for env vars

### 5.2 Function Length
- Functions > 50 lines — consider extraction
- Functions with > 4 parameters — consider grouping into an object
- Functions with > 3 levels of nesting — extract inner logic
- Functions doing > 1 thing — split

### 5.3 Nesting Depth
- > 3 levels of if/for/while nesting — extract to a function or use early returns
- Guard clauses: `if (!condition) return` at the top instead of wrapping everything in `if (condition) { ... }`
- Avoid `else` after `return` — unnecessary nesting

### 5.4 Dead Code
- Unused imports, variables, functions
- Commented-out code (use git history, not comments)
- Unreachable code after `return`, `throw`, `break`
- Unused CSS classes, unused exports

### 5.5 Magic Numbers/Strings
- `if (status === 3)` — use `if (status === OrderStatus.SHIPPED)`
- `setTimeout(fn, 86400000)` — use `const ONE_DAY_MS = 24 * 60 * 60 * 1000`
- Hardcoded URLs, paths, limits — extract to constants or config

### 5.6 Inconsistent Formatting
- Should be handled by Prettier/Biome — but check if formatter is configured
- Mixed tabs/spaces, inconsistent semicolons, trailing commas
- Use `.editorconfig` + Prettier to eliminate this category

### 5.7 Unclear Control Flow
- Deeply nested ternaries: `cond1 ? val1 : cond2 ? val2 : cond3 ? val3 : val4` — use if/else or lookup table
- Complex boolean expressions: `!a && (b || c) && !d` — extract to named variables
- Negative logic: `if (!isNotValid)` — rewrite as `if (isValid)`

---

## Part 6: Error Handling

### 6.1 Swallowed Errors
- `catch (e) {}` — empty catch blocks hide bugs
- `catch (e) { console.log(e) }` — logging without handling, user gets no feedback
- `.catch(() => null)` — silently returning null hides failures
- `try { ... } catch { /* ignored */ }` — at minimum, log and re-throw or handle

### 6.2 Missing Error Boundaries
- React: No error boundary around route components — unhandled error crashes entire app
- API: No global error middleware — unhandled errors leak stack traces
- Async: No try/catch in async route handlers — unhandled promise rejection

### 6.3 Inconsistent Error Propagation
- Some functions throw, some return error objects, some return null — pick one pattern
- API: Some endpoints return `{ error: "message" }` with 200, others return 4xx — be consistent
- Services: Some throw, some return Result type — be consistent within a layer

### 6.4 Unhelpful Error Messages
- `throw new Error("Failed")` — failed at what? What should the user do?
- `return res.status(400).json({ error: "Invalid input" })` — which input? What's valid?
- Good: `throw new ValidationError("email", "Email format is invalid")`
- Good: `return res.status(400).json({ error: { code: "INVALID_EMAIL", message: "Please enter a valid email address", field: "email" } })`

### 6.5 Missing try/catch in Async Flows
- `async function handler() { const data = await fetchData(); }` — no try/catch, unhandled rejection
- `useEffect(() => { fetchData(); }, [])` — async errors not caught
- Always wrap async operations that can fail with try/catch or `.catch()`

### 6.6 Unhandled Edge Cases
- What if the database is down? What if the external API times out? What if the file doesn't exist?
- What if the user is between states (e.g., payment pending → network error)?
- What if the input is valid type but invalid value (e.g., empty string, negative number)?

---

## Part 7: Testing Review

### 7.1 Missing Test Coverage for Critical Paths
- **Authentication:** Login, logout, password reset, session management
- **Payment:** Checkout, webhook handling, refund flow
- **Data mutations:** Create, update, delete operations
- **Authorization:** Access control checks for each role
- **Business logic:** Core domain rules and calculations

### 7.2 Brittle Tests
- Tests depending on implementation details (internal state, private methods)
- Tests depending on execution order
- Tests depending on timing (sleep, setTimeout) — use fake timers
- Tests depending on external services without mocking
- Tests with hardcoded dates — use relative dates or fake timers

### 7.3 Tests That Don't Assert Outcomes
- Tests that run code without checking the result
- Tests that only assert "no error thrown" — also assert the correct result
- Tests that assert implementation details instead of behavior
- Missing assertions for error cases — test that errors are thrown/rejected

### 7.4 Missing Negative Tests
- What happens with invalid input?
- What happens with missing required fields?
- What happens with unauthorized access?
- What happens when the database is unavailable?
- What happens with concurrent operations?

### 7.5 Flaky Tests
- Tests that pass/fail intermittently — investigate and fix, don't disable
- Common causes: timing issues, shared state, test order dependency, external services, random data
- Fix by: proper isolation, fake timers, mocking external services, deterministic test data

### 7.6 Untested Edge Cases
- Empty arrays/strings/objects
- Single-item collections
- Maximum inputs
- Unicode/emoji in strings
- Time zone boundaries
- Concurrent operations

### 7.7 Integration Test Gaps
- Unit tests pass but integration is untested
- API contract not tested (request/response shapes)
- Database integration not tested (schema, constraints, migrations)
- Third-party API integration not tested (mocked but not verified against real API)

---

## Part 8: Accessibility Review

### 8.1 Missing ARIA Labels
- Icon-only buttons without `aria-label`
- Form inputs without associated `<label>` (not just placeholder)
- Interactive divs/spans without `role="button"` and `aria-label`
- Images without `alt` text (decorative: `alt=""`, meaningful: descriptive alt)

### 8.2 Incorrect Semantic HTML
- `<div onClick>` instead of `<button>` — divs aren't keyboard accessible
- `<span onClick>` instead of `<a>` — spans aren't links
- `<a href="#">` for non-navigation actions — use `<button>`
- Heading levels skipped (h1 → h3) — maintain hierarchy
- `<table>` without `<thead>`, `<th scope>` — screen readers can't navigate

### 8.3 Keyboard Navigation Gaps
- Tab order doesn't follow visual order
- Focus not visible (missing `:focus-visible` styles)
- Modal doesn't trap focus (focus escapes to background)
- No skip-to-content link
- Dropdown menus not keyboard accessible (arrow keys, escape)

### 8.4 Color Contrast Issues
- Text on background below 4.5:1 ratio (normal text) or 3:1 (large text)
- UI components below 3:1 contrast against adjacent colors
- Focus indicators below 3:1 contrast
- Check with browser DevTools contrast checker or axe-core

### 8.5 Missing Alt Text
- `<img>` without `alt` attribute (even `alt=""` for decorative)
- `<img alt="image">` — not descriptive
- Background images conveying information without text alternative
- `<input type="image">` without `alt`

### 8.6 Focus Management Problems
- SPA route changes don't move focus to new content
- Modal open doesn't focus first element
- Modal close doesn't return focus to trigger
- Dynamic content insertion doesn't announce to screen readers
- Loading states don't announce (use `aria-live="polite"`)

---

## Part 9: Cross-Cutting Concerns

### 9.1 Logging Adequacy
- **Missing logs:** Critical operations (auth, payment, data mutation) without logs
- **Over-logging:** Logging sensitive data (passwords, tokens, PII)
- **Log level misuse:** Using `console.log` in production, `console.error` for non-errors
- **Missing context:** Logs without request ID, user ID, or correlation ID
- **Structured logging:** Should use JSON logs with consistent fields, not string concatenation

### 9.2 Configuration Management
- **Hardcoded config:** URLs, limits, timeouts in code — use environment variables
- **Missing env validation:** No schema for env vars — use Zod env schema
- **Client-exposed secrets:** `NEXT_PUBLIC_` / `VITE_` prefix on sensitive values
- **Environment-specific behavior:** `if (process.env.NODE_ENV === 'production')` scattered — centralize

### 9.3 Environment-Specific Behavior
- Different behavior between dev/staging/prod that isn't intentional
- Debug features enabled in production
- Test data in production database
- Different dependencies between environments

### 9.4 Feature Flag Usage
- **Missing flags:** New features deployed without ability to toggle off
- **Flag debt:** Old flags still in code after feature is permanent — clean up
- **Flag evaluation:** Flags evaluated per-request instead of cached
- **Flag testing:** Not testing both flag-on and flag-off paths

### 9.5 Backward Compatibility
- **API changes:** Breaking changes without versioning — use `/v2/` or additive changes
- **Database changes:** Schema changes without migration — always provide migration
- **Type changes:** Changing a type's shape without considering consumers
- **Deprecation:** Removing features without deprecation warning period

### 9.6 Migration Safety
- **Destructive migrations:** `DROP COLUMN` without expand-contract pattern
- **Data backfill:** Large data changes without batching — locks table
- **Rollback plan:** No way to revert migration if something breaks
- **Index creation:** `CREATE INDEX` without `CONCURRENTLY` — locks table

---

## Part 10: Review Process

### 10.1 Severity Classification

| Severity | Definition | Action |
|---|---|---|
| **Blocker** | Will cause data loss, security breach, or crash in production | Must fix before merge |
| **Critical** | Will cause incorrect behavior, security issue, or major performance problem | Must fix before merge |
| **Major** | Will cause maintainability issues, minor bugs, or poor UX | Should fix before merge, can merge with follow-up issue |
| **Minor** | Code style, naming, or minor optimization | Can fix in follow-up |
| **Nit** | Personal preference, formatting | Optional, don't block merge |

### 10.2 Constructive Feedback Framing
- **Be specific:** "This query will N+1 when users have many posts" not "this is slow"
- **Explain why:** "String concatenation in SQL allows injection because..." not "this is insecure"
- **Suggest alternatives:** "Consider using parameterized queries: `db.query('SELECT * FROM users WHERE id = $1', [id])`"
- **Ask questions:** "What happens if the API returns null here?" not "You didn't handle null"
- **Praise good work:** "Great use of discriminated unions here — makes invalid states unrepresentable"
- **Avoid:** "This is wrong", "Why did you do it this way?", sarcasm

### 10.3 Prioritized Action Items
1. Blockers and Criticals — must fix
2. Majors — should fix, create issue if deferred
3. Minors — nice to fix, create issue
4. Nits — optional, don't create issues for nits

### 10.4 Regression Risk Assessment
- **What could break?** Identify areas affected by the change
- **What tests cover this?** Verify adequate test coverage for the change
- **What's the blast radius?** How many users/features are affected if this breaks?
- **Can we roll back?** Is the change reversible (code) vs irreversible (database migration)?

### 10.5 Deployment Readiness Verdict
- **Approved:** No blockers/criticals, all majors addressed or have follow-up issues
- **Approved with comments:** Minors/nits noted, not blocking
- **Changes requested:** Blockers/criticals must be fixed before re-review
- **Blocked:** Fundamental architectural or security issues require discussion

---

## Execution Instructions for Cascade

When this skill is activated for code review:

1. **Read all changed files** — use `git diff` or read the PR changes
2. **Review systematically** — go through each part (1-10) in order
3. **Classify findings** — assign severity to each issue found
4. **Provide actionable feedback** — specific, with code examples for fixes
5. **Check for regressions** — what existing functionality could break?
6. **Verify test coverage** — are the changes tested? Are edge cases covered?
7. **Run automated checks** — lint, type check, tests if available
8. **Summarize** — deployment readiness verdict with prioritized action items
9. **Don't block on nits** — focus on what matters: correctness, security, performance
10. **Document decisions** — if something is intentionally left as-is, note why
