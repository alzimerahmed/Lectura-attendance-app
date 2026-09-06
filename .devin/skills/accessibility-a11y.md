---
name: Accessibility (a11y) Skill
description: Comprehensive methodology for building accessible web applications — 2025-2026 practices with WCAG 2.2, automated testing in CI, manual testing, and inclusive design as a philosophy
version: 1.0.0
tags: [accessibility, a11y, wcag, aria, inclusive-design, screen-reader, keyboard, compliance]
---

# Accessibility (a11y) Skill

## Purpose
This skill provides a comprehensive methodology for building accessible web applications across any kind of web project. It reflects **modern 2025-2026 accessibility practices** — WCAG 2.2 as the baseline, automated testing in CI combined with manual testing, and inclusive design as a philosophy not a checklist.

## Core Philosophy

**Accessibility is a civil right, not a feature.** Inaccessible websites exclude 15-20% of the population — people with visual, motor, cognitive, and auditory disabilities. Accessibility benefits everyone: keyboard navigation helps power users, captions help non-native speakers, clear language helps everyone.

**The #1 rule:** Automated tools catch ~30% of WCAG issues. The rest requires manual testing with real assistive technology. Never rely on automated tools alone.

---

## Part 1: WCAG 2.2 Compliance

### 1.1 Perceivable
- **1.1 Text Alternatives:** Alt text for images, transcripts for audio, captions for video
- **1.2 Time-Based Media:** Captions, audio descriptions, sign language interpretation
- **1.3 Adaptable:** Content structure independent of presentation, meaningful sequence, instructions not sensory-only
- **1.4 Distinguishable:** Color contrast, resizable text, no background interference, text spacing

### 1.2 Operable
- **2.1 Keyboard Accessible:** All functionality via keyboard, no keyboard traps, logical tab order
- **2.2 Enough Time:** No time limits (or extendable), pause/stop/rehide moving content
- **2.3 Seizures:** No more than 3 flashes per second, no red flashing
- **2.4 Navigable:** Skip links, descriptive titles, logical focus order, link purpose clear
- **2.5 Input Modalities:** Touch targets ≥ 24x24px (WCAG 2.2), no device tilt, no path-based gestures

### 1.3 Understandable
- **3.1 Readable:** Language of page declared (`<html lang="en">`), language of parts declared
- **3.2 Predictable:** Consistent navigation, consistent identification, no unexpected context changes
- **3.3 Input Assistance:** Error identification, clear error descriptions, suggestions for fixing, error prevention

### 1.4 Robust
- **4.1 Compatible:** Valid HTML, name/role/value for all UI components, status messages programmatically determined

### 1.5 WCAG 2.2 New Requirements (vs 2.1)
- **2.4.11 Focus Not Obscured (Minimum):** Focused element not entirely hidden by other content
- **2.4.12 Focus Not Obscured (Enhanced):** Focused element not hidden at all
- **2.5.7 Dragging Movements:** Alternative to dragging (e.g., click/tap to move)
- **2.5.8 Target Size (Minimum):** Touch targets ≥ 24x24px (was 44x44 in Apple HIG, 48x48 in Material)
- **3.2.6 Consistent Help:** Help mechanism in same relative order across pages
- **3.3.7 Redundant Entry:** Don't require re-entering the same information in the same process
- **3.3.8 Accessible Authentication (Minimum):** No cognitive function test for login (no CAPTCHA)

---

## Part 2: Semantic HTML & ARIA

### 2.1 Using Native HTML Elements Correctly
```html
<!-- Good: native button -->
<button onclick="submit()">Submit</button>

<!-- Bad: div with click handler -->
<div onclick="submit()">Submit</div>
```
- **Use native elements:** `<button>`, `<a>`, `<input>`, `<select>`, `<nav>`, `<main>`, `<header>`, `<footer>`, `<aside>`, `<section>`, `<article>`
- **Native = accessible by default:** Keyboard, focus, screen reader support built-in
- **Don't reinvent:** If a native element does what you need, use it

