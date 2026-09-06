---
name: Playwright Design Clone Skill
description: High-fidelity website and screenshot design cloning using Playwright MCP — survey, extract computed styles and assets, blueprint sections, build, and visual-verify until near pixel match
version: 1.0.0
tags: [playwright, design-clone, pixel-perfect, browser-automation, ui-recreation, mcp, reverse-engineering]
---

# Playwright Design Clone Skill

## Purpose

Teach the agent to **clone a live website URL or a design screenshot** into production UI code at the highest practical fidelity, using **Playwright MCP** for seeing, measuring, interacting, and verifying — not guessing from memory.

Inspired by open methodologies (One-Click-Clone, web-clone skills, visual QA loops): **recon → extract → blueprint → foundation → build → verify → iterate**.

## Core Philosophy

1. **Measure, don't invent** — Prefer `getComputedStyle`, DOM geometry, and screenshots over "looks like ~16px".
2. **Evidence before code** — Specs and tokens before components.
3. **Section contracts** — Clone one section at a time with a written spec.
4. **Visual loop closes the gap** — First pass is never final; screenshot compare until match.
5. **Honest fidelity** — Static marketing pages can approach ~100% layout/type/color; WebGL, auth walls, and heavy SPAs have hard limits.
6. **Legal boundaries** — Public/owned references only; no auth bypass.

---

## Part 1: When to Use

### Use this skill when the user asks to:

- Clone / copy / recreate a website design
- Pixel-perfect or 100% match a URL
- Rebuild a landing page to look like a reference
- Recreate UI from a screenshot, Figma export, or mockup
- Extract design tokens from a live site for a rebuild

### Do **not** use for:

- Generic "make it pretty" (use Claude Taste)
- Backend cloning / API reverse engineering for private systems
- Bypassing login, captcha, or paywalls
- Full multi-page site crawls unless explicitly scoped page-by-page

---

## Part 2: Playwright MCP Capability Map

Use Windsurf Playwright MCP tools:

| Capability | Tool(s) |
|------------|---------|
| Navigate | `browser_navigate` |
| A11y / structure snapshot | `browser_snapshot` |
| Screenshots | `browser_take_screenshot` (`fullPage`, viewports) |
| Run extraction JS | `browser_evaluate` |
| Click / hover / type | `browser_click`, `browser_hover`, `browser_type`, `browser_fill_form` |
| Viewport | `browser_resize` |
| Network / assets | `browser_network_requests`, `browser_network_request` |
| Wait for content | `browser_wait_for` |
| Tabs | `browser_tabs` |

**Rule:** If Playwright MCP is unavailable, stop and tell the user to enable it — do not fake a clone from training data alone.

---

## Part 3: Extraction Playbook (URL mode)

### 3.1 Scroll & load strategy

1. Open URL.
2. Scroll in steps (e.g. 25–50% viewport) with short waits so lazy images and scroll-triggered UI appear.
3. Return to top; capture clean above-fold shot.
4. Capture full-page shot.

### 3.2 Computed style walker (conceptual)

For each important node (headings, buttons, cards, nav links, section wrappers):

Record:

- `tagName`, classes, role, text content (truncated)
- Box: `x, y, width, height`
- `getComputedStyle` fields:
  - color, backgroundColor, backgroundImage
  - fontFamily, fontSize, fontWeight, lineHeight, letterSpacing, textTransform
  - padding*, margin*, gap, display, flex*, grid*
  - border*, borderRadius, boxShadow, opacity
  - position, zIndex, overflow, transform
  - transition, animation, cursor

Prefer **exact** values in specs (`18px`, `#1a1a1a`, `0.5rem`).

### 3.3 Design tokens to derive

From samples, cluster into:

- **Colors** — bg, surface, text, muted, border, primary, accent, success/warn/error if present
- **Type ramp** — display / h1–h6 / body / small / caption
- **Spacing scale** — 4/8-based if possible; else document exact section paddings
- **Radii & shadows** — named elevations
- **Motion** — durations/easings used on primary interactions

### 3.4 Interaction capture

For each interactive pattern:

