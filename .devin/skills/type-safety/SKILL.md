---
name: type-safety
description: Comprehensive type safety & TypeScript workflow — strict config, Zod validation, type patterns, generics, narrowing, anti-patterns, and ESLint
---

# Type Safety & TypeScript Workflow

This workflow applies the **Type Safety & TypeScript Skill** (`~/.codeium/windsurf/skills/type-safety-typescript.md`) to achieve compile-time and runtime type safety.

## When to Run
- When setting up TypeScript for a new project
- When the user says `/type-safety` or asks about TypeScript
- When tightening existing TypeScript configuration
- When adding runtime validation with Zod
- When fixing type errors or removing `any`

---

## Step 1: Configure tsconfig.json

1. Read the project context — framework, TypeScript version, existing config
2. Set `strict: true` — enables all strict mode checks
3. Enable additional strict flags:
   - `noUncheckedIndexedAccess` — array/object access returns `T | undefined`
   - `exactOptionalPropertyTypes` — `undefined` is not the same as "not present"
   - `noUnusedLocals` and `noUnusedParameters` — error on unused variables
   - `noImplicitReturns` — all code paths must return
   - `noFallthroughCasesInSwitch` — switch cases must break/return
4. Set `target: ES2022` — modern JavaScript output
5. Set `moduleResolution: bundler` — for Vite/Next.js
6. Set `verbatimModuleSyntax: true` — enforce type-only imports
7. Set `isolatedModules: true` — compatible with Vite/esbuild
8. Run `tsc --noEmit` — fix all type errors

## Step 2: Set Up Zod for Runtime Validation

1. Install Zod: `npm i zod`
2. Create schemas for all API boundaries:
   - Request body validation (POST/PUT/PATCH)
   - Query parameter validation (GET)
   - Response validation (external API calls)
   - Database row validation (if using raw SQL)
3. Use `z.infer<typeof schema>` to generate TypeScript types from schemas
4. Never define types separately from validation — single source of truth
5. Create shared schemas in `src/schemas/` or `src/lib/validation/`
6. Use `safeParse` for error handling, `parse` for throwing on invalid

## Step 3: Eliminate `any` and Type Assertions

1. Search for `any` usage: `grep -r ": any" src/`
2. Replace `any` with `unknown` — then narrow with Zod or type guards
3. Search for `as` assertions: `grep -r " as " src/`
4. Replace `as Type` with:
   - Zod validation at boundaries
   - Type guards (`is` functions)
   - Proper type definitions
5. Search for `!` non-null assertions: `grep -r "\!\." src/`
6. Replace `!` with:
   - Optional chaining (`?.`)
   - Nullish coalescing (`??`)
   - Explicit null checks
7. Run `tsc --noEmit` — verify no type errors after changes

## Step 4: Use Discriminated Unions for State

1. Identify state machines in the app (async states, form states, auth states)
2. Define discriminated union types with `status` or `type` discriminant
3. Use exhaustive switch statements — no `default` case needed
4. Add `assertNever` function for compile-time exhaustiveness checking
5. Replace boolean flag combinations with discriminated unions:
   - `isLoading && !error && data` → `{ status: 'loading' }`
6. Use in React components for clean conditional rendering

## Step 5: Apply Advanced Type Patterns

1. **Branded types:** Create branded types for domain primitives (UserId, Email, ProductId)
2. **Utility types:** Use `Pick`, `Omit`, `Partial`, `Record` instead of duplicating types
3. **Template literal types:** For event names, API routes, CSS classes
4. **Conditional types:** For generic type transformations
5. **Mapped types:** For transforming object types (Nullable, DeepPartial, DeepReadonly)
6. **Generic components:** For reusable React components with typed props
7. **Type-safe event emitters:** For WebSocket or event-driven code

## Step 6: Set Up ESLint for TypeScript

1. Install `typescript-eslint`: `npm i -D typescript-eslint`
2. Use `tseslint.configs.strict` and `tseslint.configs.stylistic` presets
3. Enable key rules:
   - `@typescript-eslint/no-explicit-any: error`
   - `@typescript-eslint/no-non-null-assertion: error`
   - `@typescript-eslint/no-unsafe-*: error` (assignment, member-access, call, argument, return)
   - `@typescript-eslint/no-floating-promises: error`
   - `@typescript-eslint/no-misused-promises: error`
   - `@typescript-eslint/consistent-type-imports: error`
4. Configure `lint-staged` to run ESLint on staged files
5. Add `npm run lint` to CI pipeline

## Step 7: Add Type Checking to CI

1. Add `tsc --noEmit` to CI pipeline: `npm run typecheck`
2. Add type checking to pre-push Git hook
3. Fail CI on any type errors
4. Run ESLint with TypeScript rules in CI
5. Consider `tsc --noEmit --incremental` for faster checks in watch mode
6. Track type error count over time — should always be zero

## Step 8: Document Type Conventions

1. Document strict tsconfig settings and why each is enabled
2. Document Zod schema conventions — where schemas live, naming patterns
3. Document branded types — which domain primitives have branded types
4. Document discriminated union patterns — how state machines are modeled
5. Document ESLint rules — which are enforced and why
6. Add `// @ts-expect-error` only with explanatory comment — never silently
7. Create type-level utility library in `src/lib/types/` for reusable patterns
