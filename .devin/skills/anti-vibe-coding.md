---
name: Anti Vibe Coding Skill
description: Comprehensive system for producing non-vibe-coded webapps — covers design systems, architecture, content writing, and code writing to ensure nothing looks or sounds like AI-generated slop
version: 1.0.0
tags: [design, architecture, content, code-quality, anti-slop, craft, taste, vibe-coding]
---

# Anti Vibe Coding Skill

## Purpose
This skill encodes the principles, patterns, and checkpoints that ensure every webapp, landing page, and digital product we produce is **indistinguishable from work crafted by a senior human team**. It covers four domains where AI-generated output betrays itself: visual design, code architecture, content/copy, and code writing style. Use this skill as the ultimate quality gate — if any domain smells like vibe-coded slop, fix it before shipping.

## Core Philosophy

**Vibe coding** is the practice of accepting AI-generated output without scrutiny — shipping the first plausible result without questioning whether it has real craft, intention, or domain expertise baked in. The result is products that feel generic, templated, and soulless. They work, but they have no point of view.

**Anti vibe coding** is the opposite: treating AI as a junior collaborator whose output must be reviewed, challenged, and reshaped by a senior practitioner with taste. Every decision must be deliberate. Every default must be questioned. Every pattern must be earned, not inherited.

**The #1 rule:** If your output could belong to any project, it belongs to no project. Every choice — visual, structural, verbal, computational — must be grounded in the specific subject, audience, and purpose.

---

## Part 1: Visual Design — Non-Vibe-Coded Design Systems

### 1.1 What Vibe-Coded Design Looks Like

Vibe-coded design converges on a small set of defaults because AI models trained on the same web data produce the same "good enough" output:

| Signal | Vibe-Coded Default | Intentional Alternative |
|--------|-------------------|------------------------|
| **Color primary** | Blue `#3B82F6` or indigo `#6366F1` | A hue chosen for the brand's emotional register — warm terracotta, cool sage, muted oxblood |
| **Background** | `#F9FAFB` (gray-50) | Tinted off-white that relates to the primary: `#FEFCF8`, `#F5F0EB`, `#F0F2F5` |
| **Gradients** | Blue-to-purple `linear-gradient(to right, #2563EB, #7C3AED)` | Flat colors, subtle texture, or gradients between adjacent hues in the same family |
| **Typography** | Inter, Roboto, or Geist as the only font | Two intentional typefaces — a display face and a body face, deliberately different from the top 10 Google Fonts |
| **Type weight** | `font-weight: 700` on all headings | Varied weight — h1 at 400 italic, h2 at 600, h3 at 500. Weight creates rhythm |
| **Border radius** | `rounded-2xl` on everything | Mix radii intentionally — sharp images, 6px cards, pill buttons. Or commit to zero radius |
| **Layout** | 3-column equal grid, `max-w-7xl mx-auto`, everything centered | Asymmetric grids, varied max-widths, intentional off-center placement, full-bleed sections |
| **Section order** | Hero then Features then Testimonials then Pricing then CTA then Footer | Reorder around content. Lead with a case study. Put pricing before features. Break the template |
| **Cards** | Equal-height cards with icon-in-circle plus heading plus paragraph | Definition lists, varied-height cards, editorial layouts, no cards at all |
| **Nav** | `sticky top-0 backdrop-blur-md bg-white/80` | Solid nav, hide-on-scroll, minimal text-only header, or side nav |
| **Animation** | Fade-up-on-scroll for every element, hover scale 1.05, duration-300 ease-in-out | Stillness as a craft signal. Animate one hero element. Custom cubic-bezier curves. Vary duration |
| **Hero** | Badge pill plus H1 plus subtitle plus 2 buttons | Editorial hero, full-bleed image with overlay text, asymmetric split, single bold statement |
| **Pricing** | 3 tiers with "Popular" badge on middle | Single price, pay-what-you-want, or custom quote CTA. If tiers, make them genuinely different |
| **Footer** | Logo column plus 3 link columns plus copyright | Single-column, two-row, or minimal footer with just essential links |
| **Empty states** | "No items found" | Empty states as invitations — show what's possible, not what's missing |
| **Loading** | Generic spinner | Skeleton screens that match the content layout, or progressive loading |

### 1.2 Design System Principles

