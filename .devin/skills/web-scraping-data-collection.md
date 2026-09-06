---
name: Web Scraping & Data Collection Skill
description: Comprehensive methodology for web scraping and data collection — 2025-2026 practices with Playwright, Cheerio, Crawlee, rate limiting, proxy rotation, anti-bot bypass, and ethical scraping
version: 1.0.0
tags: [web-scraping, data-collection, playwright, cheerio, crawlee, puppeteer, rate-limiting, proxies, anti-bot, ethical-scraping, data-pipelines]
---

# Web Scraping & Data Collection Skill

## Purpose
This skill provides a comprehensive methodology for web scraping and data collection across any kind of project. It reflects **modern 2025-2026 practices** — Playwright for JavaScript-rendered pages, Cheerio for static HTML parsing, Crawlee for orchestration, proxy rotation for reliability, rate limiting for politeness, and ethical scraping practices that respect `robots.txt` and terms of service.

## Core Philosophy

**Scrape ethically, store efficiently, and never break a site.** Web scraping is powerful but comes with responsibility. Respect `robots.txt`, honor rate limits, identify your scraper with a proper user agent, and never overload a server. The data you collect is only as valuable as the pipeline that processes it — design for reliability, deduplication, and incremental updates.

**The #1 rule:** Always check if an API exists before scraping. APIs are faster, more reliable, more legal, and more maintainable than scraping. If the data you need is available via API (even a paid one), use the API. Only scrape when no API exists, the API doesn't cover your needs, or the cost is prohibitive. Scraping is a last resort, not a first choice.

---

## Part 1: Scraping Tools

### 1.1 Tool Comparison (2025-2026)

| Tool | Best For | Rendering | Speed | Complexity |
|---|---|---|---|---|
| **Cheerio** | Static HTML | No (server-side only) | Very fast | Low |
| **Playwright** | JS-rendered pages | Yes (full browser) | Slow | Medium |
| **Crawlee** | Full crawlers | Both (Cheerio + Playwright) | Varies | Medium |
| **Puppeteer** | JS-rendered pages | Yes (Chrome only) | Slow | Medium |
| **Scrapy (Python)** | Large-scale scraping | No (can integrate Selenium) | Fast | High |

### 1.2 Decision Matrix

| Scenario | Recommended |
|---|---|
| **Static HTML pages** | Cheerio |
| **JavaScript-rendered (SPA)** | Playwright |
| **Full crawler (multiple pages)** | Crawlee + Playwright/Cheerio |
| **Login required** | Playwright (session management) |
| **Anti-bot protection** | Playwright + stealth + proxies |
| **Large-scale (10K+ pages)** | Crawlee with proxy rotation |
| **API available** | Use the API instead |

---

## Part 2: Cheerio (Static HTML)

### 2.1 Basic Usage
```typescript
import * as cheerio from 'cheerio';

async function scrapeProduct(url: string) {
  const response = await fetch(url);
  const html = await response.text();
  const $ = cheerio.load(html);

  const product = {
    name: $('h1.product-title').text().trim(),
    price: parseFloat($('.price').text().replace(/[^0-9.]/g, '')),
    description: $('.product-description').text().trim(),
    images: $('.product-images img')
      .map((_, el) => $(el).attr('src'))
      .get(),
    availability: $('.stock-status').text().trim(),
  };

  return product;
}
```

### 2.2 Selectors
```typescript
const $ = cheerio.load(html);

// CSS selectors
$('.product-list .item').each((_, el) => {
  const name = $(el).find('.name').text();
  const price = $(el).find('.price').text();
});

// Attribute extraction
const links = $('a.product-link').map((_, el) => $(el).attr('href')).get();

// Table data
const rows = $('table.data tr').map((_, el) => {
  const cells = $(el).find('td').map((_, td) => $(td).text().trim()).get();
  return cells;
}).get();

// Nested data
const articles = $('article').map((_, el) => ({
  title: $(el).find('h2').text(),
  author: $(el).find('.author').text(),
  date: $(el).find('time').attr('datetime'),
  content: $(el).find('.content').text(),
})).get();
```

---

## Part 3: Playwright (JavaScript-Rendered)

### 3.1 Setup
```bash
npm install playwright
npx playwright install
```

### 3.2 Basic Scraping
```typescript
import { chromium } from 'playwright';

async function scrapeSPA(url: string) {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();

  await page.goto(url, { waitUntil: 'networkidle' });

  // Wait for content to render
  await page.waitForSelector('.product-list');

  // Extract data
  const products = await page.$$eval('.product-item', (items) =>
    items.map((item) => ({
      name: item.querySelector('.name')?.textContent?.trim(),
      price: item.querySelector('.price')?.textContent?.trim(),
      image: item.querySelector('img')?.src,
    }))
  );

  await browser.close();
  return products;
}
```

