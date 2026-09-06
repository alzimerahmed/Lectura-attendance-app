---
name: claude-taste
description: Apply Claude's frontend design taste and modern web design principles to any website project — ensures distinctive, non-generic, anti-AI-slop design
---

# Claude Taste Frontend Design Workflow

This workflow applies the **Claude Taste Frontend Design Skill** (`~/.codeium/windsurf/skills/claude-taste-frontend-design.md`) to ensure every website built has distinctive, intentional, human-quality design — not AI slop.

## When to Run
- When building any new website UI or component
- When redesigning or reshaping an existing UI
- When the user says `/claude-taste` or asks for "good design" or "modern design"
- After the Website Research workflow completes, before writing UI code
- When the user asks to improve the visual design of a page

---

## Step 1: Read Context

1. Read the project's `research.md` if it exists — it contains the design direction, color palette, typography choices, and visual style decisions
2. Read any existing design tokens, CSS variables, or Tailwind config
3. Read the project's `package.json` to understand the tech stack (React, Next.js, Astro, etc.)
4. Identify what framework, styling system, and UI libraries are in use

## Step 2: Brainstorm Design Plan

Create a compact design token system before writing any UI code:

### Color
- Define 4-6 named hex values describing the palette
- Ensure the primary is NOT in the blue-indigo range (200-290° hue)
- Use warm or cool off-whites for backgrounds, not pure white or gray-50
- Define a sharp accent that gives the design personality
- Plan a proper dark mode palette (not inverted colors)

### Typography
- Choose a display face and a body face — deliberately different from Inter/Roboto/Geist
- Define a modular type scale (ratio 1.25 or 1.333)
- Plan line heights per context (display 1.1, body 1.6, small 1.4)
- Plan letter-spacing per context (headings tight, small text loose)
- State your font choices before coding

### Layout
- Write one-sentence prose descriptions for each section's layout
- Sketch ASCII wireframes to ideate and compare
- Plan asymmetric grids, varied section padding, varied max-widths
- Identify the section order — break the hero → features → testimonials → pricing template

### Signature
- Define the single unique element this page will be remembered by
- This should embody the brief in an appropriate way
- Spend your boldness here — keep everything else quiet and disciplined

## Step 3: Self-Critique Against AI Slop Catalog

Before building, review the design plan against the AI slop catalog:

1. **Color check:** Is the primary in the blue-indigo range? Is the background gray-50? Are there gradient buttons? → Revise
2. **Typography check:** Are you using Inter, Roboto, Geist, or system-ui? Is everything font-weight 700? → Revise
3. **Layout check:** Is there a 3-column equal grid? Is everything centered? Is padding symmetric? → Revise
4. **Animation check:** Are you planning fade-up-on-scroll for every element? Hover scale 1.05? Duration 300ms everywhere? → Revise
5. **Component check:** Are you using the badge + H1 + subtitle + 2 buttons hero? Icon-in-circle feature cards? Pricing with "Popular" badge? → Revise
6. **Default cluster check:** Does the design match one of the three AI default clusters (warm cream + terracotta, near-black + acid-green, broadsheet)? → Revise

If any check fails, revise that part. Say what you changed and why.

## Step 4: Build with Craft Signals

Implement the design following the revised plan exactly:

### Must-include craft signals:
- [ ] Custom `::selection` colors in brand palette
- [ ] Custom `:focus-visible` styles
- [ ] `@media (prefers-reduced-motion: reduce)` support
- [ ] Custom scrollbar styling
- [ ] `text-wrap: balance` on headings
- [ ] `font-feature-settings: "kern", "liga", "calt"` on body
- [ ] `font-variant-numeric: tabular-nums` on data/prices
- [ ] `-webkit-font-smoothing: antialiased`
- [ ] Proper dark mode with `@media (prefers-color-scheme: dark)`
- [ ] Custom underline styling on links
- [ ] Print styles

### Animation rules:
- Animate only `transform` and `opacity`
- Use custom `cubic-bezier` curves, not `ease-in-out`
- Vary duration by element (150ms for UI, 500ms for layout)
- One orchestrated page load > scattered micro-interactions
- Include `prefers-reduced-motion` guard

### Performance:
- AVIF/WebP images with explicit dimensions
- `font-display: swap` on all fonts
- Preload hero image and critical fonts
- Minimal JavaScript on first load

## Step 5: Critique Again

1. Take a screenshot of the result using `mcp6_browser_take_screenshot`
2. Review the screenshot:
   - Does it look like it could belong to any project? → Revise
   - Does it pass the "lookalike test" (can you distinguish it from competitors)?
   - Is there one memorable signature element?
   - Is there decoration that doesn't serve the brief? → Remove it
3. Apply Chanel's advice: remove one accessory before shipping
4. Verify accessibility: contrast ratios, focus states, semantic HTML
5. Check responsive: test at 360px, 768px, 1280px

## Step 6: Iterate

If the critique reveals issues:
1. Revise the specific problem area
2. Re-screenshot
3. Re-critique
4. Repeat until the design feels intentional and distinctive

---

## Quick Reference: Anti-Slop Checklist

Before shipping any UI, verify NONE of these are present:

- [ ] `#3B82F6` or `#6366F1` as primary color
- [ ] `from-blue-600 to-indigo-700` gradient
- [ ] `#F9FAFB` background
- [ ] Inter, Roboto, Geist, or system-ui as the only font
- [ ] `font-weight: 700` on all headings
- [ ] `grid-cols-3` with identical children
- [ ] `max-w-7xl mx-auto` on every section
- [ ] `py-24 px-6` on every section
- [ ] `text-center` on every section
- [ ] `sticky top-0 backdrop-blur-md bg-white/80` nav
- [ ] `opacity: 0, y: 20` fade-up on every element
- [ ] `transform: scale(1.05)` on hover
- [ ] `transition-all duration-300 ease-in-out`
- [ ] `rounded-2xl` on everything
- [ ] Badge pill + H1 + subtitle + 2 buttons hero
- [ ] Icon in colored circle + heading + paragraph cards
- [ ] 3 pricing tiers with "Popular" badge on middle
- [ ] FAQ accordion with chevron icons
- [ ] Newsletter input with inline button
- [ ] Stats with counting-up numbers
- [ ] Logo cloud with grayscale filter
- [ ] CTA section with gradient background
- [ ] Testimonial cards with circle avatars
- [ ] No `prefers-reduced-motion` support
