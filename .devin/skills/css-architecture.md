---
name: CSS Architecture Skill
description: Comprehensive methodology for CSS architecture — 2025-2026 practices with TailwindCSS v4, CSS layers, custom properties, container queries, subgrid, dark mode, and performance
version: 1.0.0
tags: [css, tailwind, css-architecture, css-layers, custom-properties, container-queries, dark-mode, css-modules, cascade-layers, performance]
---

# CSS Architecture Skill

## Purpose
This skill provides a comprehensive methodology for CSS architecture across any kind of web project. It reflects **modern 2025-2026 practices** — TailwindCSS v4 as the default utility framework, CSS Cascade Layers for predictable specificity, custom properties for design tokens, container queries for component-driven responsive design, subgrid for nested layouts, and CSS performance optimization.

## Core Philosophy

**CSS should be predictable, maintainable, and performant.** The cascade is powerful but dangerous — without architecture, specificity wars ensue, styles become impossible to override, and bundle size grows unbounded. Use CSS Cascade Layers to control specificity, custom properties for theming, utility classes for speed, and component-scoped styles for encapsulation.

**The #1 rule:** Use TailwindCSS for utilities and rapid development, but don't be afraid to write custom CSS when utilities become unwieldy. The best CSS architecture combines utilities for common patterns with custom CSS for complex components. Don't force everything into utilities — a 40-class Tailwind div is worse than 5 lines of custom CSS.

---

## Part 1: CSS Methodology Selection

### 1.1 Comparison (2025-2026)

| Methodology | Best For | Pros | Cons |
|---|---|---|---|
| **TailwindCSS v4** | Most projects | Rapid dev, consistent design, small bundle | Learning curve, long class lists |
| **CSS Modules** | Component-driven apps | Scoped styles, no naming conflicts | Separate CSS file per component |
| **Vanilla Extract** | Type-safe projects | TypeScript in CSS, zero runtime | Build step required, verbose |
| **CSS-in-JS (Panda)** | Dynamic styling | JS power in CSS, type-safe | Runtime or build step |
| **Plain CSS + Layers** | Simple sites | No dependencies, full control | Manual architecture |
| **SCSS/Sass** | Legacy/complex | Mature, mixins, nesting | No native browser support |

### 1.2 Decision Matrix

| Project Type | Recommended |
|---|---|
| **Next.js / React app** | TailwindCSS v4 + CSS Modules for complex components |
| **Design system** | TailwindCSS v4 + custom CSS with Cascade Layers |
| **Content site / blog** | TailwindCSS v4 or plain CSS with layers |
| **Type-safe app** | TailwindCSS v4 + Vanilla Extract for complex styles |
| **Legacy migration** | TailwindCSS v4 (incremental adoption) |
| **Static site** | TailwindCSS v4 or plain CSS |

---

## Part 2: TailwindCSS v4

### 2.1 Setup (Vite)
```bash
npm install tailwindcss @tailwindcss/vite
```
```typescript
// vite.config.ts
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [tailwindcss()],
});
```
```css
/* src/index.css */
@import "tailwindcss";
```

### 2.2 Theme Configuration (CSS-based)
```css
/* src/index.css */
@import "tailwindcss";

@theme {
  /* Colors */
  --color-brand-50: oklch(0.97 0.02 250);
  --color-brand-500: oklch(0.62 0.19 250);
  --color-brand-900: oklch(0.28 0.09 250);

  /* Typography */
  --font-sans: "Inter", system-ui, sans-serif;
  --font-display: "Fraunces", Georgia, serif;
  --font-mono: "JetBrains Mono", monospace;

  /* Spacing */
  --spacing-unit: 0.25rem;

  /* Breakpoints */
  --breakpoint-xs: 30rem;
  --breakpoint-3xl: 100rem;

  /* Animations */
  --animate-fade-in: fade-in 0.3s ease-out;

  @keyframes fade-in {
    from { opacity: 0; }
    to { opacity: 1; }
  }
}
```

### 2.3 Component Patterns
```tsx
// Button with variants using Tailwind variants or cva
import { cva, type VariantProps } from 'class-variance-authority';

const button = cva(
  'inline-flex items-center justify-center rounded-lg font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 disabled:opacity-50 disabled:pointer-events-none',
  {
    variants: {
      variant: {
        primary: 'bg-brand-500 text-white hover:bg-brand-600',
        secondary: 'bg-gray-100 text-gray-900 hover:bg-gray-200',
        outline: 'border border-gray-300 text-gray-900 hover:bg-gray-50',
        ghost: 'text-gray-700 hover:bg-gray-100',
        destructive: 'bg-red-500 text-white hover:bg-red-600',
      },
      size: {
        sm: 'h-8 px-3 text-sm',
        md: 'h-10 px-4 text-sm',
        lg: 'h-12 px-6 text-base',
        icon: 'h-10 w-10',
      },
    },
    defaultVariants: {
      variant: 'primary',
      size: 'md',
    },
  }
);

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement>, VariantProps<typeof button> {}

function Button({ className, variant, size, ...props }: ButtonProps) {
  return <button className={button({ variant, size, className })} {...props} />;
}
```

