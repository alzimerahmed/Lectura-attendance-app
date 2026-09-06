---
name: search-optimization
description: Full-spectrum search & conversion optimization workflow covering SEO, GEO, SXO, AEO, CRO, SMO, and SEM — run during and after development to maximize visibility and conversions
---

# Search & Conversion Optimization Workflow

This workflow applies the **Search & Conversion Optimization Skill** (`~/.codeium/windsurf/skills/search-conversion-optimization.md`) to ensure every website is optimized for the full spectrum of modern search and conversion disciplines — SEO, GEO, SXO, AEO, CRO, SMO, and SEM.

## When to Run
- During development — implement technical SEO, schema, and meta tags as you build
- After the Website Research workflow completes — apply the SEO plan from `research.md`
- After the Claude Taste workflow completes — ensure the design doesn't hurt Core Web Vitals
- When the user says `/search-optimization` or asks about SEO, AI search, or conversions
- After launch — for ongoing optimization, testing, and measurement

---

## Step 1: Read Context

1. Read the project's `research.md` — it contains the SEO keyword plan, meta tag plan, sitemap, and technical SEO plan
2. Read existing project structure — identify pages, routes, components
3. Read `package.json` or equivalent — understand the framework (Next.js, Astro, etc.)
4. Identify: is this SSR/SSG/CSR? (Critical for SEO and GEO)
5. Check for existing meta tags, schema, sitemap, robots.txt

## Step 2: Implement Technical SEO Foundation

Build these into the project structure — not as an afterthought:

1. **Meta tag system:**
   - Dynamic title tags (50-60 chars, keyword near start, brand at end)
   - Dynamic meta descriptions (150-160 chars, unique per page)
   - Canonical URLs on every page
   - Meta robots tags where needed

2. **robots.txt:**
   - Allow all AI crawlers (GPTBot, OAI-SearchBot, ChatGPT-User, ClaudeBot, PerplexityBot, Google-Extended)
   - Disallow admin, search, filter pages
   - Reference sitemap URL

3. **Sitemap:**
   - Auto-generated XML sitemap at `/sitemap.xml`
   - Include all indexable pages with lastmod dates
   - Submit to Google Search Console and Bing Webmaster Tools

4. **Core Web Vitals:**
   - LCP < 2.5s — preload hero image, optimize server response, SSR/SSG
   - INP < 200ms — minimize JS, defer non-critical scripts
   - CLS < 0.1 — explicit dimensions on all images/ads/embeds

5. **URL structure:**
   - Short, descriptive, keyword-rich (`/guide/seo-optimization`)
   - Hyphenated, lowercase, no trailing slashes (or consistently one way)
   - No query parameters for content pages

## Step 3: Implement Structured Data (Schema.org)

Add JSON-LD structured data for:

1. **Organization schema** — with `sameAs` array linking to all authoritative profiles (LinkedIn, GitHub, Wikipedia, Crunchbase, G2)
2. **WebSite schema** — with `SearchAction` for sitelinks search box
3. **Article/BlogPosting schema** — for all content pages (author, datePublished, dateModified, image)
4. **FAQPage schema** — for FAQ sections (critical for AEO and featured snippets)
5. **BreadcrumbList schema** — for breadcrumb navigation
6. **Product schema** — for product pages (price, availability, reviews)
7. **LocalBusiness schema** — for local businesses (NAP, hours, geo)
8. **HowTo schema** — for step-by-step guides

Validate all schema with Google Rich Results Test.

## Step 4: Implement GEO (Generative Engine Optimization)

1. **Verify AI crawler access** — confirm robots.txt allows all AI bots
2. **Server-side rendering** — ensure content is rendered server-side (AI crawlers struggle with JS)
3. **Passage-level optimization:**
   - Write self-contained paragraphs (150-300 words each)
   - Use question-form H2/H3 headings that match likely query phrasings
   - Answer-first structure (lead with the answer, then elaborate)
   - Include citations, quotations, and statistics in content
4. **Entity building:**
   - Implement Organization schema with sameAs links
   - Create consistent brand facts across all platforms
   - Consider Wikidata entry and Wikipedia article
