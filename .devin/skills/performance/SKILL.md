---
name: performance
description: Comprehensive performance optimization workflow — profile, analyze, and optimize web performance with Core Web Vitals, RUM, and budget enforcement
---

# Performance Optimization Workflow

This workflow applies the **Performance Optimization Skill** (`~/.codeium/windsurf/skills/performance-optimization.md`) to systematically profile, analyze, and optimize web performance.

## When to Run
- When performance is slow or needs improvement
- When the user says `/performance` or asks about optimization
- Before launch — verify Core Web Vitals targets
- After significant changes that could impact performance
- When setting up performance monitoring and budgets

---

## Step 1: Measure Current Performance

1. Run Lighthouse audit on key pages — record scores
2. Check Real User Monitoring (RUM) data — p75 Core Web Vitals
3. Profile with Chrome DevTools Performance panel — identify long tasks
4. Analyze bundle size — `vite-bundle-visualizer` or `@next/bundle-analyzer`
5. Check network waterfall — identify slow requests, render-blocking resources
6. Run WebPageTest for multi-location, multi-device testing
7. Record baseline metrics: LCP, CLS, INP, TTFB, FCP, bundle size

## Step 2: Identify Bottlenecks

1. **LCP > 2.5s?** Check: server response time, LCP element (image/text), render-blocking resources
2. **CLS > 0.1?** Check: images without dimensions, font loading, dynamic content insertion
3. **INP > 200ms?** Check: long tasks, expensive event handlers, main thread blocking
4. **TTFB > 800ms?** Check: server computation, database queries, CDN configuration
5. **Large bundle?** Check: heavy dependencies, missing code splitting, missing tree shaking
6. **Slow database?** Check: EXPLAIN ANALYZE, missing indexes, N+1 queries
7. Prioritize: fix the biggest bottleneck first

## Step 3: Set Performance Budgets

1. Define Core Web Vitals targets: LCP < 2.5s, CLS < 0.1, INP < 200ms
2. Define bundle size limits: JS < 200KB, CSS < 50KB, total < 600KB
3. Define resource count limits: third-party scripts < 5
4. Create `budget.json` for Lighthouse CI
5. Add bundle size checks to CI pipeline
6. Set up alerting for budget threshold breaches

## Step 4: Optimize Frontend

1. **Bundle:** Remove heavy deps, tree shake, code split (route + component based)
2. **Images:** AVIF/WebP, responsive srcset, lazy load, preload LCP image
3. **Fonts:** WOFF2, font-display: swap, preload, subset
4. **CSS:** Inline critical CSS, defer non-critical, purge unused
5. **JavaScript:** Defer non-critical, break long tasks, Web Workers for heavy computation
6. **Third-party scripts:** Audit, defer, use Partytown, server-side tracking
7. **Resource hints:** preload, prefetch, preconnect, dns-prefetch

## Step 5: Optimize Backend

1. **Database:** EXPLAIN ANALYZE slow queries, add indexes, eliminate N+1, cursor pagination
2. **Caching:** CDN cache, edge cache, application cache (Redis), database cache
3. **Connection pooling:** PgBouncer, right-size pool, set timeouts
4. **Background jobs:** Offload expensive operations to queues
5. **API response:** Compression, field selection, pagination, ETag
6. **SSR:** Streaming SSR, selective SSR, ISR, edge SSR

## Step 6: Optimize Network

1. **HTTP/2 or HTTP/3:** Verify enabled
2. **Compression:** Brotli (preferred) and Gzip, pre-compress at build time
3. **CDN:** Configure cache headers, edge functions, origin shield
4. **Resource hints:** preload critical, prefetch likely-next, preconnect to origins
5. **Service worker:** Cache static assets, stale-while-revalidate for dynamic

## Step 7: Optimize Rendering

1. **React:** Memoization, virtualization for long lists, context splitting, state colocation
2. **Debounce/throttle:** Scroll, resize, search, input handlers
3. **CSS containment:** `contain: layout style paint`, `content-visibility: auto`
4. **Layout thrashing:** Batch DOM reads and writes
5. **Animation:** Only animate `transform` and `opacity` (GPU-composited)

## Step 8: Optimize Database

1. **Indexes:** Add for frequently filtered/sorted columns, composite indexes, partial indexes
2. **Query analysis:** EXPLAIN ANALYZE — find sequential scans, bad joins
3. **Pagination:** Switch from OFFSET to cursor (keyset) pagination
4. **Read replicas:** Route reads to replica for read-heavy workloads
5. **Materialized views:** Pre-compute expensive aggregations
6. **Connection management:** Pool, timeout, monitor utilization

## Step 9: Set Up Monitoring

1. **RUM:** web-vitals library → send to analytics (GA4, Vercel Analytics, Sentry)
2. **Lighthouse CI:** Run on every PR, compare to baseline, fail on regression
3. **Bundle size check:** `size-limit` or `bundlewatch` in CI
4. **Performance dashboard:** Core Web Vitals over time, by device, by page
5. **Alerting:** p75 LCP > 2.5s, p75 INP > 200ms, bundle size > budget

## Step 10: Verify & Document

1. Re-run Lighthouse — compare to baseline
2. Check RUM data — verify field metrics improved
3. Profile with DevTools — confirm bottleneck resolved
4. Run full test suite — verify no regressions
5. Document: what was optimized, why, before/after metrics
6. Update performance budget if targets were adjusted
7. Set up continuous monitoring to catch future regressions
