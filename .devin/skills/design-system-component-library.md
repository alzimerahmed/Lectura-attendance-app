---
name: Design System & Component Library Skill
description: Comprehensive methodology for building and maintaining design systems and component libraries — 2025-2026 practices with design tokens, headless components, accessibility-first patterns, and multi-framework support
version: 1.0.0
tags: [design-system, component-library, design-tokens, ui-components, accessibility, headless-ui, tailwind, shadcn]
---

# Design System & Component Library Skill

## Purpose
This skill provides a comprehensive methodology for building and maintaining design systems and component libraries across any kind of web project. It reflects **modern 2025-2026 practices** — design tokens as the source of truth, headless UI patterns, accessibility-first components, Tailwind CSS + shadcn/ui patterns, and multi-framework support. Not just a collection of components but a system that scales.

## Core Philosophy

**A design system is a shared language, not just a component library.** It's the single source of truth for how a product looks and behaves. It includes tokens, components, patterns, documentation, and governance. Without all of these, it's just a folder of components.

**The #1 rule:** Compose, don't configure. Components should be composable building blocks, not configuration objects. `<Card><Card.Header><Card.Title>Title</Card.Title></Card.Header><Card.Body>Content</Card.Body></Card>` not `<Card title="Title" body="Content" />`.

---

## Part 1: Design Tokens

### 1.1 Token Architecture
```
Global Tokens (raw values)
  → Semantic Tokens (purpose-based)
    → Component Tokens (component-specific)
```

```css
/* Global tokens — raw values */
--color-blue-500: #3b82f6;
--color-blue-600: #2563eb;
--space-4: 1rem;
--space-6: 1.5rem;
--radius-md: 0.5rem;
--font-sans: 'Inter', system-ui, sans-serif;

/* Semantic tokens — purpose-based */
--color-primary: var(--color-blue-600);
--color-primary-hover: var(--color-blue-500);
--color-background: white;
--color-foreground: var(--color-gray-900);
--color-muted: var(--color-gray-500);
--color-border: var(--color-gray-200);
--spacing-page: var(--space-6);
--radius-card: var(--radius-md);

/* Component tokens — component-specific */
--button-bg-primary: var(--color-primary);
--button-bg-primary-hover: var(--color-primary-hover);
--button-radius: var(--radius-md);
--card-padding: var(--space-6);
--card-radius: var(--radius-card);
```

### 1.2 Color Tokens
- **Primitive palette:** Full color scale (50-950) for each hue
- **Semantic mapping:** primary, secondary, accent, destructive, success, warning, info
- **Surface tokens:** background, foreground, card, popover, muted
- **Dark mode:** Separate semantic mappings for dark theme
- **Contrast:** Verify all semantic tokens meet WCAG contrast requirements

### 1.3 Typography Tokens
```css
/* Font families */
--font-sans: 'Inter', system-ui, sans-serif;
--font-serif: 'Lora', Georgia, serif;
--font-mono: 'JetBrains Mono', monospace;

/* Font sizes — modular scale */
--text-xs: 0.75rem;    /* 12px */
--text-sm: 0.875rem;   /* 14px */
--text-base: 1rem;     /* 16px */
--text-lg: 1.125rem;   /* 18px */
--text-xl: 1.25rem;    /* 20px */
--text-2xl: 1.5rem;    /* 24px */
--text-3xl: 1.875rem;  /* 30px */
--text-4xl: 2.25rem;   /* 36px */

/* Line heights */
--leading-none: 1;
--leading-tight: 1.25;
--leading-normal: 1.5;
--leading-relaxed: 1.625;

/* Font weights */
--weight-normal: 400;
--weight-medium: 500;
--weight-semibold: 600;
--weight-bold: 700;
```

### 1.4 Spacing Tokens
```css
/* Spacing scale — base unit 0.25rem (4px) */
--space-0: 0;
--space-1: 0.25rem;   /* 4px */
--space-2: 0.5rem;    /* 8px */
--space-3: 0.75rem;   /* 12px */
--space-4: 1rem;      /* 16px */
--space-6: 1.5rem;    /* 24px */
--space-8: 2rem;      /* 32px */
--space-12: 3rem;     /* 48px */
--space-16: 4rem;     /* 64px */
--space-24: 6rem;     /* 96px */
```

