---
name: Animation & Motion Design Skill
description: Comprehensive methodology for implementing animation and motion design in web applications — 2025-2026 practices with Framer Motion, CSS animations, Web Animations API, reduced-motion support, and performance-first motion
version: 1.0.0
tags: [animation, motion, framer-motion, css-animations, web-animations-api, reduced-motion, performance, transitions, micro-interactions]
---

# Animation & Motion Design Skill

## Purpose
This skill provides a comprehensive methodology for implementing animation and motion design across any kind of web project. It reflects **modern 2025-2026 practices** — Framer Motion as the React standard, CSS animations for simple transitions, Web Animations API for imperative control, `prefers-reduced-motion` as a first-class concern, and performance-first motion that doesn't block the main thread.

## Core Philosophy

**Motion is communication, not decoration.** Every animation should serve a purpose: guide attention, provide feedback, show relationships, create continuity, or enhance understanding. Decorative animation that doesn't serve a purpose is noise — it distracts users and hurts performance.

**The #1 rule:** Respect `prefers-reduced-motion`. Some users have vestibular disorders, motion sensitivity, or simply prefer less animation. Always provide a reduced-motion alternative — it's an accessibility requirement, not an optional enhancement.

---

## Part 1: Animation Principles

### 1.1 Disney's 12 Principles (Adapted for Web)
1. **Squash & Stretch:** Elements deform slightly on impact/interaction — buttons compress on click
2. **Anticipation:** Prepare the user for what's about to happen — button lifts before click
3. **Staging:** Direct attention to one element at a time — animate the important thing
4. **Straight Ahead vs Pose-to-Pose:** Fluid frame-by-frame vs keyframe interpolation
5. **Follow Through:** Elements don't stop instantly — overshoot and settle
6. **Slow In / Slow Out:** Easing — nothing in nature moves at constant speed
7. **Arcs:** Organic motion follows curves, not straight lines
8. **Secondary Action:** Supporting motion that enhances the primary action
9. **Timing:** Duration matters — 200ms feels snappy, 500ms feels deliberate
10. **Exaggeration:** Make motion slightly more pronounced than reality
11. **Solid Drawing:** Elements have weight and depth — shadows, perspective
12. **Appeal:** Motion should feel good — smooth, natural, satisfying

### 1.2 When to Animate
- **State changes:** Loading → loaded, empty → populated, error → fixed
- **User feedback:** Button press, form submission, toggle switch
- **Navigation:** Page transitions, route changes, modal open/close
- **Data updates:** List reordering, item addition/removal, number changes
- **Attention:** Highlighting new content, drawing focus to CTAs
- **Delight:** Micro-interactions that make the experience memorable

### 1.3 When NOT to Animate
- **Critical information:** Don't delay important content with entrance animations
- **Every scroll:** Don't animate everything on scroll — it becomes noise
- **Large areas:** Don't animate huge sections — causes jank
- **Auto-playing:** Don't auto-play animations that distract from content
- **If it slows the user:** Animation should never make tasks slower

### 1.4 Duration Guidelines

| Type | Duration | Example |
|---|---|---|
| **Micro-interaction** | 100-200ms | Button press, toggle, hover |
| **Small transition** | 200-300ms | Tooltip, dropdown, small panel |
| **Medium transition** | 300-500ms | Modal, accordion, card flip |
| **Large transition** | 500-700ms | Page transition, layout shift |
| **Deliberate motion** | 700-1000ms | Hero animation, onboarding |

- **Shorter = snappier:** 150ms feels instant, 300ms feels smooth
- **Longer = deliberate:** 500ms+ draws attention but can feel slow
- **Never > 1s:** Users lose patience — break into steps or skip animation

### 1.5 Easing Functions

```css
/* Linear — avoid, feels robotic */
transition-timing-function: linear;

/* Ease — good default, natural feel */
transition-timing-function: ease;

/* Custom cubic-bezier — precise control */
transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1); /* ease-in-out */
transition-timing-function: cubic-bezier(0, 0, 0.2, 1);   /* ease-out (decelerate) */
transition-timing-function: cubic-bezier(0.4, 0, 1, 1);   /* ease-in (accelerate) */

/* Spring — organic, overshoots slightly */
/* In Framer Motion: type: "spring", stiffness: 300, damping: 30 */
```

| Easing | Use Case | Feel |
|---|---|---|
| **ease-out** | Elements entering | Fast start, slow end — natural |
| **ease-in** | Elements leaving | Slow start, fast end — exits quickly |
| **ease-in-out** | State changes | Symmetric — balanced |
| **spring** | Interactive elements | Organic, playful, overshoot |
| **linear** | Progress bars | Constant speed — mechanical |

