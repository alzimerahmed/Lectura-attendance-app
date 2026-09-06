---
name: Performance Optimization Skill
description: Comprehensive methodology for profiling, analyzing, and optimizing web performance — 2025-2026 practices with Core Web Vitals, RUM, budget enforcement, and evidence-driven optimization
version: 1.0.0
tags: [performance, core-web-vitals, lighthouse, rum, bundle-size, rendering, database, caching]
---

# Performance Optimization Skill

## Purpose
This skill provides a comprehensive methodology for profiling, analyzing, and optimizing web performance across any kind of web project. It reflects **modern 2025-2026 performance practices** — Core Web Vitals as the north star, Real User Monitoring (RUM) for field data, Lighthouse for lab data, budget enforcement in CI, and evidence-driven optimization (measure first, optimize second).

## Core Philosophy

**Measure, don't guess.** Every optimization should start with data. Profiling tells you what's slow and why. Without measurement, optimization is gambling. With measurement, it's engineering.

**The #1 rule:** Optimize the right thing. The slowest part of the user experience is the optimization target. Don't optimize JavaScript execution if the database query takes 2 seconds. Don't optimize bundle size if the images are 5MB. Profile first, optimize the bottleneck.

---

## Part 1: Core Web Vitals (2025-2026)

### 1.1 LCP (Largest Contentful Paint) — Target < 2.5s
- **What:** Time until the largest element in the viewport renders
- **Common LCP elements:** Hero image, hero text, large block
- **Optimization:**
  - Preload LCP image: `<link rel="preload" as="image" href="hero.jpg" fetchpriority="high">`
  - Optimize server response time (TTFB < 800ms)
  - Use CDN for static assets
  - Render-blocking resources: minimize CSS, defer non-critical JS
  - Use `font-display: swap` or `optional` for fonts
  - SSR/SSG for above-the-fold content

### 1.2 CLS (Cumulative Layout Shift) — Target < 0.1
- **What:** Sum of layout shift scores — visual stability
- **Common causes:** Images without dimensions, fonts causing FOIT/FOUT, dynamically injected content, ads/embeds
- **Optimization:**
  - Always set `width` and `height` on images and videos
  - Use `aspect-ratio` CSS for responsive media
  - Reserve space for ads and embeds
  - Use `font-display: swap` and `size-adjust` to minimize font shift
  - Avoid inserting content above existing content
  - Use CSS `min-height` for dynamic content containers

### 1.3 INP (Interaction to Next Paint) — Target < 200ms (replaced FID in 2024)
- **What:** Latency of user interactions (click, tap, key press) — measures responsiveness
- **Optimization:**
  - Break up long tasks (> 50ms) with `setTimeout`, `requestIdleCallback`, or `scheduler.yield()`
  - Debounce/throttle expensive event handlers
  - Use Web Workers for heavy computation
  - Minimize main thread blocking
  - Optimize React re-renders (memoization, virtualization)
  - Defer non-critical JavaScript

### 1.4 TTFB (Time to First Byte) — Target < 800ms
- **What:** Time from request to first byte of response
- **Optimization:**
  - Use CDN with edge caching
  - Optimize database queries (indexes, caching)
  - Use SSR/SSG where appropriate
  - Optimize server computation (caching, fewer DB calls)
  - Use HTTP/2 or HTTP/3 for multiplexing
  - Use connection pooling

### 1.5 FCP (First Contentful Paint) — Target < 1.8s
- **What:** Time until first content (text, image, canvas) renders
- **Optimization:**
  - Inline critical CSS
  - Preload critical resources
  - Minimize render-blocking CSS and JS
  - Use SSR/SSG for fast first paint
  - Optimize font loading

### 1.6 TBT (Total Blocking Time) — Target < 200ms (lab only)
- **What:** Sum of time the main thread was blocked (> 50ms tasks) between FCP and TTI
- **Optimization:**
  - Code split and lazy load
  - Defer non-critical JavaScript
  - Break up long tasks
  - Use Web Workers

---

## Part 2: Profiling & Measurement

