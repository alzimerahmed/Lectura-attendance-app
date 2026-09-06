# Rule: Backend Design Structural System for All Backend Work

**ALWAYS** apply the Backend Design Structural System skill and workflow when building, designing, or restructuring any backend or API. Never produce legacy patterns, AI-generated boilerplate, or anti-pattern-ridden code.

## Skill
`~/.codeium/windsurf/skills/backend-design-structural-system.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/backend-design.md` — invoke with `/backend-design`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/backend-architect.md` (parent: Project Architect)

## How to follow this rule:
1. When building any backend, API, or database schema, invoke the `/backend-design` workflow
2. Follow the workflow steps in order: Read Context → Assess Complexity → Design API → Design Data Model → Design Auth & Security → Design Code Structure → Plan Observability → Plan Testing → Plan Deployment → Check Anti-Patterns → Build
3. Always check the design against the anti-patterns catalog before building — no god objects, no fat controllers, no N+1 queries, no missing pagination, no hardcoded config, no synchronous blocking where async is needed
4. Always enforce separation of concerns: controllers (HTTP only) → services (business logic) → repositories (data access)
5. Always implement observability from day one: structured logging, health checks, metrics, distributed tracing
6. Start simple (modular monolith, layered architecture) and add complexity only when measured evidence demands it

## When this rule applies:
- Building any new backend, API, or database schema
- Restructuring or refactoring an existing backend
- Adding significant new backend features (auth, payments, real-time, etc.)
- After the Website Research workflow completes, before writing backend code
- User asks about backend architecture, API design, or database design

## When this rule does NOT apply:
- Frontend-only changes with no backend impact
- Non-website projects (CLI tools, libraries, scripts, etc.)
- User explicitly says to skip backend review
