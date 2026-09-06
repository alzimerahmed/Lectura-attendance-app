# Rule: Web Scraping & Data Collection for All Projects

**ALWAYS** apply the Web Scraping & Data Collection skill and workflow when implementing web scraping. Always check for an API first — scraping is a last resort. Scrape ethically: respect robots.txt, rate limit, and identify your scraper.

## Skill
`~/.codeium/windsurf/skills/web-scraping-data-collection.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/web-scraping.md` — invoke with `/web-scraping`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/web-scraper.md` (parent: Data Engineer)

## How to follow this rule:
1. When implementing web scraping, invoke the `/web-scraping` workflow
2. Follow the workflow steps in order: API Check → Legal → Tool Selection → Scraper → Rate Limiting → Proxies → Anti-Bot → Parse & Validate → Dedup & Store → Schedule & Monitor → Document
3. Always check for an API first — only scrape when no API exists or doesn't cover your needs
4. Always respect robots.txt — check before scraping any URL
5. Always rate limit — at least 1-2 seconds between requests per domain
6. Always validate scraped data with Zod schemas — don't store unvalidated data
7. Always set up deduplication — URL hash or content hash to avoid duplicates
8. Always use a descriptive User-Agent with contact information

## When this rule applies:
- Implementing web scraping functionality
- Setting up a crawler or scraper
- Building a data pipeline from external sources
- Dealing with anti-bot protection or proxy rotation
- User asks about web scraping or data collection

## When this rule does NOT apply:
- Projects that only use APIs for data collection
- User explicitly says to skip scraping setup
