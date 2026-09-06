# Rule: Search & Conversion Optimization for All Web Projects

**ALWAYS** apply the Search & Conversion Optimization skill and workflow when building or optimizing any website. Cover the full spectrum: SEO, GEO, SXO, AEO, CRO, SMO, and SEM.

## Skill
`~/.codeium/windsurf/skills/search-conversion-optimization.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/search-optimization.md` — invoke with `/search-optimization`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/seo-specialist.md` (parent: Project Architect)

## How to follow this rule:
1. When building or optimizing any website, invoke the `/search-optimization` workflow
2. Follow the workflow steps in order: Read Context → Technical SEO → Structured Data → GEO → AEO → SXO → CRO → SMO → Analytics → SEM
3. Always implement technical SEO from the start — meta tags, schema, sitemaps, robots.txt, canonical URLs built into the project structure
4. Always allow AI crawlers in robots.txt (GPTBot, ClaudeBot, PerplexityBot, Google-Extended) — critical for GEO
5. Always implement structured data — Organization, WebSite, Article, FAQPage, BreadcrumbList at minimum
6. Always optimize for Core Web Vitals — LCP < 2.5s, INP < 200ms, CLS < 0.1
7. Always set up Open Graph and Twitter Card meta tags on every page
8. Reflect modern 2025-2026 optimization standards including AI search engines, voice search, and evolving SERP features — not outdated keyword-stuffing tactics

## When this rule applies:
- Building any new website
- After the Website Research workflow completes — apply the SEO plan from research.md
- After the Claude Taste workflow completes — ensure design doesn't hurt Core Web Vitals
- User asks about SEO, AI search visibility, conversion optimization, or paid search
- After launch — for ongoing optimization, testing, and measurement

## When this rule does NOT apply:
- Non-website projects (CLI tools, libraries, scripts, etc.)
- User explicitly says to skip optimization
