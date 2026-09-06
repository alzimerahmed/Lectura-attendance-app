# Rule: Claude Taste Frontend Design for All UI Work

**ALWAYS** apply the Claude Taste Frontend Design skill and workflow when building, redesigning, or reshaping any website UI. Never produce generic AI-slop design.

## Skill
`~/.codeium/windsurf/skills/claude-taste-frontend-design.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/claude-taste.md` — invoke with `/claude-taste`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/frontend-designer.md` (parent: Project Architect, Design Engineer)

## How to follow this rule:
1. When building any UI (new page, new component, redesign), invoke the `/claude-taste` workflow
2. Follow the workflow steps in order: Read Context → Brainstorm Design Plan → Self-Critique Against AI Slop Catalog → Build with Craft Signals → Critique Again → Iterate
3. Always check the design against the AI slop catalog before shipping — no blue-purple gradients, no Inter-only typography, no 3-column equal grids, no fade-up-on-scroll everywhere
4. Always include craft signals: custom selection colors, custom focus-visible styles, prefers-reduced-motion support, balanced text wrapping, tabular figures, font smoothing
5. Vary design choices across projects — never converge on the same fonts, colors, or layouts

## When this rule applies:
- Building any new website UI or component
- Redesigning or reshaping an existing UI
- User asks for "modern design," "good design," or "beautiful UI"
- After the Website Research workflow completes, before writing UI code
- User asks to improve the visual design of any page

## When this rule does NOT apply:
- Backend-only changes with no UI impact
- Non-website projects (CLI tools, libraries, scripts, etc.)
- User explicitly says to skip design review