### 2.1 Lighthouse Audits
```bash
# CLI
npx lighthouse https://example.com --output html --output-path ./report.html

# CI
npx lighthouse https://staging.example.com --budget-path=./budget.json --max-warnings=0
```
- **Categories:** Performance, Accessibility, Best Practices, SEO
- **Lab data:** Simulated environment — good for regression detection
- **Limitations:** Doesn't reflect real user conditions (network, device, location)
- **In CI:** Run on every PR, compare scores, fail on regression

### 2.2 Chrome DevTools Performance Panel
- **Record:** Record a user interaction, analyze the flame chart
- **Identify:** Long tasks (red triangles), layout thrashing (purple), forced reflow
- **Network:** Waterfall chart for request timing
- **Memory:** Heap snapshots for memory leaks, allocation timeline
- **Coverage:** CSS/JS coverage — find unused code

### 2.3 Real User Monitoring (RUM)
```javascript
// Use web-vitals library
import { onLCP, onCLS, onINP, onTTFB, onFCP } from 'web-vitals';

onLCP((metric) => sendToAnalytics('LCP', metric.value));
onCLS((metric) => sendToAnalytics('CLS', metric.value));
onINP((metric) => sendToAnalytics('INP', metric.value));
```
- **Field data:** Real users, real devices, real networks
- **Tools:** Google Analytics 4, Vercel Analytics, SpeedCurve, Sentry Performance
- **Percentiles:** Focus on p75 (75th percentile) — the worst experience for 25% of users
- **Segments:** Compare by device (mobile vs desktop), connection (4G vs 3G), geography

### 2.4 WebPageTest
- **Advanced testing:** Multi-location, multi-device, multi-connection speed
- **Waterfall:** Detailed request waterfall with timing breakdown
- **Filmstrip:** Visual filmstrip of page rendering over time
- **Lighthouse:** Integrated Lighthouse audit
- **Custom scripting:** Test specific user flows

### 2.5 Performance Budgets
```json
// budget.json
{
  "resourceSizes": [
    { "resourceType": "script", "budget": 200 },
    { "resourceType": "stylesheet", "budget": 50 },
    { "resourceType": "image", "budget": 300 },
    { "resourceType": "total", "budget": 600 }
  ],
  "resourceCounts": [
    { "resourceType": "third-party", "budget": 5 }
  ],
  "timings": [
    { "metric": "LCP", "budget": 2500 },
    { "metric": "CLS", "budget": 0.1 },
    { "metric": "INP", "budget": 200 }
  ]
}
```
- **Enforce in CI:** Lighthouse budget, bundle size limits, custom checks
- **Fail the build:** If budget exceeded, block the PR
- **Track over time:** Monitor budget headroom — alert when approaching limit

### 2.6 Lighthouse CI
```yaml
# .github/workflows/lighthouse.yml
jobs:
  lighthouse:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: treosh/lighthouse-ci-action@v11
        with:
          urls: |
            https://staging.example.com
            https://staging.example.com/about
          budgetPath: ./budget.json
          uploadArtifacts: true
```

---

## Part 3: Frontend Performance

### 3.1 Bundle Size Analysis
```bash
# Analyze bundle
npx vite-bundle-visualizer  # Vite
npx @next/bundle-analyzer   # Next.js
```
- **Identify heavy dependencies:** moment.js → date-fns, lodash → lodash-es or native
- **Tree shaking:** Ensure ESM imports, `sideEffects: false` in package.json
- **Code splitting:** Route-based, component-based, vendor splitting
- **Analyze:** What's in the bundle? What can be deferred? What can be removed?

### 3.2 Code Splitting Strategies
```javascript
// Route-based splitting
const Dashboard = lazy(() => import('./Dashboard'));

// Component-based splitting
const HeavyChart = lazy(() => import('./HeavyChart'));

// Prefetch on hover/link visibility
<Link to="/settings" prefetch="intent">Settings</Link>
```
- **Route-based:** Each route is a separate chunk
- **Component-based:** Heavy components (charts, editors, maps) loaded on demand
- **Vendor splitting:** Separate node_modules from app code
- **Prefetch:** Load chunks before they're needed (hover, idle, visible)
- **Preload:** Load critical chunks immediately