### 1.5 Border Radius Tokens
```css
--radius-sm: 0.25rem;   /* 4px */
--radius-md: 0.5rem;    /* 8px */
--radius-lg: 0.75rem;   /* 12px */
--radius-xl: 1rem;      /* 16px */
--radius-2xl: 1.5rem;   /* 24px */
--radius-full: 9999px;
```

### 1.6 Shadow Tokens
```css
--shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
--shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1);
--shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1);
--shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1);
```

### 1.7 Animation Tokens
```css
--duration-fast: 150ms;
--duration-normal: 250ms;
--duration-slow: 400ms;
--ease-default: cubic-bezier(0.4, 0, 0.2, 1);
--ease-in: cubic-bezier(0.4, 0, 1, 1);
--ease-out: cubic-bezier(0, 0, 0.2, 1);
--ease-spring: cubic-bezier(0.34, 1.56, 0.64, 1);
```

### 1.8 z-index Tokens
```css
--z-base: 0;
--z-dropdown: 1000;
--z-sticky: 1100;
--z-overlay: 1200;
--z-modal: 1300;
--z-popover: 1400;
--z-toast: 1500;
--z-tooltip: 1600;
```

### 1.9 Token Management with Style Dictionary
```json
// tokens.json
{
  "color": {
    "blue": {
      "500": { "value": "#3b82f6", "type": "color" },
      "600": { "value": "#2563eb", "type": "color" }
    }
  }
}
```
- **Style Dictionary:** Transform tokens to CSS variables, Tailwind config, iOS, Android
- **Single source:** Define once, export everywhere
- **Versioning:** Version your tokens for backward compatibility
- **Documentation:** Document each token's purpose and usage

---

## Part 2: Component Architecture

### 2.1 Headless UI Patterns (Radix, Headless UI, Ark UI)
- **What:** Components with all behavior, accessibility, and keyboard interaction built-in — but no styling
- **Why:** Full control over appearance, guaranteed accessibility, consistent behavior
- **Libraries:** Radix UI, Headless UI (Tailwind Labs), Ark UI, React Aria
- **Pattern:** `<Dialog.Root><Dialog.Trigger>Open</Dialog.Trigger><Dialog.Content>...</Dialog.Content></Dialog.Root>`

### 2.2 Compound Components
```tsx
// Compound component pattern
const Card = ({ children, className }) => (
  <div className={cn("rounded-lg border p-6", className)}>{children}</div>
);

Card.Header = ({ children }) => <div className="mb-4">{children}</div>;
Card.Title = ({ children }) => <h3 className="text-lg font-semibold">{children}</h3>;
Card.Description = ({ children }) => <p className="text-sm text-muted">{children}</p>;
Card.Body = ({ children }) => <div>{children}</div>;
Card.Footer = ({ children }) => <div className="mt-4 flex justify-end gap-2">{children}</div>;

// Usage — composable, flexible
<Card>
  <Card.Header>
    <Card.Title>Project Alpha</Card.Title>
    <Card.Description>Created 3 days ago</Card.Description>
  </Card.Header>
  <Card.Body>Content here</Card.Body>
  <Card.Footer>
    <Button variant="ghost">Cancel</Button>
    <Button>Save</Button>
  </Card.Footer>
</Card>
```

### 2.3 Slot Pattern (Radix)
```tsx
// Slot allows wrapping without extra DOM element
import { Slot } from '@radix-ui/react-slot';

const Button = ({ asChild, children, ...props }) => {
  const Comp = asChild ? Slot : 'button';
  return <Comp {...props}>{children}</Comp>;
};

// Usage — renders as anchor, not button inside anchor
<Button asChild>
  <a href="/about">About</a>
</Button>
```

### 2.4 Polymorphic Components
```tsx
// Component that can render as different elements
type ButtonProps<C extends React.ElementType> = {
  as?: C;
  children: React.ReactNode;
} & React.ComponentPropsWithoutRef<C>;

const Button = <C extends React.ElementType = 'button'>({
  as,
  children,
  ...props
}: ButtonProps<C>) => {
  const Comp = as || 'button';
  return <Comp {...props}>{children}</Comp>;
};
```

