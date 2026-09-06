---
name: Testing & QA Skill
description: Comprehensive methodology for authoring tests and designing test architecture — 2025-2026 practices with test pyramids, CI gates, and confidence-driven testing
version: 1.0.0
tags: [testing, qa, unit-tests, integration, e2e, contract, load-testing, tdd, ci]
---

# Testing & QA Skill

## Purpose
This skill provides a comprehensive methodology for authoring tests and designing test architecture across any kind of web project. It reflects **modern 2025-2026 testing practices** — not just writing tests but designing a test architecture that catches regressions early, runs fast in CI, and provides confidence to ship.

## Core Philosophy

**Tests provide confidence, not coverage.** 100% coverage with brittle tests that don't assert outcomes is worse than 70% coverage with meaningful tests that verify behavior. Write tests that give you confidence to ship, not tests that chase a metric.

**The #1 rule:** Test behavior, not implementation. Tests that assert implementation details break on every refactor. Tests that assert behavior only break when behavior changes.

---

## Part 1: Test Strategy & Architecture

### 1.1 Test Pyramid Design
```
        /\
       /e2e\        — Few, slow, high-confidence (critical user journeys)
      /------\
     /integration\  — Moderate, medium speed (API, component composition)
    /------------\
   /    unit      \ — Many, fast, low-level (pure functions, logic)
  /----------------\
```

- **Unit (70%):** Pure functions, utility logic, business rules — fast, isolated
- **Integration (20%):** API routes with test DB, component composition, auth flows
- **E2E (10%):** Critical user journeys in real browser — signup, checkout, key flows

### 1.2 Test Matrix Planning
| What | Level | Tool | Speed |
|---|---|---|---|
| Pure functions | Unit | Vitest/Jest | <100ms |
| React components | Unit/Integration | Vitest + Testing Library | <500ms |
| API routes | Integration | Vitest + supertest/fetch | <1s |
| Database operations | Integration | Vitest + test DB | <2s |
| Auth flows | Integration | Vitest + test DB | <2s |
| Critical journeys | E2E | Playwright | <30s |
| Visual regression | E2E | Percy/Chromatic | <10s |
| Load | Performance | k6/Artillery | minutes |

### 1.3 Deciding What to Test at Each Level
- **Unit test:** Pure functions, calculations, formatters, validators, reducers, state transitions
- **Integration test:** API endpoints, database queries, middleware chains, component composition
- **E2E test:** Signup, checkout, password reset, key navigation, payment flow
- **Don't unit test:** Trivial getters/setters, framework code, third-party libraries
- **Don't E2E test:** Every component variant, every API endpoint, edge cases (test at lower levels)

### 1.4 Test Isolation Principles
- Each test should be independent — no test depends on another test's execution
- Each test should set up its own data — no shared mutable state between tests
- Each test should clean up after itself — or use a fresh database per test file
- Tests should be runnable in any order — no implicit ordering
- Tests should be parallelizable — no shared resources that block parallelism

### 1.5 Test Data Management
- **Factories:** Create test data with sensible defaults, override only what matters for the test
- **Fixtures:** Static test data for consistent scenarios — use sparingly, prefer factories
- **Seed data:** Minimal data needed for the test — don't seed the entire database
- **Cleanup:** Truncate tables between test files, or use transactions with rollback per test
- **Don't use production data:** PII concerns, data drift, schema differences

### 1.6 Test Database Strategies
- **Transaction rollback:** Begin transaction before test, roll back after — fastest, no cleanup
- **Truncate between tests:** `TRUNCATE TABLE` — slower but works with multiple connections
- **Database per test file:** Most isolated, slower setup — good for parallel test files
- **In-memory database:** SQLite in-memory for unit tests — but watch for dialect differences
- **Test container:** Spin up real PostgreSQL in Docker — most realistic, slower startup

### 1.7 Parallel vs Sequential Execution
- **Parallel by default:** Unit and integration tests should be parallelizable
- **Sequential when:** Tests share a database without transaction isolation, tests modify global state
- **CI parallelization:** Split test files across CI runners for faster execution
- **Sharding:** `vitest --shard=1/4` to split tests across 4 runners

---

## Part 2: Unit Testing