### 3.3 Tree Shaking
- **ESM:** Use `import`/`export` — not `require`/`module.exports`
- **sideEffects:** `"sideEffects": false` in package.json (if safe)
- **Barrel files:** `index.ts` re-exports can prevent tree shaking — import directly
- **Verify:** Check bundle analyzer for unused code
- **Named imports:** `import { debounce } from 'lodash-es'` not `import _ from 'lodash-es'`

### 3.4 Lazy Loading
```javascript
// Components
const Component = lazy(() => import('./Component'));

// Images
<img src="image.jpg" loading="lazy" />

// Below-the-fold content
<div ref={ref}>{isVisible && <HeavyComponent />}</div>
```
- **Components:** `React.lazy` + `Suspense` for route/component splitting
- **Images:** `loading="lazy"` for below-the-fold images
- **Iframes:** `loading="lazy"` for embedded content
- **Conditional rendering:** Only render when visible (Intersection Observer)

### 3.5 Image Optimization
- **Format:** AVIF (best) → WebP (fallback) → JPEG/PNG (universal)
- **Responsive:** `srcset` and `sizes` for different screen sizes
- **Art direction:** `<picture>` with `<source>` for different layouts
- **CDN:** Cloudflare Images, Cloudinary, Vercel Image Optimization
- **Lazy load:** `loading="lazy"` for below-the-fold
- **Preload LCP:** `<link rel="preload" as="image" fetchpriority="high">`
- **Compression:** Quality 80 for WebP, 50 for AVIF
- **Metadata:** Strip EXIF metadata (privacy + size)

### 3.6 Font Optimization
```css
/* Font display */
@font-face {
  font-family: 'CustomFont';
  src: url('/fonts/custom.woff2') format('woff2');
  font-display: swap;
}
```
- **Format:** WOFF2 (best compression)
- **font-display:** `swap` (show fallback, swap when loaded) or `optional` (no swap if slow)
- **Preload:** `<link rel="preload" as="font" href="/fonts/custom.woff2" crossorigin>`
- **Subset:** Only load characters you need (latin, latin-ext)
- **Variable fonts:** One file, multiple weights — but watch file size
- **System fonts:** For body text, consider system font stack (zero download)

### 3.7 Critical CSS
- **Extract above-the-fold CSS:** Inline in `<style>` tag
- **Defer non-critical CSS:** Load asynchronously
- **Tools:** Critters, critical, Penthouse
- **Next.js:** Built-in CSS optimization
- **Target:** < 14KB critical CSS (fits in first TCP packet)

### 3.8 JavaScript Execution Optimization
- **Defer non-critical:** `defer` or `async` on scripts
- **Break long tasks:** `scheduler.yield()`, `requestIdleCallback`, `setTimeout(0)`
- **Web Workers:** Offload heavy computation
- **Debounce/throttle:** Scroll, resize, input handlers
- **requestAnimationFrame:** For visual updates, not `setTimeout`
- **Avoid:** `JSON.parse` on large payloads on main thread

### 3.9 Render Performance
- **Avoid layout thrashing:** Don't read and write DOM in the same loop
- **Batch DOM writes:** Use `requestAnimationFrame` for visual updates
- **CSS containment:** `contain: layout style paint` for isolated components
- **content-visibility:** `auto` for off-screen content — skips rendering
- **will-change:** Use sparingly — only for elements about to animate
- **transform and opacity:** Only these trigger compositor (GPU) — avoid animating layout properties

### 3.10 Third-Party Script Management
- **Audit:** What third-party scripts are loaded? Are all necessary?
- **Defer:** Load third-party scripts after page interaction
- **Partytown:** Run third-party scripts in a Web Worker
- **Server-side analytics:** Use server-side tracking where possible
- **Consent:** Don't load analytics until consent given
- **Impact:** Each third-party script adds latency — minimize count and size

