---
name: web-scraping
description: Comprehensive web scraping & data collection workflow — tool selection, Cheerio/Playwright/Crawlee, rate limiting, proxies, anti-bot, data pipelines, scheduling, and ethics
---

# Web Scraping & Data Collection Workflow

This workflow applies the **Web Scraping & Data Collection Skill** (`~/.codeium/windsurf/skills/web-scraping-data-collection.md`) to build reliable, ethical scraping pipelines.

## When to Run
- When implementing web scraping functionality
- When the user says `/web-scraping` or asks about data collection
- When setting up a crawler or scraper
- When building a data pipeline from external sources
- When dealing with anti-bot protection or proxy rotation

---

## Step 1: Check for API First

1. Before scraping, check if the target site has a public API
2. Check: developer documentation, API directories (RapidAPI, PublicAPIs.org)
3. Check: does the site expose JSON via network requests? (intercept with browser dev tools)
4. If API exists (even paid), use it — faster, more reliable, more legal, more maintainable
5. Only proceed with scraping if no API exists or API doesn't cover your needs
6. Document why scraping is necessary instead of API

## Step 2: Check Legal & Ethical Constraints

1. Read the target site's Terms of Service — check if scraping is prohibited
2. Check robots.txt — verify target URLs are allowed for your user agent
3. Don't scrape personal data (PII) — GDPR/CCPA applies
4. Don't scrape copyrighted content without permission
5. Don't overload the server — treat rate limits seriously
6. If in doubt about legality, consult a lawyer
7. Document legal review and ethical considerations

## Step 3: Choose Scraping Tool

1. Read the project context — what data, source sites, rendering needs
2. **Cheerio:** For static HTML pages (server-rendered, no JavaScript needed)
3. **Playwright:** For JavaScript-rendered pages (SPAs, React/Vue apps)
4. **Crawlee:** For full crawlers (multiple pages, pagination, link following)
5. **API interception:** Use Playwright to intercept the site's own API calls — cleanest approach
6. Install chosen tool and any browser binaries (`npx playwright install`)

## Step 4: Implement Scraper

1. **Static HTML (Cheerio):**
   - Fetch page with `fetch()`
   - Parse with `cheerio.load(html)`
   - Extract data with CSS selectors
2. **JavaScript-rendered (Playwright):**
   - Launch browser: `chromium.launch({ headless: true })`
   - Navigate: `page.goto(url, { waitUntil: 'networkidle' })`
   - Wait for selectors: `page.waitForSelector('.content')`
   - Extract: `page.$$eval('.item', handler)`
3. **API interception (Playwright):**
   - Listen to `page.on('response')` for API calls
   - Parse JSON responses directly — cleaner than DOM scraping
4. **Full crawler (Crawlee):**
   - Set up CheerioCrawler or PlaywrightCrawler
   - Configure requestHandler for each page
   - Use `enqueueLinks` for pagination and link following
   - Set maxConcurrency and maxRequestsPerCrawl

## Step 5: Set Up Rate Limiting

1. Implement rate limiter: at least 1-2 seconds between requests per domain
2. Limit concurrent requests: 1-5 per domain
3. Set reasonable timeouts: 30 seconds per page
4. Implement retry with exponential backoff: 3 retries max
5. Use Crawlee's built-in rate limiting if using Crawlee
6. Set descriptive User-Agent with contact info
7. Scrape during off-peak hours when possible
8. Cache scraped pages — don't re-scrape what you already have

## Step 6: Set Up Proxy Rotation (If Needed)

1. Determine if proxies are needed: IP blocking, geo-restrictions, scale
2. Choose proxy provider: Bright Data, Smartproxy, Apify, ScraperAPI
3. Configure proxy list with server, username, password
4. Rotate proxies: random selection per request or round-robin
5. Handle proxy failures: retry with different proxy
6. Monitor proxy health: track success rate per proxy
7. Use residential proxies for anti-bot protected sites
8. Only use proxies when necessary — they add cost and complexity

## Step 7: Handle Anti-Bot Protection

1. Use `playwright-extra` with `puppeteer-extra-plugin-stealth`
2. Set realistic browser fingerprint: user agent, viewport, headers
3. Add human-like behavior: mouse movements, random delays, scrolling
4. Use residential proxies — datacenter IPs are easily detected
5. Avoid triggering CAPTCHAs: rate limit, rotate proxies, use stealth
6. For CAPTCHAs: use solving service (2Captcha) or avoid triggering
7. For Cloudflare: residential proxies + stealth + realistic fingerprint
8. If anti-bot is too aggressive, reconsider API or alternative data source

## Step 8: Parse & Validate Data

1. Extract structured data from HTML/DOM using CSS selectors
2. Clean data: trim whitespace, remove HTML entities, normalize formats
3. Validate with Zod schemas: ensure required fields, correct types, valid URLs
4. Handle missing fields: use optional fields or null, don't crash
5. Log validation errors: track which pages had invalid data
6. Transform data: convert prices to numbers, dates to ISO format, etc.
7. Add metadata: scraped_at timestamp, source URL, scraper version

## Step 9: Set Up Deduplication & Storage

1. Generate URL hash (MD5) or content hash (SHA-256) for deduplication
2. Check for duplicates before storing — skip if already scraped
3. Use database upsert (ON CONFLICT DO UPDATE) for incremental updates
4. Only update if data has changed — compare relevant fields
5. Track scrape history: when was each record first scraped, last updated
6. Store raw HTML alongside parsed data for re-parsing without re-scraping
7. Set up data retention policy: delete old data that's no longer needed

## Step 10: Schedule & Monitor

1. Set up scheduled scraping with Inngest, BullMQ, or cron:
   - Daily: product prices, availability
   - Weekly: content updates, new listings
   - Monthly: comprehensive crawls
2. Track metrics: pages scraped, errors, duplicates, duration, data quality
3. Set up alerts: error rate > 10%, no data scraped, scraper not running
4. Log all scraping activity for audit trail
5. Monitor target site for structure changes — detect when selectors break
6. Set up health check: verify scraper is producing data
7. Regular review: check if API became available, if data is still needed

## Step 11: Document & Maintain

1. Document scraping targets: URLs, selectors, data extracted
2. Document schedule: when and how often each source is scraped
3. Document data schema: what fields, types, validation rules
4. Document proxy configuration: provider, rotation strategy
5. Document legal review: ToS analysis, robots.txt compliance
6. Document error handling: what happens on failures, retry strategy
7. Regular maintenance: update selectors when site structure changes
8. Regular audit: verify data quality, check for new APIs, review legality