### 2.1 Test Structure (Arrange/Act/Assert)
```typescript
test('calculateTotal applies discount correctly', () => {
  // Arrange
  const items = [{ price: 100, quantity: 2 }, { price: 50, quantity: 1 }];
  const discount = 0.1;

  // Act
  const total = calculateTotal(items, discount);

  // Assert
  expect(total).toBe(225); // (200 + 50) * 0.9
});
```

### 2.2 Testing Pure Functions vs Side-Effectful Code
- **Pure functions:** Easy — call with input, assert output. No mocks needed.
- **Side-effectful code:** Mock the side effects (database, API, filesystem), assert the function's behavior and that the side effect was called correctly
- **Avoid mocking what you own:** If you need to mock your own module, consider extracting the dependency

### 2.3 Dependency Injection for Testability
```typescript
// Hard to test — direct dependency
function getUser(id: string) {
  return db.users.findById(id); // db is a module-level import
}

// Testable — injected dependency
function getUser(id: string, db: Database) {
  return db.users.findById(id);
}
// Test: getUser('123', mockDb)
```

### 2.4 Testing Async Code
```typescript
// Async/await
test('fetchUser returns user data', async () => {
  const user = await fetchUser('123');
  expect(user.name).toBe('Alice');
});

// Rejection
test('fetchUser throws on invalid ID', async () => {
  await expect(fetchUser('')).rejects.toThrow('Invalid ID');
});

// Multiple awaits
test('parallel fetch', async () => {
  const [a, b] = await Promise.all([fetchUser('1'), fetchUser('2')]);
  expect(a).toBeDefined();
  expect(b).toBeDefined();
});
```

### 2.5 Testing Error Paths
- Test that functions throw/reject with the right error type
- Test error messages are helpful and specific
- Test that errors include context (what failed, why, what to do)
- Test edge cases: empty input, null, undefined, invalid types

### 2.6 Parameterized Tests
```typescript
test.each([
  [1, 1, 2],
  [0, 0, 0],
  [-1, 1, 0],
  [0.1, 0.2, 0.3],
])('add(%f, %f) = %f', (a, b, expected) => {
  expect(add(a, b)).toBeCloseTo(expected);
});
```

### 2.7 Property-Based Testing
```typescript
import { fc, testProp } from 'fast-check';

testProp('reverse(reverse(arr)) === arr', [fc.array(fc.integer())], (arr) => {
  expect(reverse(reverse(arr))).toEqual(arr);
});
```
- Generate random inputs to find edge cases you didn't think of
- Test invariants: properties that should always hold
- Good for: sorting algorithms, parsers, state machines, math functions

### 2.8 Snapshot Testing (When to Use and When to Avoid)
- **Use for:** Stable output that rarely changes (serialized objects, generated configs)
- **Use for:** Component rendering smoke tests (does it render without crashing?)
- **Avoid for:** Complex UI output — snapshots become noise, reviewed without scrutiny
- **Avoid for:** Business logic — assert specific outcomes instead
- **Rules:** Review snapshot diffs carefully, update only when intentional, don't snapshot everything

---

## Part 3: Integration Testing

### 3.1 Testing API Routes with Real or Test Databases
```typescript
test('POST /api/users creates a user', async () => {
  const res = await fetch('/api/users', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: 'test@example.com', name: 'Test' }),
  });

  expect(res.status).toBe(201);
  const user = await res.json();
  expect(user.email).toBe('test@example.com');
  expect(user.id).toBeDefined();

  // Verify in database
  const dbUser = await db.users.findById(user.id);
  expect(dbUser).toBeDefined();
});
```

### 3.2 Testing Component Composition
```typescript
test('UserProfile displays user data and posts', () => {
  render(
    <UserProfile user={mockUser} posts={mockPosts} />
  );

  expect(screen.getByText(mockUser.name)).toBeInTheDocument();
  expect(screen.getAllByTestId('post-card')).toHaveLength(mockPosts.length);
});
```

### 3.3 Testing Auth Flows
- Test login with valid credentials → returns session/token
- Test login with invalid credentials → returns error
- Test protected route without auth → returns 401/redirect
- Test protected route with auth → returns data
- Test role-based access → admin can access, regular user cannot
- Test token expiration and refresh

### 3.4 Testing Middleware Chains
- Test that auth middleware sets the user on the request
- Test that CORS middleware sets correct headers
- Test that rate limiting middleware blocks after threshold
- Test that validation middleware rejects invalid input
- Test middleware order — does auth run before validation?

