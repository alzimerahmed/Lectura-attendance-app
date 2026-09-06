# Rule: Design System & Component Library for All Projects

**ALWAYS** apply the Design System & Component Library skill and workflow when building or maintaining a design system. A design system is a shared language — tokens, components, patterns, documentation, and governance.

## Skill
`~/.codeium/windsurf/skills/design-system-component-library.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/design-system.md` — invoke with `/design-system`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/design-system-builder.md` (parent: Design Engineer)

## How to follow this rule:
1. When building a design system or component library, invoke the `/design-system` workflow
2. Follow the workflow steps in order: Read Context → Tokens → Theming → Foundation → Core Components → Accessibility → Storybook → Tests → Documentation → Versioning → Multi-Framework
3. Always use design tokens as the single source of truth (global → semantic → component)
4. Always use headless UI libraries (Radix, Headless UI, Ark UI) for accessibility
5. Always use compound component pattern — compose, don't configure
6. Always use cva for variant management with type safety
7. Always build accessibility-first — ARIA, keyboard, focus management, screen reader support
8. Always set up Storybook with documentation, a11y addon, and visual regression

## When this rule applies:
- Building a new design system
- Setting up a component library
- Establishing design tokens and theming
- Standardizing UI components across a project
- User asks about design systems or component libraries

## When this rule does NOT apply:
- Simple projects with only a few components
- User explicitly says to skip design system setup
