---
name: Search & Conversion Optimization Skill
description: Full-spectrum modern web optimization covering SEO, GEO, SXO, AEO, CRO, SMO, and SEM — 2025-2026 standards including AI search engines, voice search, and evolving SERP features
version: 1.0.0
tags: [seo, geo, sxo, aeo, cro, smo, sem, optimization, conversion, search, ai-search]
---

# Search & Conversion Optimization Skill

## Purpose
This skill encodes the full spectrum of modern web optimization disciplines for any kind of website. It reflects **modern 2025-2026 optimization standards** including AI search engines, voice search, and evolving SERP features — not outdated keyword-stuffing tactics or generic checklists. Every recommendation should be grounded in the specific project's audience, goals, and content.

## Core Philosophy

**Optimization is not a checklist — it's a continuous discipline.** The search landscape has fractured across Google, AI engines (ChatGPT, Perplexity, Gemini, Claude), voice assistants, and social platforms. A modern optimization strategy must address all surfaces where users discover, evaluate, and convert.

**The #1 rule:** Optimize for the user first, the engine second. Content that genuinely answers questions, solves problems, and provides value will outperform content engineered purely for algorithms — especially as AI engines increasingly reward authenticity, specificity, and source transparency.

---

## Part 1: SEO (Search Engine Optimization)

### 1.1 On-Page Optimization

**Meta tags:**
- **Title tag:** 50-60 characters, primary keyword near the start, brand name at end. Unique per page. Write for humans, optimize for intent.
- **Meta description:** 150-160 characters. Not a ranking factor directly, but influences CTR. Include keyword, value proposition, and implicit CTA. Unique per page.
- **Meta robots:** `index, follow` by default. Use `noindex` for thin/duplicate pages, search results, filter pages.

**Heading hierarchy:**
- One `H1` per page — matches or closely aligns with the title tag
- `H2` for major sections, `H3` for subsections — never skip levels
- Headings should match likely query phrasings (question-form headings perform well for AI/featured snippets)
- Use descriptive headings that work as standalone summaries when extracted

**Semantic HTML:**
- Use HTML5 landmarks: `<header>`, `<nav>`, `<main>`, `<article>`, `<section>`, `<aside>`, `<footer>`
- `<article>` for self-contained content, `<section>` for thematic groupings
- `<time datetime="">` for dates, `<address>` for contact info
- Proper `<ul>`/`<ol>` for lists, `<table>` with `<thead>`/`<tbody>` for tabular data

**Internal linking:**
- Link from high-authority pages to newer/important pages
- Use descriptive anchor text (not "click here" or "read more")
- Aim for 3-5 internal links per page minimum
- Create topic clusters: pillar page → sub-topic pages → interlink
- Use breadcrumb navigation with schema markup

**Content optimization:**
- **E-E-A-T signals:** Experience, Expertise, Authoritativeness, Trustworthiness
  - Author bylines with credentials and bios
  - Original research, case studies, first-hand experience
  - Citations to authoritative sources
  - Transparent methodology and data sources
- **Content depth:** Cover the topic comprehensively, not just keyword density
- **Search intent matching:** Informational (guide, tutorial), Navigational (brand, product page), Commercial (comparison, review), Transactional (buy, sign up, download)
- **Freshness signals:** Update dates, refresh content regularly, add new sections
- **Entity optimization:** Define and connect entities (people, places, organizations, concepts) within content

### 1.2 Technical SEO

**Core Web Vitals (2026 thresholds):**
- **LCP (Largest Contentful Paint):** < 2.5s — optimize hero image, preload critical resources, server response time < 600ms
- **INP (Interaction to Next Paint):** < 200ms — minimize JavaScript, defer non-critical scripts, use web workers for heavy computation
- **CLS (Cumulative Layout Shift):** < 0.1 — set explicit dimensions on images/ads/embeds, avoid injecting content above existing content

**Crawlability:**
- Server-side rendering (SSR) or static generation preferred — AI crawlers and some search engines struggle with JavaScript-heavy pages
- If using client-side rendering, implement dynamic rendering or pre-rendering for bots
- Ensure no `noindex` tags on pages you want indexed
- Check robots.txt doesn't block important resources (CSS, JS, images)
- Use `fetch as Google` / URL inspection to verify rendering

