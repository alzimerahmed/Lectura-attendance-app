# Rule: State Management & Data Fetching for All Projects

**ALWAYS** apply the State Management & Data Fetching skill and workflow when architecting state and data fetching. State should live where it's needed — don't put everything in a global store by default.

## Skill
`~/.codeium/windsurf/skills/state-management-data-fetching.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/state-management.md` — invoke with `/state-management`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/state-manager.md` (parent: Feature Engineer)

## How to follow this rule:
1. When architecting state management, invoke the `/state-management` workflow
2. Follow the workflow steps in order: Classify → Choose Tools → Server State → URL State → Global State → Forms → Caching → Optimistic Updates → States → Patterns → Anti-Patterns → Document
3. Always classify state before choosing tools — server, URL, form, local, global, persistent
4. Always use TanStack Query for server state — never store server data in Redux/Zustand
5. Always use URL search params for shareable state (filters, pagination, sorting)
6. Always use React Hook Form + Zod for form state with validation
7. Always implement optimistic updates for mutations that update existing data
8. Always use the simplest solution that works — don't over-engineer with global stores

## When this rule applies:
- Architecting state management for a new project
- Setting up TanStack Query, Zustand, or other state tools
- Refactoring existing state management
- Implementing data fetching patterns
- User asks about state management or data fetching

## When this rule does NOT apply:
- Static sites with no interactive state
- User explicitly says to skip state management setup
