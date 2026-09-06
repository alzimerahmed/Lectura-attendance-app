# Rule: Accessibility (a11y) for All Projects

**ALWAYS** apply the Accessibility skill and workflow when building or auditing web applications. Accessibility is a civil right, not a feature — never exclude users with disabilities.

## Skill
`~/.codeium/windsurf/skills/accessibility-a11y.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/accessibility.md` — invoke with `/accessibility`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/a11y-specialist.md` (parent: Quality Engineer)

## How to follow this rule:
1. When building or auditing accessibility, invoke the `/accessibility` workflow
2. Follow the workflow steps in order: Automated Testing → WCAG 2.2 Review → Semantic HTML/ARIA → Keyboard Navigation → Screen Reader Testing → Visual Accessibility → Motor Accessibility → Cognitive Accessibility → CI Gates → Legal Compliance
3. Always target WCAG 2.2 AA compliance as the minimum standard
4. Always combine automated testing (axe-core) with manual screen reader testing
5. Always test keyboard navigation — Tab through every page, verify all interactions
6. Always ensure color contrast: 4.5:1 normal text, 3:1 large text and UI components
7. Always set up automated a11y testing in CI (vitest-axe, Playwright axe, Pa11y)
8. Never rely on automated tools alone — they catch only ~30% of WCAG issues

## When this rule applies:
- Building any new website or web application
- Before launch — verify WCAG 2.2 compliance
- After significant UI changes
- When setting up automated a11y testing in CI
- User asks about accessibility

## When this rule does NOT apply:
- Non-web projects
- User explicitly says to skip accessibility