### 3.5 Database Integration Testing with Transactions/Rollback
```typescript
beforeEach(async () => {
  await db.query('BEGIN');
});

afterEach(async () => {
  await db.query('ROLLBACK');
});

test('createOrder decreases inventory', async () => {
  const product = await createProduct({ name: 'Widget', stock: 10 });
  await createOrder({ productId: product.id, quantity: 3 });

  const updated = await getProduct(product.id);
  expect(updated.stock).toBe(7);
});
```

### 3.6 Testing External API Integrations with Mocks/Stubs
- **Mock the HTTP client:** Intercept fetch/axios calls, return canned responses
- **Use MSW (Mock Service Worker):** Intercept network requests at the service worker level
- **Test with real API in staging:** Run a subset of tests against the real API in CI
- **Test error scenarios:** API timeout, 500 error, rate limited, malformed response
- **Never call real external APIs in unit/integration tests** — flaky, slow, costs money

---

## Part 4: End-to-End Testing

### 4.1 Playwright/Cypress Test Design
```typescript
// Playwright
test('user can sign up and create a post', async ({ page }) => {
  await page.goto('/signup');
  await page.fill('[name=email]', 'test@example.com');
  await page.fill('[name=password]', 'SecurePass123!');
  await page.click('button[type=submit]');

  await page.waitForURL('/dashboard');
  await page.click('text=New Post');
  await page.fill('[name=title]', 'My First Post');
  await page.fill('[name=content]', 'Hello world!');
  await page.click('text=Publish');

  await expect(page.locator('text=My First Post')).toBeVisible();
});
```

### 4.2 Page Object Models
```typescript
class SignupPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/signup');
  }

  async fillEmail(email: string) {
    await this.page.fill('[name=email]', email);
  }

  async fillPassword(password: string) {
    await this.page.fill('[name=password]', password);
  }

  async submit() {
    await this.page.click('button[type=submit]');
  }

  async signup(email: string, password: string) {
    await this.goto();
    await this.fillEmail(email);
    await this.fillPassword(password);
    await this.submit();
  }
}
```

### 4.3 Testing Critical User Journeys
- **Signup/Registration:** Fill form, submit, verify redirect, verify email
- **Login:** Enter credentials, submit, verify dashboard
- **Checkout:** Add to cart, fill shipping, pay, verify order confirmation
- **Password reset:** Request reset, click email link, set new password, login
- **Search:** Enter query, verify results, click result, verify detail page

### 4.4 Testing Across Browsers
```typescript
// Playwright config
projects: [
  { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
  { name: 'firefox', use: { ...devices['Desktop Firefox'] } },
  { name: 'webkit', use: { ...devices['Desktop Safari'] } },
  { name: 'mobile-chrome', use: { ...devices['Pixel 5'] } },
  { name: 'mobile-safari', use: { ...devices['iPhone 13'] } },
]
```

### 4.5 Visual Regression Testing
- **Percy/Chromatic:** Take screenshots of components/pages, compare to baseline
- **Review diffs carefully:** Pixel differences can be meaningful or noise
- **Set dynamic regions:** Exclude areas with dynamic content (dates, ads, user-generated)
- **Run on every PR:** Catch unintended visual changes before merge
- **Don't over-rely:** Visual regression doesn't catch functional bugs

### 4.6 Testing Real Auth Flows
- Use a test account with known credentials
- Don't test with real user accounts
- Test both success and failure paths
- Test session persistence across page reloads
- Test logout and session invalidation

### 4.7 Testing File Uploads
```typescript
test('user can upload profile picture', async ({ page }) => {
  await page.goto('/profile');
  await page.setInputFiles('input[type=file]', 'tests/fixtures/avatar.png');
  await page.click('text=Upload');
  await expect(page.locator('img[alt=Avatar]')).toHaveAttribute('src', /avatar/);
});
```

### 4.8 Testing WebSockets/Real-Time Features
- Wait for connection to establish before asserting
- Test message sending and receiving
- Test reconnection after disconnect
- Test multiple clients interacting
- Use `page.waitForFunction` to wait for real-time updates

---

## Part 5: Contract Testing