---

## Part 4: Backend Performance

### 4.1 Database Query Optimization
- **EXPLAIN ANALYZE:** Always profile slow queries
- **Indexes:** Add indexes for frequently filtered/sorted columns
- **N+1 elimination:** Use eager loading (`include`, `preload`, `with`)
- **SELECT:** Don't `SELECT *` — select only needed columns
- **Pagination:** Always paginate — never fetch all records
- **Connection pooling:** Use PgBouncer or application-level pooling
- **Query caching:** Cache expensive query results (Redis, materialized views)

### 4.2 Caching Strategies
| Strategy | Use Case | Implementation |
|---|---|---|
| **CDN cache** | Static assets, SSR pages | Cache-Control headers, CDN rules |
| **Edge cache** | API responses, SSR | Cloudflare Workers, Vercel Edge |
| **Application cache** | Expensive computations | Redis, Memcached |
| **Database cache** | Query results | Materialized views, Redis |
| **Browser cache** | Static assets | Cache-Control, ETag, Last-Modified |
| **In-memory cache** | Single-instance hot data | LRU cache, Map |

### 4.3 N+1 Query Detection and Resolution
```typescript
// Bad: N+1 — 1 query for users + N queries for posts
const users = await db.users.findMany();
for (const user of users) {
  user.posts = await db.posts.findMany({ where: { userId: user.id } });
}

// Good: 2 queries total
const users = await db.users.findMany({ include: { posts: true } });

// Good: Manual eager loading
const users = await db.users.findMany();
const posts = await db.posts.findMany({ where: { userId: { in: users.map(u => u.id) } } });
```

### 4.4 Connection Pooling
- **PgBouncer:** Connection pooler for PostgreSQL — reduces connection overhead
- **Pool size:** Right-size for your workload — too many connections waste resources
- **Timeout:** Set connection timeout and query timeout
- **Monitoring:** Track pool utilization, wait time, connection errors
- **Serverless:** Use connection pooler or HTTP-based database proxy (Neon, Supabase)

### 4.5 Background Job Processing
- **Offload:** Move expensive operations to background jobs (email, image processing, reports)
- **Queue:** Use BullMQ, Celery, or cloud queues (SQS, Cloud Tasks)
- **Workers:** Separate worker process for CPU-intensive tasks
- **Rate limiting:** Don't overwhelm external APIs with background calls
- **Monitoring:** Track queue depth, job duration, failure rate

### 4.6 API Response Optimization
- **Compression:** Enable Brotli/Gzip on API responses
- **Field selection:** Allow clients to select fields (GraphQL, sparse fieldsets)
- **Pagination:** Cursor pagination for large datasets
- **Caching:** Cache-Control headers for cacheable responses
- **ETag:** Use ETag for conditional requests — save bandwidth
- **Minimize payload:** Don't return unnecessary data

### 4.7 Server-Side Rendering Optimization
- **Streaming:** Use streaming SSR (React 18 `renderToPipeableStream`) for faster TTFB
- **Selective SSR:** Only SSR above-the-fold content — hydrate rest on client
- **Caching:** Cache SSR output at CDN edge
- **ISR (Incremental Static Regeneration):** Static pages with periodic revalidation
- **Edge SSR:** Render at edge (Cloudflare Workers, Vercel Edge) for lower latency

---

## Part 5: Network Optimization

### 5.1 HTTP/2 and HTTP/3
- **HTTP/2:** Multiplexing, header compression, server push (deprecated)
- **HTTP/3:** QUIC protocol — faster connection setup, no head-of-line blocking
- **Enable:** Most CDNs and servers support HTTP/2 by default
- **Verify:** Check `ALPN` header or browser DevTools protocol column

### 5.2 Compression (Brotli/Gzip)
- **Brotli:** Better compression than Gzip — supported by all modern browsers
- **Level:** Brotli level 4-6 for dynamic content, 11 for static assets
- **CDN:** Enable at CDN edge
- **Minimum size:** Don't compress responses < 1KB — overhead exceeds benefit