### 2.2 ARIA Roles, States, and Properties
```html
<!-- Role -->
<div role="button" tabindex="0" aria-pressed="false" onclick="toggle()">Toggle</div>

<!-- Live regions -->
<div aria-live="polite" aria-atomic="true">Status updates appear here</div>

<!-- Expanded state -->
<button aria-expanded="false" aria-controls="menu">Menu</button>
<ul id="menu" hidden>...</ul>
```
- **First rule of ARIA:** Don't use ARIA if a native element exists
- **Roles:** `button`, `link`, `navigation`, `search`, `tablist`, `tab`, `tabpanel`, `dialog`, `alert`, `status`, `menu`, `menuitem`, `combobox`, `listbox`, `option`, `slider`, `spinbutton`
- **States:** `aria-expanded`, `aria-checked`, `aria-selected`, `aria-pressed`, `aria-hidden`, `aria-disabled`, `aria-busy`
- **Properties:** `aria-label`, `aria-labelledby`, `aria-describedby`, `aria-live`, `aria-atomic`, `aria-controls`, `aria-owns`, `aria-required`, `aria-invalid`, `aria-current`

### 2.3 Landmark Roles
```html
<header role="banner">...</header>
<nav role="navigation">...</nav>
<main role="main">...</main>
<aside role="complementary">...</aside>
<footer role="contentinfo">...</footer>
<form role="search">...</form>
```
- **HTML5 elements imply roles:** `<header>`, `<nav>`, `<main>`, `<aside>`, `<footer>` have implicit landmark roles
- **Don't duplicate:** Don't add `role="navigation"` to `<nav>` — it's implicit
- **One main:** Only one `<main>` element per page
- **Skip links:** "Skip to main content" link at top of page

### 2.4 Heading Hierarchy
```html
<h1>Page Title</h1>
  <h2>Section Title</h2>
    <h3>Subsection Title</h3>
  <h2>Another Section</h2>
```
- **One h1 per page:** Main page heading
- **Don't skip levels:** h1 → h3 is wrong — use h1 → h2 → h3
- **Use for structure, not style:** Don't use heading levels for visual size — use CSS
- **Screen reader navigation:** Headings are the primary navigation method for screen reader users

### 2.5 Accessible Forms
```html
<label for="email">Email address</label>
<input type="email" id="email" name="email" required aria-describedby="email-hint" />
<p id="email-hint">We'll never share your email.</p>
```
- **Label every input:** `<label>` with `for` attribute matching input `id`
- **Don't use placeholder as label:** Placeholder disappears on input, low contrast
- **Group related fields:** `<fieldset>` + `<legend>` for radio/checkbox groups
- **Error identification:** `aria-invalid="true"`, `aria-describedby` pointing to error message
- **Required fields:** `required` attribute + `aria-required="true"` (if not using native)
- **Autocomplete:** Use `autocomplete` attribute for common fields (name, email, tel, address)

### 2.6 Accessible Tables
```html
<table>
  <caption>Monthly Sales Report</caption>
  <thead>
    <tr>
      <th scope="col">Month</th>
      <th scope="col">Revenue</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th scope="row">January</th>
      <td>$10,000</td>
    </tr>
  </tbody>
</table>
```
- **Caption:** `<caption>` for table title
- **Headers:** `<th scope="col">` for column headers, `<th scope="row">` for row headers
- **Data tables only:** Don't use tables for layout — use CSS
- **Complex tables:** Use `headers` attribute for cells associated with multiple headers

### 2.7 Accessible Navigation
- **Skip link:** First focusable element — "Skip to main content"
- **Consistent navigation:** Same nav across pages, same order
- **Current page indicator:** `aria-current="page"` on current nav item
- **Keyboard accessible:** Tab through nav items, Enter/Space to activate
- **Dropdown menus:** Arrow keys to navigate, Escape to close, Enter to select
- **Mobile nav:** Hamburger menu must be keyboard accessible and have proper ARIA

---

## Part 3: Keyboard Navigation

### 3.1 Tab Order
- **Logical order:** Tab order follows visual order (usually DOM order)
- **Positive tabindex:** Avoid `tabindex="1"`+ — disrupts natural tab order
- **tabindex="0":** Makes non-focusable elements focusable in DOM order
- **tabindex="-1":** Makes element focusable programmatically but not via Tab
- **Test:** Tab through the entire page — can you reach all interactive elements?