A non-vibe-coded design system has:

- **Point of view** — the system embodies a specific aesthetic stance. It's not "clean and modern" (that's no stance). It's "editorial broadsheet with warm paper tones and deliberate asymmetry" or "Swiss-influenced technical documentation with strict grids and mono accents"
- **Constraint** — fewer colors, fewer sizes, fewer components. Constraint forces intentionality. A 12-color palette with 8 type sizes is a vibe-coded palette. A 6-color palette with 5 type sizes is a designed palette
- **Consistency with surprise** — the system is internally consistent (same spacing scale, same type ramp, same color logic) but breaks its own rules deliberately at key moments for emphasis
- **Materiality** — the design feels like it's made of something. Paper, glass, concrete, ink. Not just "digital surfaces"
- **Cultural grounding** — the design references a real tradition or movement. Bauhaus, Memphis, Swiss, Y2K, editorial print. Not "modern minimalism" which is the absence of a reference

### 1.3 Craft Signals in Design

These are the small details that separate designed from generated:

- Custom `::selection` colors in brand palette
- Custom `:focus-visible` styles (not just browser defaults)
- `@media (prefers-reduced-motion: reduce)` support
- Custom scrollbar styling
- `text-wrap: balance` on headings
- `font-feature-settings: "kern", "liga", "calt"` on body
- `font-variant-numeric: tabular-nums` on data/prices
- `-webkit-font-smoothing: antialiased`
- Proper dark mode with `@media (prefers-color-scheme: dark)` — not just inverted colors
- Custom underline styling on links
- Print styles
- `scroll-behavior: smooth` with reduced-motion guard
- `overscroll-behavior: none` on modals
- `touch-action: manipulation` on interactive elements

### 1.4 Design Anti-Patterns to Reject

- **The SaaS Template Look** — nav, hero with gradient text, 3 feature cards, testimonial carousel, pricing table, CTA banner, footer. This is the #1 most common vibe-coded layout
- **The Portfolio Template Look** — full-bleed hero image, 2-column project grid, about section with portrait, contact form. Every AI portfolio looks like this
- **The Landing Page Template Look** — hero with app screenshot, feature list with checkmarks, social proof logos, FAQ accordion, email signup. Identifiable from across the room
- **The Dashboard Template Look** — sidebar nav, top bar with search, stat cards, chart area, data table. Every AI dashboard is this
- **Gradient text headlines** — `bg-clip-text text-transparent bg-gradient-to-r` — the single most overused AI design trick. Use a distinctive typeface instead
- **Glassmorphism everywhere** — `backdrop-blur` on every card. Use it once, intentionally, or not at all
- **Aurora/mesh gradients** — blob backgrounds behind everything. One background image, placed with intention
- **Stats with counting-up numbers** — `useCountUp` on mount. Show the number. It's more confident
- **Logo clouds with grayscale filter** — `grayscale opacity-50 hover:grayscale-0`. Either show real logos in color or don't show them
- **FAQ accordion with chevron icons** — Use a definition list or just put answers on the page
- **Newsletter input with inline button** — `flex` input and button in a row. Use a proper form with spacing
- **Testimonial cards with circle avatars** — Use full quotes with attribution, editorial-style
- **CTA section with gradient background** — A flat color or a real image. Gradient CTAs are the wallpaper of vibe-coded sites

---

## Part 2: Architecture — Non-Vibe-Coded System Architecture

### 2.1 What Vibe-Coded Architecture Looks Like

Vibe-coded architecture is recognizable by its lack of opinion. It defaults to whatever the AI model has seen most frequently in training data, resulting in structures that are technically functional but architecturally thoughtless:

| Signal | Vibe-Coded Default | Intentional Alternative |
|--------|-------------------|------------------------|
| **Folder structure** | Flat `components/`, `pages/`, `lib/`, `utils/` with everything dumped together | Domain-oriented folders — `features/auth/`, `features/billing/` — each self-contained with its own components, hooks, types, tests |
| **API design** | CRUD endpoints on everything, REST with no versioning, no pagination | Thoughtful API surface — versioned, paginated, filtered, with proper status codes and error envelopes |
| **State management** | Global Redux/Zustand store for everything, or no state management at all | State classified by lifetime and scope — server state (React Query/SWR), URL state (search params), ephemeral state (useState), global state (context/zustand only when truly shared) |
| **Error handling** | `try/catch` with `console.error` and a toast notification | Structured error handling — error boundaries, typed errors, error propagation patterns, user-friendly messages, logging pipeline |
| **Data fetching** | `useEffect` + `fetch` in every component | Data fetching library with caching, deduplication, optimistic updates, background refetch |
| **Auth** | JWT in localStorage, no refresh tokens, no role checks | Proper auth — httpOnly cookies or secure token storage, refresh token rotation, RBAC/ABAC checks at route and API level |
| **Database** | Single `schema.prisma` with all models flat, no relations, no indexes | Normalized schema with explicit relations, indexes on query patterns, migration strategy, connection pooling |
| **Testing** | No tests, or only snapshot tests | Testing pyramid — unit for logic, integration for APIs, e2e for critical paths. Meaningful assertions, not snapshots |
| **Environment config** | Hardcoded URLs, `.env.local` with everything, no validation | Validated environment config — Zod schema for env vars, typed config object, fail-fast on missing required values |
| **Types** | `any` everywhere, no Zod validation, untyped API responses | Strict TypeScript, Zod schemas at API boundaries, inferred types from schemas, no `any` without explicit justification |

### 2.2 Architecture Principles

A non-vibe-coded architecture has:

- **Domain boundaries** — code is organized by business domain, not by technical function. `features/auth/` contains everything auth-related: components, hooks, API calls, types, tests. This makes the codebase navigable and domains independently deployable
- **Explicit data flow** — data flows in one direction, and the flow is visible. Server → cache → component → user action → mutation → server. No hidden side effects, no mystery updates
- **Failure-aware** — every external call has error handling, every assumption has validation, every edge case has a test. The system degrades gracefully, not catastrophically
- **Right-sized abstractions** — abstractions are extracted after duplication is proven, not preemptively. Three concrete implementations before extracting a generic. No premature DRY
- **Observable** — structured logging, error tracking, performance metrics. You can debug production issues without guessing
- **Security by default** — auth checks at every layer, input validation at every boundary, secrets in secure storage, CORS configured correctly, security headers set
- **Migration-ready** — database changes go through migrations, not `db push`. Schema changes are reviewed, tested, and rollback-able

### 2.3 Architecture Anti-Patterns to Reject

- **God components** — a single 500-line component that does data fetching, state management, rendering, and side effects. Split by responsibility
- **Prop drilling** — passing props through 5+ levels. Use context, composition, or state management
- **Client-side everything** — no server components, no SSR, no static generation. Everything is a client component with `useEffect`. Use the framework's capabilities
- **No error boundaries** — one error crashes the whole app. Wrap route segments and critical features in error boundaries
- **Unvalidated env vars** — `process.env.DATABASE_URL` used directly with no check. Validate at startup with Zod, fail fast
- **Fat API routes** — business logic, data access, and HTTP handling all in one route handler. Layer it: route → service → repository
- **No pagination** — API returns all records. Always paginate, even if you expect small datasets
- **Synchronous blocking** — heavy operations on the main thread. Use background jobs, streaming, or web workers
- **Hardcoded config** — API URLs, feature flags, and constants scattered in code. Centralize in a typed config module
- **No migration strategy** — `db push` in development, no migration files. Use proper migrations from day one
- **Missing indexes** — queries that scan full tables. Index based on query patterns, not guesswork
- **N+1 queries** — fetching a list, then fetching related data one-by-one. Use joins, includes, or batch loading
- **Untyped API contracts** — frontend and backend share no schema. Define shared types or use a contract-first approach
- **No rate limiting** — API endpoints open to unlimited requests. Add rate limiting from the start
- **CORS `*`** — `Access-Control-Allow-Origin: *` on everything. Configure CORS for actual allowed origins

### 2.4 Architecture Craft Signals

- Error boundaries at route segment level
- Loading states that match content layout (skeletons, not spinners)
- Optimistic updates for mutations
- Proper HTTP cache headers (`Cache-Control`, `ETag`, `Stale-While-Revalidate`)
- Database indexes documented and justified
- API versioning (`/api/v1/`)
- Structured error responses (`{ error: { code, message, details } }`)
- Health check endpoint (`/api/health`)
- Request ID for tracing
- Graceful shutdown handling

---

