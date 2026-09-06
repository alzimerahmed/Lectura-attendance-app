---
name: monorepo
description: Comprehensive monorepo management workflow — pnpm workspaces, Turborepo, shared configs, Changesets, CI/CD, and development workflow
---

# Monorepo Management Workflow

This workflow applies the **Monorepo Management Skill** (`~/.codeium/windsurf/skills/monorepo-management.md`) to set up and manage a scalable monorepo.

## When to Run
- When setting up a monorepo for a new project
- When the user says `/monorepo` or asks about monorepos
- When configuring Turborepo or pnpm workspaces
- When setting up Changesets for package versioning
- When optimizing monorepo CI/CD

---

## Step 1: Set Up pnpm Workspaces

1. Install pnpm: `npm install -g pnpm`
2. Create `pnpm-workspace.yaml` with package globs: `apps/*` and `packages/*`
3. Create root `package.json` with `private: true`, `packageManager` field, and `engines`
4. Set Node version: add `.nvmrc` with `20`
5. Run `pnpm install` to initialize workspace
6. Verify: `pnpm list -r --depth -1` shows all packages
7. Never use npm or yarn for monorepos — pnpm is faster, stricter, and more disk-efficient

## Step 2: Set Up Turborepo

1. Install Turborepo: `pnpm add -D -w turbo`
2. Create `turbo.json` with task definitions:
   - `build`: depends on `^build`, outputs `dist/**` and `.next/**`
   - `dev`: `cache: false`, `persistent: true`
   - `test`: depends on `^build`, outputs `coverage/**`
   - `lint`: no outputs
   - `typecheck`: depends on `^build`, no outputs
3. Add turbo scripts to root `package.json`: `dev`, `build`, `test`, `lint`, `typecheck`
4. Configure inputs for cache keys: source files, configs, env vars
5. Enable Vercel Remote Cache: `npx turbo login && npx turbo link`
6. Test: `pnpm build` should run tasks in topological order with caching

## Step 3: Create Package Structure

1. Create `apps/` directory for deployable applications (web, admin, api, mobile)
2. Create `packages/` directory for shared libraries (ui, utils, types, config, api-client)
3. Each package gets its own `package.json` with:
   - `name`: `@repo/package-name`
   - `version`: `0.1.0`
   - `private: true` (for internal packages)
   - `main`, `module`, `types`, `exports` fields
   - Scripts: `build`, `dev`, `lint`, `typecheck`, `test`
4. Use `tsup` for building packages: `tsup src/index.ts --format esm,cjs --dts`
5. Run `pnpm install` after creating new packages to link them

## Step 4: Set Up Shared Configurations

1. Create `packages/config/` with subdirectories: `eslint/`, `tsconfig/`, `tailwind/`
2. **ESLint config:** Export base config from `@repo/eslint-config`
3. **TypeScript config:** Export base `tsconfig.json` from `@repo/tsconfig`
4. **Tailwind config:** Export shared theme from `@repo/tailwind-config`
5. Each app/package extends shared configs:
   - ESLint: `import config from '@repo/eslint-config'; export default config;`
   - TypeScript: `"extends": "@repo/tsconfig/base.json"`
6. Install shared dev dependencies at root: TypeScript, ESLint, Prettier, Vitest
7. Verify: all packages can lint and typecheck using shared configs

## Step 5: Configure Cross-Package Dependencies

1. Use `workspace:*` protocol for internal package dependencies
2. Declare ALL imports in `package.json` — no phantom dependencies
3. Example: `apps/web` depends on `@repo/ui`, `@repo/utils`, `@repo/api-client`
4. pnpm strict mode ensures packages only see declared dependencies
5. Run `pnpm install` after adding new cross-package dependencies
6. Verify: `pnpm why @repo/ui` shows which packages depend on it
7. Use `peerDependencies` for React in UI packages — don't bundle React

## Step 6: Set Up Changesets for Versioning

1. Install: `pnpm add -D -w changeset`
2. Initialize: `npx changeset init`
3. Configure `.changeset/config.json`:
   - `baseBranch: "main"`
   - `access: "restricted"` (for private packages)
   - `ignore`: list of apps that don't need versioning
4. Workflow:
   - Make changes in a package
   - Run `npx changeset` — select packages, bump type, write summary
   - Commit changeset file with code changes
   - When ready: `npx changeset version` — bumps versions, updates changelogs
   - Then: `npx changeset publish` — publishes to npm
5. Set up automated release GitHub Action with `changesets/action`
6. Only version packages that have changes — independent versioning

## Step 7: Set Up CI/CD for Monorepo

1. Use `pnpm install --frozen-lockfile` in CI
2. Enable Turbo Remote Cache via GitHub Actions cache
3. Use change detection: `turbo build --filter=...[main]` — only build changed packages
4. Run quality checks in parallel: lint, typecheck, test as separate jobs
5. Deploy job depends on quality checks passing
6. Use `actions/cache` for `.turbo` directory
7. Set up automated release workflow with Changesets action
8. Keep CI under 5 minutes with caching and filtering

## Step 8: Set Up Development Workflow

1. `pnpm dev` runs all dev servers simultaneously via Turborepo
2. `turbo dev --filter=web` runs only the web app's dev server
3. `turbo dev --filter=web...` runs web app and its dependencies in dev mode
4. Package dev mode: `tsup --watch` for packages, `vite`/`next dev` for apps
5. Path aliases: configure in each app's `tsconfig.json` and bundler config
6. Hot reload: changes in packages should trigger HMR in consuming apps
7. Document common dev commands in root `README.md`

## Step 9: Create Shared Packages

1. **`@repo/types`:** Shared TypeScript types and Zod schemas
2. **`@repo/ui`:** Shared UI components with Button, Input, Card, etc.
3. **`@repo/utils`:** Shared utility functions (formatting, validation, helpers)
4. **`@repo/api-client`:** API client SDK with typed methods
5. **`@repo/database`:** Prisma schema, client, and migrations
6. **`@repo/config`:** Shared ESLint, TypeScript, Tailwind configs
7. Each package: build with `tsup`, export from `src/index.ts`, document in README

## Step 10: Enable Remote Caching

1. Sign in to Vercel: `npx turbo login`
2. Link repository: `npx turbo link`
3. Remote cache is now shared across:
   - Local development (faster repeated builds)
   - CI pipelines (skip already-built packages)
   - Team members (shared cache across machines)
4. Configure in CI: use `actions/cache` for `.turbo` directory
5. Monitor cache hit rate — aim for > 80% in CI
6. Cache keys are based on inputs (source files, configs, env vars)

## Step 11: Document & Maintain

1. Document monorepo structure in root `README.md`
2. Document how to add new packages and apps
3. Document release process with Changesets
4. Document CI/CD pipeline and caching strategy
5. Document cross-package dependency conventions
6. Regularly update Turborepo and pnpm versions
7. Audit package dependencies — remove unused, update outdated
8. Monitor build times — catch regressions with Turbo analytics