---

## Part 3: CSS Cascade Layers

### 3.1 Why Layers
```css
/* Without layers — specificity wars */
/* Hard to override, unpredictable cascade */

/* With layers — full control */
@layer reset, base, components, utilities;

@layer reset {
  * { margin: 0; padding: 0; box-sizing: border-box; }
}

@layer base {
  body { font-family: var(--font-sans); line-height: 1.6; }
  h1 { font-size: 2rem; font-weight: 700; }
}

@layer components {
  .card { border-radius: 0.75rem; padding: 1.5rem; background: white; }
  .button { display: inline-flex; padding: 0.5rem 1rem; border-radius: 0.5rem; }
}

@layer utilities {
  .text-center { text-align: center; }
  .mt-4 { margin-top: 1rem; }
}
```

### 3.2 Layer Order
- **`reset`** — Normalize/reset styles (lowest priority)
- **`base`** — Element defaults (body, headings, links)
- **`components`** — Component classes (.card, .button)
- **`utilities`** — Utility classes (.mt-4, .text-center) (highest priority)
- **Unlayered styles** — Highest priority (use sparingly)

### 3.3 TailwindCSS v4 + Layers
```css
@import "tailwindcss";

/* Tailwind v4 automatically uses layers */
/* @layer theme, base, components, utilities */

/* Add custom layers */
@layer components {
  .btn-primary {
    @apply bg-brand-500 text-white rounded-lg px-4 py-2 hover:bg-brand-600;
  }
}
```

---

## Part 4: Custom Properties (Design Tokens)

### 4.1 Token System
```css
:root {
  /* Color tokens */
  --color-bg: oklch(1 0 0);
  --color-bg-subtle: oklch(0.97 0 0);
  --color-fg: oklch(0.15 0 0);
  --color-fg-muted: oklch(0.45 0 0);
  --color-border: oklch(0.9 0 0);
  --color-brand: oklch(0.62 0.19 250);
  --color-brand-fg: oklch(1 0 0);

  /* Spacing scale */
  --space-1: 0.25rem;
  --space-2: 0.5rem;
  --space-3: 0.75rem;
  --space-4: 1rem;
  --space-6: 1.5rem;
  --space-8: 2rem;
  --space-12: 3rem;
  --space-16: 4rem;

  /* Typography */
  --font-sans: "Inter", system-ui, sans-serif;
  --font-display: "Fraunces", Georgia, serif;
  --text-xs: 0.75rem;
  --text-sm: 0.875rem;
  --text-base: 1rem;
  --text-lg: 1.125rem;
  --text-xl: 1.25rem;
  --text-2xl: 1.5rem;
  --text-3xl: 1.875rem;
  --text-4xl: 2.25rem;

  /* Radii */
  --radius-sm: 0.375rem;
  --radius-md: 0.5rem;
  --radius-lg: 0.75rem;
  --radius-full: 9999px;

  /* Shadows */
  --shadow-sm: 0 1px 2px oklch(0 0 0 / 0.05);
  --shadow-md: 0 4px 6px oklch(0 0 0 / 0.1);
  --shadow-lg: 0 10px 15px oklch(0 0 0 / 0.1);

  /* Transitions */
  --transition-fast: 150ms ease-out;
  --transition-normal: 250ms ease-out;
  --transition-slow: 400ms ease-out;

  /* Z-index scale */
  --z-base: 0;
  --z-dropdown: 1000;
  --z-sticky: 1100;
  --z-modal: 1200;
  --z-toast: 1300;
}

/* Dark mode */
@media (prefers-color-scheme: dark) {
  :root {
    --color-bg: oklch(0.15 0 0);
    --color-bg-subtle: oklch(0.2 0 0);
    --color-fg: oklch(0.95 0 0);
    --color-fg-muted: oklch(0.65 0 0);
    --color-border: oklch(0.25 0 0);
  }
}

/* Manual dark mode */
[data-theme="dark"] {
  --color-bg: oklch(0.15 0 0);
  --color-bg-subtle: oklch(0.2 0 0);
  --color-fg: oklch(0.95 0 0);
  --color-fg-muted: oklch(0.65 0 0);
  --color-border: oklch(0.25 0 0);
}
```