### 5.1 Consumer-Driven Contract Testing (Pact)
- **Consumer writes tests** defining what it expects from the provider
- **Pact generates a contract** from consumer tests
- **Provider verifies** it meets the contract
- Catches breaking API changes before they reach production

### 5.2 API Schema Validation
- Validate API responses against OpenAPI/JSON Schema
- Test that responses match the documented schema
- Test that invalid requests are rejected with proper errors
- Use `zod` or `ajv` for runtime schema validation in tests

### 5.3 OpenAPI Spec Testing
- Test that every documented endpoint exists and responds
- Test that request/response shapes match the spec
- Test that error responses match documented error schemas
- Test that authentication requirements are enforced as documented

### 5.4 GraphQL Schema Testing
- Test that queries return the expected shape
- Test that mutations create/update/delete correctly
- Test that subscriptions deliver expected events
- Test query depth limiting and cost analysis
- Test introspection is disabled in production

### 5.5 Testing Backward Compatibility of API Changes
- **Additive changes:** New fields, new endpoints — should be backward compatible
- **Breaking changes:** Removed fields, changed types, changed behavior — require versioning
- **Contract tests:** Run old contract against new API version to verify compatibility
- **Deprecation testing:** Verify deprecated endpoints still work during deprecation period

---

## Part 6: Load & Performance Testing

### 6.1 k6/Artillery Test Design
```javascript
// k6
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 20 },   // ramp up
    { duration: '1m', target: 20 },     // steady
    { duration: '30s', target: 0 },     // ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],   // 95% of requests < 500ms
    http_req_failed: ['rate<0.01'],      // < 1% failures
  },
};

export default function () {
  const res = http.get('https://example.com/api/products');
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
  sleep(1);
}
```

### 6.2 Establishing Baselines
- Run load tests on a clean deployment to establish baseline performance
- Record: response times (p50, p90, p95, p99), throughput (req/s), error rate
- Compare future test runs against baselines to detect regressions
- Store baselines in version control or a performance dashboard

### 6.3 Testing Under Expected vs Peak Load
- **Expected load:** Normal traffic — verify system handles it smoothly
- **Peak load:** Highest expected traffic (sale, launch, event) — verify no degradation
- **Stress test:** Beyond peak — find the breaking point and failure mode
- **Spike test:** Sudden traffic surge — verify auto-scaling and graceful degradation

### 6.4 Identifying Bottlenecks
- **Database:** Slow queries, missing indexes, connection pool exhaustion
- **API:** CPU-bound processing, blocking I/O, large payloads
- **Frontend:** Large bundles, render blocking, memory leaks
- **Network:** Bandwidth limits, latency, DNS resolution
- **Infrastructure:** CPU, memory, disk I/O, container resource limits

### 6.5 Testing Rate Limiting
- Send requests above the rate limit → verify 429 response
- Verify rate limit headers (X-RateLimit-Limit, X-RateLimit-Remaining)
- Verify rate limit resets after the window
- Test rate limiting per user, per IP, per API key

### 6.6 Testing Database Connection Limits
- Monitor connection pool usage during load tests
- Verify pool doesn't exhaust under peak load
- Test connection timeout behavior
- Verify connections are released properly after use

### 6.7 Soak Tests
- Run at expected load for an extended period (hours)
- Detect: memory leaks, connection leaks, file handle leaks
- Monitor: response time degradation over time, error rate increase
- Verify: garbage collection, cache behavior, log rotation

### 6.8 Spike Tests
- Sudden 10x traffic increase for short duration
- Verify: auto-scaling kicks in, graceful degradation, no crashes
- Test: queue behavior, rate limiting, circuit breakers

---

## Part 7: Mocking & Stubbing

### 7.1 When to Mock vs Stub vs Fake vs Spy

| Technique | What It Does | When to Use |
|---|---|---|
| **Mock** | Replaces object, verifies interactions | Verify a function was called with specific args |
| **Stub** | Replaces function, returns canned data | Replace external dependency with predictable response |
| **Fake** | Simplified working implementation | In-memory database, fake payment processor |
| **Spy** | Wraps real object, records calls | Verify side effects without replacing the real object |

### 7.2 Mocking External Services
- **MSW (Mock Service Worker):** Best for browser tests — intercepts at network level
- **nock:** Node.js HTTP mocking — intercepts http/https requests
- **Vi.fn / jest.fn:** Mock individual functions — for unit tests
- **Don't mock the thing you're testing** — mock its dependencies

