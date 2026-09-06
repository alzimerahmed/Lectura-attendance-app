# Rule: Testing & QA for All Projects

**ALWAYS** apply the Testing & QA skill and workflow when setting up testing infrastructure and writing tests. Design a test architecture that catches regressions early, runs fast in CI, and provides confidence to ship.

## Skill
`~/.codeium/windsurf/skills/testing-qa.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/testing.md` — invoke with `/testing`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/test-engineer.md` (parent: Quality Engineer)

## How to follow this rule:
1. When setting up tests or testing infrastructure, invoke the `/testing` workflow
2. Follow the workflow steps in order: Assess → Design Architecture → Set Up Infrastructure → Unit Tests → Integration Tests → E2E Tests → Contract Tests → Visual & A11y → Load Tests → CI Gates → Review
3. Always test behavior, not implementation — assert outcomes, not internal state
4. Always write regression tests for bug fixes — test that reproduces the bug
5. Always set up CI test gates: lint → typecheck → unit → integration → e2e → build
6. Always include accessibility testing (axe-core) and visual regression in CI
7. Never disable flaky tests — investigate and fix the root cause
8. Design tests for the test pyramid: ~70% unit, ~20% integration, ~10% e2e

## When this rule applies:
- Setting up testing infrastructure for a new project
- Writing tests for a new feature
- Before deploying — verify test coverage and CI gates
- User asks about tests or testing strategy

## When this rule does NOT apply:
- Non-code projects
- User explicitly says to skip testing
