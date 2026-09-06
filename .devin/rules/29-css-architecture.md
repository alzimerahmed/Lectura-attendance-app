# Rule: CSS Architecture for All Projects

**ALWAYS** apply the CSS Architecture skill and workflow when setting up or refactoring CSS. Use TailwindCSS for utilities, CSS Cascade Layers for predictable specificity, and custom properties for all design tokens.

## Skill
`~/.codeium/windsurf/skills/css-architecture.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/css-architecture.md` — invoke with `/css-architecture`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/css-architect.md` (parent: Design Engineer)

## How to follow this rule:
1. When setting up CSS, invoke the `/css-architecture` workflow
2. Follow the workflow steps in order: Methodology → TailwindCSS → Tokens → Layers → Dark Mode → Container Queries → Modern CSS → Components → Performance → Anti-Patterns → Document
3. Always use TailwindCSS v4 as the default utility framework — not Bootstrap or Bulma
4. Always use CSS Cascade Layers — reset, base, components, utilities — for predictable specificity
5. Always use custom properties for all design tokens — colors (OKLCH), spacing, typography, radii
6. Always implement dark mode with custom properties — never hardcode color values
7. Always use container queries for component-driven responsive design
8. Never use `!important`, IDs for styling, inline styles, or magic numbers

## When this rule applies:
- Setting up CSS for a new project
- Configuring TailwindCSS or design tokens
- Setting up dark mode or responsive design
- Refactoring CSS architecture
- User asks about CSS architecture

## When this rule does NOT apply:
- Projects with no custom CSS (e.g., plain HTML emails)
- User explicitly says to skip CSS architecture