### 3.2 Focus Management
- **Visible focus:** `:focus-visible` styles with sufficient contrast (3:1 minimum)
- **Focus trap:** In modals — Tab cycles within modal, Escape closes
- **Focus return:** When modal closes, focus returns to triggering element
- **SPA route changes:** Move focus to new page content (h1 or main)
- **Dynamic content:** Use `aria-live` to announce changes
- **Skip to content:** Focus main content after skip link activation

### 3.3 Keyboard Shortcuts
- **Don't conflict:** Avoid overriding browser shortcuts (Ctrl+S, Ctrl+F, etc.)
- **Single key shortcuts:** Avoid — can be triggered accidentally by screen reader users
- **Documentation:** Document all keyboard shortcuts in an help page
- **Toggle:** Allow users to disable custom shortcuts

### 3.4 Focus-Visible Styling
```css
/* Good: visible focus with good contrast */
*:focus-visible {
  outline: 2px solid #007bff;
  outline-offset: 2px;
}

/* Bad: removing focus outline */
*:focus {
  outline: none; /* NEVER do this without replacement */
}
```
- **Never remove focus outline** without providing an alternative
- **Use `:focus-visible`** not `:focus` — shows for keyboard, not mouse
- **Sufficient contrast:** Focus indicator 3:1 against adjacent colors
- **Minimum size:** At least 2px outline, visible at all zoom levels

### 3.5 Skip Links
```html
<a href="#main" class="skip-link">Skip to main content</a>
<!-- ... -->
<main id="main">
```
```css
.skip-link {
  position: absolute;
  top: -40px;
  left: 0;
  /* ... */
}
.skip-link:focus {
  top: 0;
}
```
- First focusable element on the page
- Visually hidden until focused
- Jumps focus to main content
- Essential for keyboard users on long nav pages

---

## Part 4: Screen Reader Testing

### 4.1 NVDA (Windows, Free)
- **Download:** nvaccess.org
- **Browser:** Firefox or Chrome
- **Navigation:** Arrow keys to read, H for headings, K for links, F for forms, T for tables
- **Test:** Read through entire page with NVDA — is content logical and complete?

### 4.2 JAWS (Windows, Commercial)
- **Industry standard** in enterprise environments
- **Browser:** Chrome or Edge
- **Similar navigation** to NVDA but more features
- **Test:** Same as NVDA — read through, navigate by elements

### 4.3 VoiceOver (macOS/iOS, Built-in)
- **Mac:** Cmd+F5 to toggle, Trackpad/Web navigation
- **iPhone:** Settings → Accessibility → VoiceOver
- **Navigation:** VO+Arrow keys, VO+Space to activate, R for landmarks, H for headings
- **Test:** Read through page, navigate by headings/landmarks/links

### 4.4 TalkBack (Android, Built-in)
- **Settings:** Settings → Accessibility → TalkBack
- **Navigation:** Swipe right/left to move, double-tap to activate
- **Test:** Navigate through page, interact with forms and buttons

### 4.5 Testing Methodology
1. **Turn off monitor** (or close eyes) — navigate only by screen reader
2. **Navigate by headings:** Can you understand page structure from headings alone?
3. **Navigate by landmarks:** Can you find main content, nav, search?
4. **Fill out forms:** Can you complete forms with only screen reader?
5. **Interact with dynamic content:** Are updates announced? Are errors clear?
6. **Check images:** Are all meaningful images described? Are decorative images silent?
7. **Test modals/dialogs:** Is the dialog announced? Is focus trapped? Can you close it?

---

## Part 5: Motor Accessibility

### 5.1 Touch Target Sizing
- **WCAG 2.2 (Minimum):** 24x24 CSS pixels
- **Apple HIG:** 44x44 points
- **Material Design:** 48x48 dp
- **Spacing:** At least 8px between adjacent targets
- **Test:** Can you reliably tap each target with a finger on mobile?

### 5.2 Spacing Between Interactive Elements
- **Minimum 8px** between adjacent interactive elements
- **Prevent accidental taps:** Especially for destructive actions (delete, cancel)
- **Consider hand tremor:** Larger targets help users with motor impairments