**Sitemaps:**
- XML sitemap at `/sitemap.xml` — auto-generated, includes all indexable pages
- Image sitemap for important visual content
- News sitemap for news content (if applicable)
- Video sitemap for video content
- Submit to Google Search Console and Bing Webmaster Tools

**Robots.txt:**
```
# Allow all AI crawlers (critical for GEO)
User-agent: GPTBot
Allow: /

User-agent: OAI-SearchBot
Allow: /

User-agent: ChatGPT-User
Allow: /

User-agent: ClaudeBot
Allow: /

User-agent: Claude-SearchBot
Allow: /

User-agent: PerplexityBot
Allow: /

User-agent: Google-Extended
Allow: /

# Standard search engines
User-agent: *
Allow: /
Disallow: /admin/
Disallow: /search?
Disallow: /*?filter=

Sitemap: https://example.com/sitemap.xml
```

**Canonical URLs:**
- `<link rel="canonical" href="https://example.com/page" />` on every page
- Self-referencing canonical on the canonical version
- Use canonical tags to consolidate duplicate/near-duplicate content
- Use `rel="alternate" hreflang="x"` for international content

**Schema.org structured data:**
- **Organization:** `Organization` schema with `sameAs` array linking to all authoritative profiles (LinkedIn, GitHub, Wikipedia, Crunchbase, G2)
- **WebSite:** `WebSite` schema with `SearchAction` for sitelinks search box
- **Article:** `Article` or `BlogPosting` schema with author, datePublished, dateModified, image
- **Product:** `Product` schema with offers, reviews, aggregateRating
- **FAQPage:** `FAQPage` schema for question-based content (critical for AEO)
- **BreadcrumbList:** For breadcrumb navigation
- **LocalBusiness:** For local businesses with NAP, hours, geo
- **HowTo:** For step-by-step guides
- **VideoObject:** For video content
- Use JSON-LD format (not Microdata or RDFa)
- Validate with Google's Rich Results Test

### 1.3 Off-Page SEO

**Backlink strategy:**
- Focus on quality over quantity — one link from an authoritative site > 100 low-quality links
- Earn links through: original research/data, expert commentary, digital PR, resource pages, broken link building
- Monitor backlink profile with Ahrefs, Semrush, or Search Console
- Disavow toxic/spammy links
- Build topical authority by getting links from sites in your niche

**Social signals:**
- Active social presence correlates with search visibility
- Content engagement (shares, comments) signals relevance
- Brand mentions (even without links) are recognized by search engines
- Co-citations and co-occurrences help AI engines understand entity relationships

### 1.4 Keyword Research Methodology

1. **Seed keywords:** Start with core topics and product/service terms
2. **Expand:** Use keyword tools (Ahrefs, Semrush, Google Keyword Planner) to find related terms
3. **Analyze intent:** Classify each keyword by search intent (informational, navigational, commercial, transactional)
4. **Assess difficulty:** Prioritize low-difficulty, high-intent keywords first
5. **SERP analysis:** Look at what currently ranks — what format, depth, and features appear
6. **Long-tail keywords:** Target specific, lower-volume queries with clear intent
7. **Question keywords:** Target "how", "what", "why", "when", "where" queries for AEO/GEO
8. **Cluster:** Group keywords by topic to create content clusters around pillar pages

### 1.5 SERP Feature Targeting

| Feature | How to Target |
|---|---|
| **Featured Snippet** | Answer the question concisely in 40-60 words, use question-form H2, format with lists/tables |
| **People Also Ask** | Create FAQ sections answering related questions, use FAQPage schema |
| **Knowledge Panel** | Build entity signals: Wikidata entry, Wikipedia article, Organization schema with sameAs |
| **Image Pack** | Optimize image filenames, alt text, surrounding text, use ImageObject schema |
| **Video Carousel** | Use VideoObject schema, video sitemap, descriptive titles and descriptions |
| **Local Pack** | Google Business Profile, local citations, review strategy, LocalBusiness schema |
| **AI Overview** | Strong SEO + E-E-A-T + structured data + direct answers (see GEO section) |
| **Site Links** | Clear site architecture, internal linking, descriptive navigation labels |

---

## Part 2: GEO (Generative Engine Optimization)

GEO is the practice of optimizing content for retrieval and citation by generative AI search systems — ChatGPT, Perplexity, Gemini, Claude, Google AI Overviews, and Microsoft Copilot.

### 2.1 How AI Engines Cite Content

