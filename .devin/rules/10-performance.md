# Rule: Performance Optimization for All Projects

**ALWAYS** apply the Performance Optimization skill and workflow when profiling, analyzing, and optimizing web performance. Measure first, optimize second — never guess at what's slow.

## Skill
`~/.codeium/windsurf/skills/performance-optimization.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/performance.md` — invoke with `/performance`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/performance-engineer.md` (parent: Quality Engineer)

## How to follow this rule:
1. When optimizing performance, invoke the `/performance` workflow
2. Follow the workflow steps in order: Measure → Identify Bottlenecks → Set Budgets → Frontend → Backend → Network → Rendering → Database → Monitoring → Verify
3. Always measure before optimizing — Lighthouse, RUM, DevTools profiling
4. Always target Core Web Vitals: LCP < 2.5s, CLS < 0.1, INP < 200ms
5. Always set performance budgets and enforce in CI
6. Always optimize the biggest bottleneck first — use data to prioritize
7. Always verify improvements by re-measuring after optimization
8. Always set up continuous monitoring to catch regressions

## When this rule applies:
- When performance is slow or needs improvement
- Before launch — verify Core Web Vitals targets
- After significant changes that could impact performance
- When setting up performance monitoring and budgets
- User asks about performance optimization

## When this rule does NOT apply:
- Non-web projects
- User explicitly says to skip performance optimization