### 5.3 Drag-and-Drop Alternatives
- **WCAG 2.2 (2.5.7):** Must provide alternative to dragging movements
- **Alternative:** Click to select, click to place — or keyboard arrow keys
- **Example:** Kanban board — drag cards, but also provide keyboard move (arrow keys + Enter)

### 5.4 Gesture Alternatives
- **Swipe:** Provide button alternative
- **Pinch to zoom:** Provide +/- buttons
- **Long press:** Provide right-click or menu button alternative
- **Multi-touch:** Provide single-touch alternative
- **Device tilt:** Provide button alternative (WCAG 2.5.4)

### 5.5 Voice Control Compatibility
- **Dragon NaturallySpeaking:** Users navigate by voice — say "click Submit"
- **Number labels:** Voice control may show numbers for each clickable element
- **Accessible names:** `aria-label` or visible text becomes the voice command target
- **Test:** Can all interactive elements be activated by voice?

### 5.6 Switch Device Testing
- **Switch devices:** Single-button input for users with severe motor impairments
- **Scanning:** Tab through elements one at a time with a switch
- **Test:** Can all functionality be accessed with a single switch (Tab + Enter)?

### 5.7 Preventing Accidental Touches
- **Confirmation for destructive actions:** "Are you sure?" dialog
- **Undo:** Provide undo for actions that are hard to reverse
- **Delay on critical buttons:** Don't trigger immediately — small delay for cancel
- **Spacing:** Adequate space between "Confirm" and "Cancel" buttons

---

## Part 6: Cognitive Accessibility

### 6.1 Plain Language Guidelines
- **Short sentences:** Aim for 15-20 words per sentence
- **Common words:** Avoid jargon, define technical terms
- **Active voice:** "Click Submit" not "The Submit button should be clicked"
- **Second person:** "You will receive..." not "The user will receive..."
- **Reading level:** Aim for 8th grade level (Flesch-Kincaid 60+)
- **Tools:** Hemingway Editor, Grammarly, readable.io

### 6.2 Consistent Navigation
- **Same nav across pages:** Same items, same order, same labels
- **Same patterns:** Same interaction patterns (e.g., always click to expand)
- **Same icons:** Same icon for same action everywhere
- **Breadcrumbs:** Show location in site hierarchy
- **WCAG 3.2.3:** Consistent navigation across pages

### 6.3 Predictable Behavior
- **No surprises:** Links go to pages, buttons perform actions — don't mix
- **No automatic context changes:** Don't open new tabs without warning
- **No automatic form submission:** User must click Submit
- **No automatic redirects:** Let user choose to navigate
- **WCAG 3.2.1/3.2.2:** No unexpected context changes on focus/input

### 6.4 Error Prevention
- **Confirm before action:** "Delete this item? This cannot be undone."
- **Reversible actions:** Undo for delete, move, send
- **Legal/financial actions:** Confirmation page or modal before submission
- **Form validation:** Validate before submission, show errors clearly
- **WCAG 3.3.4:** Error prevention for legal/financial/data transactions

### 6.5 Time-Out Handling
- **Warn before timeout:** "Your session will expire in 5 minutes"
- **Extendable:** Allow user to extend the session
- **No sudden timeouts:** Don't log out without warning
- **Save data:** Don't lose form data on timeout — save draft

### 6.6 Distraction Reduction
- **No auto-playing media:** Don't auto-play audio or video
- **No flashing content:** Can trigger seizures (WCAG 2.3.1)
- **No parallax:** Can cause motion sickness
- **Reduce motion:** Respect `prefers-reduced-motion`
- **Minimal animations:** Only animate to communicate, not decorate

### 6.7 Reading Level Considerations
- **Target:** 8th grade reading level for general audience
- **Lower for critical info:** 5th-6th grade for emergency/safety information
- **Tools:** Flesch-Kincaid, Hemingway Editor
- **Structure:** Short paragraphs, bullet points, headings
- **Define terms:** Glossary for technical/jargon terms

### 6.8 Content Structure for Cognitive Disabilities
- **Clear hierarchy:** Headings, subheadings, bullet points
- **Chunk information:** Break long content into sections
- **Visual aids:** Icons, images, diagrams to support text
- **Summaries:** TL;DR at the top of long content
- **Progressive disclosure:** Show summary, expand for details

