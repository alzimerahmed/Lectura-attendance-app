# Rule: Analytics & Tracking for All Projects

**ALWAYS** apply the Analytics & Tracking skill and workflow when implementing analytics, tracking, and measurement. Measure what matters, not what's easy — track events that map to business outcomes.

## Skill
`~/.codeium/windsurf/skills/analytics-tracking.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/analytics.md` — invoke with `/analytics`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/analytics-engineer.md` (parent: Data Engineer)

## How to follow this rule:
1. When setting up analytics, invoke the `/analytics` workflow
2. Follow the workflow steps in order: Assess Needs → Choose Tools → Event Taxonomy → Consent → Tracking → Configure → Conversions → Performance → Dashboards → A/B Testing → Document
3. Always implement consent management before loading analytics scripts (GDPR/CCPA)
4. Always design an event taxonomy with consistent naming conventions and an event dictionary
5. Always use server-side tracking for conversions (accuracy, no ad blockers)
6. Always track Core Web Vitals alongside business metrics
7. Always set up dashboards with key metrics, real-time monitoring, and alerting
8. Always document the tracking plan and event dictionary

## When this rule applies:
- Setting up analytics for a new project
- Implementing conversion tracking or event tracking
- Setting up consent management for GDPR/CCPA
- Creating analytics dashboards or reports
- User asks about analytics or tracking

## When this rule does NOT apply:
- Projects where analytics is explicitly not needed
- User explicitly says to skip analytics