---

## Part 2: CSS Animations & Transitions

### 2.1 CSS Transitions (Simple State Changes)
```css
.button {
  background: var(--primary);
  transition: background 200ms ease-out, transform 150ms ease-out;
}

.button:hover {
  background: var(--primary-hover);
  transform: translateY(-1px);
}

.button:active {
  transform: translateY(0);
  transition-duration: 100ms;
}
```

### 2.2 CSS @keyframes (Complex Animations)
```css
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.element {
  animation: fadeInUp 400ms ease-out forwards;
}

/* With delay */
.element {
  animation: fadeInUp 400ms ease-out 100ms forwards;
}

/* With iterations */
.element {
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
```

### 2.3 CSS Animation Properties
```css
.element {
  animation-name: fadeInUp;
  animation-duration: 400ms;
  animation-timing-function: ease-out;
  animation-delay: 100ms;
  animation-iteration-count: 1; /* or infinite */
  animation-direction: normal; /* reverse, alternate, alternate-reverse */
  animation-fill-mode: forwards; /* none, forwards, backwards, both */
  animation-play-state: running; /* paused */
}

/* Shorthand */
.element {
  animation: fadeInUp 400ms ease-out 100ms 1 normal forwards;
}
```

### 2.4 Transition vs Animation
- **Transition:** Between two states (hover, class change) — browser interpolates
- **Animation:** Multi-step keyframes — more control, can loop, can pause
- **Use transition:** For simple A→B state changes (hover, toggle, expand)
- **Use animation:** For complex, multi-step, or looping motion

### 2.5 Staggered Animations with CSS
```css
.item {
  animation: fadeInUp 400ms ease-out backwards;
}

.item:nth-child(1) { animation-delay: 0ms; }
.item:nth-child(2) { animation-delay: 50ms; }
.item:nth-child(3) { animation-delay: 100ms; }
.item:nth-child(4) { animation-delay: 150ms; }
.item:nth-child(5) { animation-delay: 200ms; }

/* Or with CSS custom properties */
.item:nth-child(var(--n)) { animation-delay: calc(var(--n) * 50ms); }
```

### 2.6 View Transitions API
```css
/* Define view transition name */
.hero-image {
  view-transition-name: hero-image;
}

::view-transition-old(hero-image) {
  animation: fade-out 300ms ease-out;
}

::view-transition-new(hero-image) {
  animation: fade-in 300ms ease-out;
}
```
```typescript
// Trigger view transition
document.startViewTransition(() => {
  // Update DOM
  updateContent();
});
```

---

## Part 3: Framer Motion (React)

### 3.1 Basic Animation
```tsx
import { motion } from 'framer-motion';

// Simple animation
<motion.div
  initial={{ opacity: 0, y: 20 }}
  animate={{ opacity: 1, y: 0 }}
  transition={{ duration: 0.4, ease: "easeOut" }}
>
  Content
</motion.div>

// Hover animation
<motion.button
  whileHover={{ scale: 1.05 }}
  whileTap={{ scale: 0.95 }}
>
  Click me
</motion.button>
```

### 3.2 Variants (Reusable Animation Definitions)
```tsx
const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.05 },
  },
};

const itemVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0 },
};

function List({ items }) {
  return (
    <motion.ul variants={containerVariants} initial="hidden" animate="visible">
      {items.map(item => (
        <motion.li key={item.id} variants={itemVariants}>
          {item.name}
        </motion.li>
      ))}
    </motion.ul>
  );
}
```

### 3.3 AnimatePresence (Exit Animations)
```tsx
import { AnimatePresence } from 'framer-motion';

function Modal({ isOpen, onClose }) {
  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.2 }}
        >
          <motion.div
            initial={{ scale: 0.9, y: 20 }}
            animate={{ scale: 1, y: 0 }}
            exit={{ scale: 0.9, y: 20 }}
            transition={{ duration: 0.3, ease: "easeOut" }}
          >
            Modal content
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
```

### 3.4 Layout Animations
```tsx
import { motion, AnimatePresence } from 'framer-motion';

// Animate layout changes (reordering, addition, removal)
function TodoList({ items }) {
  return (
    <motion.ul layout>
      <AnimatePresence>
        {items.map(item => (
          <motion.li
            key={item.id}
            layout
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
          >
            {item.text}
          </motion.li>
        ))}
      </AnimatePresence>
    </motion.ul>
  );
}
```