### 6.9 ADHD-Friendly Design Patterns
- **Clear call-to-action:** One primary action per page
- **Minimize choices:** Don't overwhelm with options
- **Progress indicators:** Show steps in multi-step processes
- **Focus mode:** Reduce distractions when user is focused on a task
- **Break tasks into steps:** Wizard/multi-step forms instead of one long form

---

## Part 7: Visual Accessibility

### 7.1 Color Contrast Requirements

| Element | Ratio | WCAG |
|---|---|---|
| Normal text (< 18pt / 14pt bold) | 4.5:1 | AA |
| Large text (≥ 18pt / 14pt bold) | 3:1 | AA |
| UI components & graphical objects | 3:1 | AA (1.4.11) |
| Focus indicators | 3:1 | AA (2.4.11) |
| Normal text | 7:1 | AAA |
| Large text | 4.5:1 | AAA |

### 7.2 Color Blindness Simulation
- **Protanopia:** Red-blind (1% males) — red appears dark
- **Deuteranopia:** Green-blind (1% males) — most common, red-green confusion
- **Tritanopia:** Blue-blind (rare) — blue-yellow confusion
- **Achromatopsia:** Complete color blindness (very rare)
- **Tools:** Sim Daltonism (macOS), Chrome DevTools (emulate vision deficiencies), Stark (Figma)
- **Test:** Can all information be understood without color?

### 7.3 Not Relying on Color Alone
```html
<!-- Bad: only color indicates error -->
<input style="border-color: red" />

<!-- Good: color + icon + text -->
<input style="border-color: red" aria-invalid="true" />
<span class="error">⚠️ Email is required</span>
```
- **Error states:** Red border + error icon + error text
- **Status indicators:** Color + icon (✓ for success, ⚠ for warning, ✗ for error)
- **Charts:** Patterns/textures in addition to colors, direct labels
- **Links:** Underline or other visual indicator, not just different color

### 7.4 Focus Indicators
- **Visible:** Must be visible when element is focused
- **Sufficient contrast:** 3:1 against adjacent colors
- **Minimum size:** At least 2px outline
- **Don't remove:** Never `outline: none` without replacement
- **Use `:focus-visible`:** Shows for keyboard users, not mouse users
- **Custom styles:** Make them distinctive — not just browser default

### 7.5 Text Resizing
- **WCAG 1.4.4:** Text resizable up to 200% without loss of content or functionality
- **Use relative units:** `rem`, `em`, `%`, `vw` — not `px` for font-size
- **Responsive layout:** Layout adapts to larger text without horizontal scroll
- **Test:** Zoom to 200% in browser — is everything still readable and functional?

### 7.6 High Contrast Mode Support
```css
@media (forced-colors: active) {
  .button {
    border: 1px solid ButtonText;
    background: ButtonFace;
    color: ButtonText;
  }
}
```
- **Windows High Contrast Mode:** `forced-colors: active` media query
- **Use system colors:** `ButtonText`, `ButtonFace`, `Canvas`, `CanvasText`
- **Don't break:** Ensure all content is visible in high contrast mode
- **Test:** Enable Windows High Contrast Mode and test the site

---

## Part 8: Automated a11y Testing

### 8.1 axe-core Integration
```javascript
// Browser extension
// Install "axe DevTools" browser extension

// CLI
npx axe-core https://example.com

// In tests
import { axe } from 'vitest-axe';

test('homepage has no violations', async () => {
  const { container } = render(<HomePage />);
  const results = await axe(container);
  expect(results.violations).toHaveLength(0);
});
```

### 8.2 Lighthouse Accessibility Audit
- **Categories:** Accessibility audit in Lighthouse
- **Score:** 0-100, target 95+
- **Checks:** Color contrast, ARIA, alt text, labels, tab order, language attribute
- **In CI:** Run Lighthouse in CI, fail if score < 90

### 8.3 Pa11y
```bash
# CLI
npx pa11y https://example.com

# CI
npx pa11y-ci --config .pa11yci.json
```
- **Headless:** Runs in headless browser
- **Config:** Define URLs, standard (WCAG 2.2 AA), ignore rules
- **CI:** Run on every PR, fail on violations

