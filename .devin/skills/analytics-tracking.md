---
name: Analytics & Tracking Skill
description: Comprehensive methodology for implementing analytics, tracking, and measurement — 2025-2026 practices with privacy-first tracking, server-side analytics, consent management, and actionable dashboards
version: 1.0.0
tags: [analytics, tracking, google-analytics, gtm, posthog, mixpanel, amplitude, consent, privacy, events]
---

# Analytics & Tracking Skill

## Purpose
This skill provides a comprehensive methodology for implementing analytics, tracking, and measurement across any kind of web project. It reflects **modern 2025-2026 practices** — privacy-first tracking, server-side analytics, consent management, cookieless tracking, and actionable dashboards. Not just collecting data but collecting the right data and turning it into decisions.

## Core Philosophy

**Measure what matters, not what's easy.** Page views are easy to track but rarely actionable. Track events that map to business outcomes: signups, purchases, feature usage, retention. Every tracked event should answer a question you actually ask.

**The #1 rule:** Privacy is not optional. GDPR, CCPA, and browser tracking protections require consent before tracking. Design analytics with privacy as a first-class concern, not an afterthought.

---

## Part 1: Analytics Tool Selection

### 1.1 Tool Comparison

| Tool | Best For | Pricing | Privacy |
|---|---|---|---|
| **Google Analytics 4** | General web analytics, SEO | Free (limits) | GDPR concerns, US data |
| **PostHog** | Product analytics, session replay, flags | Free tier, usage-based | Self-hostable, EU option |
| **Mixpanel** | Event-based product analytics | Free tier, usage-based | GDPR compliant |
| **Amplitude** | Product analytics, user journeys | Free tier, enterprise | GDPR compliant |
| **Plausible** | Privacy-focused, simple | $9+/month | Cookieless, GDPR |
| **Fathom** | Privacy-focused, simple | $14+/month | Cookieless, GDPR |
| **Umami** | Open-source, self-hosted | Free (self-hosted) | Cookieless, GDPR |
| **Vercel Analytics** | Next.js performance + audience | Free tier included | Privacy-friendly |
| **Segment** | CDP — route data to multiple tools | Free tier, usage-based | GDPR compliant |
| **RudderStack** | Open-source CDP | Free (self-hosted) | Self-hostable |

### 1.2 Choosing the Right Tool
- **Simple site (blog, portfolio):** Plausible, Fathom, Umami — privacy-friendly, simple
- **SaaS product:** PostHog, Mixpanel, Amplitude — event tracking, funnels, retention
- **E-commerce:** GA4 + PostHog/Mixpanel — e-commerce tracking + product analytics
- **Enterprise:** Segment + multiple destinations — CDP for routing to various tools
- **Privacy-critical:** PostHog (self-hosted), Umami, Plausible — no cookies, no PII

### 1.3 Multi-Tool Strategy with CDP
```
User Action → Segment/RudderStack → Route to:
  → Google Analytics 4 (web analytics)
  → PostHog (product analytics)
  → Customer.io (email marketing)
  → Salesforce (CRM)
  → Slack (notifications)
```
- **CDP (Customer Data Platform):** Collect once, route everywhere
- **Single API:** Track events through one SDK
- **Consistency:** Same event names and properties across all tools
- **Privacy:** Handle consent at the CDP level

---

## Part 2: Event Tracking Design

### 2.1 Event Taxonomy
```
Category → Action → Label (optional) → Value (optional)

Examples:
  user → signup → email → null
  user → login → google → null
  content → view → blog_post → "getting-started-guide"
  content → search → null → "react hooks"
  feature → use → export_csv → null
  e-commerce → add_to_cart → product_id → "sku-123"
  e-commerce → purchase → order_id → "ord-456"
```