### 2.5 Component Composition Over Configuration
- **Good:** `<Card><Card.Header><Card.Title>Title</Card.Title></Card.Header></Card>`
- **Bad:** `<Card title="Title" showHeader={true} headerVariant="bold" />`
- **Why:** Composition is flexible, configuration is rigid
- **Why:** Composition lets consumers control layout, configuration doesn't
- **Exception:** Simple components (Button, Badge) can use props for variants

### 2.6 Variant Management (cva / class-variance-authority)
```tsx
import { cva } from 'class-variance-authority';

const buttonVariants = cva(
  "inline-flex items-center justify-center rounded-md font-medium transition-colors",
  {
    variants: {
      variant: {
        default: "bg-primary text-primary-foreground hover:bg-primary/90",
        destructive: "bg-destructive text-destructive-foreground hover:bg-destructive/90",
        outline: "border border-input bg-background hover:bg-accent",
        ghost: "hover:bg-accent hover:text-accent-foreground",
        link: "text-primary underline-offset-4 hover:underline",
      },
      size: {
        default: "h-10 px-4 py-2",
        sm: "h-9 rounded-md px-3",
        lg: "h-11 rounded-md px-8",
        icon: "h-10 w-10",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  }
);
```

### 2.7 Tailwind CSS + shadcn/ui Patterns
- **shadcn/ui:** Copy-paste components built on Radix UI + Tailwind CSS
- **Not a package:** You own the components — copy them into your project
- **Customizable:** Modify the source code directly — no overriding required
- **Consistent:** All components use the same design tokens
- **Pattern:** `npx shadcn@latest add button dialog card`

---

## Part 3: Core Components

### 3.1 Button
- **Variants:** default, destructive, outline, ghost, link
- **Sizes:** default, sm, lg, icon
- **States:** default, hover, focus-visible, active, disabled, loading
- **Accessibility:** `aria-disabled` for loading, `aria-pressed` for toggle, focus-visible ring
- **asChild:** Support rendering as anchor or other elements
- **Loading state:** Show spinner, disable interaction, keep text for layout stability

### 3.2 Input / Form Controls
- **Input:** text, email, password, number, search, tel, url
- **Textarea:** auto-resize option, character count
- **Select:** native vs custom (Radix Select for custom)
- **Checkbox:** indeterminate state, custom styling
- **Radio:** radio group with keyboard navigation
- **Switch:** toggle with `role="switch"`, `aria-checked`
- **Slider:** `role="slider"`, keyboard accessible, ARIA values
- **Date picker:** calendar with keyboard navigation, date range support
- **All:** labels, helper text, error states, required indicators, autocomplete

### 3.3 Dialog / Modal
- **Radix Dialog:** Focus trap, Escape to close, scroll lock, portal
- **Accessibility:** `aria-modal="true"`, `aria-labelledby`, `aria-describedby`
- **Focus management:** Focus first element on open, return focus on close
- **Sizes:** sm, md, lg, fullscreen
- **Patterns:** confirmation dialog, form dialog, side sheet

### 3.4 Dropdown Menu
- **Radix Dropdown Menu:** Keyboard navigation (arrows, Enter, Escape)
- **Items:** default, destructive, separator, label, checkbox, radio group
- **Alignment:** start, end
- **Side:** top, bottom, left, right
- **Accessibility:** `role="menu"`, `role="menuitem"`, `aria-haspopup`

### 3.5 Tooltip
- **Radix Tooltip:** Delay, skip delay, side, alignment
- **Accessibility:** `aria-describedby` or Radix's tooltip implementation
- **Content:** Short, helpful text — not critical information
- **Trigger:** Hover (desktop), long-press (mobile) — don't rely on hover for mobile

### 3.6 Toast / Notification
- **Position:** top-right, top-center, bottom-right, bottom-center
- **Types:** default, success, destructive, warning
- **Auto-dismiss:** 5 seconds (configurable), pause on hover
- **Accessibility:** `role="status"` for non-urgent, `role="alert"` for urgent
- **Action:** Optional action button (e.g., "Undo")
- **Stacking:** Multiple toasts stack with animation

### 3.7 Tabs
- **Radix Tabs:** Keyboard navigation (arrows, Home, End)
- **Accessibility:** `role="tablist"`, `role="tab"`, `role="tabpanel"`, `aria-selected`, `aria-controls`
- **Orientation:** horizontal, vertical
- **Activation:** automatic (on focus) or manual (on Enter/Space)