### 3.5 Shared Layout Animations
```tsx
import { motion, LayoutGroup } from 'framer-motion';

// Shared element transition — card expands to full page
function Card({ item, onSelect }) {
  return (
    <LayoutGroup>
      <motion.div layoutId={`card-${item.id}`} onClick={() => onSelect(item)}>
        <motion.h3 layoutId={`title-${item.id}`}>{item.title}</motion.h3>
      </motion.div>
    </LayoutGroup>
  );
}

function Detail({ item }) {
  return (
    <motion.div layoutId={`card-${item.id}`}>
      <motion.h3 layoutId={`title-${item.id}`}>{item.title}</motion.h3>
      <p>{item.description}</p>
    </motion.div>
  );
}
```

### 3.6 Spring Physics
```tsx
<motion.div
  animate={{ x: 100 }}
  transition={{
    type: "spring",
    stiffness: 300,
    damping: 30,
    mass: 1,
  }}
/>

// Or use spring preset
<motion.div
  animate={{ scale: 1 }}
  transition={{ type: "spring", stiffness: 400, damping: 10 }}
/>
```

### 3.7 Drag Gestures
```tsx
<motion.div
  drag
  dragConstraints={{ left: 0, right: 300, top: 0, bottom: 300 }}
  dragElastic={0.2}
  onDragEnd={(e, info) => {
    if (info.offset.x > 100) handleSwipeRight();
  }}
>
  Swipe me
</motion.div>
```

### 3.8 Scroll-Triggered Animations
```tsx
import { useScroll, useTransform, motion } from 'framer-motion';

function ParallaxImage() {
  const { scrollYProgress } = useScroll({ target: ref, offset: ["start end", "end start"] });
  const y = useTransform(scrollYProgress, [0, 1], ["0%", "50%"]);

  return <motion.img src={src} style={{ y }} />;
}

// Scroll progress bar
function ScrollProgress() {
  const { scrollYProgress } = useScroll();
  return <motion.div style={{ scaleX: scrollYProgress }} className="progress-bar" />;
}
```

### 3.9 Page Transitions
```tsx
// Next.js App Router page transitions
import { motion } from 'framer-motion';

export default function Page({ children }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.3, ease: "easeOut" }}
    >
      {children}
    </motion.div>
  );
}
```

---

## Part 4: Web Animations API (WAAPI)

### 4.1 Basic WAAPI
```typescript
const element = document.querySelector('.box');

const animation = element.animate(
  [
    { opacity: 0, transform: 'translateY(20px)' },
    { opacity: 1, transform: 'translateY(0)' },
  ],
  {
    duration: 400,
    easing: 'ease-out',
    fill: 'forwards',
  }
);

// Control
animation.pause();
animation.play();
animation.cancel();
animation.reverse();

// Event
animation.onfinish = () => console.log('Animation complete');
```

### 4.2 WAAPI vs CSS vs Framer Motion

| Feature | CSS | WAAPI | Framer Motion |
|---|---|---|---|
| **Complexity** | Simple | Medium | Complex |
| **Control** | Limited | Full JS control | React-optimized |
| **Performance** | Compositor | Compositor | Compositor + JS |
| **React integration** | Classes | useRef | Native |
| **Bundle size** | 0 | 0 | ~30KB |
| **Use case** | Simple transitions | Imperative control | React apps |

---

## Part 5: Reduced Motion

### 5.1 prefers-reduced-motion Media Query
```css
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
    scroll-behavior: auto !important;
  }
}
```

### 5.2 Framer Motion Reduced Motion
```tsx
import { MotionConfig } from 'framer-motion';

// Global config
<MotionConfig reducedMotion="user">
  <App />
</MotionConfig>

// "user" — respects prefers-reduced-motion
// "always" — always reduce motion
// "never" — never reduce motion

// Per-component
import { useReducedMotion } from 'framer-motion';

function Component() {
  const shouldReduceMotion = useReducedMotion();

  const variants = shouldReduceMotion
    ? { hidden: { opacity: 0 }, visible: { opacity: 1 } }
    : { hidden: { opacity: 0, y: 20 }, visible: { opacity: 1, y: 0 } };

  return <motion.div variants={variants} initial="hidden" animate="visible" />;
}
```

### 5.3 Reduced Motion Alternatives
- **Fade instead of slide:** Replace `translateY` with `opacity` only
- **Instant instead of animated:** Set duration to 0.01ms
- **Shorter duration:** If you must animate, keep it under 150ms
- **No parallax:** Disable scroll-driven parallax effects
- **No auto-play:** Don't auto-play animations
- **No looping:** Disable infinite animations
- **Inform, don't distract:** Use opacity changes, not movement