### 7.3 Mocking Time/Dates
```typescript
// Vitest
beforeEach(() => {
  vi.useFakeTimers();
  vi.setSystemTime(new Date('2026-01-15'));
});

afterEach(() => {
  vi.useRealTimers();
});

test('shows correct date', () => {
  expect(formatDate(new Date())).toBe('January 15, 2026');
});
```

### 7.4 Mocking File System
- Use `memfs` or `mock-fs` for file system mocking
- Or use a temp directory: create before test, clean up after
- Don't mock fs module directly — use a file system abstraction

### 7.5 Mocking Network Requests
- **MSW:** `http.get('/api/users', () => Response.json(mockUsers))`
- **nock:** `nock('https://api.example.com').get('/users').reply(200, mockUsers)`
- **fetch mock:** `vi.fn().mockResolvedValue(new Response(JSON.stringify(mockUsers)))`

### 7.6 Avoiding Over-Mocking
- **Symptom:** Tests pass but the feature is broken in production
- **Cause:** Mocks don't match real behavior — mock returns different shape than real API
- **Rule:** Mock at the lowest level possible (network, not function)
- **Rule:** Keep mocks simple — return minimum viable data
- **Rule:** Periodically verify mocks against real API (contract tests)

### 7.7 Testing with Real Dependencies vs Mocked
- **Real DB (test container):** More realistic, catches query issues, slower
- **Mocked DB:** Faster, isolated, but misses query/schema issues
- **Recommendation:** Use real DB for integration tests, mock for unit tests
- **Recommendation:** Use MSW for external APIs, real DB for your own

---

## Part 8: Test-Driven Development

### 8.1 TDD Red-Green-Refactor Cycle
1. **Red:** Write a failing test for the desired behavior
2. **Green:** Write the minimum code to make the test pass
3. **Refactor:** Improve the code while keeping tests green
4. Repeat for the next behavior

### 8.2 BDD with Gherkin
```gherkin
Feature: User Registration
  Scenario: Successful registration
    Given I am on the signup page
    When I enter "test@example.com" as email
    And I enter "SecurePass123!" as password
    And I click the submit button
    Then I should be redirected to the dashboard
    And I should see "Welcome" in the page
```

### 8.3 Outside-In Testing
1. Start with an E2E test describing the user behavior
2. Drop down to integration test for the API
3. Drop down to unit test for the business logic
4. Implement from the inside out

### 8.4 When TDD Adds Value vs When It's Overhead
- **Adds value:** Business logic, algorithms, parsers, state machines, API contracts
- **Adds value:** Bug fixes (write failing test first, then fix)
- **Overhead:** UI components (write component first, then smoke test)
- **Overhead:** Configuration, boilerplate, one-off scripts
- **Rule:** Use TDD when the behavior is well-defined and testable

### 8.5 Testing Legacy Code That Wasn't Designed for Testability
1. **Characterization tests:** Write tests that capture current behavior (even if buggy)
2. **Golden master:** Capture output before refactoring, verify same output after
3. **Seam identification:** Find points where you can intercept behavior for testing
4. **Dependency breaking:** Extract interfaces, parameterize constructors
5. **Don't refactor and test simultaneously** — test first, then refactor

---

## Part 9: CI Test Gates

### 9.1 Pre-Commit Hooks
```json
// package.json with husky + lint-staged
{
  "lint-staged": {
    "*.{ts,tsx}": ["eslint --fix", "prettier --write"],
    "*.{test,spec}.{ts,tsx}": ["vitest run --passWithNoTests"]
  }
}
```
- Keep pre-commit hooks fast (< 5 seconds)
- Run: format, lint, type check on staged files only
- Don't run full test suite in pre-commit — too slow

### 9.2 CI Test Pipeline Design
```yaml
# GitHub Actions example
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v2
      - run: pnpm install --frozen-lockfile
      - run: pnpm lint
      - run: pnpm typecheck
      - run: pnpm test:unit
      - run: pnpm test:integration
      - run: pnpm test:e2e
      - run: pnpm build
```