### 3.8 Accordion
- **Radix Accordion:** Keyboard navigation (arrows, Home, End)
- **Types:** single (only one open) or multiple (any can be open)
- **Accessibility:** `role="accordion"`, `aria-expanded`, `aria-controls`
- **Animation:** Smooth height animation with CSS

### 3.9 Table / Data Table
- **Structure:** `<table>`, `<thead>`, `<tbody>`, `<th scope>`, `<caption>`
- **Features:** sorting, filtering, pagination, row selection
- **Accessibility:** `aria-sort`, keyboard navigation, screen reader announcements
- **Responsive:** horizontal scroll or card layout on mobile
- **Performance:** virtualization for large datasets

### 3.10 Combobox / Autocomplete
- **Radix Combobox:** Input + popover with options
- **Features:** filtering, multi-select, creatable, async options
- **Accessibility:** `role="combobox"`, `aria-expanded`, `aria-activedescendant`
- **Keyboard:** Type to filter, arrows to navigate, Enter to select, Escape to close

### 3.11 Popover
- **Radix Popover:** Content that floats near a trigger
- **Use cases:** color picker, date picker, form in a popover
- **Accessibility:** `aria-haspopup`, focus management
- **Positioning:** auto-placement, collision detection

### 3.12 Navigation Components
- **Breadcrumbs:** `nav[aria-label="Breadcrumb"]`, `ol`, `li`, `aria-current="page"`
- **Pagination:** `nav[aria-label="Pagination"]`, keyboard accessible, `aria-current="page"`
- **Command palette:** Cmd+K, keyboard navigation, fuzzy search
- **Sidebar:** Collapsible, keyboard accessible, active state

---

## Part 4: Accessibility-First Components

### 4.1 ARIA Patterns by Component

| Component | ARIA | Keyboard |
|---|---|---|
| **Dialog** | `aria-modal`, `aria-labelledby` | Escape to close, focus trap |
| **Dropdown** | `role="menu"`, `role="menuitem"` | Arrows, Enter, Escape |
| **Tabs** | `role="tablist"`, `role="tab"`, `aria-selected` | Arrows, Home, End |
| **Accordion** | `aria-expanded`, `aria-controls` | Enter/Space, Arrows |
| **Tooltip** | `aria-describedby` | Hover, focus |
| **Toast** | `role="status"` or `role="alert"` | — |
| **Combobox** | `role="combobox"`, `aria-expanded` | Type, arrows, Enter, Escape |
| **Slider** | `role="slider"`, `aria-valuenow` | Arrows, Home, End |
| **Switch** | `role="switch"`, `aria-checked` | Enter/Space |
| **Tree** | `role="tree"`, `role="treeitem"` | Arrows, Enter |
| **Menu bar** | `role="menubar"`, `role="menuitem"` | Arrows, Enter, Escape |

### 4.2 Focus Management
- **Dialog open:** Move focus to first focusable element or dialog title
- **Dialog close:** Return focus to triggering element
- **Route change (SPA):** Move focus to h1 or main content
- **Dynamic content:** Use `aria-live` to announce changes
- **Hidden content:** `aria-hidden="true"` or `inert` attribute
- **Focus trap:** In modals — Tab cycles within dialog

### 4.3 Keyboard Navigation Implementation
- **Enter/Space:** Activate buttons, links, menu items
- **Escape:** Close dialogs, menus, popovers
- **Arrow keys:** Navigate within composite widgets (tabs, menus, lists)
- **Home/End:** Move to first/last item in composite widgets
- **Tab:** Move between focusable elements in DOM order
- **Shift+Tab:** Move backwards

### 4.4 Screen Reader Announcements
```tsx
// Live region for dynamic updates
<div aria-live="polite" aria-atomic="true" className="sr-only">
  {message}
</div>

// Alert for urgent messages
<div role="alert" className="sr-only">
  {errorMessage}
</div>
```

---

## Part 5: Theming & Dark Mode

