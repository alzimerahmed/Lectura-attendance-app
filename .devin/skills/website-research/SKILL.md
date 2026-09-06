---
name: website-research
description: Comprehensive website research & discovery workflow — run this at the start of every new website project to gather all design, content, style, and technical info before building
---

# Website Research & Discovery Workflow

This workflow follows the **Website Research & Discovery Skill** (`~/.codeium/windsurf/skills/website-research.md`) to comprehensively research and plan any website project before writing code. It reflects modern 2025-2026 research practices — using browser tools to visit and analyze reference sites, web search for industry trends, and structured documentation that can be referenced throughout the entire development process.

## When to Run
- Starting any new website project
- Redesigning or rebuilding an existing website
- When the user says `/website-research` or asks to "research" or "plan" a website
- When a user provides a reference website to clone or draw inspiration from

---

## Step 1: Project Discovery

1. Read any existing project files (README, prompt.md, package.json, etc.) to understand what exists
2. Ask the user the stakeholder questions from the skill (Phase 1.2):
   - Primary goal of the website
   - Target audience
   - Brand name and tagline
   - Existing brand assets (logo, colors, fonts)
   - Desired mood/feeling
   - 3-5 competitor or reference websites
   - Required pages/sections
   - Must-have features
   - Preferred tech stack or constraints
3. Compile a **Project Scope Summary** and confirm with the user

## Step 2: Competitive & Reference Research

1. For each competitor/reference website the user provides:
   - Navigate to the site using `mcp6_browser_navigate`
   - Take a full-page screenshot using `mcp6_browser_take_screenshot` with `fullPage: true`
   - Capture the accessibility snapshot using `mcp6_browser_snapshot` to understand structure
   - Document: layout, navigation, colors, typography, imagery, animations, content tone, CTAs, social proof, unique features, mobile behavior
2. Search the web for current design trends in the identified industry
3. Look for award-winning sites in the same category for inspiration
4. Compile a **Competitor Analysis Report** with screenshots and notes

## Step 3: Content Strategy

1. Plan the sitemap (all pages and their hierarchy)
2. For each page, document:
   - Page name and purpose
   - Key message (one sentence)
   - Content blocks needed (hero, features, about, services, testimonials, FAQ, CTA, etc.)
   - Copy direction (tone, reading level, length)
   - Media needs (images, videos, icons)
3. Define the brand voice and messaging hierarchy
4. Plan SEO keywords and meta tags for each page
5. Compile a **Content Plan Document**

## Step 4: Visual Design Research

1. **Color Palette** — Define primary, secondary, accent, background, text, and semantic colors with hex/OKLCH values. Include dark mode variant if applicable.
2. **Typography** — Select heading and body fonts, define type scale (h1-h6, body, small), line heights, letter spacing, and font loading strategy.
3. **Spacing & Layout** — Define spacing scale, container widths, grid system, section padding, and border radius scale.
4. **Imagery Direction** — Define photography style, image treatment, illustration style, icon set, and video/animation approach.
5. **Component Design** — Document visual direction for buttons, cards, forms, navigation, modals, hero, footer, testimonials, pricing, etc.
6. **Motion & Interaction** — Define page load animations, scroll-triggered effects, hover interactions, transition timing, and micro-interactions.
7. Compile a **Visual Design Specification**

## Step 5: UX & User Flow Research

1. Map the user journey for each primary persona (entry, first impression, key actions, decision path, conversion, exit)
2. Create text-based wireframe descriptions for each page:
   - Above-the-fold content
   - Content sections in order
   - CTA placement
   - Mobile layout (how sections stack)
3. Plan accessibility: color contrast, focus states, semantic HTML, alt text, keyboard nav, ARIA, reduced motion
4. Compile a **UX Plan Document**

## Step 6: Technical Research

1. Recommend a technology stack based on project needs:
   - Framework, styling, UI components, animations
   - CMS, e-commerce, forms, analytics
   - Hosting/deployment, database, auth
2. Define a performance budget (Lighthouse targets, bundle sizes, image/font budgets)
3. Plan technical SEO (meta tags, sitemap, robots.txt, canonical URLs, structured data, OG assets, favicons)
4. Define responsive breakpoints and mobile-first/desktop-first approach
5. Compile a **Technical Specification**

## Step 7: Compile & Save Research Document

1. Combine all findings into a single `research.md` file in the project root
2. Structure the document with clear sections:
   - Project Scope
   - Competitor Analysis
   - Industry Trends
   - Visual Direction / Mood Board
   - Sitemap & Information Architecture
   - Content Plan (page-by-page)
   - SEO Plan
   - Color Palette
   - Typography System
   - Spacing & Layout System
   - Component Design Direction
   - Motion & Interaction Plan
   - User Journey Maps
   - Wireframe Descriptions
   - Accessibility Plan
   - Tech Stack Recommendation
   - Performance Budget
   - Technical SEO Plan
   - Responsive Breakpoints
3. Save the file using `write_to_file`
4. Present a summary to the user and ask for confirmation before proceeding to development

## Step 8: Development Handoff

After the user confirms the research document:
1. Use the research document as the single source of truth for all development decisions
2. Reference it when making design, content, or architecture choices
3. If any decision deviates from the research, note the reason in the code or research doc
4. Keep the research document updated as scope evolves

---

## Quick Reference: Tool Usage

- `mcp6_browser_navigate` — Visit competitor/reference sites
- `mcp6_browser_take_screenshot` — Capture visual references (use `fullPage: true`)
- `mcp6_browser_snapshot` — Understand page structure and accessibility
- `search_web` — Research industry trends and find inspiration
- `read_url_content` — Read content from reference pages
- `write_to_file` — Save the research document
- `code_search` — Understand any existing project code
- `read_file` — Read existing project files for context