### 8.4 jest-axe / vitest-axe
```typescript
import { axe } from 'vitest-axe';
import { render } from '@testing-library/react';

test('Button has no a11y violations', async () => {
  const { container } = render(<Button>Click me</Button>);
  const results = await axe(container);
  expect(results.violations).toHaveLength(0);
});
```
- **Unit test level:** Test each component for a11y
- **Fast:** Runs in test suite, no browser needed
- **Catches:** ~30% of WCAG issues automatically

### 8.5 Playwright a11y Testing
```typescript
import { AxeBuilder } from '@axe-core/playwright';

test('homepage a11y', async ({ page }) => {
  await page.goto('/');
  const results = await new AxeBuilder({ page }).analyze();
  expect(results.violations).toEqual([]);
});
```
- **E2E level:** Test full pages for a11y
- **Real browser:** Tests in actual browser environment
- **In CI:** Run on every PR against staging

### 8.6 CI/CD a11y Gates
```yaml
# GitHub Actions
jobs:
  a11y:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: pnpm install
      - run: pnpm test:a11y  # vitest-axe
      - run: npx playwright test --grep a11y  # Playwright axe
      - run: npx pa11y-ci  # Pa11y on deployed URLs
```
- **Block PRs:** Fail CI on a11y violations
- **Report:** Generate a11y report for review
- **Trend:** Track violations over time — should decrease

### 8.7 What Automated Tools Can and Cannot Detect
- **Can detect:** Missing alt text, missing labels, color contrast (computed), ARIA misuse, heading order, HTML validity, lang attribute, duplicate IDs
- **Cannot detect:** Logical reading order, meaningful alt text, clear error messages, appropriate heading content, keyboard trap detection, live region appropriateness, cognitive accessibility, business logic accessibility

---

## Part 9: Legal Compliance

### 9.1 ADA (Americans with Disabilities Act)
- **Title III:** Public accommodations — websites must be accessible
- **Case law:** Domino's v. Robles (2019) — websites must be accessible
- **Standard:** WCAG 2.1 AA is the de facto standard (courts reference it)
- **Risk:** Lawsuits, demand letters, settlement costs

### 9.2 Section 508 (US Federal)
- **Applies to:** Federal agencies and contractors
- **Standard:** WCAG 2.0 AA (updated to align with WCAG 2.1 AA)
- **Requirement:** All federal websites and digital services must be accessible
- **VPAT:** Voluntary Product Accessibility Template — document compliance

### 9.3 EAA (European Accessibility Act)
- **Effective:** June 28, 2025
- **Applies to:** Products and services in the EU — websites, apps, e-books, e-commerce
- **Standard:** EN 301 549 (aligns with WCAG 2.1 AA)
- **Scope:** Private sector companies selling to EU consumers

### 9.4 EN 301 549
- **European standard:** Harmonized with WCAG 2.1 AA
- **Covers:** ICT products and services — websites, software, hardware
- **Used by:** EAA, public sector procurement
- **Updates:** Regularly updated to align with latest WCAG

### 9.5 AODA (Accessibility for Ontarians with Disabilities Act)
- **Effective:** January 1, 2021 (large organizations)
- **Standard:** WCAG 2.0 AA (Level A and AA)
- **Applies to:** Ontario organizations with 50+ employees
- **Reporting:** File accessibility compliance report

### 9.6 Accessibility Statement Generation
- **Required by:** EAA, some other regulations
- **Contents:**
  - Commitment to accessibility
  - Standard followed (WCAG 2.2 AA)
  - Known limitations and alternatives
  - How to report accessibility issues
  - Contact information
  - Date of last review
- **Tools:** W3C accessibility statement generator

### 9.7 VPAT (Voluntary Product Accessibility Template)
- **Purpose:** Document how product conforms to accessibility standards
- **Sections:** Success criteria, conformance level, remarks/explanations
- **Standards:** Section 508, WCAG 2.1, EN 301 549
- **Used for:** Government procurement, enterprise sales
- **Honesty:** Document both conformance and non-conformance