AI engines split crawled pages into **200-400 token passages** (roughly 150-300 words), embed each passage, and retrieve the top-N by semantic similarity to the query. The LLM composes an answer from those passages and cites their source URLs.

**Your page is never the citation unit — the passage is.**

### 2.2 Passage-Level Optimization

1. **Self-contained passages:** Write each paragraph as if it might be extracted in isolation. A passage that fully answers a question without requiring context from earlier paragraphs is more likely to be cited.

2. **Heading-to-passage anchoring:** Most retrieval systems anchor passages to their nearest preceding heading. Use descriptive H2/H3 headings that match likely query phrasings — question-form headings ("How do I [X]?") perform well.

3. **Answer-first structure:** 44.2% of AI citations come from the first 30% of a page's content. Lead with the direct answer, then elaborate.

4. **Citation-worthy formatting:**
   - Cite your own sources with inline references
   - Include quotations from named experts or studies
   - Add statistics — concrete numbers, not vague claims
   - Use authoritative, confident, specific language
   - Clean, readable, fluent writing (fluency optimization is a proven GEO tactic)

5. **Fact density and source transparency:**
   - Back claims with data, studies, and verifiable sources
   - Include methodology descriptions
   - Link to primary sources
   - Show your work — AI engines prefer content that demonstrates rigor

### 2.3 Entity-Based Content Modeling

AI engines treat brands as entities. Build your entity stack:

1. **Wikidata entry:** Create a Q-ID for your brand with key facts
2. **Organization schema with sameAs:** Link to all authoritative profiles (LinkedIn, GitHub, Wikipedia, Crunchbase, G2, industry directories)
3. **Wikipedia article:** If notable enough, a Wikipedia article is the strongest entity signal
4. **Consistent brand facts:** Same name, description, founding date, founders across all platforms
5. **Co-citations and co-occurrences:** Get mentioned alongside related entities on authoritative sites

**Entity authority (0.664 correlation) beats backlinks (0.218) for AI citation.**

### 2.4 Technical Accessibility for AI Crawlers

Allow AI crawlers in robots.txt (see Section 1.2). Critical bots:
- **GPTBot, OAI-SearchBot, ChatGPT-User** (OpenAI/ChatGPT)
- **ClaudeBot, Claude-SearchBot** (Anthropic/Claude)
- **PerplexityBot** (Perplexity)
- **Google-Extended** (Gemini/AI Overviews)

If AI crawlers can't reach your pages, nothing else matters. Bot access is the #1 cause of AI invisibility.

### 2.5 Platform-Specific Tactics

| Platform | What Works |
|---|---|
| **ChatGPT** | Server-side rendered content, brand entity signals, multi-platform presence (Reddit, YouTube), fresh content (26% recency bias) |
| **Perplexity** | Verifiable cited sources, real-time niche expert content, fact-dense passages, fastest feedback loop (2-4 weeks) |
| **Google AI Overviews** | Strong SEO correlates directly, E-E-A-T signals, structured data, direct answers. Only 38% of citations from top-10 pages (down from 76%) |
| **Gemini** | Google index retrieval, entity strength, same as AI Overviews |
| **Claude** | Anthropic's crawler, values well-structured content with clear sourcing |

### 2.6 llms.txt File

Consider adding an `/llms.txt` file at your site root — a markdown-formatted file that provides AI systems with a structured overview of your content, similar to how robots.txt guides crawlers. Include:
- Project/brand description
- Key pages and their purposes
- API documentation links (if applicable)
- Content hierarchy and navigation guide

### 2.7 GEO Measurement

- **Share of Model:** How often your brand appears in AI answers for relevant queries
- **AI referral traffic:** Filter GA4 sessions by source: `chat.openai.com`, `perplexity.ai`, `gemini.google.com`, `claude.ai`
- **Direct brand testing:** Ask ChatGPT/Perplexity "What is [your brand]?" and "Who founded [your brand]?" — if the AI gets it right, entity is established
- **Citation tracking:** Monitor which pages/passages get cited across platforms
- **Conversion from AI:** AI referral visitors convert at ~14-16% vs <3% for Google organic

---

## Part 3: SXO (Search Experience Optimization)

SXO blends SEO and UX to optimize the entire journey from SERP impression to conversion. You must earn the click, earn the scroll, and earn the conversion.

### 3.1 Search Intent Matching