### 2.2 Naming Conventions
- **snake_case:** `user_signed_up`, `item_added_to_cart`, `purchase_completed`
- **Past tense:** `signed_up` not `sign_up`, `purchased` not `purchase`
- **Specific:** `button_clicked` not `click` — which button? where?
- **Consistent:** Same event name across all surfaces (web, mobile, API)
- **Documented:** Event dictionary with name, trigger, properties, description

### 2.3 Event Properties
```typescript
// User signs up
track('user_signed_up', {
  method: 'email',           // email, google, github
  plan: 'free',              // free, pro, enterprise
  source: 'landing_page',    // where did they come from?
  trial_days: 14,            // trial length
});

// Purchase completed
track('purchase_completed', {
  order_id: 'ord-456',
  revenue: 99.00,
  currency: 'USD',
  items: 3,
  product_ids: ['sku-1', 'sku-2', 'sku-3'],
  payment_method: 'credit_card',
  discount_code: 'SAVE10',
});
```

### 2.4 User Identification
- **Anonymous ID:** Generated on first visit — `localStorage` or cookie
- **User ID:** Set after login — persistent across sessions
- **Associate:** Link anonymous ID to user ID after signup
- **Properties:** User traits (plan, role, signup_date, company)
- **PII:** Don't track PII (email, name, phone) unless necessary and consented

### 2.5 Session Tracking
- **Session start:** First event in a period (30 min inactivity = new session)
- **Session ID:** Unique per session — link all events in a session
- **Session properties:** Landing page, referrer, UTM params, device, location
- **Session duration:** Last event timestamp - first event timestamp

### 2.6 Page View Tracking
- **Traditional:** Page load fires pageview event
- **SPA:** Track route changes — `history.pushState` / `popstate` listener
- **Next.js:** `useReportWebVitals` or router events
- **Properties:** path, title, referrer, search query, load time

### 2.7 Custom Event Tracking
```typescript
// React hook for tracking
function useTrack() {
  return useCallback((event: string, properties?: Record<string, any>) => {
    if (typeof window !== 'undefined' && window.analytics) {
      window.analytics.track(event, properties);
    }
  }, []);
}

// Usage
const track = useTrack();
<button onClick={() => track('export_clicked', { format: 'csv' })}>
  Export
</button>
```

### 2.8 E-commerce Tracking
```typescript
// View item
track('item_viewed', {
  item_id: 'sku-123',
  item_name: 'Wireless Headphones',
  category: 'Electronics',
  price: 99.00,
  currency: 'USD',
});

// Add to cart
track('item_added_to_cart', {
  item_id: 'sku-123',
  quantity: 2,
  price: 99.00,
  currency: 'USD',
});

// Purchase
track('purchase_completed', {
  transaction_id: 'ord-456',
  items: [{ item_id: 'sku-123', quantity: 2, price: 99.00 }],
  total: 198.00,
  tax: 15.84,
  shipping: 0,
  currency: 'USD',
  coupon: 'SAVE10',
});
```

---

## Part 3: Privacy & Consent

### 3.1 GDPR Compliance
- **Lawful basis:** Consent before setting non-essential cookies or tracking
- **Transparency:** Clear privacy policy explaining what data is collected and why
- **Data minimization:** Only collect data you need
- **Right to access:** Users can request their data
- **Right to erasure:** Users can request data deletion ("right to be forgotten")
- **Data retention:** Define and enforce retention periods
- **DPA:** Data Processing Agreement with analytics vendor

### 3.2 CCPA/CPRA Compliance
- **Right to know:** What personal information is collected
- **Right to delete:** Request deletion of personal information
- **Right to opt-out:** Sale of personal information (includes "sharing" under CPRA)
- **Right to non-discrimination:** Don't degrade service for users who opt out
- **Notice at collection:** Inform users at point of collection

### 3.3 Consent Management Platforms
- **Cookiebot:** Popular CMP, auto-blocks scripts until consent
- **OneTrust:** Enterprise CMP with workflow and templates
- **Consent Manager (Osano):** Privacy-focused CMP
- **Klaro:** Open-source, lightweight consent manager
- **Custom:** Build your own with a consent banner + script blocking