### 3.3 Handling Interactions
```typescript
async function scrapeWithLogin(url: string) {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();

  // Navigate to login page
  await page.goto('https://example.com/login');

  // Fill login form
  await page.fill('#email', process.env.SCRAPING_EMAIL!);
  await page.fill('#password', process.env.SCRAPING_PASSWORD!);
  await page.click('button[type="submit"]');

  // Wait for redirect after login
  await page.waitForURL('**/dashboard');

  // Navigate to target page
  await page.goto(url, { waitUntil: 'networkidle' });

  // Click "Load More" button repeatedly
  while (await page.isVisible('.load-more')) {
    await page.click('.load-more');
    await page.waitForTimeout(1000); // Wait for new content
  }

  // Extract all loaded data
  const data = await page.$$eval('.item', (items) =>
    items.map((item) => ({
      title: item.querySelector('.title')?.textContent?.trim(),
    }))
  );

  await browser.close();
  return data;
}
```

### 3.4 Intercepting API Calls
```typescript
async function scrapeViaAPI(url: string) {
  const browser = await chromium.launch();
  const page = await browser.newPage();

  const apiResponses: any[] = [];

  // Intercept network requests
  page.on('response', async (response) => {
    if (response.url().includes('/api/products')) {
      const data = await response.json();
      apiResponses.push(data);
    }
  });

  await page.goto(url, { waitUntil: 'networkidle' });

  await browser.close();

  // Use the API data directly — much cleaner than DOM scraping
  return apiResponses.flatMap(r => r.products || []);
}
```

---

## Part 4: Crawlee (Orchestration)

### 4.1 Setup
```bash
npm install crawlee playwright
```

### 4.2 Cheerio Crawler
```typescript
import { CheerioCrawler } from 'crawlee';

const crawler = new CheerioCrawler({
  async requestHandler({ request, $, enqueueLinks }) {
    const title = $('title').text();
    const links = $('a').map((_, el) => $(el).attr('href')).get();

    console.log(`Scraped ${request.url}: ${title}`);

    // Enqueue more links
    await enqueueLinks({
      globs: ['https://example.com/products/*'],
    });

    // Save data
    await Dataset.pushData({
      url: request.url,
      title,
      links: links.slice(0, 10),
    });
  },
  maxRequestsPerCrawl: 100,
  maxConcurrency: 5,
});

await crawler.run(['https://example.com']);
```

### 4.3 Playwright Crawler
```typescript
import { PlaywrightCrawler } from 'crawlee';

const crawler = new PlaywrightCrawler({
  async requestHandler({ page, request, enqueueLinks }) {
    // Wait for content
    await page.waitForSelector('.product-list');

    // Extract data
    const products = await page.$$eval('.product-item', (items) =>
      items.map((item) => ({
        name: item.querySelector('.name')?.textContent?.trim(),
        price: item.querySelector('.price')?.textContent?.trim(),
      }))
    );

    await Dataset.pushData({ url: request.url, products });

    // Enqueue pagination links
    await enqueueLinks({
      selector: '.pagination a',
    });
  },
  maxConcurrency: 3,
  requestHandlerTimeoutSecs: 60,
});

await crawler.run(['https://example.com/products']);
```

---

## Part 5: Rate Limiting & Politeness

### 5.1 Rate Limiter
```typescript
class RateLimiter {
  private lastRequest = 0;
  constructor(private minInterval: number = 1000) {}

  async waitForNextRequest() {
    const now = Date.now();
    const elapsed = now - this.lastRequest;
    if (elapsed < this.minInterval) {
      await new Promise(resolve => setTimeout(resolve, this.minInterval - elapsed));
    }
    this.lastRequest = Date.now();
  }
}

// Usage
const limiter = new RateLimiter(2000); // 2 seconds between requests

for (const url of urls) {
  await limiter.waitForNextRequest();
  await scrapePage(url);
}
```

### 5.2 Politeness Rules
- **Respect robots.txt:** Always check and follow robots.txt
- **Rate limit:** At least 1-2 seconds between requests
- **User agent:** Identify your scraper with contact info
- **Cache:** Don't re-scrape pages you already have
- **Off-peak:** Scrape during off-peak hours if possible
- **Concurrent requests:** Limit to 1-5 concurrent connections per domain
- **Timeout:** Set reasonable timeouts (30s) — don't hold connections open
- **Retry with backoff:** Exponential backoff on failures, max 3 retries

