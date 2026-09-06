---
name: accessibility
description: Comprehensive accessibility audit workflow — WCAG 2.2 compliance, automated testing, manual screen reader testing, and inclusive design review
---

# Accessibility Audit Workflow

This workflow applies the **Accessibility (a11y) Skill** (`~/.codeium/windsurf/skills/accessibility-a11y.md`) to systematically audit and ensure web accessibility.

## When to Run
- When building a new project — integrate a11y from the start
- When the user says `/accessibility` or asks about a11y
- Before launch — verify WCAG 2.2 compliance
- After significant UI changes
- When setting up automated a11y testing in CI

---

## Step 1: Automated Testing

1. Run axe-core (browser extension or CLI) on key pages
2. Run Lighthouse accessibility audit — target score 95+
3. Run Pa11y on deployed URLs
4. Run vitest-axe / jest-axe on components in test suite
5. Run Playwright axe tests on E2E flows
6. Review all violations — filter false positives, classify real issues
7. Note: automated tools catch ~30% of WCAG issues — manual testing required

## Step 2: WCAG 2.2 Compliance Review

Go through each principle systematically:
- [ ] **Perceivable:** Alt text, captions, transcripts, contrast, resizable text
- [ ] **Operable:** Keyboard access, no traps, logical tab order, skip links, touch targets ≥ 24x24px
- [ ] **Understandable:** Language declared, consistent nav, predictable behavior, error prevention
- [ ] **Robust:** Valid HTML, ARIA correct, status messages announced
- [ ] **WCAG 2.2 new:** Focus not obscured, dragging alternatives, target size, consistent help, redundant entry prevention, accessible authentication

## Step 3: Semantic HTML & ARIA Review

1. Check native HTML usage: `<button>`, `<a>`, `<nav>`, `<main>`, `<header>`, `<footer>`
2. Check ARIA: roles, states, properties — no ARIA if native element exists
3. Check landmark roles: `<main>`, `<nav>`, `<header>`, `<footer>`, `<aside>`
4. Check heading hierarchy: one h1, no skipped levels, logical structure
5. Check forms: labels with `for`, `aria-describedby` for hints/errors, `autocomplete`
6. Check tables: `<caption>`, `<th scope>`, proper structure
7. Check navigation: skip link, `aria-current="page"`, keyboard accessible dropdowns

## Step 4: Keyboard Navigation Testing

1. Tab through entire page — verify logical tab order
2. Check all interactive elements are keyboard accessible
3. Check focus is visible (`:focus-visible` with 3:1 contrast)
4. Test modal: focus trap, Escape to close, focus return to trigger
5. Test SPA route changes: focus moves to new content
6. Test dropdown menus: arrow keys, Enter, Escape
7. Test forms: Tab through fields, submit with Enter
8. Check for keyboard traps (tab gets stuck)
9. Verify skip link works — focus jumps to main content

## Step 5: Screen Reader Testing

1. **NVDA (Windows) or VoiceOver (Mac):** Read through entire page
2. Navigate by headings — can you understand page structure?
3. Navigate by landmarks — can you find main, nav, search?
4. Fill out forms — are labels announced? Are errors clear?
5. Check dynamic content — are updates announced (aria-live)?
6. Check images — meaningful images described, decorative images silent?
7. Check modals — dialog announced, focus trapped, close announced?
8. Check links — purpose clear from link text alone?

## Step 6: Visual Accessibility Review

1. Check color contrast: 4.5:1 normal text, 3:1 large text, 3:1 UI components
2. Simulate color blindness: protanopia, deuteranopia, tritanopia
3. Check information not relying on color alone (errors, status, links)
4. Check focus indicators: visible, 3:1 contrast, minimum 2px
5. Test text resizing: 200% zoom without loss of content/functionality
6. Test high contrast mode: `forced-colors: active` — all content visible
7. Check dark mode: contrast maintained in both light and dark themes

## Step 7: Motor Accessibility Review

1. Check touch target sizes: ≥ 24x24px (WCAG 2.2), ideally 44x44px (Apple HIG)
2. Check spacing between interactive elements: ≥ 8px
3. Check drag-and-drop alternatives: keyboard or click-based alternative
4. Check gesture alternatives: swipe, pinch, long press alternatives
5. Test voice control compatibility: accessible names for all interactive elements
6. Test switch device access: all functionality via single switch (Tab + Enter)
7. Check confirmation for destructive actions

## Step 8: Cognitive Accessibility Review

1. Check reading level: aim for 8th grade, use plain language
2. Check consistent navigation: same items, same order across pages
3. Check predictable behavior: no unexpected context changes
4. Check error prevention: confirmation for destructive actions, undo available
5. Check time-out handling: warning before timeout, extendable, data saved
6. Check distraction reduction: no auto-play, no flashing, respect reduced-motion
7. Check content structure: clear hierarchy, chunked information, summaries
8. Check ADHD-friendly patterns: one primary action, progress indicators, focus mode

## Step 9: Set Up CI a11y Gates

1. Add vitest-axe / jest-axe to unit test suite — test each component
2. Add Playwright axe tests for E2E — test full pages
3. Add Pa11y CI for deployed URLs
4. Add Lighthouse a11y audit in CI — fail if score < 90
5. Block PRs on a11y violations
6. Track violations over time — should decrease
7. Generate a11y report for each PR

## Step 10: Legal Compliance & Documentation

1. Determine applicable regulations: ADA, EAA (June 2025), Section 508, AODA
2. Generate accessibility statement: commitment, standard, contact, limitations, last review date
3. Create VPAT if selling to government/enterprise
4. Document audit findings: WCAG criteria, severity, remediation plan
5. Track remediation: issue → fix → verification → close
6. Schedule regular audits: at least annually, or after major changes
7. Keep audit records for compliance evidence
