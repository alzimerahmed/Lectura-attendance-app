---
name: documentation
description: Comprehensive documentation workflow — README, API docs, ADRs, inline comments, component docs, changelogs, CI checks, and doc-as-code practices
---

# Documentation Workflow

This workflow applies the **Documentation Skill** (`~/.codeium/windsurf/skills/documentation.md`) to create comprehensive, maintainable documentation.

## When to Run
- When setting up documentation for a new project
- When the user says `/documentation` or asks about docs
- When creating API documentation or ADRs
- When setting up Storybook or changelog automation
- When improving existing documentation

---

## Step 1: Create README.md

1. Read the project context — what the project does, who uses it, tech stack
2. Write one-line description at the top
3. Add badges: CI status, license, PRs welcome
4. Add Features section — 3-5 bullet points of key features
5. Add Quick Start section — 3-5 commands to running app (most important)
6. Add Usage section — basic and advanced examples
7. Add Project Structure — visual directory tree
8. Add Scripts table — all npm scripts with descriptions
9. Add Tech Stack — key technologies and versions
10. Add Contributing link to CONTRIBUTING.md
11. Add License section
12. Review: can a new developer clone and run in < 5 minutes using only README?

## Step 2: Create CONTRIBUTING.md

1. Write development setup — detailed steps from clone to running
2. Document code style — TypeScript strict, ESLint, Prettier, Conventional Commits
3. Document testing — how to run, what to test, coverage expectations
4. Document PR process — branch naming, template, review, merge strategy
5. Document project structure — link to README
6. Document issue reporting — bug reports, feature requests, security issues
7. Add code of conduct reference if applicable
8. Review: can a new contributor make their first PR using only this guide?

## Step 3: Create API Documentation

1. For REST APIs: generate OpenAPI spec from code (Hono, Fastify, Express plugins)
2. For simple APIs: create API.md with all endpoints documented
3. For each endpoint document:
   - HTTP method and path
   - Authentication requirement
   - Request parameters (query, path, body)
   - Response format with examples
   - Error responses with status codes
   - curl example
4. Serve interactive docs: Swagger UI or ReDoc at `/docs`
5. Keep API docs in sync with code — generate from schemas where possible
6. Version the API and document breaking changes

## Step 4: Set Up Architecture Decision Records

1. Create `adr/` directory in project root
2. Create ADR template (markdown file with Status, Context, Decision, Consequences)
3. Write initial ADRs for key decisions:
   - Framework choice (e.g., ADR-001: Use Next.js)
   - Database choice (e.g., ADR-002: Use PostgreSQL)
   - Architecture pattern (e.g., ADR-003: Modular monolith)
   - Authentication strategy (e.g., ADR-004: JWT with refresh tokens)
4. Number ADRs sequentially
5. Mark status: Proposed, Accepted, Deprecated, Superseded
6. Link superseded ADRs to their replacements
7. Add ADRs to PR review — require ADR for significant architectural changes

## Step 5: Add Inline Code Comments

1. Comment WHY, not WHAT — the code shows what, comments explain why
2. Document non-obvious business logic — tax rules, legal requirements, edge cases
3. Link to context — ADRs, issues, documentation, specs
4. Add JSDoc for all public APIs — functions, classes, types exported from modules
5. Add JSDoc for React components — props, variants, examples
6. Use `@example` blocks in JSDoc — show usage, not just types
7. Mark TODOs with author and date: `// TODO(alice, 2025-01): remove after migration`
8. Remove commented-out code — use git history, not comments
9. Don't over-comment — obvious code needs no comments

## Step 6: Set Up Component Documentation

1. Install Storybook: `npx storybook@latest init`
2. Create stories for all UI components
3. Use `autodocs` tag — auto-generate documentation from stories
4. Document props with `argTypes` — control type, description, options
5. Create stories for each variant and state
6. Add usage examples in story descriptions
7. Deploy Storybook to Chromatic or GitHub Pages for team access
8. Add Storybook build to CI — ensure stories don't break

## Step 7: Create CHANGELOG.md

1. Use Keep a Changelog format
2. Sections: Added, Changed, Deprecated, Removed, Fixed, Security
3. Use [Unreleased] section for upcoming changes
4. Link versions to git tags or GitHub releases
5. Automate with standard-version or semantic-release:
   - Analyzes Conventional Commits
   - Auto-bumps version
   - Generates changelog entry
   - Creates git tag
6. Review changelog in PRs — require changelog entry for user-facing changes
7. Keep changelog human-readable — not just a git log dump

## Step 8: Set Up Documentation CI Checks

1. Check for broken links: `npx linkinator docs/ README.md`
2. Check for stale TODOs: `npx leasot 'docs/**/*.md'`
3. Validate code examples: type-check TypeScript in markdown blocks
4. Build Storybook: verify all stories render
5. Check for required docs: README, CONTRIBUTING, CHANGELOG exist
6. Lint markdown: `npx markdownlint docs/ *.md`
7. Check spelling: `npx cspell 'docs/**/*.md' '*.md'`
8. Add docs check job to CI pipeline

## Step 9: Create docs/ Directory

1. Create `docs/` directory for detailed documentation
2. Add guides:
   - `docs/getting-started.md` — detailed tutorial for newcomers
   - `docs/architecture.md` — system design overview
   - `docs/deployment.md` — how to deploy
   - `docs/troubleshooting.md` — common issues and solutions
   - `docs/configuration.md` — all configuration options
3. Add diagrams: architecture, data flow, sequence diagrams (Mermaid or images)
4. Keep docs next to code — update in same PR as code changes
5. Use clear headings and navigation — table of contents for long docs
6. Cross-link between docs — help readers find related information

## Step 10: Establish Documentation Review Process

1. Require doc updates in PRs that change public APIs
2. Require README updates for new scripts or setup changes
3. Require ADR for significant architectural changes
4. Require changelog entry for user-facing changes
5. Require Storybook stories for new UI components
6. Review docs for accuracy — do the examples actually work?
7. Review docs for clarity — can a newcomer understand this?
8. Review docs for completeness — are edge cases documented?
9. Regularly audit docs — remove stale content, update outdated info
