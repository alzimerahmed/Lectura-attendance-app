---
name: anti-vibe-coding
description: Comprehensive anti-vibe-coding audit — checks design, architecture, content, and code for AI-generated slop patterns and fixes them before shipping
---

# Anti Vibe Coding Workflow

This workflow applies the **Anti Vibe Coding Skill** (`~/.codeium/windsurf/skills/anti-vibe-coding.md`) to ensure every webapp, landing page, and digital product is indistinguishable from work crafted by a senior human team.

## When to Run
- Before shipping any webapp, landing page, or digital product
- After building a new page or feature
- When reviewing code for quality
- When the user says `/anti-vibe-coding` or asks to "check for vibe coding" or "check for AI slop"
- As a final quality gate before deployment
- After any major UI, architecture, content, or code change

---

## Step 1: Read Context

1. Read the project's `research.md` if it exists — brand voice, visual direction, audience
2. Read the project's `package.json` to understand the tech stack
3. Read existing design tokens, CSS variables, Tailwind config
4. Scan the project structure — folder organization, component count, file sizes
5. Identify what's been built so far and what's being shipped

## Step 2: Design Audit

Review the visual design against the vibe-coded design catalog:

### Color Check
- [ ] Primary color is NOT blue `#3B82F6` or indigo `#6366F1`
- [ ] Background is NOT `#F9FAFB` (gray-50)
- [ ] No blue-to-purple gradients
- [ ] No gradient text headlines
- [ ] Palette has 6-12 named colors, not just 3-4
- [ ] Accents are desaturated (not raw Tailwind semantic colors)
- [ ] Dark mode is proper (not just inverted)

### Typography Check
- [ ] At least 2 typefaces (display + body)
- [ ] Neither is Inter, Roboto, or Geist as the only font
- [ ] Not all headings are `font-weight: 700`
- [ ] Letter-spacing varies by context (tight for headings, loose for small text)
- [ ] Line-height varies by context (1.1 display, 1.6 body)
- [ ] `text-wrap: balance` on headings
- [ ] `font-feature-settings` enabled on body

### Layout Check
- [ ] NOT a 3-column equal grid
- [ ] NOT `max-w-7xl mx-auto` on every section
- [ ] NOT `text-center` on every section
- [ ] Section order breaks the standard template
- [ ] Padding varies between sections
- [ ] At least one asymmetric or full-bleed section
- [ ] No icon-in-circle feature cards
- [ ] No 3-tier pricing with "Popular" badge

### Animation Check
- [ ] No fade-up-on-scroll for every element
- [ ] No hover scale 1.05
- [ ] No `duration-300 ease-in-out` everywhere
- [ ] `prefers-reduced-motion` support implemented
- [ ] Animation is reserved for key moments, not scattered

### Component Check
- [ ] No sticky nav with `backdrop-blur-md bg-white/80`
- [ ] No stats with counting-up numbers
- [ ] No logo cloud with grayscale filter
- [ ] No FAQ accordion with chevron icons
- [ ] No testimonial cards with circle avatars
- [ ] No CTA section with gradient background
- [ ] No glassmorphism on every card
- [ ] No aurora/mesh gradient blobs

### Craft Signal Check
- [ ] Custom `::selection` colors
- [ ] Custom `:focus-visible` styles
- [ ] Custom scrollbar styling
- [ ] `font-variant-numeric: tabular-nums` on data
- [ ] `-webkit-font-smoothing: antialiased`
- [ ] Custom underline styling on links
- [ ] One signature element that makes the design memorable

**If any check fails:** Note the failure, revise the specific issue, re-check.

## Step 3: Architecture Audit

Review the system architecture against the vibe-coded architecture catalog:

### Structure Check
- [ ] Code organized by domain (not just flat `components/`, `lib/`, `utils/`)
- [ ] No god components (300+ lines)
- [ ] No fat API routes (business logic in route handlers)
- [ ] Error boundaries at route segment level

### API Check
- [ ] API is versioned (`/api/v1/`)
- [ ] Pagination on list endpoints
- [ ] Structured error responses (`{ error: { code, message, details } }`)
- [ ] Rate limiting on API endpoints
- [ ] Health check endpoint exists
- [ ] Proper HTTP cache headers

### Data Check
- [ ] Database has indexes on query patterns
- [ ] No N+1 queries
- [ ] Migrations used (not `db push`)
- [ ] No untyped API contracts

### Security Check
- [ ] Auth checks at route and API level
- [ ] CORS configured for actual allowed origins (not `*`)
- [ ] Environment variables validated with Zod at startup
- [ ] No hardcoded secrets or config values

### State Check
- [ ] State classified by type (server, URL, ephemeral, global)
- [ ] Data fetching uses a library with caching (not raw `useEffect` + `fetch`)
- [ ] Loading states are skeletons, not spinners
- [ ] Optimistic updates where appropriate