### 4.2 Using OKLCH
```css
/* OKLCH is the modern color space — perceptually uniform */
/* Better than HSL for consistent lightness across hues */

:root {
  --color-blue-500: oklch(0.62 0.19 250);
  --color-red-500: oklch(0.64 0.22 25);
  --color-green-500: oklch(0.72 0.17 145);
  --color-yellow-500: oklch(0.82 0.16 85);
}

/* Alpha modifications */
.overlay {
  background: oklch(0 0 0 / 0.5);
}
```

---

## Part 5: Container Queries

### 5.1 Why Container Queries
Media queries respond to viewport size. Container queries respond to the parent container size — enabling truly component-driven responsive design. A component can adapt its layout based on where it's placed, not the screen size.

### 5.2 Usage
```css
/* Define a containment context */
.card-container {
  container-type: inline-size;
  container-name: card;
}

/* Query the container size */
@container card (min-width: 400px) {
  .card {
    display: grid;
    grid-template-columns: 200px 1fr;
    gap: 1.5rem;
  }
}

@container card (max-width: 399px) {
  .card {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
}
```

### 5.3 Container Query Units
```css
.sidebar {
  /* cqw = container query width */
  font-size: clamp(0.875rem, 3cqw, 1.125rem);

  /* cqh = container query height */
  padding: 2cqh 4cqw;
}
```

### 5.4 TailwindCSS Container Queries
```tsx
// @tailwindcss/container-queries plugin
<div className="@container">
  <div className="@sm:grid @sm:grid-cols-2 @lg:grid-cols-3">
    Content adapts to container size, not viewport
  </div>
</div>
```

---

## Part 6: Modern CSS Features

### 6.1 Subgrid
```css
/* Parent grid */
.grid {
  display: grid;
  grid-template-columns: 200px 1fr 100px;
  gap: 1rem;
}

/* Child inherits parent grid tracks */
.grid-item {
  display: grid;
  grid-template-columns: subgrid; /* Uses parent's columns */
  grid-column: span 3;
}
```

### 6.2 :has() Selector (Parent Selector)
```css
/* Style parent based on child state */
form:has(input[type="checkbox"]:checked) {
  background: var(--color-bg-subtle);
}

/* Card with image vs without */
.card:has(img) {
  padding: 0;
}
.card:not(:has(img)) {
  padding: 1.5rem;
}

/* Show error when field has error */
.field:has(.error-message) {
  border-color: var(--color-red-500);
}
```

### 6.3 Nesting (Native)
```css
.card {
  padding: 1.5rem;
  border-radius: var(--radius-lg);

  & .title {
    font-size: var(--text-xl);
    font-weight: 700;
  }

  &:hover {
    box-shadow: var(--shadow-lg);
  }

  & > p {
    color: var(--color-fg-muted);
  }
}
```

### 6.4 Logical Properties
```css
/* Use logical properties for RTL/i18n support */
.card {
  padding-inline: 1.5rem;  /* padding-left + padding-right */
  padding-block: 1rem;      /* padding-top + padding-bottom */
  margin-inline-start: 1rem; /* margin-left in LTR, margin-right in RTL */
  inset-inline-start: 0;    /* left in LTR, right in RTL */
  text-align: start;        /* left in LTR, right in RTL */
}
```

### 6.5 text-wrap: Balance
```css
/* Balance text across lines — no orphans */
h1, h2, h3 {
  text-wrap: balance;
}

/* Pretty wrap for paragraphs — avoid orphans on last line */
p {
  text-wrap: pretty;
}
```

---

## Part 7: Dark Mode

### 7.1 Strategy
```css
/* 1. Use custom properties for all colors */
:root {
  --color-bg: oklch(1 0 0);
  --color-fg: oklch(0.15 0 0);
}

/* 2. Override in dark mode */
@media (prefers-color-scheme: dark) {
  :root {
    --color-bg: oklch(0.15 0 0);
    --color-fg: oklch(0.95 0 0);
  }
}

/* 3. Manual toggle with data attribute */
[data-theme="dark"] {
  --color-bg: oklch(0.15 0 0);
  --color-fg: oklch(0.95 0 0);
}
```

### 7.2 Dark Mode Toggle
```tsx
function ThemeToggle() {
  const [theme, setTheme] = useState<'light' | 'dark'>('light');

  useEffect(() => {
    const saved = localStorage.getItem('theme') as 'light' | 'dark' | null;
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const initial = saved || (prefersDark ? 'dark' : 'light');
    setTheme(initial);
    document.documentElement.dataset.theme = initial;
  }, []);

  const toggle = () => {
    const next = theme === 'light' ? 'dark' : 'light';
    setTheme(next);
    document.documentElement.dataset.theme = next;
    localStorage.setItem('theme', next);
  };

  return <button onClick={toggle}>{theme === 'light' ? '🌙' : '☀️'}</button>;
}
```