### 5.3 robots.txt Check
```typescript
import robotsParser from 'robots-parser';

async function checkRobotsTxt(targetUrl: string): Promise<boolean> {
  const url = new URL(targetUrl);
  const robotsUrl = `${url.origin}/robots.txt`;

  const response = await fetch(robotsUrl);
  const robotsText = await response.text();

  const robots = robotsParser(robotsUrl, robotsText);

  return robots.isAllowed(targetUrl, 'MyScraper/1.0');
}

// Usage
const canScrape = await checkRobotsTxt('https://example.com/products');
if (!canScrape) {
  console.log('robots.txt disallows scraping this URL');
  return;
}
```

---

## Part 6: Proxy Rotation

### 6.1 Why Proxies
- **IP blocking:** Sites block IPs that make too many requests
- **Rate limits:** Different IPs have separate rate limits
- **Geo-restrictions:** Access content restricted to certain countries
- **Reliability:** If one proxy fails, use another

### 6.2 Proxy Providers
| Provider | Type | Pricing |
|---|---|---|
| **Bright Data** | Residential, datacenter | $500+/mo |
| **Smartproxy** | Residential, datacenter | $75+/mo |
| **Apify** | Residential, datacenter | $49+/mo |
| **ScraperAPI** | Proxy API | $49+/mo |
| **Free proxies** | Unreliable | Free (not recommended) |

### 6.3 Proxy Rotation with Playwright
```typescript
import { chromium } from 'playwright';

const proxies = [
  { server: 'http://proxy1.example.com:8080', username: 'user', password: 'pass' },
  { server: 'http://proxy2.example.com:8080', username: 'user', password: 'pass' },
  { server: 'http://proxy3.example.com:8080', username: 'user', password: 'pass' },
];

function getRandomProxy() {
  return proxies[Math.floor(Math.random() * proxies.length)];
}

async function scrapeWithProxy(url: string) {
  const proxy = getRandomProxy();
  const browser = await chromium.launch({
    headless: true,
    proxy,
  });

  try {
    const page = await browser.newPage();
    await page.goto(url, { waitUntil: 'networkidle', timeout: 30000 });
    const data = await page.content();
    return data;
  } catch (error) {
    console.error(`Proxy ${proxy.server} failed:`, error);
    // Retry with different proxy
    return scrapeWithProxy(url);
  } finally {
    await browser.close();
  }
}
```

---

## Part 7: Anti-Bot Bypass

### 7.1 Playwright Stealth
```bash
npm install playwright-extra puppeteer-extra-plugin-stealth
```
```typescript
import { chromium } from 'playwright-extra';
import StealthPlugin from 'puppeteer-extra-plugin-stealth';

chromium.use(StealthPlugin());

async function scrapeWithStealth(url: string) {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();

  // Set realistic user agent
  await page.setExtraHTTPHeaders({
    'Accept-Language': 'en-US,en;q=0.9',
  });

  // Set realistic viewport
  await page.setViewportSize({ width: 1920, height: 1080 });

  // Add realistic mouse movements
  await page.mouse.move(100, 100);
  await page.waitForTimeout(500);
  await page.mouse.move(200, 200);

  await page.goto(url, { waitUntil: 'networkidle' });
  const data = await page.content();

  await browser.close();
  return data;
}
```

### 7.2 Handling CAPTCHAs
- **2Captcha / Anti-Captcha:** Paid CAPTCHA solving services
- **hCaptcha:** Use Playwright with stealth to avoid triggering
- **Cloudflare:** Use residential proxies and realistic browser fingerprint
- **reCAPTCHA:** Very hard to bypass — consider using API instead
- **Best approach:** Avoid triggering CAPTCHAs by rate limiting and using proxies

---

## Part 8: Data Storage & Pipelines

### 8.1 Data Pipeline
```typescript
// 1. Scrape → 2. Parse → 3. Validate → 4. Deduplicate → 5. Store

async function scrapeAndStore(url: string) {
  // 1. Scrape
  const html = await fetchPage(url);

  // 2. Parse
  const data = parseData(html);

  // 3. Validate
  const validated = validateData(data);

  // 4. Deduplicate
  const isDuplicate = await checkDuplicate(validated);
  if (isDuplicate) return;

  // 5. Store
  await storeData(validated);
}
```

### 8.2 Data Validation
```typescript
import { z } from 'zod';

const productSchema = z.object({
  name: z.string().min(1),
  price: z.number().positive(),
  currency: z.string().length(3),
  url: z.string().url(),
  image: z.string().url().optional(),
  description: z.string().optional(),
  scrapedAt: z.date(),
});

function validateProduct(data: unknown) {
  const result = productSchema.safeParse(data);
  if (!result.success) {
    console.error('Validation failed:', result.error.issues);
    return null;
  }
  return result.data;
}
```