### 9.3 Flaky Test Management
- **Detect:** Track tests that fail intermittently — quarantine and investigate
- **Fix:** Don't disable flaky tests — fix the root cause (timing, shared state, external deps)
- **Retry:** As a last resort, retry flaky tests in CI (but fix them ASAP)
- **Quarantine:** Move flaky tests to a separate suite that doesn't block PRs — but set a deadline to fix

### 9.4 Test Retry Strategies
- **Don't retry blindly:** Retrying hides flaky tests
- **Retry only for known flaky tests:** With a comment explaining why
- **Max retries:** 1-2 retries, not more
- **Report retries:** Track how often retries are needed — investigate high retry rates

### 9.5 Test Coverage Thresholds
```json
// vitest.config.ts
{
  "coverage": {
    "thresholds": {
      "lines": 80,
      "functions": 80,
      "branches": 75,
      "statements": 80
    }
  }
}
```
- Start with a baseline and gradually increase
- Don't chase 100% — focus on critical paths
- Coverage ≠ quality — review what's tested, not just the percentage
- Fail the build if coverage drops below threshold

### 9.6 Coverage Reporting (Istanbul/c8)
- Generate coverage reports: `vitest run --coverage`
- HTML report for detailed view
- LCOV format for CI integration (Codecov, Coveralls)
- Track coverage trends over time
- Identify uncovered critical paths

### 9.7 Failing the Build on Regressions
- All tests must pass for PR to merge
- Coverage must meet thresholds
- Lint must pass
- Type check must pass
- E2E tests on critical paths must pass
- Use branch protection rules to enforce

### 9.8 Test Parallelization in CI
- Split test files across multiple CI runners
- Use `--shard` flag (Vitest) or `--shard` (Playwright)
- Balance shards by test count or duration
- Cache dependencies between runners
- Use remote cache for Turborepo/Nx

---

## Part 10: Visual & Accessibility Testing

### 10.1 Visual Regression Tools (Percy, Chromatic)
- Take screenshots of components/pages on every PR
- Compare to baseline — highlight visual diffs
- Review diffs before approving
- Set dynamic regions to exclude (dates, ads, user content)
- Run on different viewport sizes

### 10.2 Component Screenshot Testing
```typescript
// Playwright visual comparison
test('button matches visual baseline', async ({ page }) => {
  await page.goto('/components/button');
  await expect(page.locator('.btn-primary')).toHaveScreenshot('btn-primary.png');
});
```

### 10.3 axe-core Automated a11y Testing in CI
```typescript
import { axe } from 'vitest-axe';

test('homepage has no accessibility violations', async () => {
  const { container } = render(<HomePage />);
  const results = await axe(container);
  expect(results.violations).toHaveLength(0);
});
```

### 10.4 Color Contrast Testing
- Use axe-core to check contrast ratios automatically
- WCAG AA: 4.5:1 for normal text, 3:1 for large text
- Test in both light and dark mode
- Test with custom themes if applicable

### 10.5 Keyboard Navigation Automated Tests
```typescript
test('modal traps focus and returns on close', async () => {
  render(<App />);
  await page.keyboard.press('Tab'); // focus trigger
  await page.keyboard.press('Enter'); // open modal
  // Focus should be in modal
  await expect(page.locator('[aria-modal]')).toBeFocused();
  // Tab cycles within modal
  await page.keyboard.press('Tab');
  await page.keyboard.press('Tab');
  // Escape closes and returns focus
  await page.keyboard.press('Escape');
  await expect(page.locator('button[data-trigger]')).toBeFocused();
});
```

---

## Execution Instructions for Cascade

When this skill is activated for testing:

1. **Read the project structure** — understand the framework, existing tests, test setup
2. **Design the test architecture** — decide what to test at each level (unit, integration, e2e)
3. **Set up test infrastructure** — test runner, test database, mocking tools, CI integration
4. **Write tests for critical paths first** — auth, payment, data mutations, business logic
5. **Test behavior, not implementation** — assert outcomes, not internal state
6. **Use factories for test data** — sensible defaults, override only what matters
7. **Ensure test isolation** — no shared state, no order dependency, parallelizable
8. **Set up CI gates** — lint, typecheck, test, coverage thresholds, branch protection
9. **Handle flaky tests** — investigate and fix, don't disable
10. **Add accessibility testing** — axe-core in CI, keyboard navigation tests
11. **Add visual regression** — Percy/Chromatic for component stability
12. **Run load tests** — establish baselines, test under expected and peak load
