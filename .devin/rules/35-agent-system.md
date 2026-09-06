# Rule: Agent System for Orchestrating All Workflows

**ALWAYS** use the agent system to orchestrate the 37 workflows across all projects. Main agents coordinate, sub-agents execute — this ensures the right workflow runs at the right time in the right order.

## Agent System Files
- **Main agents**: `~/.codeium/windsurf/windsurf/agents/` (9 orchestrators + README + INDEX)
- **Sub-agents**: `~/.codeium/windsurf/windsurf/sub-agents/` (37 specialists)
- **Master index**: `~/.codeium/windsurf/windsurf/agents/INDEX.md`
- **Project-local**: `.devin/agents/`, `.devin/sub-agents/`, `.devin/workflows/`, `.devin/skills/`, `.devin/rules/`

## Main Agents (Orchestrators)

| Agent | File | Domain | Sub-Agents |
|-------|------|--------|-----------|
| Full Stack Orchestrator | `agents/full-stack-orchestrator.md` | End-to-end builds | All 8 below |
| Project Architect | `agents/project-architect.md` | Research, design, architecture, SEO | 4 |
| Design Engineer | `agents/design-engineer.md` | UI/UX, content, CSS, animation, media, i18n, design clone | 8 |
| Feature Engineer | `agents/feature-engineer.md` | Payments, uploads, search, real-time, email, PWA, state | 7 |
| Quality Engineer | `agents/quality-engineer.md` | Code review, debugging, testing, security, performance, a11y | 6 |
| Infrastructure Engineer | `agents/infrastructure-engineer.md` | DevOps, git, build, monorepo, DX, caveman | 6 |
| Data Engineer | `agents/data-engineer.md` | Database, analytics, migration, web scraping | 4 |
| Docs Engineer | `agents/docs-engineer.md` | Documentation, type safety | 2 |
| Vibe Coding Guardian | `agents/vibe-coding-guardian.md` | Anti-vibe-coding audits, slop prevention | 7 |

## How to follow this rule:
1. For end-to-end project builds, start with the **Full Stack Orchestrator** which coordinates all 8 domain agents in lifecycle order
2. For targeted work, invoke the relevant main agent directly (e.g., `quality-engineer` for a security audit)
3. Main agents delegate to sub-agents — each sub-agent wraps exactly one workflow and adds persona, triggers, inputs, outputs, and delegation rules
4. Sub-agents can also be invoked directly for targeted work (e.g., just run the security audit)
5. Always follow the lifecycle: Inception (Project Architect) → Foundation (Design + Infra + Docs in parallel) → Build (Data + Features) → Quality Gate → Anti-Vibe-Coding Audit → Ship
6. Respect phase gates — don't proceed to the next phase until the current phase is confirmed complete
7. When agents disagree: security wins over DX, performance wins for Core Web Vitals, design wins for visual identity
8. Project-local copies in `.devin/agents/` and `.devin/sub-agents/` override global copies when they exist

## Sub-Agent → Workflow → Rule Mapping

| Sub-Agent | Workflow | Rule File |
|-----------|----------|-----------|
| researcher | website-research | `01-website-research.md` |
| frontend-designer | claude-taste | `02-claude-taste.md` |
| backend-architect | backend-design | `03-backend-design.md` |
| seo-specialist | search-optimization | `04-search-optimization.md` |
| code-reviewer | review | `05-code-review.md` |
| debugger | debug | `06-debug.md` |
| test-engineer | testing | `07-testing.md` |
| devops-engineer | deploy | `08-deploy.md` |
| security-auditor | security | `09-security.md` |
| performance-engineer | performance | `10-performance.md` |
| a11y-specialist | accessibility | `11-accessibility.md` |
| content-writer | content | `12-content.md` |
| design-system-builder | design-system | `13-design-system.md` |
| analytics-engineer | analytics | `14-analytics.md` |
| i18n-specialist | i18n | `15-i18n.md` |
| database-engineer | database | `16-database.md` |
| migration-specialist | migration | `17-migration.md` |
| animation-engineer | animation | `18-animation.md` |
| media-optimizer | media | `19-media.md` |
| state-manager | state-management | `20-state-management.md` |
| pwa-engineer | pwa | `21-pwa.md` |
| payment-integrator | payment | `22-payment.md` |
| file-handler | file-handling | `23-file-handling.md` |
| search-architect | search | `24-search.md` |
| realtime-engineer | real-time | `25-real-time.md` |
| git-master | git-workflow | `26-git-workflow.md` |
| build-optimizer | build-tools | `27-build-tools.md` |
| type-safety-engineer | type-safety | `28-type-safety.md` |
| css-architect | css-architecture | `29-css-architecture.md` |
| dx-optimizer | dx | `30-dx.md` |
| docs-writer | documentation | `31-documentation.md` |
| monorepo-manager | monorepo | `32-monorepo.md` |
| email-engineer | email | `33-email.md` |
| web-scraper | web-scraping | `34-web-scraping.md` |
| vibe-coding-auditor | anti-vibe-coding | `36-anti-vibe-coding.md` |
| caveman-compressor | caveman | `37-caveman.md` |
| design-cloner | playwright-design-clone | `38-playwright-design-clone.md` |

## When this rule applies:
- Starting any new website project (use Full Stack Orchestrator)
- Needing targeted work on a specific domain (use the relevant main agent)
- User asks to "build", "research", "design", "deploy", "audit", "optimize", "clone", or "caveman"
- Any time a workflow from the 37-workflow system needs to be executed

## When this rule does NOT apply:
- Non-website projects (CLI tools, libraries, scripts) — though some agents (git-master, test-engineer, security-auditor) may still be useful
- User explicitly says to skip the agent system and invoke workflows directly
