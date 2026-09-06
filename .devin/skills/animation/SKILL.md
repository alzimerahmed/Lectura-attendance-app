---
name: animation
description: Comprehensive animation & motion design workflow — principles, tools, micro-interactions, page transitions, scroll animations, reduced-motion, and performance
---

# Animation & Motion Design Workflow

This workflow applies the **Animation & Motion Design Skill** (`~/.codeium/windsurf/skills/animation-motion-design.md`) to implement purposeful, performant, accessible motion.

## When to Run
- When implementing animations in a web project
- When the user says `/animation` or asks about motion design
- When building micro-interactions, page transitions, or scroll animations
- When setting up Framer Motion or CSS animation system
- When ensuring reduced-motion accessibility

---

## Step 1: Read Context

1. Read the project's `research.md` if available — brand personality, motion direction
2. Identify the framework (React, Vue, Svelte) and existing animation setup
3. Check performance budget — animation should not impact Core Web Vitals
4. Review existing animations — what's already in place, what needs improvement
5. Identify key user flows that benefit from motion

## Step 2: Define Motion Language

1. Define duration standards: micro (100-200ms), small (200-300ms), medium (300-500ms), large (500-700ms)
2. Define easing functions: ease-out (entering), ease-in (leaving), spring (interactive)
3. Define spring configs: stiffness, damping for different interaction types
4. Document motion principles: when to animate, when not to animate
5. Align with brand personality: playful (springs, overshoot) vs professional (ease-out, subtle)

## Step 3: Choose Animation Tools

1. **CSS transitions:** Simple state changes (hover, toggle, expand) — zero bundle cost
2. **CSS @keyframes:** Complex multi-step animations — zero bundle cost
3. **Framer Motion:** React apps with complex animation needs — ~30KB
4. **Web Animations API:** Imperative control, non-React apps — zero bundle cost
5. **Lottie:** Designer-created animations (After Effects export) — lazy loaded
6. **View Transitions API:** Cross-page transitions — native browser API

## Step 4: Implement Micro-Interactions

1. **Button press:** `whileTap={{ scale: 0.98 }}` — 150ms ease-out
2. **Hover:** `whileHover={{ scale: 1.02 }}` — 200ms ease-out
3. **Toggle switch:** Spring animation for thumb position
4. **Loading states:** Skeleton shimmer, spinner, progress bar
5. **Toasts:** Slide in from edge, spring physics
6. **Tooltips:** Scale + fade, 150ms ease-out
7. **Form focus:** Border/outline transition, 200ms
8. **Copy to clipboard:** Checkmark animation, feedback

## Step 5: Implement Page Transitions

1. **Route changes:** Fade + slide, 300ms ease-out
2. **Modal open:** Scale + fade, 300ms ease-out (or spring)
3. **Modal close:** Scale + fade, 200ms ease-in
4. **Accordion:** Height animation, 300ms ease-out
5. **Tab switching:** Cross-fade or slide, 200ms
6. **Drawer/side sheet:** Slide from edge, 300ms ease-out
7. **Use AnimatePresence:** For exit animations in React

## Step 6: Implement Scroll Animations (Sparingly)

1. **Reveal on scroll:** Fade + slide up when entering viewport — `useInView` with `once: true`
2. **Scroll progress bar:** Top of page, scaleX bound to scroll progress
3. **Parallax:** Subtle Y transform on scroll — only for hero sections
4. **Sticky headers:** Animate on scroll position change
5. **Don't overuse:** Not every element needs scroll animation — it becomes noise
6. **Performance:** Limit number of scroll listeners, use `useScroll` efficiently

## Step 7: Implement Layout Animations

1. **List reordering:** `layout` prop on Framer Motion — smooth FLIP animations
2. **Item add/remove:** `AnimatePresence` with height/opacity animation
3. **Shared element transitions:** `layoutId` for card → detail view
4. **Grid changes:** `layout` on grid items for smooth reflow
5. **Filter/sort:** Animate items to new positions

## Step 8: Ensure Reduced-Motion Support

1. Add global `@media (prefers-reduced-motion: reduce)` CSS — disable all animations
2. Wrap app in `<MotionConfig reducedMotion="user">` for Framer Motion
3. Use `useReducedMotion()` hook for per-component alternatives
4. Replace slide/movement with opacity-only for reduced motion
5. Disable parallax, auto-play, and infinite animations
6. Test with Chrome DevTools → Rendering → Emulate prefers-reduced-motion: reduce
7. Verify all content is accessible without animation

## Step 9: Optimize Performance

1. **Animate only compositor properties:** `transform` and `opacity` — never width, height, margin
2. **Use `will-change` sparingly:** Apply before animation, remove after
3. **Avoid layout thrashing:** Batch DOM reads and writes
4. **Use `contain` property:** Isolate rendering for animated sections
5. **Lazy load Lottie:** Don't bundle all animations upfront
6. **Limit simultaneous animations:** Too many concurrent animations cause jank
7. **Test on mobile:** Verify 60fps on low-end devices
8. **Monitor INP:** Animations should not delay interaction response

## Step 10: Test & Verify

1. **Performance:** Chrome DevTools → Performance → record animation, check for jank
2. **Reduced motion:** Verify all animations have reduced-motion alternatives
3. **Cross-browser:** Test in Chrome, Firefox, Safari (WAAPI support varies)
4. **Mobile:** Test on real devices — not just DevTools emulation
5. **Accessibility:** Screen reader users should not be affected by animations
6. **No layout shift:** Animations should not cause CLS (Cumulative Layout Shift)
7. **60fps:** All animations should run at 60fps on target devices

## Step 11: Document Motion System

1. Document duration standards (micro, small, medium, large)
2. Document easing functions and when to use each
3. Document spring configs for different interaction types
4. Document when to animate and when not to
5. Document reduced-motion strategy
6. Create reusable animation variants/patterns
7. Share motion guidelines with the team