### 5.1 CSS Variables for Theming
```css
:root {
  --background: 0 0% 100%;
  --foreground: 222 47% 11%;
  --primary: 222 47% 11%;
  --primary-foreground: 210 40% 98%;
  --muted: 210 40% 96%;
  --muted-foreground: 215 16% 47%;
  --border: 214 32% 91%;
  --radius: 0.5rem;
}

.dark {
  --background: 222 47% 11%;
  --foreground: 210 40% 98%;
  --primary: 210 40% 98%;
  --primary-foreground: 222 47% 11%;
  --muted: 217 33% 17%;
  --muted-foreground: 215 20% 65%;
  --border: 217 33% 20%;
}
```

### 5.2 Theme Switching
```tsx
// Theme provider
const [theme, setTheme] = useState<'light' | 'dark' | 'system'>('system');

useEffect(() => {
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
  const handleChange = () => {
    if (theme === 'system') {
      document.documentElement.classList.toggle('dark', mediaQuery.matches);
    }
  };
  mediaQuery.addEventListener('change', handleChange);
  return () => mediaQuery.removeEventListener('change', handleChange);
}, [theme]);
```

### 5.3 System Preference Detection
- **`prefers-color-scheme`:** `light` or `dark` — user's OS preference
- **`prefers-contrast`:** `more` — user wants higher contrast
- **`prefers-reduced-motion`:** `reduce` — user wants less animation
- **Respect preferences:** Don't override user's system preferences

### 5.4 Theme Persistence
- **localStorage:** Store user's theme preference
- **No flash:** Set theme class before React hydration (inline script in `<head>`)
- **SSR-safe:** Use `class:` on `<html>` element, not client-only state

### 5.5 Brand Theming Support
- **CSS variables:** Allow overriding all semantic tokens
- **Theme prop:** `<ThemeProvider theme={brandTheme}>`
- **Runtime switching:** Change CSS variables at runtime
- **Multi-brand:** Support multiple brands in one deployment

---

## Part 6: Documentation

### 6.1 Storybook Setup
```bash
npx storybook@latest init
```
- **Stories:** One `.stories.tsx` file per component
- **Controls:** Interactive props for testing
- **Docs:** Auto-generated from component props and stories
- **Addons:** Accessibility, viewport, themes, interactions

### 6.2 Component Documentation
```tsx
/**
 * Button component for triggering actions.
 *
 * @example
 * <Button variant="default" size="lg">Click me</Button>
 * <Button variant="outline" asChild><a href="/">Home</a></Button>
 */
```
- **Purpose:** What the component is for
- **When to use:** Appropriate use cases
- **When not to use:** Anti-patterns
- **Props:** All props with types, defaults, descriptions
- **Examples:** Common usage patterns
- **Accessibility:** ARIA attributes, keyboard interaction

### 6.3 Usage Guidelines
- **Do/Don't:** Visual examples of correct and incorrect usage
- **Combinations:** How components work together
- **Layout:** Spacing, alignment, grouping patterns
- **Content:** What text to use, how to label
- **Responsive:** How component adapts to different screen sizes

### 6.4 Design Tokens Documentation
- **Name:** Token name and CSS variable
- **Value:** Current value
- **Usage:** Where and when to use
- **Dark mode:** Value in dark theme
- **Examples:** Visual swatches, spacing examples

### 6.5 Live Playground / Sandbox
- **Interactive:** Users can modify props and see results
- **Code export:** Generate code from the playground
- **Copy-paste:** Easy to copy component code
- **Themes:** Switch between light/dark in the playground

---

## Part 7: Testing Components

### 7.1 Visual Regression Testing
- **Chromatic:** Cloud-based visual testing for Storybook
- **Percy:** Visual regression for Storybook and E2E
- **Playwright screenshots:** `expect(page).toHaveScreenshot()`
- **Review:** Visual diffs reviewed on every PR
- **Stability:** Set dynamic regions to exclude (dates, animations)

### 7.2 Accessibility Testing in Storybook
```tsx
// .storybook/main.ts
addons: ['@storybook/addon-a11y']
```
- **axe-core:** Run axe on every story
- **Violations:** Show in panel, fail in CI
- **Tabs, dialogs, menus:** Test keyboard interaction
- **Screen reader:** Test with NVDA/VoiceOver periodically

### 7.3 Unit Testing Components
```tsx
import { render, screen } from '@testing-library/react';
import { Button } from './Button';

test('renders button with text', () => {
  render(<Button>Click me</Button>);
  expect(screen.getByRole('button', { name: 'Click me' })).toBeInTheDocument();
});

test('calls onClick when clicked', () => {
  const onClick = vi.fn();
  render(<Button onClick={onClick}>Click me</Button>);
  screen.getByRole('button').click();
  expect(onClick).toHaveBeenCalled();
});
```

