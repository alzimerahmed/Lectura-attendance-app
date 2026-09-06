---
name: content
description: Comprehensive content & UX writing workflow — voice/tone, microcopy, content architecture, SEO, accessibility, and measurement
---

# Content & UX Writing Workflow

This workflow applies the **Content & UX Writing Skill** (`~/.codeium/windsurf/skills/content-ux-writing.md`) to craft user-centered content and UX copy.

## When to Run
- When writing or revising website content
- When the user says `/content` or asks about UX writing
- When designing new pages or features that need copy
- When creating a content strategy or style guide
- When auditing existing content

---

## Step 1: Read Context

1. Read the project's `research.md` if available — audience, brand, goals
2. Understand the product/service — what it does, who it's for
3. Review existing content — what exists, what's missing, what needs improvement
4. Identify key pages and user flows that need content
5. Understand brand voice — or define it if not yet established

## Step 2: Define Voice & Tone

1. Define 3-5 brand voice attributes (e.g., confident, warm, clear, practical)
2. Define anti-attributes — what the brand is NOT
3. Create tone matrix by context (onboarding, success, error, warning, empty state)
4. Write do/don't examples for each attribute
5. Document voice guidelines: contractions, directness, positivity, specificity, humanity

## Step 3: Content Architecture

1. Define information hierarchy for each page (H1 → H2 → H3)
2. Create page structure patterns for each page type
3. Plan content templates for reusable page types
4. Identify content components (CTAs, testimonials, feature cards)
5. Plan content reuse and modularity — single source of truth for shared content

## Step 4: Write Microcopy

1. **Buttons:** Action-oriented, specific, verb-first, 1-3 words
2. **Forms:** Labels (noun phrase), helper text, placeholders, validation messages
3. **Errors:** Specific, actionable, blameless, helpful, near the field
4. **Success messages:** Confirm what happened, specific, next steps if any
5. **Confirmation dialogs:** Clear consequence, specific action button, destructive styling
6. **Loading states:** Set expectations, skeleton screens, progress indicators
7. **Empty states:** What's empty, why, what to do, encouragement
8. **Navigation:** Clear, familiar, short, consistent, user language
9. **Links:** Descriptive, action-oriented, indicates destination

## Step 5: Write Page Content

1. **Landing pages:** Hero headline (5-10 words), subheadline, CTA, social proof, features (benefit-first), FAQ, final CTA
2. **Product pages:** Name, tagline, description, specs, pricing, reviews, CTA, FAQ
3. **Onboarding:** Welcome, step indicators, progress, completion, skip option, encouragement
4. **Documentation:** Title, overview, quick start, details, examples, API reference
5. **Blog posts:** Title, meta, intro, body, conclusion, author bio, related posts
6. **Email:** Subject line, preview text, body, CTA, personalization, unsubscribe

## Step 6: SEO Content Optimization

1. Integrate primary keyword in H1, first paragraph, meta title, URL
2. Place secondary keywords in H2s, body, image alt text
3. Write meta descriptions (150-160 chars, compelling, unique per page)
4. Structure headings hierarchically for SEO (H1 → H2 → H3)
5. Optimize for featured snippets (question → 40-60 word answer)
6. Add internal links with descriptive anchor text (3-5 per page)
7. Ensure appropriate content length for page type

## Step 7: AI Search Optimization (GEO)

1. Structure passages as self-contained 200-400 token blocks
2. Use answer-first structure (answer, then elaborate)
3. Define entities consistently (brand name, founding date, locations)
4. Add question-based headings with direct answers
5. Use citation-worthy formatting (statistics, definitions, comparisons)
6. Create llms.txt file at site root
7. Add FAQ schema structured data

## Step 8: Accessibility Review

1. Check reading level — aim for 8th grade (Flesch-Kincaid 60+)
2. Write alt text for all meaningful images — describe what the image conveys
3. Ensure all links are descriptive (not "click here" or "learn more")
4. Verify heading structure is logical and hierarchical (no skipped levels)
5. Check form labels are persistent and accessible
6. Use plain language: short sentences, common words, active voice, second person
7. Ensure error messages are specific, actionable, and blameless

## Step 9: Internationalization Preparation

1. Write for translation: short sentences, no idioms, no cultural references
2. Use consistent terminology throughout
3. Design for text expansion (German +30%, French +20%, Spanish +25%)
4. Use `Intl.DateTimeFormat`, `Intl.NumberFormat` for formatting
5. Use `Intl.PluralRules` for pluralization
6. Plan for RTL support with CSS logical properties
7. Avoid hardcoded text in images

## Step 10: Create Style Guide

1. Document voice and tone system (attributes, tone matrix, do/don'ts)
2. Define grammar rules (Oxford comma, capitalization, abbreviations)
3. Create terminology list (product names, feature names, industry terms)
4. Define formatting standards (headings, lists, bold, links, images)
5. Compile word list (preferred terms: "log in" not "login", etc.)
6. Include examples throughout — show, don't just tell

## Step 11: Measure & Iterate

1. Set up content metrics: time on page, bounce rate, scroll depth, conversion rate
2. Conduct user testing: 5-second test, comprehension test, task completion
3. A/B test key copy: headlines, CTAs, button text, error messages
4. Monitor search queries — what are users searching for on your site?
5. Review heatmaps — where are users clicking and scrolling?
6. Calculate content ROI: organic traffic, conversion rate, support reduction
7. Schedule regular content reviews — quarterly for key pages
