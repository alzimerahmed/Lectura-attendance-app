---
name: css-architecture
description: Comprehensive CSS architecture workflow — methodology selection, TailwindCSS v4, cascade layers, design tokens, container queries, dark mode, modern CSS, and performance
---

# CSS Architecture Workflow

This workflow applies the **CSS Architecture Skill** (`~/.codeium/windsurf/skills/css-architecture.md`) to establish a scalable, maintainable, and performant CSS foundation.

## When to Run
- When setting up CSS for a new project
- When the user says `/css-architecture` or asks about CSS
- When configuring TailwindCSS or design tokens
- When setting up dark mode or responsive design
- When refactoring CSS architecture

---

## Step 1: Choose CSS Methodology

1. Read the project context — framework, existing CSS approach, design system needs
2. **TailwindCSS v4 (default):** Rapid development, consistent design, small bundle — best for most projects
3. **CSS Modules:** For complex components where utilities become unwieldy — scoped styles
4. **Vanilla Extract:** For type-safe CSS — TypeScript in CSS, zero runtime
5. **Plain CSS + Layers:** For simple sites — no dependencies, full control
6. Most projects: TailwindCSS v4 for utilities + CSS Modules for complex components

## Step 2: Set Up TailwindCSS v4

1. Install: `npm install tailwindcss @tailwindcss/vite`
2. Add Vite plugin: `tailwindcss()` in `vite.config.ts`
3. Import in CSS: `@import "tailwindcss"` in main CSS file
4. Configure theme in CSS using `@theme` block — colors, fonts, spacing, breakpoints
5. Use OKLCH color space for perceptually uniform colors
6. Install `class-variance-authority` (cva) for component variants

## Step 3: Define Design Tokens

1. Define color tokens with OKLCH: background, foreground, brand, border, muted
2. Define spacing scale: `--space-1` through `--space-16` (0.25rem to 4rem)
3. Define typography scale: `--text-xs` through `--text-4xl`
4. Define font families: sans, display, mono
5. Define border radii: sm, md, lg, full
6. Define shadows: sm, md, lg
7. Define transitions: fast (150ms), normal (250ms), slow (400ms)
8. Define z-index scale: base, dropdown, sticky, modal, toast
9. Store all tokens as CSS custom properties in `:root`

## Step 4: Set Up Cascade Layers

1. Declare layer order: `@layer reset, base, components, utilities`
2. **Reset layer:** Normalize/reset styles — box-sizing, margins, padding
3. **Base layer:** Element defaults — body, headings, links, buttons
4. **Components layer:** Component classes — .card, .button, .nav
5. **Utilities layer:** Utility classes — .mt-4, .text-center (highest priority)
6. TailwindCSS v4 automatically uses layers — add custom components in `@layer components`
7. Unlayered styles have highest priority — use sparingly

## Step 5: Implement Dark Mode

1. Use custom properties for ALL colors — never hardcode color values
2. Override tokens in `@media (prefers-color-scheme: dark)` for automatic dark mode
3. Add `[data-theme="dark"]` selector for manual toggle
4. Create theme toggle component — save preference to localStorage
5. Add inline script in `<head>` to prevent flash of wrong theme (FOUC)
6. Test both light and dark modes for all components
7. Ensure sufficient contrast in both modes (WCAG AA: 4.5:1)

## Step 6: Use Container Queries

1. Identify components that need to adapt based on parent size (not viewport)
2. Set `container-type: inline-size` on the parent wrapper
3. Use `@container` queries to adjust layout based on container width
4. Use container query units (cqw, cqh) for responsive typography and spacing
5. Install `@tailwindcss/container-queries` for Tailwind container query utilities
6. Test components in different container contexts (sidebar, main, grid)

## Step 7: Apply Modern CSS Features

1. **Subgrid:** For nested grids that inherit parent track definitions
2. **`:has()` selector:** Style parent based on child state (form validation, card with image)
3. **Native nesting:** Use `&` for nested selectors (no preprocessor needed)
4. **Logical properties:** `padding-inline`, `margin-block`, `inset-inline-start` for RTL/i18n
5. **`text-wrap: balance`:** For headings — prevent orphaned words
6. **`text-wrap: pretty`:** For paragraphs — better line breaking
7. **`color-mix()`:** For dynamic color mixing (hover states, overlays)

## Step 8: Set Up Component Patterns

1. Use `cva` (class-variance-authority) for component variants
2. Define base styles + variant styles + size styles
3. Export typed props from cva for TypeScript integration
4. Use CSS Modules for complex components with many states
5. Keep Tailwind class lists manageable — extract to cva or component
6. Create a component library structure: `src/components/ui/`

## Step 9: Optimize CSS Performance

1. **Critical CSS:** Inline above-the-fold CSS, load rest asynchronously
2. **CSS containment:** `contain: layout style paint` for isolated components
3. **Content-visibility:** `content-visibility: auto` for off-screen list items
4. **Font performance:** `font-display: swap`, subset fonts, use `woff2`
5. **Purge unused CSS:** TailwindCSS does this automatically in production
6. **Minify:** Use `lightningcss` or `esbuild` for CSS minification
7. **Avoid `will-change`:** Only add right before animation, remove after

## Step 10: Avoid Anti-Patterns

1. **No IDs for styling:** Use classes — IDs have too-high specificity
2. **No `!important`:** Use cascade layers or proper specificity
3. **No inline styles:** Use classes — inline styles can't be cached or queried
4. **No magic numbers:** Use design tokens — `var(--space-4)` not `16px`
5. **No deep selectors:** Don't pierce component boundaries
6. **No global resets in components:** Keep resets in reset layer
7. **No hardcoded colors:** Always use custom properties for theming

## Step 11: Document CSS Conventions

1. Document token system — all custom properties and their usage
2. Document dark mode strategy — how to add new themed colors
3. Document component patterns — cva usage, CSS Module conventions
4. Document cascade layer order — where to add new styles
5. Document responsive strategy — container queries vs media queries
6. Document browser support — target browsers, fallbacks needed
7. Create CSS style guide — naming conventions, file structure