### 5.3 CDN Configuration
- **Cache-Control:** `public, max-age=31536000, immutable` for hashed assets
- **Cache-Control:** `public, max-age=0, must-revalidate` for HTML
- **Surrogate-Key:** For targeted cache invalidation
- **Edge functions:** Run logic at edge for lower latency
- **Origin shield:** Reduce origin requests with secondary CDN layer

### 5.4 Resource Hints
```html
<!-- Preload critical resources -->
<link rel="preload" as="font" href="/font.woff2" crossorigin>
<link rel="preload" as="image" href="/hero.jpg" fetchpriority="high">

<!-- Prefetch next page -->
<link rel="prefetch" href="/next-page.js">

<!-- Preconnect to origins -->
<link rel="preconnect" href="https://api.example.com">
<link rel="dns-prefetch" href="https://cdn.example.com">

<!-- Prerender (Speculation Rules API) -->
<script type="speculationrules">
{ "prerender": [{ "where": { "hrefMatches": "/dashboard" } }] }
</script>
```

### 5.5 Service Worker Caching
- **Cache-first:** Static assets (CSS, JS, fonts, images)
- **Stale-while-revalidate:** API responses, dynamic content
- **Network-first:** Fresh content with offline fallback
- **Workbox:** Google's library for service worker caching strategies
- **Update flow:** `skipWaiting()` + `clients.claim()` for immediate updates

### 5.6 Prefetching Strategies
- **Link hover:** Prefetch route on link hover/touch start
- **Viewport:** Prefetch links that enter viewport
- **Intent:** Prefetch on visible + idle (Next.js `prefetch="intent"`)
- **Speculation Rules:** Prerender pages user is likely to navigate to
- **Data prefetch:** Prefetch API data for likely next page

---

## Part 6: Memory Management

### 6.1 Detecting Memory Leaks
- **DevTools Memory panel:** Heap snapshot comparison, allocation timeline
- **Signs:** Growing memory usage over time, increasing GC frequency, degraded performance
- **Common causes:**
  - Uncleared intervals/timeouts
  - Event listeners not removed
  - Closures holding large objects
  - Detached DOM nodes
  - Caches without eviction
  - WebSocket connections not closed

### 6.2 Heap Snapshots
1. Take snapshot 1 (baseline)
2. Perform actions (navigate, interact)
3. Take snapshot 2
4. Compare snapshots — what grew?
5. Look for retained objects that shouldn't be retained

### 6.3 Garbage Collection Optimization
- **Avoid frequent allocations:** Reuse objects, object pooling for frequent operations
- **Avoid creating objects in hot paths:** Move object creation outside loops
- **Typed arrays:** Use for numeric data — more efficient than regular arrays
- **WeakMap/WeakSet:** For data associated with objects that may be GC'd

### 6.4 Detached DOM Nodes
- **Cause:** Element removed from DOM but still referenced in JavaScript
- **Detection:** Heap snapshot — search for "Detached DOM"
- **Fix:** Clear references when removing elements: `element = null`
- **React:** Use proper cleanup in `useEffect` — remove listeners, clear refs

---

## Part 7: Rendering Performance

### 7.1 React Rendering Optimization
- **Memoization:** `React.memo` for components, `useMemo` for values, `useCallback` for functions
- **Key props:** Stable, unique keys for list items — don't use array index
- **Context splitting:** Split contexts to prevent unnecessary consumer re-renders
- **State colocation:** Keep state as close as possible to where it's used
- **Virtualization:** `react-window` or `@tanstack/react-virtual` for long lists
- **useDeferredValue:** Defer expensive renders until browser is idle
- **useTransition:** Mark non-urgent updates as transitions

### 7.2 Virtualization for Long Lists
```javascript
import { FixedSizeList } from 'react-window';

<FixedSizeList height={600} itemCount={10000} itemSize={35} width="100%">
  {({ index, style }) => <div style={style}>Item {index}</div>}
</FixedSizeList>
```
- Only render visible items + overscan
- Dramatically reduces DOM nodes and memory
- Use for lists > 100 items
- Consider `@tanstack/react-virtual` for variable height items