### 3.4 Consent Banner Implementation
```tsx
function ConsentBanner() {
  const [consent, setConsent] = useState<ConsentState>('unknown');

  useEffect(() => {
    const stored = localStorage.getItem('consent');
    if (stored) setConsent(JSON.parse(stored));
  }, []);

  const handleAccept = () => {
    const consent = { analytics: true, marketing: true, timestamp: Date.now() };
    localStorage.setItem('consent', JSON.stringify(consent));
    setConsent('accepted');
    // Load analytics scripts
    loadAnalytics();
  };

  if (consent === 'accepted' || consent === 'rejected') return null;

  return (
    <div role="dialog" aria-label="Cookie consent">
      <p>We use cookies for analytics and marketing. You can accept or reject.</p>
      <button onClick={handleAccept}>Accept all</button>
      <button onClick={handleReject}>Reject all</button>
      <Link href="/privacy">Privacy policy</Link>
    </div>
  );
}
```

### 3.5 Script Blocking Before Consent
```tsx
// Block analytics scripts until consent
function AnalyticsScript() {
  const [consent, setConsent] = useState(false);

  useEffect(() => {
    const checkConsent = () => {
      const stored = localStorage.getItem('consent');
      if (stored) setConsent(JSON.parse(stored).analytics);
    };
    checkConsent();
    window.addEventListener('consent-change', checkConsent);
    return () => window.removeEventListener('consent-change', checkConsent);
  }, []);

  if (!consent) return null;

  return <Script src="https://analytics.example.com/script.js" />;
}
```

### 3.6 Cookieless Tracking
- **Plausible/Fathom/Umami:** No cookies, no PII — hash-based unique visitors
- **Privacy-friendly:** No consent needed in many jurisdictions (check legal)
- **Limitations:** Less accurate user tracking, no cross-domain tracking
- **Server-side:** Track events server-side — no client-side cookies needed

### 3.7 Server-Side Tracking
```typescript
// Server-side event tracking (no cookies, no client JS)
app.post('/api/signup', async (req, res) => {
  const user = await createUser(req.body);

  // Track server-side
  await analytics.track({
    event: 'user_signed_up',
    userId: user.id,
    properties: {
      method: req.body.method,
      plan: user.plan,
    },
    // No cookies needed — use server-side identification
  });

  res.json(user);
});
```
- **Benefits:** No consent needed (in many cases), no ad blockers, more accurate
- **Limitations:** Less client-side context (viewport, device details)
- **GTM Server-Side:** Google Tag Manager server-side container

### 3.8 Data Retention Policies
- **Analytics data:** 14-26 months (GA4 default)
- **Session recordings:** 3-6 months
- **User profiles:** Delete on account deletion
- **Logs:** 30-90 days
- **Implement:** Set retention in analytics tool settings
- **Automate:** Script to delete data older than retention period

---

## Part 4: Google Analytics 4

### 4.1 GA4 Setup
```html
<!-- Google tag (gtag.js) -->
<script async src="https://www.googletagmanager.com/gtag/js?id=G-XXXXXXXXXX"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());
  gtag('config', 'G-XXXXXXXXXX');
</script>
```

### 4.2 GA4 Events
```typescript
// Automatic events: page_view, scroll, click, view_search_results
// Enhanced measurement: scrolls, outbound clicks, site search, video engagement

// Custom events
gtag('event', 'signup', {
  method: 'email',
  plan: 'pro',
});

// E-commerce
gtag('event', 'purchase', {
  transaction_id: 'T-12345',
  value: 99.00,
  currency: 'USD',
  items: [{
    item_id: 'SKU_12345',
    item_name: 'Pro Plan',
    price: 99.00,
    quantity: 1,
  }],
});
```