| Intent Type | User Goal | Content Format | CTA Style |
|---|---|---|---|
| **Informational** | Learn/understand | Guide, tutorial, explainer | Soft (newsletter, related articles) |
| **Navigational** | Find specific page | Brand/product page | Direct (navigate, sign in) |
| **Commercial** | Compare/evaluate | Comparison, review, listicle | Medium (demo, quote, trial) |
| **Transactional** | Buy/act | Product, pricing, checkout | Strong (buy, sign up, download) |

### 3.2 User Journey Optimization (SERP → Conversion)

1. **SERP impression:** Title tag + meta description + rich snippet → maximize CTR
2. **Click:** Page loads fast (LCP < 2.5s) → prevent bounce
3. **First scroll:** Above-the-fold content matches the promise from the SERP → earn the scroll
4. **Engagement:** Content delivers value, easy to scan, visually appealing → extend dwell time
5. **Conversion:** Clear CTA, minimal friction, trust signals → convert
6. **Post-conversion:** Thank you page, next steps, cross-sell → extend relationship

### 3.3 CTR Optimization

- **Title tag:** Include current year for freshness, numbers for listicles, power words, brackets for context `[2026 Guide]`
- **Meta description:** Include value proposition, keyword, implicit CTA
- **Rich snippets:** Implement schema to earn enhanced SERP features (stars, FAQ, breadcrumbs)
- **URL structure:** Short, descriptive, keyword-rich (`/guide/seo-optimization` not `/p=123`)
- **Favicon:** Ensure favicon is set — appears in SERP on mobile

### 3.4 Dwell Time and Engagement Signals

- **Fast load:** Bounce rate doubles when load time goes from 1s to 3s
- **Above-the-fold quality:** Deliver the answer/value immediately — don't bury it
- **Scannable content:** Short paragraphs, bullet lists, bold key points, descriptive headings
- **Visual content:** Images, charts, videos break up text and increase engagement
- **Internal linking:** Keep users on your site with relevant next-step links
- **Mobile experience:** 60%+ of searches are mobile — mobile UX directly impacts engagement signals

### 3.5 Zero-Click Search Optimization

~60% of Google searches end without a click. Optimize for zero-click by:
- **Winning the snippet:** If users get the answer in the SERP, your brand still gains visibility
- **Brand visibility in AI Overviews:** Being cited in the AI answer builds brand awareness even without a click
- **Knowledge panel presence:** Build entity signals to earn knowledge panel real estate
- **Feature image:** Optimize for image pack inclusion — images get impressions without clicks
- **Measure impressions, not just clicks:** Track Search Console impression share and average position

### 3.6 Rich Snippet Targeting

| Rich Snippet | Schema Type | Requirements |
|---|---|---|
| **FAQ** | `FAQPage` | Question + answer pairs, visible on page |
| **How-to** | `HowTo` | Step-by-step instructions with time estimates |
| **Product** | `Product` | Price, availability, reviews, rating |
| **Recipe** | `Recipe` | Ingredients, cook time, nutrition, image |
| **Review** | `Review` or `AggregateRating` | Rating, author, item reviewed |
| **Event** | `Event` | Name, date, location, offers |
| **Article** | `Article` / `BlogPosting` | Headline, author, date, image |
| **Breadcrumb** | `BreadcrumbList` | Navigation path |
| **Video** | `VideoObject` | Thumbnail, duration, upload date |
| **Organization** | `Organization` | Logo, contact, sameAs links |

---

## Part 4: AEO (Answer Engine Optimization)

AEO optimizes content so search platforms can directly provide answers — through AI chatbots, voice assistants, featured snippets, and AI-generated answers.

### 4.1 Featured Snippet Optimization

- **Target question-form queries:** "How to...", "What is...", "Why does..."
- **Answer concisely:** 40-60 word answer block immediately after the question heading
- **Use the right format:** Paragraph (definitions), list (steps/items), table (comparisons)
- **Repeat the question:** Include the full question in the answer for context
- **Position the answer:** Place the answer block directly below the H2 question heading
- **Don't give everything away:** Provide enough to win the snippet, but leave depth for the click

### 4.2 FAQ Schema Implementation

```json
{
  "@context": "https://schema.org",
  "@type": "FAQPage",
  "mainEntity": [{
    "@type": "Question",
    "name": "What is search engine optimization?",
    "acceptedAnswer": {
      "@type": "Answer",
      "text": "Search engine optimization (SEO) is the practice of improving a website's visibility in search engine results pages through on-page, technical, and off-page optimization strategies."
    }
  }]
}
```