### 8.3 Deduplication
```typescript
async function checkDuplicate(data: { url: string }): Promise<boolean> {
  // Check by URL hash
  const hash = createHash('md5').update(data.url).digest('hex');
  const existing = await db.query('SELECT 1 FROM scraped_data WHERE url_hash = $1', [hash]);
  return existing.rows.length > 0;
}

// Or check by content hash
async function checkContentHash(content: string): Promise<boolean> {
  const hash = createHash('sha256').update(content).digest('hex');
  const existing = await db.query('SELECT 1 FROM scraped_data WHERE content_hash = $1', [hash]);
  return existing.rows.length > 0;
}
```

### 8.4 Incremental Updates
```typescript
async function upsertData(data: Product) {
  await db.query(`
    INSERT INTO products (id, name, price, url, scraped_at)
    VALUES ($1, $2, $3, $4, $5)
    ON CONFLICT (url) DO UPDATE
    SET name = EXCLUDED.name,
        price = EXCLUDED.price,
        scraped_at = EXCLUDED.scraped_at
    WHERE products.price != EXCLUDED.price
       OR products.name != EXCLUDED.name
  `, [data.id, data.name, data.price, data.url, new Date()]);
}
```

---

## Part 9: Scheduling & Monitoring

### 9.1 Scheduled Scraping
```typescript
// Using Inngest for scheduled scraping
import { inngest } from '@/lib/inngest';

export const scheduledScrape = inngest.createFunction(
  { id: 'scheduled-scrape', name: 'Daily Product Scrape' },
  { cron: '0 2 * * *' }, // Every day at 2 AM
  async ({ step }) => {
    const urls = await step.run('get-urls', () => getTargetUrls());

    for (const url of urls) {
      await step.run(`scrape-${url}`, async () => {
        const data = await scrapeProduct(url);
        await storeData(data);
      });
    }
  }
);
```

### 9.2 Monitoring
```typescript
// Track scraping metrics
const metrics = {
  pagesScraped: 0,
  errors: 0,
  duplicates: 0,
  startTime: Date.now(),
};

function logMetrics() {
  console.log({
    pagesScraped: metrics.pagesScraped,
    errors: metrics.errors,
    duplicates: metrics.duplicates,
    duration: Date.now() - metrics.startTime,
    errorRate: metrics.errors / metrics.pagesScraped,
  });
}

// Alert on high error rate
if (metrics.errorRate > 0.1) {
  await sendAlert('Scraping error rate > 10%');
}
```

---

## Part 10: Legal & Ethical Considerations

### 10.1 Legal Guidelines
- **Check ToS:** Read the website's Terms of Service — some prohibit scraping
- **robots.txt:** Always respect robots.txt directives
- **Copyright:** Don't republish copyrighted content without permission
- **Personal data:** GDPR/CCPA apply to personal data — don't scrape PII
- **Rate limits:** Don't overload servers — treat as DoS attack threshold
- **Public data:** Generally legal to scrape publicly accessible data
- **Login-required:** Scraping behind login may violate ToS
- **Consult legal:** When in doubt, consult a lawyer

### 10.2 Ethical Guidelines
- **Identify yourself:** Use descriptive user agent with contact info
- **Rate limit:** At least 1-2 seconds between requests
- **Off-peak:** Scrape during low-traffic hours
- **Cache:** Don't re-scrape what you already have
- **Minimal data:** Only collect what you need
- **Attribute:** Give credit to data source
- **Opt-out:** Provide a way for sites to opt out of your scraper

### 10.3 User Agent
```typescript
const USER_AGENT = 'MyScraper/1.0 (+https://myapp.com; contact@myapp.com)';

// Always set user agent
const response = await fetch(url, {
  headers: { 'User-Agent': USER_AGENT },
});

// Or with Playwright
const browser = await chromium.launch();
const page = await browser.newPage();
await page.setExtraHTTPHeaders({ 'User-Agent': USER_AGENT });
```

---

## Execution Instructions for Cascade

When this skill is activated for web scraping & data collection:

1. **Read the project context** — what data to collect, source sites, volume, frequency
2. **Check for API first** — always prefer API over scraping if available
3. **Choose scraping tool** — Cheerio (static), Playwright (JS-rendered), Crawlee (full crawler)
4. **Check robots.txt** — verify scraping is allowed for target URLs
5. **Set up rate limiting** — at least 1-2 seconds between requests
6. **Set up proxy rotation** — if scraping at scale or facing IP blocks
7. **Implement data parsing** — extract structured data from HTML/DOM
8. **Validate data** — Zod schemas for all scraped data
9. **Set up deduplication** — URL hash or content hash to avoid duplicates
10. **Set up storage** — database with upsert for incremental updates
11. **Schedule scraping** — Inngest, BullMQ, or cron for regular runs
12. **Monitor** — track pages scraped, errors, duplicates, duration
13. **Document** — scraping targets, schedule, data schema, legal considerations