### 7.4 Interaction Testing
```tsx
import { userEvent } from '@testing-library/user-event';

test('dialog opens and closes', async () => {
  const user = userEvent.setup();
  render(<MyDialog />);

  await user.click(screen.getByText('Open'));
  expect(screen.getByRole('dialog')).toBeInTheDocument();

  await user.keyboard('{Escape}');
  expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
});
```

---

## Part 8: Versioning & Governance

### 8.1 Semantic Versioning
- **Major (1.0.0 → 2.0.0):** Breaking changes — component API changes, token renames
- **Minor (1.0.0 → 1.1.0):** New components, new features, backward compatible
- **Patch (1.0.0 → 1.0.1):** Bug fixes, style adjustments, no new features
- **Changelog:** Document all changes with migration guides for major versions

### 8.2 Deprecation Policy
- **Announce:** Mark deprecated in docs and with console warning
- **Provide alternative:** Show the replacement component or pattern
- **Timeline:** 2 minor versions before removal
- **Document:** Migration guide for each deprecated component

### 8.3 Contribution Guidelines
- **RFC process:** Propose new components or changes via RFC
- **Design review:** Review by design team before implementation
- **Code review:** Review by engineering team
- **Accessibility review:** Verify WCAG compliance
- **Documentation:** Must include docs, stories, and tests

### 8.4 Component Lifecycle
```
Proposed → In Review → Experimental → Stable → Deprecated → Removed
```
- **Proposed:** RFC submitted, under discussion
- **Experimental:** Implemented but may change — opt-in
- **Stable:** Production-ready, backward compatible
- **Deprecated:** Still works but will be removed — don't use in new code
- **Removed:** No longer in the library

---

## Part 9: Multi-Framework & Cross-Platform

### 9.1 Sharing Design Tokens Across Frameworks
- **Style Dictionary:** Transform tokens to CSS, Tailwind, iOS, Android, Figma
- **Single source:** Define tokens once in JSON, export everywhere
- **Versioning:** Version tokens separately from components
- **Sync:** Automate token sync between code and design tools

### 9.2 Web Components (Lit)
- **Framework-agnostic:** Custom elements work in any framework
- **Shadow DOM:** Encapsulated styles — no leakage
- **Use case:** Design system used across React, Vue, Angular, vanilla JS
- **Limitation:** harder to compose, less ergonomic than React components

### 9.3 React vs Vue vs Svelte Component Ports
- **Same tokens:** All frameworks use the same design tokens
- **Same behavior:** Keyboard, accessibility, interaction patterns identical
- **Same naming:** Component names and props are consistent
- **Different implementation:** Each framework uses its idioms

### 9.4 Figma to Code Sync
- **Tokens:** Figma variables → Style Dictionary → CSS variables
- **Figma plugin:** Sync tokens between Figma and code
- **Design lint:** Figma plugin that checks designs against design system
- **Handoff:** Designers use tokens, developers use same tokens

---

## Execution Instructions for Cascade

When this skill is activated for design system work:

1. **Read the project context** — framework, existing components, styling approach
2. **Define design tokens** — colors, typography, spacing, radius, shadows, animations, z-index
3. **Set up token architecture** — global → semantic → component tokens
4. **Choose component foundation** — Radix UI, Headless UI, Ark UI, or React Aria
5. **Build core components** — Button, Input, Dialog, Dropdown, Toast, Tabs, Accordion, Table, Combobox
6. **Use compound component pattern** — composable, not configurable
7. **Use cva for variants** — variant + size matrices with type safety
8. **Ensure accessibility** — ARIA, keyboard navigation, focus management, screen reader support
9. **Set up theming** — CSS variables, dark mode, system preference, brand theming
10. **Set up Storybook** — stories, controls, docs, a11y addon, visual regression
11. **Write tests** — unit tests, interaction tests, visual regression, accessibility tests
12. **Document everything** — usage guidelines, do/don't, examples, props, accessibility
13. **Set up versioning** — semantic versioning, changelog, deprecation policy
14. **Multi-framework** — share tokens across frameworks, Figma sync