### 7.3 Debouncing and Throttling
```javascript
// Debounce: wait until calls stop for N ms
const debouncedSearch = debounce(search, 300);

// Throttle: call at most once per N ms
const throttledScroll = throttle(handleScroll, 16); // 60fps
```
- **Debounce:** Search input, resize handler, autosave
- **Throttle:** Scroll handler, mouse move, animation
- **requestAnimationFrame:** For visual updates — syncs with browser paint

### 7.4 Web Workers
```javascript
// main.js
const worker = new Worker(new URL('./worker.js', import.meta.url));
worker.postMessage({ data: largeDataset });
worker.onmessage = (e) => setResult(e.data);

// worker.js
self.onmessage = (e) => {
  const result = expensiveComputation(e.data);
  self.postMessage(result);
};
```
- Offload heavy computation to background thread
- No DOM access in workers
- Use for: data processing, parsing, encryption, image manipulation

### 7.5 CSS Containment
```css
.isolated-component {
  contain: layout style paint;
}

.off-screen-section {
  content-visibility: auto;
  contain-intrinsic-size: 0 500px;
}
```
- **layout:** Isolates layout changes to the element
- **paint:** Isolates painting to the element's bounds
- **style:** Isolates style recalculation
- **content-visibility: auto:** Skips rendering for off-screen content

### 7.6 Layout Thrashing
```javascript
// Bad: read-write-read-write forces reflow each time
elements.forEach(el => {
  const height = el.offsetHeight;  // read (forces layout)
  el.style.height = height + 10 + 'px';  // write (invalidates layout)
});

// Good: batch reads then writes
const heights = elements.map(el => el.offsetHeight);  // all reads
elements.forEach((el, i) => {
  el.style.height = heights[i] + 10 + 'px';  // all writes
});
```

---

## Part 8: Build Optimization

### 8.1 Minification
- **JavaScript:** esbuild (Vite default), terser, swc
- **CSS:** lightningcss, cssnano, esbuild
- **HTML:** html-minifier-terser
- **JSON:** Remove whitespace in production
- **Mangle:** Shorten variable names (terser)

### 8.2 Source Map Management
- **Production:** Generate source maps for error tracking, don't serve publicly
- **Upload to Sentry:** `sentry-cli sourcemaps upload`
- **Hidden source maps:** `sourcemap: 'hidden'` — not referenced in bundle
- **No source maps:** If not using error tracking — smallest bundle

### 8.3 Asset Inlining
```javascript
// Vite: inline assets < 4KB
build: { assetsInlineLimit: 4096 }
```
- Inline small assets as base64 data URIs
- Reduces HTTP requests
- Don't inline large assets — increases HTML size

### 8.4 Brotli/Gzip Pre-compression
```javascript
// Vite plugin
import { compression } from 'vite-plugin-compression';

plugins: [
  compression({ algorithm: 'brotliCompress' }),
  compression({ algorithm: 'gzip' }),
]
```
- Pre-compress at build time — faster than runtime compression
- Serve both Brotli and Gzip — let CDN negotiate
- CDN can also compress at edge

### 8.5 Polyfill Management
- **Target modern browsers:** Don't polyfill for browsers nobody uses
- **@vitejs/plugin-legacy:** Generate legacy bundle for older browsers
- **Core-JS:** Use `browserslist` to control polyfills
- **Feature detection:** Use `?` optional chaining, `??` nullish coalescing — widely supported

### 8.6 Dynamic Import vs Static Import
```javascript
// Static: always in bundle
import { heavyFunction } from 'heavy-lib';

// Dynamic: loaded on demand
const { heavyFunction } = await import('heavy-lib');
```
- Use dynamic import for: route components, heavy libraries, feature-gated code
- Use static import for: critical path code, small utilities

---

## Part 9: Database Performance