### 5.4 Testing Reduced Motion
- **DevTools:** Chrome DevTools → Rendering → Emulate `prefers-reduced-motion: reduce`
- **OS setting:** macOS: System Preferences → Accessibility → Display → Reduce motion
- **OS setting:** Windows: Settings → Accessibility → Visual effects → Animation off
- **Test:** Verify all animations have reduced-motion alternatives

---

## Part 6: Performance

### 6.1 What to Animate (Compositor-Friendly Properties)
```css
/* GOOD — compositor thread, 60fps */
transform: translateX(100px);     /* GPU accelerated */
transform: scale(1.1);            /* GPU accelerated */
transform: rotate(45deg);         /* GPU accelerated */
opacity: 0.5;                     /* GPU accelerated */
filter: blur(4px);                /* GPU accelerated (but expensive) */

/* BAD — main thread, causes jank */
width: 200px;                     /* triggers layout */
height: 100px;                    /* triggers layout */
margin: 20px;                     /* triggers layout */
padding: 10px;                    /* triggers layout */
top: 50px;                        /* triggers layout */
left: 100px;                      /* triggers layout */
background-color: red;            /* triggers paint */
box-shadow: 0 0 20px black;       /* triggers paint (expensive) */
```

### 6.2 will-change Hint
```css
/* Hint browser to prepare for animation */
.animated-element {
  will-change: transform, opacity;
}

/* Remove after animation — don't leave it on */
.animated-element.done {
  will-change: auto;
}
```
- **Use sparingly:** `will-change` consumes memory — don't apply to many elements
- **Apply before animation:** Set it just before the animation starts
- **Remove after:** Clean up to free resources

### 6.3 Contain Property
```css
/* Isolate element's rendering — improves performance */
.card {
  contain: layout style paint;
}

/* Strict containment */
.isolated-section {
  contain: strict;
}
```

### 6.4 Avoid Layout Thrashing
```javascript
// BAD: forces synchronous layout (read after write)
element.style.transform = 'translateX(100px)';
const width = element.offsetWidth; // forces layout
element.style.transform = 'translateX(200px)';

// GOOD: batch reads and writes
const width = element.offsetWidth; // read first
element.style.transform = 'translateX(100px)'; // then write
```

### 6.5 Animation Frame Scheduling
```typescript
// Use requestAnimationFrame for JS-driven animations
function animate(element, start, end, duration) {
  const startTime = performance.now();

  function frame(currentTime) {
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);
    const eased = easeOutCubic(progress);
    const value = start + (end - start) * eased;

    element.style.transform = `translateX(${value}px)`;

    if (progress < 1) {
      requestAnimationFrame(frame);
    }
  }

  requestAnimationFrame(frame);
}
```

### 6.6 Bundle Size Considerations
- **CSS animations:** 0KB — use for simple transitions
- **Framer Motion:** ~30KB gzipped — use for React apps with complex animation
- **GSAP:** ~30KB gzipped — use for complex timelines and non-React apps
- **Web Animations API:** 0KB — built into browser, use for imperative control
- **Lottie:** Variable — use for designer-created animations (After Effects export)

---

## Part 7: Micro-Interactions

### 7.1 Button Press
```tsx
<motion.button
  whileHover={{ scale: 1.02 }}
  whileTap={{ scale: 0.98 }}
  transition={{ duration: 0.15, ease: "easeOut" }}
>
  Click me
</motion.button>
```

### 7.2 Toggle Switch
```tsx
<motion.div
  className="track"
  onClick={() => setOn(!on)}
  animate={{ backgroundColor: on ? "var(--primary)" : "var(--muted)" }}
>
  <motion.div
    className="thumb"
    animate={{ x: on ? 24 : 0 }}
    transition={{ type: "spring", stiffness: 500, damping: 30 }}
  />
</motion.div>
```

### 7.3 Loading Spinner
```tsx
<motion.div
  animate={{ rotate: 360 }}
  transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
  className="spinner"
/>
```

### 7.4 Skeleton Loading
```css
.skeleton {
  background: linear-gradient(
    90deg,
    var(--muted) 25%,
    var(--muted-hover) 50%,
    var(--muted) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
```

### 7.5 Toast Notification
```tsx
<AnimatePresence>
  {toasts.map(toast => (
    <motion.div
      key={toast.id}
      initial={{ opacity: 0, x: 100 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: 100 }}
      transition={{ type: "spring", stiffness: 300, damping: 30 }}
    >
      {toast.message}
    </motion.div>
  ))}
</AnimatePresence>
```

