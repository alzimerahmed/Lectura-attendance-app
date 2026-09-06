---
name: design-system
description: Comprehensive design system & component library workflow — tokens, components, accessibility, theming, documentation, testing, and governance
---

# Design System & Component Library Workflow

This workflow applies the **Design System & Component Library Skill** (`~/.codeium/windsurf/skills/design-system-component-library.md`) to build and maintain a scalable design system.

## When to Run
- When building a new design system
- When the user says `/design-system` or asks about components
- When setting up a component library
- When establishing design tokens and theming
- When standardizing UI components across a project

---

## Step 1: Read Context

1. Read the project's `research.md` if available — brand, audience, visual direction
2. Identify the framework (React, Vue, Svelte) and styling approach (Tailwind, CSS Modules)
3. Review existing components — what exists, what's inconsistent, what's missing
4. Check for existing design tokens or style guide
5. Identify the component foundation preference (Radix, Headless UI, Ark UI, React Aria)

## Step 2: Define Design Tokens

1. **Color:** Primitive palette (50-950 per hue) → semantic mapping (primary, secondary, etc.) → dark mode variants
2. **Typography:** Font families, modular scale (xs-4xl), line heights, font weights
3. **Spacing:** Base unit (4px), scale (0, 1, 2, 3, 4, 6, 8, 12, 16, 24)
4. **Border radius:** sm, md, lg, xl, 2xl, full
5. **Shadows:** sm, md, lg, xl
6. **Animation:** Duration (fast, normal, slow), easing (default, in, out, spring)
7. **z-index:** base, dropdown, sticky, overlay, modal, popover, toast, tooltip
8. Set up Style Dictionary for token transformation (CSS, Tailwind, Figma)

## Step 3: Set Up Theming

1. Define CSS variables for all semantic tokens
2. Create dark mode theme (`.dark` class overrides)
3. Implement theme provider with system preference detection
4. Add theme persistence (localStorage)
5. Prevent flash of incorrect theme (inline script before hydration)
6. Support brand theming (override CSS variables at runtime)

## Step 4: Choose Component Foundation

1. Select headless UI library: Radix UI (most comprehensive), Headless UI, Ark UI, React Aria
2. Set up Tailwind CSS + shadcn/ui pattern (copy-paste components you own)
3. Install `class-variance-authority` for variant management
4. Install `tailwind-merge` for class deduplication
5. Create `cn()` utility for combining classes

## Step 5: Build Core Components

Build each with compound component pattern, cva variants, and full accessibility:
1. **Button** — variants (default, destructive, outline, ghost, link), sizes, loading, asChild
2. **Input** — text, email, password, with labels, helper text, error states
3. **Textarea** — auto-resize option, character count
4. **Select** — Radix Select for custom, native for simple
5. **Checkbox** — indeterminate, custom styling, keyboard accessible
6. **Radio** — radio group with arrow key navigation
7. **Switch** — `role="switch"`, `aria-checked`, keyboard accessible
8. **Dialog** — focus trap, Escape, scroll lock, portal, sizes
9. **Dropdown Menu** — keyboard navigation, items, separators, labels
10. **Tooltip** — delay, side, alignment, accessible
11. **Toast** — positions, types, auto-dismiss, action button, stacking
12. **Tabs** — keyboard navigation (arrows, Home, End), ARIA roles
13. **Accordion** — single/multiple, keyboard, smooth animation
14. **Table** — sorting, pagination, responsive, ARIA
15. **Combobox** — filtering, async, multi-select, keyboard
16. **Popover** — positioning, collision detection, focus management
17. **Navigation** — breadcrumbs, pagination, command palette, sidebar

## Step 6: Ensure Accessibility

1. Implement correct ARIA roles, states, and properties for each component
2. Implement keyboard navigation: Enter/Space, Escape, arrows, Home/End, Tab
3. Implement focus management: dialog open/close, route changes, dynamic content
4. Add `aria-live` regions for dynamic announcements
5. Add `:focus-visible` styles with 3:1 contrast
6. Test with screen readers (NVDA, VoiceOver)
7. Run axe-core on all components
8. Verify color contrast in light and dark mode

## Step 7: Set Up Storybook

1. Initialize Storybook: `npx storybook@latest init`
2. Create stories for each component with controls for all props
3. Add a11y addon — run axe on every story
4. Add viewport addon — test responsive behavior
5. Add themes addon — test light/dark mode
6. Write documentation for each component (purpose, when to use, props, examples)
7. Add do/don't examples for usage guidelines

## Step 8: Write Tests

1. **Unit tests:** Render, interact, assert — Testing Library + userEvent
2. **Interaction tests:** Keyboard navigation, focus management, ARIA
3. **Visual regression:** Chromatic or Percy — screenshot each story
4. **Accessibility:** vitest-axe or jest-axe on each component
5. **E2E:** Playwright tests for component interactions in real browser

## Step 9: Document the System

1. Document design tokens — name, value, usage, dark mode, examples
2. Document each component — purpose, props, examples, accessibility, do/don't
3. Create usage guidelines — spacing, layout, content, responsive
4. Document theming — how to override tokens, dark mode, brand theming
5. Create a live playground — interactive prop modification, code export
6. Write contribution guidelines — RFC process, review, lifecycle

## Step 10: Set Up Versioning & Governance

1. Implement semantic versioning (major.minor.patch)
2. Maintain changelog with migration guides for major versions
3. Define deprecation policy (2 minor versions before removal)
4. Define component lifecycle: Proposed → Experimental → Stable → Deprecated → Removed
5. Set up contribution guidelines — RFC, design review, code review, a11y review
6. Set up CI gates — tests, a11y, visual regression must pass

## Step 11: Multi-Framework & Cross-Platform (if needed)

1. Share design tokens via Style Dictionary (CSS, Tailwind, iOS, Android, Figma)
2. If multi-framework: consider Web Components (Lit) for framework-agnostic components
3. If React + Vue + Svelte: port components with same tokens, behavior, naming
4. Set up Figma to code sync — tokens plugin, design lint plugin
5. Document framework-specific differences and limitations
