# Rule: Code Review Before Merging

**ALWAYS** apply the Code Review skill and workflow before merging any PR or deploying significant changes. Review for correctness, security, performance, architecture, error handling, testing, and accessibility.

## Skill
`~/.codeium/windsurf/skills/code-review.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/review.md` — invoke with `/review`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/code-reviewer.md` (parent: Quality Engineer)

## How to follow this rule:
1. Before merging or deploying, invoke the `/review` workflow
2. Follow the workflow steps in order: Read Context → Correctness → Security → Performance → Architecture → Style → Error Handling → Testing → Accessibility → Cross-Cutting → Verdict
3. Always classify findings by severity: Blocker, Critical, Major, Minor, Nit
4. Never merge with unresolved Blockers or Criticals
5. Provide constructive, specific, actionable feedback with code examples for fixes
6. Assess regression risk — what could break, what tests cover it, blast radius, rollback plan
7. Issue a deployment readiness verdict: Approved, Approved with comments, Changes requested, or Blocked

## When this rule applies:
- Before merging any PR
- After completing a feature or significant change
- Before deploying to production
- User asks for a code review or says `/review`

## When this rule does NOT apply:
- Trivial changes (typo fixes, comment updates, formatting)
- User explicitly says to skip review