### 4.3 Question-Based Content Structuring

- Structure content around questions your audience asks
- Use tools: AnswerThePublic, AlsoAsked, Google "People Also Ask", Quora, Reddit
- Create FAQ sections at the bottom of articles
- Answer one question per H2 section — makes extraction clean for AI engines
- Group related questions into FAQ pages with FAQPage schema

### 4.4 Concise Answer Formatting (40-60 Word Blocks)

```
## What is GEO (Generative Engine Optimization)?

GEO (Generative Engine Optimization) is the practice of optimizing digital content for citation by AI-powered search engines like ChatGPT, Perplexity, and Google AI Overviews. Unlike traditional SEO, which targets ranking positions, GEO targets being cited within AI-generated answers through passage-level optimization, entity signals, and structured data.
```

### 4.5 Voice Search Optimization

- **Conversational queries:** Voice searches are longer and more natural ("What's the best Italian restaurant near me?" not "Italian restaurant NYC")
- **Question-form content:** Answer who, what, where, when, why, how questions
- **Local optimization:** Voice searches are often local — optimize Google Business Profile
- **Featured snippet targeting:** Voice assistants read featured snippets aloud
- **Schema markup:** FAQPage, LocalBusiness, HowTo schemas help voice assistants find answers
- **Page speed:** Voice results often come from fast-loading pages
- **Concise answers:** Voice assistants prefer 40-60 word answers they can read aloud

### 4.6 Direct Answer Targeting

- **Definition boxes:** "X is a Y that does Z" format
- **Step-by-step lists:** Numbered lists for process questions
- **Comparison tables:** For "X vs Y" queries
- **Data points:** Statistics and numbers for quantitative queries
- **People also ask:** Answer every PAA question related to your topic

---

## Part 5: CRO (Conversion Rate Optimization)

### 5.1 Landing Page Optimization

**Above the fold:**
- Clear, specific headline that matches the ad/search intent
- Subheadline that clarifies the value proposition
- Single, prominent CTA
- Trust signal (logo, rating, testimonial, guarantee)
- Hero image/video that demonstrates the product or outcome

**Page structure:**
- **F-pattern or Z-pattern:** Match reading patterns with visual hierarchy
- **One column for mobile, max two for desktop** — reduce cognitive load
- **Progressive disclosure:** Show essential info first, details on scroll/interaction
- **Sticky CTA on mobile:** Keep the conversion button accessible

### 5.2 A/B Testing Methodology

1. **Hypothesis:** "Changing X to Y will increase conversion by Z% because [reason]"
2. **Sample size calculation:** Use a significance calculator — need enough traffic for statistical power (typically 95% confidence)
3. **One variable at a time:** Test one change per experiment (headline, CTA color, form length, layout)
4. **Duration:** Run for at least 2 weeks or 1 full business cycle to account for day-of-week variation
5. **Measure:** Primary metric (conversion rate) + secondary metrics (bounce rate, time on page, scroll depth)
6. **Analyze:** Statistical significance, not just "it looks better"
7. **Iterate:** Winner becomes the new control; test the next hypothesis

### 5.3 Funnel Analysis

- Map the full funnel: Visit → Engage → Consider → Convert → Retain
- Identify drop-off points using GA4 funnel reports
- Optimize the biggest drop-off first (highest impact)
- Measure micro-conversions: email signup, demo request, add to cart, trial start
- Attribution: Use data-driven attribution in GA4, not last-click only

### 5.4 Heatmaps and Session Recording Analysis

- **Heatmaps:** Identify where users click, scroll, and hover — find attention hotspots and dead zones
- **Session recordings:** Watch real user sessions to find friction points, confusion, rage clicks
- **Form analytics:** See which fields cause abandonment
- **Tools:** Hotjar, Clarity, FullStory, VWO
- **Act on data:** Don't just collect — identify patterns, form hypotheses, test fixes

### 5.5 Form Optimization

- **Minimize fields:** Every additional field reduces completion rate. Ask only for what's essential
- **Single column:** Multi-column forms have lower completion rates
- **Top-aligned labels:** Labels above inputs perform better than left-aligned or placeholder-only
- **Smart defaults:** Pre-fill where possible (country from IP, name from account)
- **Inline validation:** Validate as the user types, not on submit. Show green checkmarks, not red errors
- **Progress indicators:** For multi-step forms, show progress (Step 2 of 3)
- **Guest checkout:** Don't force account creation to convert
- **Autofill support:** Use `autocomplete` attributes correctly