### 9.1 Index Strategy
- **B-tree:** Default — good for equality and range queries
- **GIN:** Good for array, JSONB, full-text search
- **GiST:** Good for geometric, nearest-neighbor
- **Partial index:** `CREATE INDEX ... WHERE condition` — smaller, faster
- **Covering index:** `CREATE INDEX ... INCLUDE (column)` — index-only scan
- **Composite index:** Column order matters — most selective first

### 9.2 Query Analysis with EXPLAIN
```sql
EXPLAIN ANALYZE SELECT * FROM orders WHERE user_id = 123 ORDER BY created_at DESC LIMIT 10;
```
- **Seq Scan:** Bad on large tables — needs index
- **Index Scan:** Good — using index
- **Index Only Scan:** Best — all data from index, no table access
- **Sort:** Bad if not using index — add index on sort column
- **Nested Loop:** Can be slow on large datasets — check join strategy

### 9.3 Pagination Optimization
```sql
-- Bad: OFFSET — gets slower as offset grows
SELECT * FROM items ORDER BY id OFFSET 10000 LIMIT 20;

-- Good: Cursor (keyset) pagination — constant time
SELECT * FROM items WHERE id > 10000 ORDER BY id LIMIT 20;
```

### 9.4 Read Replicas
- **Setup:** Primary for writes, replicas for reads
- **Read/write splitting:** Route reads to replica, writes to primary
- **Replication lag:** Monitor — reads may see stale data
- **Use cases:** Read-heavy workloads, reporting, analytics

### 9.5 Materialized Views
```sql
CREATE MATERIALIZED VIEW order_summary AS
SELECT user_id, COUNT(*), SUM(total) FROM orders GROUP BY user_id;

REFRESH MATERIALIZED VIEW CONCURRENTLY order_summary;
```
- Pre-compute expensive aggregations
- Refresh concurrently to avoid locks
- Good for dashboards, reports, analytics

---

## Part 10: Monitoring & Continuous Performance

### 10.1 Performance Dashboard
- **Core Web Vitals over time:** LCP, CLS, INP, TTFB, FCP
- **By device:** Mobile vs desktop
- **By geography:** Different regions may have different performance
- **By page:** Which pages are slowest?
- **Trends:** Is performance getting better or worse?

### 10.2 Alerting on Regressions
- **Lighthouse CI:** Fail PR if score drops below threshold
- **Bundle size:** Fail PR if bundle size increases beyond budget
- **RUM alerting:** Alert if p75 LCP exceeds 2.5s
- **TTFB alerting:** Alert if p75 TTFB exceeds 800ms

### 10.3 Performance Regression Testing
- **Lighthouse in CI:** Run on every PR, compare to baseline
- **Bundle size check:** `size-limit` or `bundlewatch` in CI
- **Custom timing:** Playwright timing for critical user flows
- **Visual regression:** Catch performance-impacting layout changes

### 10.4 Continuous Optimization Culture
- **Performance budget:** Enforce in CI — can't merge if over budget
- **Performance review:** Include in code review — does this change impact performance?
- **Regular audits:** Monthly performance review — what regressed? What can improve?
- **User feedback:** Monitor for complaints about speed — correlate with metrics

---

## Execution Instructions for Cascade

When this skill is activated for performance optimization:

1. **Measure first** — run Lighthouse, check RUM data, profile with DevTools
2. **Identify bottlenecks** — what's the slowest part? LCP? INP? TTFB? Bundle size?
3. **Set performance budgets** — Core Web Vitals targets, bundle size limits
4. **Optimize the bottleneck** — don't guess, use data to prioritize
5. **Frontend:** Bundle analysis, code splitting, image/font optimization, render performance
6. **Backend:** Query optimization, caching, connection pooling, background jobs
7. **Network:** CDN, compression, resource hints, HTTP/2+
8. **Database:** Indexes, EXPLAIN ANALYZE, cursor pagination, materialized views
9. **Monitor:** Set up RUM, Lighthouse CI, performance dashboards, alerting
10. **Verify:** Re-measure after optimization — confirm improvement
11. **Enforce budgets:** CI gates to prevent regressions
12. **Document:** Record what was optimized, why, and the before/after metrics