1. Baseline screenshot/state
2. Hover / focus / open
3. Note class toggles or style deltas
4. Document in BEHAVIORS.md

### 3.5 Assets

- Prefer downloading originals into project `public/` or `docs/design-clone/assets/`
- Keep filenames stable; record original URLs in manifest
- SVGs: inline when small icons; file when complex

---

## Part 4: Screenshot Mode Playbook

When only an image is provided:

1. Vision pass: sections, grid, type hierarchy, palette, CTAs, density
2. Mark all measurements as **inferred** unless measurable from image scale
3. Rebuild with the same verify loop against the image
4. If user later provides a URL, upgrade inferred values to measured ones

---

## Part 5: Build Rules for High Fidelity

1. **Tokens first** — CSS variables or Tailwind theme from extracted values.
2. **No redesign** — Do not "improve" the reference unless asked.
3. **Exact copy** — Use real text from the page/screenshot.
4. **Layout fidelity** — Match max-widths, column counts, gaps, and section order.
5. **Tailwind mapping** — Use standard utilities only when equal; otherwise arbitrary values (`text-[15px]`, `tracking-[-0.02em]`) or raw CSS.
6. **Stack match** — Prefer the project's existing stack (this repo often React + Vite + Tailwind).
7. **A11y floor** — Semantic landmarks, alt text, focus styles even when cloning visual-first designs.
8. **Performance** — Local assets, modern image formats when re-encoding is needed.

---

## Part 6: Visual QA Loop

Repeat until pass or user accepts residual gaps:

1. Same viewport as reference (width + device scale if possible)
2. Side-by-side: reference vs clone
3. Diff categories:
   - **Spacing** — padding/margin/gap
   - **Type** — size/weight/line-height/tracking
   - **Color** — fills, borders, overlays
   - **Alignment** — columns, baselines
   - **Missing** — icons, dividers, gradients, blurs
4. One surgical fix per issue cluster
5. Re-screenshot

**Pass criteria (practical 100% goal):**

- Desktop and mobile layouts match section structure and proportions
- Type and color are indistinguishable at a glance
- Primary interactions (hover buttons, sticky nav) behave similarly
- Document any intentional omissions (maps, third-party widgets, live data)

---

## Part 7: Output Artifacts

```
docs/design-clone/
├── PAGE_TOPOLOGY.md
├── BEHAVIORS.md
├── site-manifest.json
├── styles.json
├── screenshots/
│   ├── ref-desktop.png
│   ├── ref-mobile.png
│   ├── clone-desktop.png
│   └── clone-mobile.png
├── assets/
└── specs/
    ├── header.spec.md
    ├── hero.spec.md
    └── ...
```

Plus the actual app components/pages in the project source tree.

---

## Part 8: Anti-Patterns

| Anti-pattern | Do instead |
|--------------|------------|
| Guessing colors from memory | Extract hex from computed styles / eyedrop from screenshot |
| Building whole page in one shot | Spec + build per section |
| Skipping mobile | Always capture 390px |
| Replacing real images with placeholders | Mirror assets |
| "Close enough" after first pass | Run visual loop |
| Cloning behind login | Stop; request public URL or screenshots |
| Inventing copy | Use verbatim text |

---

## Part 9: Integration with Other Skills

| Skill | Relationship |
|-------|----------------|
| **Website Research** | Research is strategy; this skill is **visual replication** |
| **Claude Taste** | Use when creating *new* design; **not** when user wants exact clone |
| **CSS Architecture** | Tokens and cascade after extraction |
| **Media Optimization** | Optimize mirrored assets after clone |
| **Animation** | Recreate motion from BEHAVIORS.md |
| **Anti Vibe Coding** | After clone, ensure code quality isn't sloppy even if design is copied |
| **Caveman** | Orthogonal — communication mode only |

---

## Part 10: Quality Checklist

- [ ] Playwright MCP used for URL mode
- [ ] Multi-viewport references saved
- [ ] Tokens from evidence, not vibes
- [ ] Section specs exist before bulk UI code
- [ ] Assets local and referenced correctly
- [ ] Clone screenshots compared to reference
- [ ] Residual gaps listed honestly
- [ ] Scope/legal constraints respected
