---
name: analytics
description: Comprehensive analytics & tracking workflow — tool selection, event design, privacy/consent, conversion tracking, dashboards, and A/B testing
---

# Analytics & Tracking Workflow

This workflow applies the **Analytics & Tracking Skill** (`~/.codeium/windsurf/skills/analytics-tracking.md`) to implement comprehensive analytics and measurement.

## When to Run
- When setting up analytics for a new project
- When the user says `/analytics` or asks about tracking
- When implementing conversion tracking or event tracking
- When setting up consent management for GDPR/CCPA
- When creating analytics dashboards or reports

---

## Step 1: Assess Analytics Needs

1. Read the project context — purpose, audience, business goals
2. Identify compliance requirements — GDPR, CCPA, EAA
3. Determine analytics needs: web analytics, product analytics, e-commerce tracking, or all
4. Check existing analytics setup — what's already in place?
5. Identify key business questions analytics should answer

## Step 2: Choose Analytics Tools

1. Select primary analytics tool based on needs:
   - Simple site: Plausible, Fathom, Umami (privacy-friendly, cookieless)
   - SaaS product: PostHog, Mixpanel, Amplitude (event tracking, funnels, retention)
   - E-commerce: GA4 + PostHog/Mixpanel (e-commerce + product analytics)
   - Enterprise: Segment/RudderStack CDP + multiple destinations
2. Consider privacy requirements — self-hosting, cookieless, EU data residency
3. Set up CDP if multi-tool strategy needed (Segment, RudderStack)

## Step 3: Design Event Taxonomy

1. Define naming conventions — snake_case, past tense, specific, consistent
2. Create event dictionary — name, trigger, properties, description for each event
3. Define key events: signup, login, purchase, feature_use, content_view, search
4. Define event properties — what context to capture with each event
5. Define user identification strategy — anonymous ID → user ID association
6. Define session tracking — session start, properties, duration

## Step 4: Implement Consent Management

1. Choose consent management platform — Cookiebot, OneTrust, Klaro, or custom
2. Implement consent banner — clear, accessible, accept/reject options
3. Block analytics scripts until consent given
4. Persist consent choice in localStorage/cookie
5. Allow consent withdrawal at any time
6. Document data collection in privacy policy
7. Set up data retention policies in analytics tools

## Step 5: Implement Event Tracking

1. Set up analytics SDK (client-side and/or server-side)
2. Implement page view tracking (SPA route changes for single-page apps)
3. Implement custom event tracking for key user actions
4. Implement e-commerce tracking (view_item, add_to_cart, purchase)
5. Implement user identification (anonymous → authenticated)
6. Implement UTM parameter capture and persistence
7. Set up server-side tracking for conversions (accuracy, no ad blockers)

## Step 6: Configure Analytics Tools

1. **GA4 (if using):** Data streams, events, conversions, audiences, enhanced measurement
2. **PostHog/Mixpanel (if using):** Funnels, retention, user flows, cohorts, session replay
3. **GTM (if using):** Data layer, triggers, variables, tags, server-side container
4. **CDP (if using):** Destinations, event mapping, identity resolution

## Step 7: Set Up Conversion Tracking

1. Define primary conversions (signup, purchase, lead_form_submit)
2. Define micro conversions (view_pricing, add_to_cart, start_signup)
3. Assign conversion values
4. Configure attribution model (data-driven recommended)
5. Set up cross-domain tracking if needed
6. Implement UTM parameter persistence through to conversion
7. Set up server-side conversion tracking for accuracy

## Step 8: Set Up Performance Monitoring

1. Implement Core Web Vitals tracking (LCP, CLS, INP, TTFB, FCP)
2. Send web vitals data to analytics
3. Set up error tracking (Sentry, Bugsnag)
4. Track custom performance metrics (API response time, feature load time)
5. Link errors to analytics events for context

## Step 9: Create Dashboards & Reports

1. **Key metrics dashboard:** Traffic, engagement, conversions, funnel, retention, performance
2. **Real-time dashboard:** Active users, live events, conversions today, alerts
3. **Scheduled reports:** Weekly summary, monthly report, stakeholder report
4. **Alerting:** Traffic anomaly, conversion drop, error spike, performance regression
5. Set up automated delivery (email, Slack)

## Step 10: Set Up A/B Testing

1. Design experiment — hypothesis, primary/secondary metrics, sample size, duration
2. Implement with feature flags (PostHog, GrowthBook, custom)
3. Track which variant each user sees
4. Track conversions with experiment context
5. Monitor for statistical significance (p < 0.05)
6. Document results and decisions

## Step 11: Document & Review

1. Create event dictionary — all events with properties and descriptions
2. Create tracking plan — what to track, when, how
3. Document dashboard guides — how to read each dashboard
4. Review privacy compliance — GDPR, CCPA, consent, retention
5. Audit tracking implementation — verify events fire correctly
6. Schedule regular reviews — quarterly analytics audit
