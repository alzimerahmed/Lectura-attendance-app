---
name: playwright-design-clone
description: Clone a website URL or design screenshot at high fidelity using Playwright MCP — survey, extract computed styles, blueprint sections, build, and visual-verify until match
---

# Playwright Design Clone Workflow

**Activation**: `/playwright-design-clone`, `/design-clone`, "clone this site", "copy this design", "pixel perfect clone", "recreate this screenshot", "100% clone"

**Skill**: `playwright-design-clone.md`  
**MCP**: Playwright browser tools (`browser_navigate`, `browser_snapshot`, `browser_take_screenshot`, `browser_evaluate`, `browser_click`, `browser_hover`, `browser_resize`, `browser_network_requests`, etc.)

## Legal / Scope Guard (always first)

1. Only clone **public** pages the user has rights to reference, or **user-owned** designs / screenshots they provide.
2. Do **not** bypass auth, captchas, paywalls, or DRM.
3. Prefer design recreation for learning / redesign / owned products — not wholesale theft of third-party brands.
4. State fidelity limits honestly (complex WebGL, authenticated apps, heavy SPAs).

---

## Mode A — Live URL Clone

### Step 1: Preconditions

1. Confirm Playwright MCP is available (navigate + snapshot + screenshot + evaluate).
2. Confirm target stack for output (default: match existing project; else React/Vite + Tailwind or Next.js + Tailwind).
3. Create working dirs:
   - `docs/design-clone/`
   - `docs/design-clone/screenshots/`
   - `docs/design-clone/assets/`
   - `docs/design-clone/specs/`

### Step 2: Recon (Playwright)

1. `browser_navigate` → target URL.
2. Wait for network idle / key content.
3. Slow full-page scroll top → bottom to trigger lazy load, sticky headers, scroll animations.
4. Capture **full-page** and **above-fold** screenshots at:
   - Desktop **1440** (or 1280)
   - Tablet **768**
   - Mobile **390**
5. `browser_snapshot` for accessibility tree / structure.
6. Hover primary CTAs, cards, nav links; click tabs/accordions if present; note state changes.
7. Save findings to `docs/design-clone/BEHAVIORS.md`.

// turbo
### Step 3: Extract Evidence (Playwright evaluate)

Use `browser_evaluate` to collect:

1. **Document meta** — title, description, theme-color, favicon URLs
2. **Fonts** — `document.fonts`, link stylesheets, `@font-face` families/weights
3. **CSS variables** — `:root` / `html` computed custom properties
4. **Color / type / spacing samples** — from key elements via `getComputedStyle`
5. **Section map** — top-level landmarks (`header`, `main` sections, `footer`) with bounding boxes
6. **Asset inventory** — `img`, `video`, `source`, CSS `background-image`, SVG icons
7. **Motion hints** — transition/animation properties on interactive nodes; scroll libraries if detectable

Write:

- `docs/design-clone/site-manifest.json` (structure + assets + meta)
- `docs/design-clone/styles.json` (tokens + per-section computed styles)
- `docs/design-clone/PAGE_TOPOLOGY.md` (sections top-to-bottom)

### Step 4: Blueprint (no code yet)

For each section produce `docs/design-clone/specs/<section>.spec.md`:

- DOM hierarchy (semantic)
- Exact layout (flex/grid, gaps, max-width, padding)
- Typography (family, size, weight, line-height, letter-spacing, color)
- Colors / borders / shadows / radii
- Assets + alt text
- Interactive states (hover/focus/active/open)
- Responsive rules at 1440 / 768 / 390
- Verbatim copy

Also write global tokens into CSS variables / Tailwind theme **before** components.

### Step 5: Foundation

1. Install fonts (local or Google/Fontshare) matching extracted families.
2. Define design tokens from `styles.json` (exact px/rem/hex — no guessing).
3. Download/mirror assets into `docs/design-clone/assets/` or project `public/`.
4. Scaffold layout shell (nav + main + footer) only after tokens exist.

### Step 6: Build Section-by-Section

1. Build one section at a time from its spec + reference screenshots.
2. Use **exact** values from computed styles; map to Tailwind only when equivalent is exact (or use arbitrary values / CSS vars).
3. Preserve real content and assets — no placeholder lorem unless original is placeholder.
4. Implement hover/focus/scroll behaviors documented in BEHAVIORS.md.
5. After each major section: screenshot clone at same viewport as reference.

### Step 7: Visual Verify Loop

1. Run clone locally; open with Playwright.
2. Screenshot same viewports as reference.
3. Compare side-by-side (and pixel diff mentally / via tools if available):
   - Layout alignment
   - Spacing rhythm
   - Type scale
   - Color fidelity
   - Component density
   - Sticky/nav behavior
4. Fix surgically; re-screenshot.
5. Stop when desktop + mobile are visually matched for in-scope sections (target: **near 100% for static layout/type/color**; note residual gaps for canvas/WebGL/auth).

### Step 8: Report

Deliver:

- Clone path + how to run
- Fidelity notes (what matched / what could not)
- Asset + token locations
- Remaining diffs list (if any)

---

## Mode B — Screenshot / Design File Clone

When input is an image (Figma export, mockup, screenshot) instead of a URL:

1. Read image with the file viewer (vision).
2. Infer layout grid, type hierarchy, palette, components, spacing rhythm.
3. Write `docs/design-clone/PAGE_TOPOLOGY.md` + section specs from vision (mark values as **inferred** when not measurable).
4. Build foundation tokens from inferred palette/type.
5. Implement section-by-section.
6. Screenshot the implementation; compare to the provided design image.
7. Iterate until match.

**Note:** Screenshot mode cannot extract true `getComputedStyle` — be explicit about inferred vs measured values. Prefer URL mode when a live page exists.

---

## Playwright MCP Tool Map

| Goal | Tools |
|------|--------|
| Open page | `browser_navigate` |
| Structure | `browser_snapshot` |
| Visual truth | `browser_take_screenshot` (fullPage when needed) |
| Computed CSS / DOM walk | `browser_evaluate` |
| Interactions | `browser_click`, `browser_hover`, `browser_type`, `browser_press_key` |
| Responsive | `browser_resize` |
| Assets / network | `browser_network_requests`, `browser_network_request` |
| Wait | `browser_wait_for` |

---

## Completion Checklist

- [ ] Legal/scope confirmed
- [ ] Multi-viewport reference screenshots saved
- [ ] Topology + specs written before bulk coding
- [ ] Tokens applied globally first
- [ ] Assets mirrored locally
- [ ] Sections built from specs, not vibes
- [ ] Clone screenshots compared to reference
- [ ] Residual gaps documented honestly

## Invoke

```
/playwright-design-clone https://example.com
/design-clone https://example.com
clone this design: https://example.com
recreate this screenshot 100% (attach image)
```