### 4.3 GA4 Configuration
- **Data streams:** Web, iOS, Android — unified tracking
- **Events:** Mark key events as conversions
- **Audiences:** Create user segments for remarketing
- **Conversions:** Define conversion events (signup, purchase)
- **Enhanced measurement:** Enable automatic tracking (scrolls, clicks, searches)
- **User data collection:** Enable Google Signals for cross-device tracking (requires consent)

### 4.4 GA4 Conversions
- **Mark as conversion:** In GA4 admin → Events → Mark as conversion
- **Key conversions:** signup, purchase, lead_form_submit, demo_request
- **Conversion value:** Assign monetary value to non-purchase conversions
- **Conversion window:** 7 days default (configurable up to 90 days)

### 4.5 GA4 Audiences and Segments
- **Audiences:** Users who signed up in last 7 days, users who abandoned checkout
- **Remarketing:** Use audiences for Google Ads remarketing
- **User exploration:** Drill into individual user journeys
- **Funnel exploration:** Multi-step funnel analysis
- **Path exploration:** User path through the site

---

## Part 5: Product Analytics (PostHog/Mixpanel/Amplitude)

### 5.1 Funnels
```
View Landing → Sign Up → Onboarding Step 1 → Onboarding Complete → First Action → Retained (Day 7)
```
- **Steps:** Define each step with event and optional property filter
- **Conversion rate:** Percentage of users who complete each step
- **Time to convert:** How long between steps
- **Breakdown:** By source, device, plan, user segment
- **Identify drop-off:** Where are users leaving the funnel?

### 5.2 Retention Analysis
- **Cohort retention:** Users who signed up in week X — what % return in week X+1, X+2...
- **N-day retention:** Return on exact day N
- **Unbounded retention:** Return on any day after N
- **Stickiness:** How often users return (daily, weekly, monthly active)
- **Retention by segment:** Compare retention across user segments

### 5.3 User Flows
- **Visual flow:** User path through events — where do they go after signup?
- **Sankey diagram:** Visual representation of user paths
- **Identify patterns:** Common paths, unexpected paths, dead ends
- **Optimize:** Remove friction in the most common paths

### 5.4 Cohort Analysis
- **Signup cohort:** Users who signed up in the same period
- **Behavior cohort:** Users who performed a specific action
- **Compare:** Do users who use feature X retain better than those who don't?
- **A/B test cohorts:** Compare control vs variant cohorts

### 5.5 Session Replay (PostHog, Hotjar, FullStory)
- **Record user sessions:** Watch how users interact with the site
- **Identify UX issues:** Where do users get confused? Click the wrong thing?
- **Privacy:** Mask sensitive fields (passwords, credit cards, PII)
- **Consent:** Only record users who have given consent
- **Sampling:** Record a percentage of sessions, not all

### 5.6 Heatmaps (Hotjar, PostHog, Clarity)
- **Click heatmap:** Where do users click most?
- **Scroll heatmap:** How far do users scroll?
- **Move heatmap:** Where do users hover? (desktop only)
- **Identify:** Are users clicking non-clickable elements? Missing the CTA?
- **Mobile:** Separate heatmaps for mobile vs desktop

### 5.7 Feature Flags Integration
- **PostHog:** Feature flags + analytics in one platform
- **A/B test:** Track events by flag variant
- **Gradual rollout:** Enable feature for % of users, track impact
- **Kill switch:** Disable feature if metrics regress

---

## Part 6: Tag Management (GTM)

### 6.1 Google Tag Manager Setup
```html
<!-- GTM head -->
<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-XXXXXXX');</script>
```

### 6.2 Data Layer
```typescript
// Push events to data layer
window.dataLayer = window.dataLayer || [];
dataLayer.push({
  event: 'signup',
  method: 'email',
  plan: 'pro',
});
```