5. **llms.txt file** — add at site root with structured content overview
6. **Test AI visibility** — ask ChatGPT/Perplexity about the brand/topic

## Step 5: Implement AEO (Answer Engine Optimization)

1. **Question-based content structure:**
   - Research questions with AnswerThePublic, AlsoAsked, Google PAA
   - Structure content around questions your audience asks
   - One question per H2 section

2. **Concise answer blocks:**
   - 40-60 word answers immediately after question headings
   - Definition format: "X is a Y that does Z"
   - Lists for process questions, tables for comparisons

3. **FAQ sections:**
   - Add FAQ section at the bottom of articles
   - Implement FAQPage schema for all FAQ content
   - Answer every People Also Ask question related to your topic

4. **Voice search optimization:**
   - Conversational, natural language answers
   - Local optimization for "near me" queries
   - 40-60 word answers that can be read aloud

## Step 6: Implement SXO (Search Experience Optimization)

1. **Search intent matching:**
   - Classify each page by intent (informational, navigational, commercial, transactional)
   - Match content format and CTA style to intent

2. **CTR optimization:**
   - Compelling title tags with current year, numbers, power words
   - Meta descriptions with value proposition and implicit CTA
   - Rich snippets via schema markup

3. **Engagement optimization:**
   - Above-the-fold content delivers the promise from the SERP
   - Scannable content (short paragraphs, bullet lists, bold key points)
   - Internal linking to keep users on site
   - Mobile-first experience

4. **Zero-click optimization:**
   - Win featured snippets with concise answer blocks
   - Build entity signals for knowledge panel
   - Track impressions, not just clicks

## Step 7: Implement CRO (Conversion Rate Optimization)

1. **Landing page optimization:**
   - Clear headline matching search/ad intent
   - Single prominent CTA above the fold
   - Trust signals visible (reviews, ratings, guarantees)
   - Progressive disclosure — essential info first

2. **Form optimization:**
   - Minimize fields (ask only for essentials)
   - Single column, top-aligned labels
   - Inline validation as user types
   - Guest checkout (no forced registration)

3. **CTA design:**
   - Action-oriented copy ("Get started" not "Submit")
   - Contrasting but harmonious color
   - Above the fold + repeated at decision points
   - Mobile thumb zone placement

4. **Trust signals:**
   - Testimonials with real names, photos, titles
   - Reviews and ratings (third-party verified)
   - Security badges, SSL, privacy policy
   - Pricing transparency

5. **A/B testing plan:**
   - Identify highest-impact pages for testing
   - Form hypotheses based on data (heatmaps, analytics)
   - Set up testing infrastructure (GA4 experiments, Optimizely, VWO)

## Step 8: Implement SMO (Social Media Optimization)

1. **Open Graph tags** — for every page, dynamically:
   - `og:title`, `og:description`, `og:image` (1200x630px)
   - `og:url`, `og:site_name`, `og:type`

2. **Twitter/X Card tags** — for every page:
   - `twitter:card` (summary_large_image)
   - `twitter:title`, `twitter:description`, `twitter:image`
   - `twitter:site`, `twitter:creator`

3. **Shareable content structure:**
   - Numbered lists, data/statistics, practical tools
   - Social sharing buttons at top and bottom
   - Click-to-tweet boxes for key quotes

4. **Social proof integration:**
   - Share counts, testimonials, UGC displays
   - Real-time activity notifications

## Step 9: Set Up Analytics & Measurement

1. **GA4 setup:**
   - Enhanced measurement (page views, scrolls, outbound clicks, site search)
   - Custom events for key conversions (sign_up, purchase, lead, contact)
   - Conversion value tracking for ROAS
   - Filter AI referral traffic (chat.openai.com, perplexity.ai, gemini.google.com, claude.ai)

2. **Google Search Console:**
   - Verify site ownership
   - Submit sitemap
   - Monitor impressions, clicks, CTR, position
   - Check Core Web Vitals report
   - Review coverage and enhancement reports

3. **Conversion tracking:**
   - Google Ads conversion actions (if running paid search)
   - Enhanced conversions with hashed email
   - Consent Mode v2 for GDPR compliance

