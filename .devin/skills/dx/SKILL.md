---
name: dx
description: Comprehensive developer experience workflow — project setup, editor integration, code quality, error messages, fast feedback, onboarding, tools, CI/CD, and metrics
---

# Developer Experience (DX) Workflow

This workflow applies the **Developer Experience Skill** (`~/.codeium/windsurf/skills/developer-experience.md`) to create a frictionless, productive development environment.

## When to Run
- When setting up a new project
- When the user says `/dx` or asks about developer experience
- When improving onboarding or tooling
- When reducing build times or friction
- When setting up VSCode integration or CI

---

## Step 1: Set Up One-Command Setup

1. Ensure `npm install && npm run dev` works out of the box
2. Create `.env.example` with all required environment variables documented
3. Add `engines` field to `package.json` specifying Node version
4. Add `.nvmrc` or `.tool-versions` for version management
5. Create database setup script if applicable: `npm run db:setup`
6. Add seed data script: `npm run db:seed`
7. Test: clone repo fresh, run setup, verify app works in < 5 minutes

## Step 2: Set Up VSCode Integration

1. Create `.vscode/settings.json` — format on save, ESLint auto-fix, TypeScript SDK
2. Create `.vscode/extensions.json` — recommend ESLint, Prettier, Tailwind, Jest
3. Create `.vscode/launch.json` — debug configurations for dev server and tests
4. Create workspace snippets for common patterns (React components, hooks)
5. Configure Tailwind CSS IntelliSense with custom class regex
6. Set up file exclusions (node_modules, dist, .next)
7. Share settings with team — commit `.vscode/` to repo

## Step 3: Set Up Code Quality Automation

1. Install and configure ESLint with `typescript-eslint` strict config
2. Install and configure Prettier with `prettier-plugin-tailwindcss`
3. Set up Husky: `npx husky init`
4. Add pre-commit hook: `npx lint-staged` (format and lint staged files)
5. Add pre-push hook: `npm run typecheck && npm run test`
6. Add commit-msg hook: `npx commitlint` (enforce Conventional Commits)
7. Create `.editorconfig` for cross-editor consistency
8. Configure `lint-staged` in `package.json` — only process changed files

## Step 4: Set Up Fast Feedback Loop

1. **Dev server:** Use Vite (instant cold start, fast HMR) or Turbopack (Next.js)
2. **Tests:** Use Vitest (fast, Vite-native, watch mode, UI)
3. **Type checking:** Use `tsc --noEmit --incremental` for faster checks
4. **HMR:** Ensure React Fast Refresh preserves state on edits
5. **Error overlay:** Configure build errors to show in browser
6. **Hot reload:** CSS changes should update without page reload
7. Measure: cold start < 2s, HMR < 200ms, tests < 30s, build < 30s

## Step 5: Write Clear Error Messages

1. Create custom error class with code, statusCode, and context
2. Make errors actionable — include what went wrong and how to fix it
3. Add context — userId, requestId, relevant data
4. In development: show full stack traces and error details
5. In production: show user-friendly message, log details server-side
6. Set up error boundary with dev-only stack trace display
7. Use structured logging — timestamp, level, message, context

## Step 6: Create Onboarding Documentation

1. Write `README.md` with Quick Start (5 steps to running app)
2. Write `CONTRIBUTING.md` with:
   - Development setup (detailed)
   - Code style (ESLint, Prettier, Conventional Commits)
   - Testing (how to run, what to test, coverage expectations)
   - PR process (branch naming, template, review, merge)
   - Project structure (directory layout and purpose)
3. Create Architecture Decision Records (ADRs) for key decisions
4. Add inline code comments for complex logic
5. Create API documentation if applicable
6. Keep documentation up to date — review in PRs

## Step 7: Set Up Path Aliases

1. Configure in `vite.config.ts` — `@/` → `./src/`, `@components/`, `@lib/`, `@hooks/`
2. Configure in `tsconfig.json` — matching `paths` entries
3. Configure in ESLint — `import/resolver` for alias resolution
4. Configure in Vitest — if using test-specific aliases
5. Test: verify imports work with aliases in editor and build
6. Document aliases in CONTRIBUTING.md

## Step 8: Set Up Package.json Scripts

1. `dev` — start dev server
2. `build` — production build
3. `preview` — preview production build
4. `test` — run tests once
5. `test:watch` — run tests in watch mode
6. `test:ui` — run tests with UI
7. `test:coverage` — run tests with coverage
8. `lint` — run ESLint
9. `lint:fix` — run ESLint with auto-fix
10. `format` — run Prettier
11. `typecheck` — run `tsc --noEmit`
12. `check` — run lint + typecheck + test (pre-push gate)
13. `clean` — remove build artifacts and cache
14. `fresh` — clean install (remove node_modules and lockfile)

## Step 9: Optimize CI/CD for DX

1. Use `npm ci` for reproducible, fast installs
2. Enable caching: npm cache, build cache, test cache
3. Run jobs in parallel: lint, typecheck, test simultaneously
4. Build job depends on quality jobs passing
5. Keep CI under 5 minutes — use caching aggressively
6. Show clear failure messages in CI — not just "exit code 1"
7. Enable required status checks before merge
8. Auto-cancel in-progress CI when new commits pushed

## Step 10: Track DX Metrics

1. **Time to first success:** Have new team member onboard — target < 5 min
2. **Cold start:** `npm run dev` to browser ready — target < 2 sec
3. **HMR speed:** Save file to browser update — target < 200ms
4. **Test suite:** `npm run test` — target < 30 sec
5. **Build time:** `npm run build` — target < 30 sec
6. **Type check:** `npm run typecheck` — target < 10 sec
7. **CI time:** Push to all checks pass — target < 5 min
8. Track trends over time — catch regressions early
9. Conduct regular DX audits — friction logs, onboarding tests

## Step 11: Document & Maintain

1. Document all scripts in `CONTRIBUTING.md`
2. Document environment setup in `README.md`
3. Document VSCode setup — extensions, settings, snippets
4. Document debugging — how to use launch configs
5. Document testing — how to run, write, and debug tests
6. Review DX in retrospectives — what's slow, what's confusing
7. Regularly update tooling — new ESLint, Prettier, Vite versions