### 6.3 Triggers and Variables
- **Triggers:** Page view, click, form submit, custom event, scroll depth
- **Variables:** Click element, page URL, data layer variable, custom JavaScript
- **Tags:** GA4 event, Facebook Pixel, Google Ads, custom HTML/JS
- **Preview mode:** Test tags before publishing

### 6.4 Server-Side GTM
- **Server-side container:** Runs on your server — not in the browser
- **Benefits:** No ad blockers, better privacy, faster page load
- **Setup:** Google Cloud Run or Cloudflare Workers
- **Route:** Client GTM → Server GTM → Analytics tools

---

## Part 7: Conversion Tracking

### 7.1 Defining Conversions
- **Primary conversion:** The main action (signup, purchase, lead)
- **Micro conversions:** Steps toward primary (view pricing, add to cart, start signup)
- **Value:** Assign value to each conversion — revenue or relative value
- **Attribution:** Which channel/source gets credit for the conversion?

### 7.2 Cross-Domain Tracking
- **GA4:** Configure `linker` domains in GA4 settings
- **Cookie:** First-party cookie shared across domains via URL parameter
- **Server-side:** Use server-side tracking for cross-domain without cookies
- **User ID:** Link sessions across domains with user ID after login

### 7.3 Attribution Models
- **Last click:** Last channel gets 100% credit (default in most tools)
- **First click:** First channel gets 100% credit
- **Linear:** Equal credit to all channels
- **Time decay:** More credit to channels closer to conversion
- **Data-driven:** Algorithmic attribution based on actual data (GA4 default)

### 7.4 UTM Parameters
```
?utm_source=newsletter
&utm_medium=email
&utm_campaign=summer_sale
&utm_content=header_button
&utm_term=running_shoes
```
- **source:** Where the traffic comes from (newsletter, google, facebook)
- **medium:** How they get there (email, cpc, organic, social)
- **campaign:** Specific campaign name (summer_sale, black_friday)
- **content:** Which specific link (header_button, sidebar_banner)
- **term:** Paid keyword (for paid search)
- **Persist:** Store UTMs in first session, attribute to conversion

### 7.5 Server-Side Conversion Tracking
```typescript
// Track conversion server-side for accuracy
app.post('/api/checkout/success', async (req, res) => {
  const order = await processPayment(req.body);

  // Server-side conversion tracking
  await trackConversion({
    event: 'purchase',
    transaction_id: order.id,
    value: order.total,
    currency: order.currency,
    items: order.items,
    // Attribution from session
    utm_source: req.session.utm_source,
    utm_medium: req.session.utm_medium,
    utm_campaign: req.session.utm_campaign,
  });

  res.json(order);
});
```

---

## Part 8: Performance Monitoring Integration

### 8.1 Core Web Vitals Tracking
```typescript
import { onLCP, onCLS, onINP, onTTFB, onFCP } from 'web-vitals';

function sendToAnalytics(metric: any) {
  fetch('/api/analytics/web-vitals', {
    method: 'POST',
    body: JSON.stringify({
      name: metric.name,
      value: metric.value,
      id: metric.id,
      page: window.location.pathname,
    }),
  });
}

onLCP(sendToAnalytics);
onCLS(sendToAnalytics);
onINP(sendToAnalytics);
onTTFB(sendToAnalytics);
onFCP(sendToAnalytics);
```

### 8.2 Error Tracking Integration
- **Sentry:** Error tracking + performance monitoring
- **Bugsnag:** Error tracking with release tracking
- **LogRocket:** Session replay + error tracking
- **Integration:** Link errors to analytics events for context

### 8.3 Custom Performance Metrics
- **API response time:** Track API call duration
- **Time to interactive:** When can users first interact?
- **Time to first action:** How long until user's first meaningful action?
- **Feature load time:** How long does a specific feature take to load?

---

## Part 9: Dashboards & Reporting