### 9.8 Audit Documentation
- **Audit report:** Date, scope, methodology, findings, remediation plan
- **Remediation tracking:** Issue → fix → verification → close
- **Regular audits:** At least annually, or after major changes
- **Keep records:** For legal defense and compliance evidence

---

## Part 10: Inclusive Design Patterns

### 10.1 Designing for Situational Impairments
- **One hand:** Holding a phone, eating — design for one-handed use
- **Bright sunlight:** Screen glare — high contrast, no low-contrast gray text
- **Noisy environment:** Can't hear audio — captions, visual alerts
- **Poor connection:** Slow loading — progressive enhancement, offline support
- **Stress/cognitive load:** Emergency situations — simple, clear, urgent

### 10.2 Designing for Permanent Disabilities
- **Blindness:** Screen reader, braille display — semantic HTML, ARIA, keyboard access
- **Low vision:** Screen magnifier, high contrast — resizable text, high contrast, zoom
- **Motor impairments:** Switch device, voice control — keyboard access, large targets
- **Deafness:** Sign language, captions — captions, transcripts, visual alerts
- **Cognitive disabilities:** Plain language, clear structure, consistent navigation

### 10.3 Designing for Temporary Impairments
- **Broken arm:** One-handed — keyboard access, voice control
- **Eye infection:** Temporary low vision — high contrast, resizable text
- **Ear infection:** Temporary hearing loss — captions, visual alerts
- **Fatigue/stress:** Reduced cognitive capacity — simple, clear, forgiving

### 10.4 Accessible Forms
```html
<form>
  <fieldset>
    <legend>Shipping Address</legend>
    
    <div class="field">
      <label for="name">Full Name <span aria-hidden="true">*</span></label>
      <input type="text" id="name" name="name" required autocomplete="name" />
    </div>
    
    <div class="field">
      <label for="email">Email <span aria-hidden="true">*</span></label>
      <input type="email" id="email" name="email" required autocomplete="email"
             aria-describedby="email-error" aria-invalid="false" />
      <p id="email-error" class="error" role="alert"></p>
    </div>
    
    <button type="submit">Place Order</button>
  </fieldset>
</form>
```
- **Labels:** Every input has a visible label
- **Required indicators:** Don't rely on color alone — use `*` and `aria-required`
- **Error messages:** `role="alert"`, specific, actionable, blameless
- **Instructions:** Before the form, not after the field
- **Autocomplete:** Use standard `autocomplete` values for autofill

### 10.5 Accessible Tables
- **Caption:** `<caption>` for table title
- **Scope:** `<th scope="col">` and `<th scope="row">`
- **Responsive:** Don't hide columns on mobile — use scroll or card layout
- **Sort indicators:** `aria-sort="ascending"` / `aria-sort="descending"`

### 10.6 Accessible Motion
```css
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
    scroll-behavior: auto !important;
  }
}
```
- **Respect preference:** `prefers-reduced-motion: reduce` — disable animations
- **Alternatives:** Instant transitions, fade instead of slide, no parallax
- **Essential motion:** If animation is essential, provide text alternative
- **No auto-playing:** Don't auto-play animations, videos, or carousels

---

## Execution Instructions for Cascade

When this skill is activated for accessibility:

1. **Read the project structure** — framework, components, pages
2. **Run automated tests** — axe-core, Lighthouse a11y, Pa11y
3. **Check WCAG 2.2 compliance** — go through each principle (Perceivable, Operable, Understandable, Robust)
4. **Check semantic HTML** — native elements, ARIA usage, heading hierarchy, landmarks
5. **Check keyboard navigation** — tab order, focus management, focus visibility, skip links
6. **Check forms** — labels, error handling, required fields, autocomplete
7. **Check color contrast** — 4.5:1 normal text, 3:1 large text and UI components
8. **Check images** — alt text, decorative images, meaningful descriptions
9. **Check motion** — prefers-reduced-motion, no auto-playing, no flashing
10. **Manual screen reader test** — NVDA/VoiceOver — read through entire page
11. **Manual keyboard test** — Tab through entire page, test all interactions
12. **Check legal compliance** — ADA, EAA, Section 508, AODA as applicable
13. **Generate accessibility statement** — commitment, standard, contact, limitations
14. **Document findings** — classify by WCAG criteria, create issues, track remediation