### 7.3 Prevent Flash of Wrong Theme
```html
<!-- In <head> — before CSS loads -->
<script>
  const theme = localStorage.getItem('theme') ||
    (matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
  document.documentElement.dataset.theme = theme;
</script>
```

---

## Part 8: CSS Performance

### 8.1 Critical CSS
```html
<!-- Inline critical above-the-fold CSS -->
<style>
  /* Only styles needed for first paint */
  body { font-family: var(--font-sans); }
  .hero { min-height: 100vh; display: grid; place-items: center; }
</style>

<!-- Load rest asynchronously -->
<link rel="preload" href="/styles.css" as="style" onload="this.rel='stylesheet'">
```

### 8.2 CSS Containment
```css
/* Isolate component rendering — performance boost */
.card {
  contain: layout style paint;
}

/* Content-visibility — skip rendering off-screen elements */
.long-list-item {
  content-visibility: auto;
  contain-intrinsic-size: 200px;
}
```

### 8.3 Will-Change (Use Sparingly)
```css
/* Only add will-change right before animation, remove after */
.modal {
  will-change: transform, opacity;
  transition: transform 200ms, opacity 200ms;
}

/* Don't overuse — it consumes GPU memory */
```

### 8.4 Font Performance
```css
@font-face {
  font-family: "Inter";
  src: url("/fonts/inter-var.woff2") format("woff2");
  font-weight: 100 900;
  font-display: swap; /* Show fallback immediately, swap when loaded */
  unicode-range: U+0000-00FF; /* Subset for Latin */
}
```

---

## Part 9: CSS Modules

### 9.1 When to Use
- Complex components where Tailwind utilities become unwieldy
- When you need scoped styles (no class name collisions)
- For styles that need JavaScript interactivity (CSS variables in JS)

### 9.2 Usage
```tsx
/* Button.module.css */
.button {
  display: inline-flex;
  align-items: center;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-md);
  font-weight: 500;
  transition: background var(--transition-fast);
}

.primary {
  background: var(--color-brand);
  color: var(--color-brand-fg);
}

.primary:hover {
  background: color-mix(in oklch, var(--color-brand) 90%, black);
}
```

```tsx
import styles from './Button.module.css';

function Button({ variant = 'primary' }) {
  return (
    <button className={`${styles.button} ${styles[variant]}`}>
      Click me
    </button>
  );
}
```

---

## Part 10: Anti-Patterns

### 10.1 Don't Use IDs for Styling
```css
/* BAD — IDs have high specificity, hard to override */
#header { background: blue; }

/* GOOD — use classes */
.header { background: blue; }
```

### 10.2 Don't Use `!important`
```css
/* BAD — nuclear option, creates arms race */
.text-red { color: red !important; }

/* GOOD — use cascade layers or increase specificity properly */
@layer utilities {
  .text-red { color: red; }
}
```

### 10.3 Don't Inline Styles
```tsx
/* BAD — no caching, no media queries, no pseudo-classes */
<div style={{ display: 'flex', gap: '1rem' }}>

/* GOOD — use classes */
<div className="flex gap-4">
```

### 10.4 Don't Use Magic Numbers
```css
/* BAD — why 37px? */
.margin { margin-top: 37px; }

/* GOOD — use design tokens */
.margin { margin-top: var(--space-8); }
```

---

## Execution Instructions for Cascade

When this skill is activated for CSS architecture:

1. **Read the project context** — framework, existing CSS approach, design system needs
2. **Choose CSS methodology** — TailwindCSS v4 (default), CSS Modules (complex components), Vanilla Extract (type-safe)
3. **Set up TailwindCSS v4** — install, configure theme in CSS, set up Vite plugin
4. **Define design tokens** — colors (OKLCH), spacing, typography, radii, shadows, transitions
5. **Set up Cascade Layers** — reset, base, components, utilities — for predictable specificity
6. **Implement dark mode** — custom properties, `prefers-color-scheme`, manual toggle, prevent flash
7. **Use container queries** — for component-driven responsive design
8. **Use modern CSS features** — subgrid, `:has()`, nesting, logical properties, text-wrap
9. **Set up component patterns** — `cva` for variants, CSS Modules for complex components
10. **Optimize performance** — critical CSS, containment, font-display: swap, content-visibility
11. **Avoid anti-patterns** — no IDs for styling, no `!important`, no inline styles, no magic numbers
12. **Document** — token system, dark mode strategy, component patterns, CSS conventions
