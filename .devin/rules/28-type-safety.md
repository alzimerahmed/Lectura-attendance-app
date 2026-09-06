# Rule: Type Safety & TypeScript for All Projects

**ALWAYS** apply the Type Safety & TypeScript skill and workflow when working with TypeScript. Never use `any` — use `unknown` and narrow. Types are your safety net, not your straightjacket.

## Skill
`~/.codeium/windsurf/skills/type-safety-typescript.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/type-safety.md` — invoke with `/type-safety`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/type-safety-engineer.md` (parent: Docs Engineer)

## How to follow this rule:
1. When setting up TypeScript, invoke the `/type-safety` workflow
2. Follow the workflow steps in order: Configure tsconfig → Zod → Eliminate any → Discriminated Unions → Advanced Patterns → ESLint → CI → Document
3. Always enable `strict: true` plus `noUncheckedIndexedAccess` and `exactOptionalPropertyTypes`
4. Always use Zod for runtime validation at all API boundaries — single source of truth for types and validation
5. Never use `any` — use `unknown` and narrow with type guards or Zod
6. Never use `as` type assertions — use Zod validation or type guards instead
7. Never use `!` non-null assertions — use optional chaining or nullish coalescing
8. Always use discriminated unions for state machines — exhaustive switch with `assertNever`

## When this rule applies:
- Setting up TypeScript for a new project
- Tightening existing TypeScript configuration
- Adding runtime validation with Zod
- Fixing type errors or removing `any`
- User asks about type safety or TypeScript

## When this rule does NOT apply:
- JavaScript-only projects
- User explicitly says to skip type safety setup
