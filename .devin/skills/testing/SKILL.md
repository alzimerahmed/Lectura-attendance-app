---
name: testing
description: Comprehensive testing & QA workflow — design test architecture, author tests at all levels, and set up CI gates for confidence-driven shipping
---

# Testing & QA Workflow

This workflow applies the **Testing & QA Skill** (`~/.codeium/windsurf/skills/testing-qa.md`) to design and implement a comprehensive test architecture.

## When to Run
- When setting up testing infrastructure for a new project
- When the user says `/testing` or asks about tests
- Before deploying — verify test coverage and CI gates
- When adding tests for a new feature

---

## Step 1: Assess Testing Needs

1. Read the project structure — framework, existing tests, test setup
2. Identify critical paths: auth, payment, data mutations, business logic
3. Determine test levels needed: unit, integration, e2e, visual, a11y, load
4. Check existing test infrastructure: runner, database, mocking, CI

## Step 2: Design Test Architecture

1. Plan the test pyramid: ~70% unit, ~20% integration, ~10% e2e
2. Decide what to test at each level:
   - Unit: pure functions, business logic, validators, formatters
   - Integration: API routes, database operations, component composition, auth
   - E2E: critical user journeys (signup, checkout, password reset)
3. Choose test database strategy: transaction rollback, truncate, or test containers
4. Plan parallelization: which tests can run in parallel, which must be sequential

## Step 3: Set Up Test Infrastructure

1. Configure test runner (Vitest/Jest) with path aliases, environment, setup files
2. Set up test database — migrations, seed data, cleanup strategy
3. Configure mocking tools — MSW for API mocking, vi.useFakeTimers for time
4. Set up Playwright for E2E — browser config, base URL, auth storage state
5. Configure coverage — thresholds, reporter format, exclude patterns

## Step 4: Write Unit Tests

1. Test pure functions: input → expected output
2. Test business logic: calculations, validations, state transitions
3. Test error paths: invalid input, edge cases, empty/null/undefined
4. Use parameterized tests for multiple input combinations
5. Use property-based testing for invariants and edge case discovery
6. Test behavior, not implementation — assert outcomes, not internal state

## Step 5: Write Integration Tests

1. Test API routes with test database — request → response + database state
2. Test component composition — render with props, assert output and interactions
3. Test auth flows — login, logout, protected routes, role-based access
4. Test middleware chains — auth, CORS, validation, rate limiting
5. Test database operations — CRUD, constraints, transactions
6. Use MSW for external API mocking — don't call real external services

## Step 6: Write E2E Tests

1. Test critical user journeys: signup, checkout, password reset, key navigation
2. Use page object models for maintainable selectors and actions
3. Test across browsers (Chromium, Firefox, WebKit) and viewports (desktop, mobile)
4. Test real auth flows with test accounts
5. Test file uploads, websockets, and real-time features
6. Keep E2E tests focused — don't test every edge case at this level

## Step 7: Set Up Contract Testing

1. Validate API responses against OpenAPI/JSON Schema
2. Test that every documented endpoint exists and responds correctly
3. Test backward compatibility when making API changes
4. Use Pact for consumer-driven contract testing if multiple consumers

## Step 8: Add Visual & Accessibility Testing

1. Set up visual regression (Percy/Chromatic) — screenshot components/pages
2. Set up axe-core in CI — automated a11y testing
3. Test color contrast in light and dark mode
4. Test keyboard navigation — tab order, focus trapping, escape behavior
5. Review visual diffs carefully on every PR

## Step 9: Set Up Load & Performance Testing

1. Write k6/Artillery load tests for critical endpoints
2. Establish baselines: response times (p50, p95, p99), throughput, error rate
3. Test under expected and peak load
4. Run soak tests to detect memory/connection leaks
5. Run spike tests to verify auto-scaling and graceful degradation

## Step 10: Configure CI Test Gates

1. Pre-commit hooks: lint + format staged files (fast, < 5s)
2. CI pipeline: lint → typecheck → unit tests → integration tests → e2e → build
3. Coverage thresholds: set baseline, gradually increase
4. Branch protection: require all checks to pass before merge
5. Flaky test management: detect, investigate, fix — don't disable
6. Test parallelization: split test files across CI runners

## Step 11: Review & Iterate

1. Review test coverage — focus on critical paths, not just percentage
2. Check for brittle tests — implementation details, order dependency, timing
3. Verify tests assert outcomes — not just "no error thrown"
4. Check for missing negative tests — invalid input, unauthorized, service down
5. Run full suite locally and in CI — verify no flaky tests
6. Document test architecture — how to run, what to test where, conventions