## Part 3: Content Writing — Non-Vibe-Coded Copy

### 3.1 What Vibe-Coded Copy Sounds Like

Vibe-coded copy is the verbal equivalent of gradient buttons. It fills space without saying anything. It sounds professional but means nothing. It could appear on any website for any company in any industry:

| Signal | Vibe-Coded Default | Intentional Alternative |
|--------|-------------------|------------------------|
| **Headline** | "Transform your business with our innovative solution" | A specific claim with a concrete noun and active verb — "Cut your deployment time from 40 minutes to 4" |
| **Subheadline** | "We help companies leverage cutting-edge technology to drive growth and innovation" | A sentence that could only be written for this specific product — "The only deployment platform that reads your CI logs and suggests fixes" |
| **CTA** | "Get Started" / "Learn More" / "Sign Up Now" | Verbs that describe the action's outcome — "Read the docs", "Start your first deploy", "See it work" |
| **Feature descriptions** | "Powerful features designed to streamline your workflow" | Name the feature, say what it does, say who it's for — "Auto-rollback watches your deploy for 5 minutes and reverts if error rates spike" |
| **About page** | "We're a passionate team of innovators dedicated to making the world better" | A specific origin story — "We started this in 2023 after our team spent 6 hours debugging a failed deploy that could have been auto-rolled back" |
| **Value props** | "Save time. Reduce costs. Increase productivity." | One specific, measurable promise — "Deploys 10x faster. 0 rollback anxiety." |
| **Social proof** | "Trusted by 10,000+ companies worldwide" | A specific customer with a specific result — "Vercel reduced their rollback time from 12 minutes to 30 seconds using Auto-Rollback" |
| **Error messages** | "Something went wrong. Please try again." | What went wrong and what to do — "Your session expired. Sign in again to continue editing." |
| **Empty states** | "No items found" | What's possible here — "No projects yet. Create your first one and it'll show up here." |
| **Microcopy** | "Loading..." / "Submitting..." | State-aware — "Reading your CI logs...", "Deploying to production...", "Rolling back..." |
| **Pricing** | "Basic / Pro / Enterprise" with feature lists | Pricing tied to outcomes — "For solo devs / For teams that ship daily / For platforms that can't go down" |
| **Meta descriptions** | "The best [product] for [use case]. Try it free today!" | A sentence that earns the click — "Auto-rollback reverts failed deploys in 30 seconds. Works with Vercel, Netlify, and Railway." |

### 3.2 Content Principles

- **Specific over generic** — "10x faster" beats "much faster". "30 seconds" beats "fast". "Vercel" beats "leading companies"
- **Active over passive** — "We deploy your code" beats "Your code is deployed". "You control everything" beats "Everything can be controlled"
- **Concrete over abstract** — "Cut deployment time from 40 minutes to 4" beats "Streamline your deployment process"
- **Short over long** — If a sentence doesn't earn its words, cut it. Every word competes for the reader's attention
- **Honest over hype** — Don't claim "AI-powered" unless there's actual AI. Don't say "revolutionary" unless you're actually revolutionizing something. Underpromise, overdeliver
- **User's voice over marketing voice** — Write from the user's perspective, not the company's. "You" not "we". The user is the hero, the product is the tool
- **One idea per sentence** — Don't stack claims. "10x faster. 50% fewer errors." is two sentences, not one
- **No filler phrases** — Cut "in today's fast-paced world", "at the end of the day", "it's worth noting that", "when it comes to". These are word-padding

### 3.3 Vibe-Coded Copy Red Flags

Words and phrases that instantly signal AI-generated copy:

- "leverage" (as a verb for using something)
- "cutting-edge", "state-of-the-art", "best-in-class"
- "seamless", "seamlessly"
- "robust" (when describing your own product)
- "innovative", "innovation" (when describing your own product)
- "empower", "empowering"
- "comprehensive" (when describing your own product)
- "next-generation"
- "game-changing", "revolutionary"
- "world-class"
- "trusted by thousands" (without naming any)
- "designed to" (just describe what it does)
- "powerful" (describe the power, don't claim it)
- "intuitive" (if it's intuitive, you don't need to say so)
- "at the forefront of"
- "in today's [adjective] world"
- "unlock your potential"
- "drive results"
- "streamline your workflow"
- "supercharge your [anything]"
- "the future of [anything]"
- "reimagine" anything
- "delight your users"
- "elevate your [anything]"

### 3.4 Content Craft Signals

- Headlines that make a specific, falsifiable claim
- CTAs that describe the next action, not the desired emotion
- Error messages that name the problem and suggest a fix
- Empty states that show what's possible, not what's missing
- Loading states that describe what's happening
- Microcopy that anticipates the user's question
- Pricing described in outcomes, not feature lists
- About page with a real origin story, not "passionate team"
- Social proof with named customers and specific results
- Meta descriptions that earn the click with a concrete promise
- No exclamation marks in body copy (one per page maximum, if any)
- No emoji in professional copy (unless the brand voice explicitly calls for it)
- Sentences vary in length — short for punch, long for flow, one word for emphasis
- Reading level appropriate for the audience (not dumbed down, not jargon-stuffed)

---

## Part 4: Code Writing — Non-Vibe-Coded Code

### 4.1 What Vibe-Coded Code Looks Like

Vibe-coded code is functional but soulless. It works, but it has no craftsmanship. It's the code equivalent of a mass-produced item — no attention to detail, no consideration for the next developer, no pride in the work:

| Signal | Vibe-Coded Default | Intentional Alternative |
|--------|-------------------|------------------------|
| **Naming** | `data`, `item`, `result`, `handleClick`, `handleSubmit` | Domain-specific names — `pendingInvoices`, `rollbackResult`, `triggerAutoRollback` |
| **Comments** | Obvious comments (`// increment i by 1`) or no comments | Comments explain *why*, not *what* — `// We retry 3x because network blips are common in this region` |
| **Error handling** | `catch (e) { console.log(e) }` | Typed errors with context — `throw new DeploymentError('Rollback failed', { deployId, reason })` |
| **Functions** | 200-line functions doing 5 things | 20-line functions doing one thing, composed together |
| **Imports** | Everything imported at top, unused imports left in | Only what's needed, organized: stdlib → third-party → local, unused removed |
| **Types** | `any`, `Record<string, any>`, `as any` | Specific types, Zod schemas, `unknown` with narrowing, `satisfies` operator |
| **Conditionals** | Deep nesting, `if/if/if/else`, complex boolean expressions | Early returns, guard clauses, extracted predicates, `if (!condition) return` |
| **Async** | `async/async/async` with no error boundaries, unhandled promise rejections | `try/catch` at appropriate levels, `Promise.allSettled` for parallel ops, loading and error states |
| **Components** | 300-line mega-components with inline styles, no prop validation | 50-line components with typed props, extracted sub-components, no inline styles |
| **CSS** | Utility class soup — `className="flex items-center justify-center px-4 py-2 text-sm font-medium text-white bg-blue-500 rounded-lg hover:bg-blue-600"` | Extracted component classes, CSS modules, or styled components with semantic names |
| **Hooks** | Custom hooks with no cleanup, no dependency array, no error handling | Hooks with proper cleanup, correct deps, error boundaries, loading states |
| **Tests** | No tests, or tests that test the implementation not the behavior | Tests that describe behavior — `it('rolls back when error rate exceeds 5%')` not `it('calls rollback function')` |
| **Constants** | Magic numbers and strings scattered in code | Named constants in a config file — `MAX_RETRY_COUNT = 3`, `ROLLBACK_ERROR_THRESHOLD = 0.05` |
| **Dead code** | Commented-out blocks, unused functions, leftover debug logs | Clean — no dead code, no debug logs, no commented-out blocks |

### 4.2 Code Writing Principles

- **Readability over cleverness** — code is read 10x more than it's written. Optimize for the reader, not the writer. No one-line tricks that require 5 minutes to decode
- **Naming is the primary documentation** — a well-named function doesn't need a comment. A poorly named function can't be saved by a comment. Spend time on names
- **Functions do one thing** — if a function has "and" in its description, it does two things. Split it. `fetchAndParseAndStore` becomes `fetch` + `parse` + `store`
- **Pure where possible** — pure functions are testable, cacheable, and predictable. Push side effects to the edges of the system
- **Fail loud, fail early** — don't silently swallow errors. Don't return null when something unexpected happens. Throw, log, surface. Silent failures are the #1 source of production bugs
- **Consistency over preference** — if the codebase uses `const` arrow functions, use them too. If it uses `interface`, don't use `type`. Match the existing style, even if you prefer something else
- **No surprise side effects** — a function called `getUser` should not also update the database, send an email, and clear the cache. Name it `syncUserFromApi` if it does
- **Defensive at boundaries, trusting internally** — validate input at API boundaries, trust types within your own code. Don't re-validate what's already validated
- **Composition over inheritance** — compose small functions and components. Avoid deep inheritance hierarchies
- **Explicit over implicit** — don't rely on side effects, global state, or "magic" behavior. Make data flow visible

### 4.3 Vibe-Coded Code Red Flags

- `any` type without a comment explaining why
- `// TODO` with no name or date
- `console.log` left in production code
- Commented-out code blocks (git remembers, you don't need to)
- Functions longer than 50 lines
- Components longer than 100 lines
- More than 3 levels of nesting
- `useEffect` with no dependency array or `[]` when it uses props/state
- Inline styles when a class would work
- `dangerouslySetInnerHTML` without sanitization
- `as any` type assertions
- `eslint-disable` without a reason
- `// @ts-ignore` without a reason
- Empty catch blocks
- `try/catch` that catches everything (catch specific errors)
- Variables named `temp`, `foo`, `bar`, `baz`, `x`, `y` in production code
- Boolean flags as function parameters (`render(true, false, true)`)
- Default exports for everything (use named exports, they're refactorable)
- Re-exporting everything from an index file (tree-shaking killer)
- `switch` statements without a default case
- `if/else if` chains longer than 3 branches (use a lookup table or polymorphism)

### 4.4 Code Craft Signals

- Named constants for all magic numbers and strings
- Functions with descriptive verbs in their names (`parseDeploymentConfig` not `processData`)
- Types that describe the domain (`DeploymentStatus` not `string`)
- Error classes that carry context (`DeploymentError` with `deployId`, `stage`, `reason`)
- Tests named as sentences (`it('returns 404 when deployment does not exist')`)
- No dead code, no unused imports, no commented-out blocks
- Consistent formatting (enforced by Prettier or Biome, not by hand)
- Linting rules that catch real bugs (not just style preferences)
- Git commits that explain why, not what (`fix: retry rollback on transient network errors` not `fix: changed code`)
- PR descriptions that describe the change, the reason, and the testing approach
- Dependency arrays in hooks are correct and complete
- Cleanup functions in useEffect for subscriptions, timers, and observers
- Memoization only when measurably needed (not preemptively)
- Error boundaries around route segments
- Loading states for all async operations
- Accessible by default (semantic HTML, ARIA where needed, keyboard navigation)

---

## Part 5: The Anti-Vibe-Coding Master Checklist

Run this checklist before shipping any webapp, landing page, or digital product. If any item fails, fix it before proceeding.

### Design Checklist

- [ ] Primary color is NOT blue `#3B82F6` or indigo `#6366F1`
- [ ] Background is NOT `#F9FAFB` (gray-50)
- [ ] No blue-to-purple gradients
- [ ] No gradient text headlines (`bg-clip-text`)
- [ ] Typography uses at least 2 typefaces, neither is Inter/Roboto/Geist as the only font
- [ ] Not all headings are `font-weight: 700`
- [ ] No `rounded-2xl` on everything (radii are intentionally varied or consistently sharp)
- [ ] Layout is NOT a 3-column equal grid
- [ ] NOT every section is `max-w-7xl mx-auto text-center`
- [ ] Section order breaks the hero-features-testimonials-pricing-CTA template
- [ ] No icon-in-circle feature cards
- [ ] No 3-tier pricing with "Popular" badge on middle
- [ ] No fade-up-on-scroll for every element
- [ ] No hover scale 1.05
- [ ] No `transition-all duration-300 ease-in-out` everywhere
- [ ] No sticky nav with `backdrop-blur-md bg-white/80`
- [ ] No stats with counting-up numbers
- [ ] No logo cloud with grayscale filter
- [ ] No FAQ accordion with chevron icons
- [ ] No testimonial cards with circle avatars
- [ ] No CTA section with gradient background
- [ ] No glassmorphism on every card
- [ ] No aurora/mesh gradient blobs
- [ ] Custom `::selection` colors implemented
- [ ] Custom `:focus-visible` styles implemented
- [ ] `prefers-reduced-motion` support implemented
- [ ] `text-wrap: balance` on headings
- [ ] Dark mode is proper (not just inverted colors)
- [ ] One signature element that makes the design memorable

### Architecture Checklist

- [ ] Code organized by domain, not just by technical function
- [ ] API is versioned (`/api/v1/`)
- [ ] API has pagination on list endpoints
- [ ] API has structured error responses
- [ ] Environment variables validated with Zod at startup
- [ ] No `any` types without explicit justification
- [ ] Zod schemas at API boundaries
- [ ] Error boundaries at route segment level
- [ ] No god components (300+ lines)
- [ ] No fat API routes (business logic in route handlers)
- [ ] Database has indexes on query patterns
- [ ] No N+1 queries
- [ ] Migrations used (not `db push`)
- [ ] Auth checks at route and API level
- [ ] CORS configured for actual allowed origins (not `*`)
- [ ] Rate limiting on API endpoints
- [ ] Health check endpoint exists
- [ ] No hardcoded config values (URLs, flags, constants)
- [ ] Loading states are skeletons, not spinners
- [ ] Proper HTTP cache headers set

### Content Checklist

- [ ] Headline makes a specific, falsifiable claim
- [ ] No "leverage", "seamless", "robust", "innovative", "empower", "comprehensive"
- [ ] No "cutting-edge", "state-of-the-art", "best-in-class", "next-generation"
- [ ] No "game-changing", "revolutionary", "world-class", "trusted by thousands"
- [ ] No "in today's [adjective] world" or "at the end of the day"
- [ ] No "unlock your potential", "drive results", "streamline your workflow"
- [ ] No "supercharge", "the future of", "reimagine", "delight", "elevate"
- [ ] CTAs describe the next action, not the desired emotion
- [ ] Error messages name the problem and suggest a fix
- [ ] Empty states show what's possible, not what's missing
- [ ] Loading states describe what's happening
- [ ] Social proof has named customers with specific results (or is removed)
- [ ] About page has a real origin story
- [ ] No exclamation marks in body copy
- [ ] No emoji in professional copy (unless brand voice requires it)
- [ ] Sentences vary in length
- [ ] Every sentence earns its words (no filler)

### Code Checklist

- [ ] No `any` types without justification comments
- [ ] No `console.log` in production code
- [ ] No commented-out code blocks
- [ ] No functions longer than 50 lines
- [ ] No components longer than 100 lines
- [ ] No more than 3 levels of nesting
- [ ] No `as any` type assertions
- [ ] No `eslint-disable` or `@ts-ignore` without reasons
- [ ] No empty catch blocks
- [ ] No variables named `temp`, `foo`, `bar`, `baz` in production code
- [ ] No magic numbers or strings (use named constants)
- [ ] No unused imports
- [ ] No dead code
- [ ] Functions have descriptive verb names
- [ ] Types describe the domain (not `string`, but `DeploymentStatus`)
- [ ] Error classes carry context
- [ ] Tests named as sentences describing behavior
- [ ] Hook dependency arrays are correct and complete
- [ ] Cleanup functions in useEffect
- [ ] Semantic HTML used (not div soup)
- [ ] Accessible by default (keyboard nav, ARIA where needed)
- [ ] Consistent formatting (enforced by tooling)
- [ ] Git commits explain why, not what

---

## Part 6: The Anti-Vibe-Coding Process

### Before Writing Code
1. Read the project's `research.md` for context on brand, audience, and purpose
2. Identify the design tradition or movement this project references
3. Define the one signature element that makes this project memorable
4. Write down 3 things this project will NOT do (negative space defines identity)

### During Development
1. Check every default against the vibe-coded defaults table — is this the AI default or an intentional choice?
2. Name things after domain concepts, not generic programming terms
3. Write copy that could only belong to this project
4. Comment why, not what
5. Handle errors with context, not `console.log`

### Before Shipping
1. Run the master checklist (Part 5)
2. Take a screenshot — does it look like it could belong to any project?
3. Read the copy aloud — does it sound like a human or a marketing bot?
4. Review the code — would a senior engineer approve this PR?
5. Ask: "If I saw this on a stranger's laptop, would I think 'AI generated this'?"
6. If yes to any of the above, revise until the answer is no