### 7.6 Number Animation
```tsx
function AnimatedNumber({ value }) {
  const spring = useSpring(value, { stiffness: 120, damping: 20 });
  const display = useTransform(spring, (latest) => Math.round(latest));

  return <motion.span>{display}</motion.span>;
}
```

### 7.7 Tooltip
```tsx
<motion.div
  initial={{ opacity: 0, scale: 0.9 }}
  animate={{ opacity: 1, scale: 1 }}
  exit={{ opacity: 0, scale: 0.9 }}
  transition={{ duration: 0.15, ease: "easeOut" }}
>
  Tooltip text
</motion.div>
```

---

## Part 8: Scroll-Driven Animations

### 8.1 Scroll-Linked Animations (CSS)
```css
/* New CSS scroll-driven animations (2025) */
@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

.element {
  animation: fade-in linear;
  animation-timeline: view();
  animation-range: entry 0% entry 100%;
}
```

### 8.2 Scroll Progress (Framer Motion)
```tsx
const { scrollYProgress } = useScroll();
const scaleX = useSpring(scrollYProgress, { stiffness: 100, damping: 30 });

return <motion.div style={{ scaleX }} className="fixed top-0 left-0 h-1 bg-primary" />;
```

### 8.3 Parallax
```tsx
function Parallax({ children }) {
  const ref = useRef(null);
  const { scrollYProgress } = useScroll({ target: ref, offset: ["start end", "end start"] });
  const y = useTransform(scrollYProgress, [0, 1], ["-20%", "20%"]);

  return (
    <div ref={ref}>
      <motion.div style={{ y }}>{children}</motion.div>
    </div>
  );
}
```

### 8.4 Reveal on Scroll
```tsx
import { useInView } from 'framer-motion';

function Reveal({ children }) {
  const ref = useRef(null);
  const inView = useInView(ref, { once: true, margin: "-100px" });

  return (
    <motion.div
      ref={ref}
      initial={{ opacity: 0, y: 20 }}
      animate={inView ? { opacity: 1, y: 0 } : {}}
      transition={{ duration: 0.4, ease: "easeOut" }}
    >
      {children}
    </motion.div>
  );
}
```

### 8.5 Don't Overuse Scroll Animations
- **Not everything needs to animate on scroll** — it becomes noise
- **Use for hero sections and key content** — not every paragraph
- **Respect reduced motion** — disable scroll animations entirely
- **Performance:** Too many scroll listeners cause jank
- **User control:** Let users scroll at their own pace without forced animation

---

## Part 9: Lottie Animations

### 9.1 When to Use Lottie
- **Designer-created animations:** After Effects → Lottie JSON
- **Complex illustrations:** Animated logos, characters, scenes
- **Onboarding:** Step-by-step animated guides
- **Empty states:** Playful illustrations with motion

### 9.2 Lottie Implementation
```tsx
import { Player } from '@lottiefiles/react-lottie-player';

<Player
  src="/animations/empty-state.json"
  loop
  autoplay
  speed={1}
  style={{ width: '200px', height: '200px' }}
/>
```

### 9.3 Lottie Performance
- **File size:** Lottie JSON can be large — optimize in After Effects
- **Limit frames:** Reduce frame count for smaller files
- **No expressions:** Expressions increase CPU usage
- **Lazy load:** Only load Lottie when needed — don't bundle all animations
- **Reduced motion:** Show first frame only, don't autoplay

---

## Execution Instructions for Cascade

When this skill is activated for animation & motion design:

1. **Read the project context** — framework, existing animations, performance budget
2. **Choose animation tools** — CSS (simple), Framer Motion (React), WAAPI (imperative), Lottie (designer)
3. **Define motion language** — durations, easings, spring configs that match brand personality
4. **Implement micro-interactions** — button press, toggle, hover, focus, loading states
5. **Implement page transitions** — route changes, modal open/close, expand/collapse
6. **Implement scroll animations** — reveal on scroll, parallax, scroll progress (sparingly)
7. **Implement layout animations** — list reordering, shared element transitions
8. **Ensure reduced-motion support** — `prefers-reduced-motion` media query, Framer Motion `useReducedMotion`
9. **Optimize performance** — animate only transform/opacity, use `will-change` sparingly, avoid layout thrashing
10. **Test** — 60fps on mobile, reduced motion, no jank, no layout shift
11. **Document** — motion guidelines, easing curves, duration standards, when to animate