### 5.6 CTA Design and Placement

- **Action-oriented copy:** "Get started" > "Submit". "Download free guide" > "Download". Benefit + action.
- **Contrasting color:** CTA should stand out from page palette — but harmonize, not clash
- **Size and whitespace:** CTA should be prominent with breathing room
- **Above the fold:** At least one CTA visible without scrolling
- **Repeat CTAs:** Place at natural decision points throughout the page
- **Mobile thumb zone:** Place CTAs in the lower 2/3 of mobile screens for thumb accessibility
- **Urgency (when genuine):** "Limited time", "Only 3 left" — only when true and enforceable

### 5.7 Trust Signals

- **Social proof:** Testimonials with real names, photos, and titles. Case studies with measurable results.
- **Reviews and ratings:** Star ratings, review count, third-party review badges (Trustpilot, G2)
- **Client logos:** Real client logos, not grayscale stock. Better: name clients in a sentence.
- **Certifications and badges:** Industry certifications, security badges, partner status
- **Guarantees:** Money-back guarantee, free trial, no credit card required
- **Transparency:** Pricing transparency, team page, about page with real photos
- **Security signals:** SSL certificate, privacy policy, terms of service, GDPR compliance

### 5.8 Friction Reduction

- **Page speed:** Every 1s delay reduces conversions by 7%
- **Simplify navigation:** Remove distractions on conversion pages — no header nav on checkout
- **Reduce choices:** Paradox of choice — fewer options convert better
- **Clear error messages:** Specific, actionable, non-blaming
- **Guest options:** Don't force registration to convert
- **Multiple payment options:** Credit card, PayPal, Apple Pay, Google Pay, BNPL
- **Progressive profiling:** Ask for info over time, not all at once

### 5.9 Psychological Triggers

| Trigger | How to Use Ethically |
|---|---|
| **Urgency** | "Offer ends Friday" — only when the deadline is real |
| **Scarcity** | "Only 3 seats left" — only when inventory is actually limited |
| **Reciprocity** | Give value first (free guide, tool, audit) before asking for anything |
| **Social proof** | "Join 10,000+ subscribers" — real numbers only |
| **Authority** | Expert author bylines, certifications, media mentions |
| **Commitment** | Start with a small ask (free trial) before the bigger ask (paid plan) |
| **Loss aversion** | "Don't miss out" framing — more powerful than "gain X" framing |
| **Anchoring** | Show original price next to discounted price |

---

## Part 6: SMO (Social Media Optimization)

### 6.1 Open Graph Optimization

```html
<meta property="og:type" content="website" />
<meta property="og:title" content="Page Title — Brand" />
<meta property="og:description" content="Compelling description that makes people want to click" />
<meta property="og:image" content="https://example.com/og-image.jpg" />
<meta property="og:image:width" content="1200" />
<meta property="og:image:height" content="630" />
<meta property="og:url" content="https://example.com/page" />
<meta property="og:site_name" content="Brand Name" />
<meta property="og:locale" content="en_US" />
```

**OG image specs (2026):**
- **Size:** 1200x630px (works across Facebook, X, LinkedIn, Slack, Discord)
- **Format:** JPG or PNG, < 1MB
- **Content:** Brand logo, headline text, relevant imagery, high contrast
- **Text-safe area:** Keep critical content in center 800x400px (platforms crop differently)

### 6.2 Twitter/X Card Optimization

```html
<meta name="twitter:card" content="summary_large_image" />
<meta name="twitter:title" content="Page Title — Brand" />
<meta name="twitter:description" content="Compelling description" />
<meta name="twitter:image" content="https://example.com/twitter-card.jpg" />
<meta name="twitter:site" content="@brandhandle" />
<meta name="twitter:creator" content="@authorhandle" />
```

**Card types:**
- `summary_large_image` — large image card, best for articles/pages (1200x600px)
- `summary` — small square image card (1200x1200px)
- `player` — for video/audio content

### 6.3 Shareable Content Structuring

