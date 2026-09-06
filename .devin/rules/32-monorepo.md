# Rule: Monorepo Management for All Projects

**ALWAYS** apply the Monorepo Management skill and workflow when setting up or working with a monorepo. Use pnpm workspaces and Turborepo — not npm, yarn, or Lerna.

## Skill
`~/.codeium/windsurf/skills/monorepo-management.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/monorepo.md` — invoke with `/monorepo`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/monorepo-manager.md` (parent: Infrastructure Engineer)

## How to follow this rule:
1. When setting up a monorepo, invoke the `/monorepo` workflow
2. Follow the workflow steps in order: pnpm Workspaces → Turborepo → Package Structure → Shared Configs → Cross-Package Deps → Changesets → CI/CD → Dev Workflow → Shared Packages → Remote Cache → Document
3. Always use pnpm workspaces — not npm or yarn — for disk efficiency and strict dependency isolation
4. Always use Turborepo for task orchestration and caching — not Lerna (deprecated)
5. Always use `workspace:*` protocol for cross-package dependencies — explicit, no phantom deps
6. Always use Changesets for independent package versioning — not unified versioning
7. Always set up shared configs (ESLint, TypeScript, Tailwind) in a `packages/config/` package
8. Always enable Turbo Remote Cache for shared caching across team and CI

## When this rule applies:
- Setting up a monorepo for a new project
- Configuring Turborepo or pnpm workspaces
- Setting up Changesets for package versioning
- Optimizing monorepo CI/CD
- User asks about monorepo management

## When this rule does NOT apply:
- Single-package projects (no monorepo needed)
- User explicitly says to skip monorepo setup