### 9.1 Key Metrics Dashboard
- **Traffic:** Sessions, users, page views (by source, device, geography)
- **Engagement:** Bounce rate, pages per session, avg session duration
- **Conversions:** Conversion rate, total conversions, revenue
- **Funnel:** Step-by-step conversion through key funnel
- **Retention:** Day 1, Day 7, Day 30 retention
- **Performance:** Core Web Vitals (LCP, CLS, INP)

### 9.2 Real-Time Dashboard
- **Active users:** Users currently on the site
- **Live events:** Events happening in real-time
- **Conversions today:** Conversions in the last 24 hours
- **Alerts:** Anomaly detection — traffic spike, conversion drop

### 9.3 Scheduled Reports
- **Weekly summary:** Key metrics, week-over-week change
- **Monthly report:** Comprehensive performance review
- **Stakeholder report:** Executive summary for non-technical audience
- **Automated:** Schedule email/Slack delivery

### 9.4 Alerting
- **Traffic anomaly:** Traffic drops 50%+ from baseline
- **Conversion drop:** Conversion rate drops below threshold
- **Error spike:** Error rate exceeds threshold
- **Performance regression:** LCP exceeds 2.5s for p75
- **Tools:** GA4 custom alerts, Datadog, PagerDuty, Slack webhooks

---

## Part 10: A/B Testing Integration

### 10.1 Experiment Design
- **Hypothesis:** "Changing the CTA from 'Sign up' to 'Start free trial' will increase signups by 10%"
- **Primary metric:** Signup conversion rate
- **Secondary metrics:** Bounce rate, time on page, downstream retention
- **Sample size:** Calculate required sample for statistical significance
- **Duration:** Run for at least 1-2 weeks to account for day-of-week effects

### 10.2 Implementation with Feature Flags
```typescript
// PostHog A/B test
import { useFeatureFlag } from 'posthog-js/react';

function HeroSection() {
  const ctaVariant = useFeatureFlag('cta-text-experiment');

  return (
    <button>
      {ctaVariant === 'test' ? 'Start free trial' : 'Sign up'}
    </button>
  );
}
```

### 10.3 Tracking Experiment Results
```typescript
// Track which variant the user saw
track('experiment_viewed', {
  experiment_id: 'cta-text-experiment',
  variant: ctaVariant, // 'control' or 'test'
});

// Track conversion with experiment context
track('signup_completed', {
  experiment_id: 'cta-text-experiment',
  variant: ctaVariant,
});
```

### 10.4 Statistical Significance
- **P-value:** < 0.05 for statistical significance
- **Confidence interval:** Range of likely effect size
- **Power:** Probability of detecting a real effect (aim for 80%+)
- **Tools:** PostHog experiments, GrowthBook, Statsig — built-in significance calculators

---

## Execution Instructions for Cascade

When this skill is activated for analytics & tracking:

1. **Read the project context** — purpose, audience, business goals, compliance requirements
2. **Choose analytics tools** — based on needs (simple, product, enterprise, privacy-critical)
3. **Design event taxonomy** — naming conventions, event dictionary, properties
4. **Implement consent management** — GDPR/CCPA compliant consent banner with script blocking
5. **Set up tracking** — page views, custom events, e-commerce, user identification
6. **Set up server-side tracking** — for accuracy and privacy (conversions, key events)
7. **Configure GA4** — data streams, events, conversions, audiences, enhanced measurement
8. **Configure product analytics** — funnels, retention, user flows, cohorts (if applicable)
9. **Set up conversion tracking** — define conversions, UTM persistence, attribution, cross-domain
10. **Set up performance monitoring** — Core Web Vitals, error tracking, custom metrics
11. **Create dashboards** — key metrics, real-time, scheduled reports, alerting
12. **Set up A/B testing** — experiment design, feature flag integration, significance tracking
13. **Document everything** — event dictionary, tracking plan, dashboard guides
14. **Review privacy compliance** — GDPR, CCPA, data retention, consent management