- **Numbered lists:** "7 ways to..." — highly shareable and scannable
- **Data and statistics:** Original research gets shared and cited
- **Infographics:** Visual data representations are highly shareable
- **Controversial (but defensible) takes:** Challenge conventional wisdom with evidence
- **Practical tools and templates:** Things people can use immediately
- **Emotional resonance:** Content that makes people feel something gets shared
- **Easy to skim:** Short paragraphs, bold key points, clear structure

### 6.4 Social Proof Integration

- Share counts on articles (if substantial)
- Testimonial widgets that pull from social media
- User-generated content displays (Instagram feeds, tweet embeds)
- "As seen in" media logos
- Real-time activity notifications ("23 people signed up today")

### 6.5 Platform-Specific Content Adaptation

| Platform | Content Style | Image Size | Optimal |
|---|---|---|---|
| **X/Twitter** | Short, punchy, thread-friendly | 1200x675px | 1-2 hashtags, question hooks |
| **LinkedIn** | Professional, insight-driven | 1200x627px | Long-form posts, no hashtags in body |
| **Instagram** | Visual-first, story-driven | 1080x1080px (feed), 1080x1920px (story) | Minimal text, strong visuals |
| **Facebook** | Conversational, community | 1200x630px | Questions, user tags |
| **TikTok** | Short video, trend-aware | 1080x1920px | Trending sounds, hooks in first 3s |
| **YouTube** | Educational, entertaining | 1280x720px thumbnails | Search-optimized titles, chapters |

### 6.6 UGC (User-Generated Content) Strategy

- Create branded hashtags for users to share experiences
- Feature customer content on product pages and social
- Run contests that encourage content creation
- Display reviews and testimonials prominently
- Build community features (forums, comments, Q&A)
- Ask for and showcase customer photos/videos

### 6.7 Social Sharing Mechanics

- **Share buttons:** Place at top AND bottom of content. Use share counts as social proof.
- **Click-to-tweet:** Pre-composed tweet boxes within articles with key quotes
- **Native sharing:** Mobile Web Share API for native share sheet
- **Email forwarding:** "Forward to a friend" links for newsletter content
- **Embed options:** Make infographics/charts embeddable with attribution links

---

## Part 7: SEM (Search Engine Marketing)

### 7.1 Paid Search Strategy (2026)

**The 2026 landscape:**
- AI Max for Search is now the default — Google's AI expands keywords, generates ad copy, selects landing pages
- Smart Bidding is the baseline — manual CPC is obsolete for most accounts
- Broad match + Smart Bidding is the recommended default (not exact match)
- Responsive Search Ads (RSAs) are the only active Search ad format
- Quality Score still matters but is less manually controllable

### 7.2 Keyword Match Types (2026)

| Match Type | Behavior | When to Use |
|---|---|---|
| **Broad match** (default) | AI expands to semantically related queries | Primary match type with Smart Bidding + 30+ conversions/month |
| **Phrase match** | Close variants in order | Secondary layer, tighter relevance control |
| **Exact match** | Close variants only | Brand terms, high-value high-specificity terms |

**Negative keywords:** The single most important defensive practice. Review search term reports weekly, add negatives for irrelevant queries.

### 7.3 Ad Copy Optimization

**Responsive Search Ads (RSAs):**
- Provide 15 headlines and 4 descriptions — Google assembles combinations
- Target "Good" or "Excellent" Ad Strength
- Write diverse, unique assets — not minor variations of the same message
- Include keywords in headlines, benefits in descriptions
- Use asset-level performance ratings to replace "Low" assets every 4-6 weeks
- Maintain tight relevance chain: Search Query → Keyword → Ad Copy → Landing Page

### 7.4 Landing Page Quality Scores

**Three Quality Score components:**
1. **Expected CTR:** Ad copy quality — does the ad earn clicks?
2. **Ad relevance:** Does the ad match the keyword intent?
3. **Landing page experience:** Relevance, clarity, speed, message match

**Landing page best practices:**
- **Message match:** Landing page headline matches the ad promise exactly
- **Load speed:** < 3 seconds (consensus threshold for Quality Score impact)
- **Mobile usability:** Mobile-optimized, responsive, touch-friendly
- **Relevance:** Content matches what the user searched for
- **Clear CTA:** One primary action, prominently displayed
- **Trust signals:** Reviews, guarantees, security badges
- **No distractions:** Remove navigation on conversion-focused pages

### 7.5 Bid Strategy