**If any check fails:** Note the failure, revise the specific issue, re-check.

## Step 4: Content Audit

Review all copy against the vibe-coded copy catalog:

### Headline Check
- [ ] Headlines make specific, falsifiable claims
- [ ] No "transform your business" or "innovative solution"
- [ ] Subheadlines could only belong to this specific product

### Filler Word Check
Search the codebase for these red-flag words and phrases:
- [ ] No "leverage", "seamless", "robust", "innovative", "empower", "comprehensive"
- [ ] No "cutting-edge", "state-of-the-art", "best-in-class", "next-generation"
- [ ] No "game-changing", "revolutionary", "world-class", "trusted by thousands"
- [ ] No "in today's [adjective] world", "at the end of the day"
- [ ] No "unlock your potential", "drive results", "streamline your workflow"
- [ ] No "supercharge", "the future of", "reimagine", "delight", "elevate"

### Microcopy Check
- [ ] CTAs describe the next action, not the desired emotion
- [ ] Error messages name the problem and suggest a fix
- [ ] Empty states show what's possible, not what's missing
- [ ] Loading states describe what's happening

### Voice Check
- [ ] Social proof has named customers with specific results (or is removed)
- [ ] About page has a real origin story
- [ ] No exclamation marks in body copy
- [ ] No emoji in professional copy (unless brand voice requires it)
- [ ] Sentences vary in length
- [ ] Every sentence earns its words

**If any check fails:** Note the failure, rewrite the specific copy, re-check.

## Step 5: Code Audit

Review the code against the vibe-coded code catalog:

### Type Safety Check
- [ ] No `any` types without justification comments
- [ ] No `as any` type assertions
- [ ] No `@ts-ignore` without reasons
- [ ] Zod schemas at API boundaries
- [ ] Types describe the domain (not `string`, but `DeploymentStatus`)

### Code Quality Check
- [ ] No `console.log` in production code
- [ ] No commented-out code blocks
- [ ] No functions longer than 50 lines
- [ ] No components longer than 100 lines
- [ ] No more than 3 levels of nesting
- [ ] No `eslint-disable` without a reason
- [ ] No empty catch blocks
- [ ] No magic numbers or strings
- [ ] No unused imports
- [ ] No dead code

### Naming Check
- [ ] No variables named `temp`, `foo`, `bar`, `baz`, `x`, `y` in production code
- [ ] Functions have descriptive verb names
- [ ] Error classes carry context
- [ ] Tests named as sentences describing behavior

### React/Next.js Check
- [ ] Hook dependency arrays are correct and complete
- [ ] Cleanup functions in useEffect
- [ ] No inline styles when a class would work
- [ ] No `dangerouslySetInnerHTML` without sanitization
- [ ] Semantic HTML used (not div soup)
- [ ] Accessible by default (keyboard nav, ARIA where needed)

**If any check fails:** Note the failure, fix the specific code, re-check.

## Step 6: Screenshot Review

1. Take a screenshot of the result using `mcp6_browser_take_screenshot`
2. Review the screenshot:
   - Does it look like it could belong to any project? → Revise
   - Does it pass the "lookalike test" (can you distinguish it from competitors)?
   - Is there one memorable signature element?
   - Is there decoration that doesn't serve the brief? → Remove it
3. Apply Chanel's advice: remove one accessory before shipping
4. Check responsive: test at 360px, 768px, 1280px

## Step 7: Compile Report

Generate a report with:
1. **Design findings** — list of vibe-coded design patterns found and fixed
2. **Architecture findings** — list of vibe-coded architecture patterns found and fixed
3. **Content findings** — list of vibe-coded copy patterns found and fixed
4. **Code findings** — list of vibe-coded code patterns found and fixed
5. **Screenshot review** — does the final result pass the lookalike test?
6. **Signature element** — what makes this project memorable?
7. **Overall verdict** — PASS or FAIL (fail = needs revision before shipping)

## Step 8: Fix and Iterate

If any audit failed:
1. Fix the specific issues identified in the report
2. Re-run the relevant audit step
3. Re-screenshot
4. Re-review
5. Repeat until all checks pass

---

## Quick Reference: Vibe-Coded Red Flags

If you see ANY of these, the project has vibe-coded slop:

**Design:** Blue primary, gray-50 bg, gradient text, Inter-only, 3-col grid, fade-up everywhere, glassmorphism, counting stats, grayscale logo cloud, FAQ accordion, gradient CTA

**Architecture:** Flat folders, no pagination, unvalidated env, `any` types, no error boundaries, fat routes, no migrations, CORS `*`, no rate limiting

**Content:** "Leverage", "seamless", "innovative", "trusted by thousands", "in today's world", "unlock potential", "Get Started", "Something went wrong", "No items found"

**Code:** `any`, `console.log`, commented-out code, 200-line functions, `as any`, empty catch, `temp`/`foo`/`bar`, magic numbers, no cleanup in useEffect, inline styles
