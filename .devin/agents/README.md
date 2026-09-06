# Agent System

A multi-agent architecture for orchestrating the 37 workflows in `.devin/workflows/`.

## Architecture

```
.devin/
├── agents/              ← Main orchestrators (9)
│   ├── README.md        ← This file
│   ├── INDEX.md         ← Master mapping of agents → workflows → sub-agents
│   ├── project-architect.md
│   ├── quality-engineer.md
│   ├── infrastructure-engineer.md
│   ├── feature-engineer.md
│   ├── design-engineer.md
│   ├── data-engineer.md
│   ├── docs-engineer.md
│   └── vibe-coding-guardian.md
├── sub-agents/          ← Domain specialists (37)
│   ├── researcher.md
│   ├── frontend-designer.md
│   ├── vibe-coding-auditor.md
│   ├── ... (one per workflow)
│   └── web-scraper.md
└── workflows/           ← Step-by-step workflows (37)
    ├── website-research.md
    ├── ... 
    └── web-scraping.md
```

## How It Works

### Main Agents (Orchestrators)
Main agents are high-level coordinators. They don't execute workflows directly — they decide **which** sub-agent to invoke **when**, and in **what order**. Each main agent owns a phase or concern of the project lifecycle.

### Sub-Agents (Specialists)
Sub-agents are domain experts. Each sub-agent wraps exactly one workflow and adds:
- **Persona** — the agent's role and expertise
- **Triggers** — when this agent should be invoked
- **Inputs** — what it needs to do its job
- **Outputs** — what it produces
- **Delegation** — when to hand off to another sub-agent

### Workflows (Execution Plans)
Workflows are the step-by-step execution plans that sub-agents follow. They contain the actual instructions, checklists, and tool usage guidance.

## The 9 Main Agents

| Agent | Role | Sub-Agents Coordinated |
|-------|------|----------------------|
| **Project Architect** | Project inception, planning, research | researcher, frontend-designer, backend-architect, seo-specialist |
| **Design Engineer** | UI/UX, content, visual design, design cloning | frontend-designer, content-writer, design-system-builder, animation-engineer, media-optimizer, css-architect, i18n-specialist, design-cloner |
| **Feature Engineer** | Feature implementation | payment-integrator, file-handler, search-architect, realtime-engineer, email-engineer, pwa-engineer, state-manager |
| **Quality Engineer** | Quality gates, reviews, audits | code-reviewer, debugger, test-engineer, security-auditor, performance-engineer, a11y-specialist |
| **Infrastructure Engineer** | DevOps, tooling, CI/CD, communication efficiency | devops-engineer, git-master, build-optimizer, monorepo-manager, dx-optimizer, caveman-compressor |
| **Data Engineer** | Database, analytics, migration | database-engineer, analytics-engineer, migration-specialist, web-scraper |
| **Docs Engineer** | Documentation & type safety | docs-writer, type-safety-engineer |
| **Vibe Coding Guardian** | Anti-vibe-coding audits, slop prevention | vibe-coding-auditor, frontend-designer, content-writer, backend-architect, code-reviewer, css-architect, a11y-specialist |

## Lifecycle

```
Project Start
    │
    ▼
┌─────────────────────┐
│  Project Architect  │  ← researcher → frontend-designer → backend-architect → seo-specialist
└────────┬────────────┘
         │
    ┌────┴────┬──────────────┬──────────────┐
    ▼         ▼              ▼              ▼
┌────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐
│ Design │ │ Feature  │ │ Data     │ │ Docs       │
│Engineer│ │ Engineer │ │ Engineer │ │ Engineer   │
└───┬────┘ └────┬─────┘ └────┬─────┘ └─────┬──────┘
    │           │            │             │
    └─────┬─────┴──────┬─────┘             │
          ▼            ▼                   │
   ┌────────────┐ ┌──────────────┐         │
   │  Quality   │ │  Infra       │◄────────┘
   │  Engineer  │ │  Engineer    │
   └────────────┘ └──────────────┘
          │
          ▼
     Ship 🚀
```

## Usage

Invoke any main agent by referencing its file. The main agent will:
1. Assess the current project state
2. Determine which sub-agents to activate
3. Delegate to sub-agents in the correct order
4. Collect and synthesize results
5. Report back with a summary and next steps

Sub-agents can also be invoked directly for targeted work (e.g., just run the security audit without the full quality gate).