| Strategy | When to Use |
|---|---|
| **Maximize Conversions** | New campaigns, learning phase, no target yet |
| **Target CPA (tCPA)** | Know your cost-per-acquisition target, 30+ conversions/month |
| **Target ROAS (tROAS)** | E-commerce, tracking revenue per conversion |
| **Maximize Conversion Value** | Variable order values, want algorithm to prioritize high-value |
| **Maximize Clicks** | Awareness campaigns only (not for conversion) |

**Rules:**
- Start with Maximize Conversions (no target) for 2-4 weeks
- Then set tCPA/tROAS within 10-20% of actual performance
- Adjust targets gradually — no more than 15-20% at a time
- Give each change 2 weeks before evaluating
- Smart Bidding needs 30-50 conversions/month at campaign level to exit learning phase

### 7.6 Remarketing / Retargeting

- **Site visitors:** Target users who visited but didn't convert
- **Cart abandoners:** Target users who added to cart but didn't checkout
- **Customer match:** Upload email lists for cross-sell/upsell
- **Similar audiences:** Target users similar to your converters
- **Dynamic remarketing:** Show specific products users viewed
- **Frequency capping:** Limit impressions to avoid ad fatigue (3-5 per day)
- **Exclusion lists:** Exclude recent converters from retargeting

### 7.7 Campaign Structure

**2026 best practice — Single Theme Ad Groups (STAGs):**
- 3-10 tightly themed ad groups per campaign
- Each ad group = one product category, service line, or intent cluster
- Consolidate more than you think necessary — Smart Bidding needs data volume
- 3-4 campaign structure is sufficient for most accounts
- Segment by: branded vs non-branded, intent level, geography (if needed)

### 7.8 Conversion Tracking Setup

1. **GA4 setup:** Enhanced measurement, custom events for key actions
2. **Google Ads conversion actions:** Import from GA4 or set up dedicated tags
3. **Conversion value:** Assign dollar values to conversions for ROAS bidding
4. **Enhanced conversions:** Pass hashed email/phone for better attribution
5. **Offline conversions:** Import CRM data for sales-cycle conversions
6. **Consent mode:** Implement Google Consent Mode v2 for GDPR compliance
7. **Server-side tagging:** Use GTM Server-Side for data quality and privacy compliance

---

## Part 8: Integration — How the Disciplines Work Together

```
User has a question
       ↓
   [SEO] — Page ranks in Google SERP
   [GEO] — Page is cited in AI answer (ChatGPT, Perplexity, AI Overviews)
   [AEO] — Page wins featured snippet / voice answer
   [SXO] — SERP to page to conversion journey is optimized
       ↓
   User visits the page
       ↓
   [CRO] — Page converts the visitor
   [SMO] — Page is optimized for social sharing when shared
       ↓
   User converts
       ↓
   [SEM] — Paid search captures high-intent users who don't find organic
   [SEM] — Remarketing brings back users who didn't convert first time
```

### Implementation Priority

1. **SEO + Technical SEO** — foundation, everything builds on this
2. **AEO** — structured data, FAQ schema, answer-formatted content
3. **GEO** — AI crawler access, passage optimization, entity building
4. **SXO** — intent matching, CTR optimization, engagement
5. **CRO** — conversion-focused page optimization, testing
6. **SMO** — social meta tags, shareable content structure
7. **SEM** — paid search for immediate visibility while organic builds

---

## Execution Instructions for Cascade

When this skill is activated during development:

1. **Read the project's `research.md`** — it contains the SEO plan, keywords, and content strategy
2. **Implement technical SEO from the start** — not as an afterthought. Meta tags, schema, sitemaps, robots.txt, canonical URLs should be built into the project structure
3. **Allow AI crawlers in robots.txt** — this is critical for GEO and often missed
4. **Implement structured data** — Organization, WebSite, Article/BlogPosting, FAQPage, BreadcrumbList at minimum
5. **Optimize for Core Web Vitals** — SSR/static generation, image optimization, minimal JS
6. **Set up Open Graph and Twitter Card meta tags** — for every page, dynamically
7. **Build answer-formatted content** — question headings, 40-60 word answer blocks, FAQ sections
8. **Set up analytics and conversion tracking** — GA4, Search Console, conversion events
9. **Create a sitemap and submit to Search Console** — auto-generated, kept up to date
10. **Test with Google Rich Results Test** — validate all schema markup
11. **Don't keyword-stuff** — write for humans, optimize for intent
12. **Measure everything** — impressions, clicks, CTR, position, conversions, AI citations
