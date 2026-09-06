# Rule: Documentation for All Projects

**ALWAYS** apply the Documentation skill and workflow when creating or updating project documentation. Documentation is code — treat it with the same rigor: version control, review in PRs, test for accuracy, automate where possible.

## Skill
`~/.codeium/windsurf/skills/documentation.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/documentation.md` — invoke with `/documentation`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/docs-writer.md` (parent: Docs Engineer)

## How to follow this rule:
1. When creating documentation, invoke the `/documentation` workflow
2. Follow the workflow steps in order: README → CONTRIBUTING → API Docs → ADRs → Inline Comments → Component Docs → Changelog → CI Checks → docs/ → Review Process
3. Always write a README with Quick Start — 3-5 commands to running app
4. Always write JSDoc for public APIs — explain why, not what
5. Always maintain ADRs for significant architectural decisions
6. Always keep a changelog — automate from Conventional Commits
7. Always set up Storybook for component documentation
8. Always check docs in CI — broken links, stale TODOs, markdown lint

## When this rule applies:
- Setting up documentation for a new project
- Creating API documentation or ADRs
- Setting up Storybook or changelog automation
- Improving existing documentation
- User asks about documentation

## When this rule does NOT apply:
- Quick prototypes or throwaway scripts
- User explicitly says to skip documentation