4. **GEO measurement:**
   - Track AI referral traffic in GA4
   - Test brand visibility in ChatGPT/Perplexity monthly
   - Monitor Share of Model for key queries

## Step 10: SEM Setup (If Applicable)

1. **Campaign structure:**
   - 3-4 campaigns (branded, non-branded, remarketing)
   - 3-10 Single Theme Ad Groups per campaign
   - Broad match + Smart Bidding as default

2. **Ad copy:**
   - Responsive Search Ads with 15 headlines, 4 descriptions
   - Target "Good" or "Excellent" Ad Strength
   - Tight relevance chain: Query → Keyword → Ad → Landing Page

3. **Bid strategy:**
   - Start with Maximize Conversions (no target) for 2-4 weeks
   - Then tCPA or tROAS within 10-20% of actual performance
   - Need 30+ conversions/month for Smart Bidding to work

4. **Negative keywords:**
   - Review search term reports weekly
   - Add negatives for irrelevant queries
   - Build negative keyword lists per campaign

5. **Remarketing:**
   - Site visitor retargeting
   - Cart abandonment retargeting
   - Dynamic remarketing for products
   - Frequency cap at 3-5 per day

---

## Quick Reference: Optimization Checklist

### Technical SEO
- [ ] Dynamic title tags (50-60 chars) on every page
- [ ] Dynamic meta descriptions (150-160 chars) on every page
- [ ] Canonical URLs on every page
- [ ] robots.txt allowing all AI crawlers
- [ ] XML sitemap auto-generated and submitted
- [ ] Core Web Vitals: LCP < 2.5s, INP < 200ms, CLS < 0.1
- [ ] Server-side rendering or static generation
- [ ] Semantic HTML with proper landmarks
- [ ] Heading hierarchy: one H1, logical H2-H3 nesting
- [ ] Internal linking strategy (3-5 links per page)
- [ ] Breadcrumb navigation with schema

### Structured Data
- [ ] Organization schema with sameAs links
- [ ] WebSite schema with SearchAction
- [ ] Article/BlogPosting schema on content pages
- [ ] FAQPage schema on FAQ sections
- [ ] BreadcrumbList schema
- [ ] Product schema on product pages (if applicable)
- [ ] LocalBusiness schema (if applicable)
- [ ] Validated with Google Rich Results Test

### GEO
- [ ] AI crawlers allowed in robots.txt
- [ ] Server-side rendered content
- [ ] Self-contained passages (150-300 words)
- [ ] Question-form headings
- [ ] Answer-first structure
- [ ] Citations and statistics in content
- [ ] Entity signals (Organization schema, sameAs, consistent brand facts)
- [ ] llms.txt file at site root

### AEO
- [ ] Question-based content structure
- [ ] 40-60 word answer blocks after question headings
- [ ] FAQ sections with FAQPage schema
- [ ] Voice search-optimized (conversational answers)
- [ ] Featured snippet-formatted content (lists, tables, definitions)

### SXO
- [ ] Search intent matched to content format and CTA
- [ ] CTR-optimized title tags and meta descriptions
- [ ] Above-the-fold content delivers SERP promise
- [ ] Scannable content structure
- [ ] Mobile-first experience
- [ ] Internal linking for engagement

### CRO
- [ ] Clear headline matching intent
- [ ] Single prominent CTA above the fold
- [ ] Trust signals visible
- [ ] Forms minimized (single column, top-aligned labels, inline validation)
- [ ] Guest checkout option
- [ ] A/B testing infrastructure ready

### SMO
- [ ] Open Graph tags on every page (1200x630px image)
- [ ] Twitter/X Card tags on every page
- [ ] Social sharing buttons at top and bottom
- [ ] Shareable content structure (lists, data, practical tools)

### Analytics
- [ ] GA4 with enhanced measurement and custom events
- [ ] Google Search Console verified and sitemap submitted
- [ ] Conversion tracking with values
- [ ] AI referral traffic filtered in GA4
- [ ] Consent Mode v2 implemented (if EU traffic)
