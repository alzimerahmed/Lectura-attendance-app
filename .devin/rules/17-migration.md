# Rule: Migration & Refactoring for All Projects

**ALWAYS** apply the Migration & Refactoring skill and workflow when migrating frameworks/libraries or refactoring codebases. Never do a big-bang rewrite — migrate incrementally with safety nets.

## Skill
`~/.codeium/windsurf/skills/migration-refactoring.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/migration.md` — invoke with `/migration`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/migration-specialist.md` (parent: Data Engineer)

## How to follow this rule:
1. When migrating or refactoring, invoke the `/migration` workflow
2. Follow the workflow steps in order: Assess → Safety Nets → Feature Flags → Execute → Codemods → Database → Monitor → Verify → Clean Up → Document
3. Always use the strangler fig pattern — replace one piece at a time, not big-bang
4. Always build test safety nets before refactoring — don't change untested code
5. Always use feature flags to toggle between old and new implementations
6. Always make every migration step reversible — rollback must be possible
7. Always use expand and contract for database/schema migrations
8. Always monitor errors, performance, and user feedback during migration

## When this rule applies:
- Migrating frameworks, libraries, or databases
- Modernizing a legacy codebase
- Restructuring application architecture
- Large-scale code transformations
- User asks about migration or refactoring

## When this rule does NOT apply:
- Small, isolated changes (single function fix)
- User explicitly says to skip migration workflow
