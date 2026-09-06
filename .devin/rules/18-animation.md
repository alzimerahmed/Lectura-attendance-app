# Rule: Animation & Motion Design for All Projects

**ALWAYS** apply the Animation & Motion Design skill and workflow when implementing animations. Motion is communication, not decoration — every animation should serve a purpose.

## Skill
`~/.codeium/windsurf/skills/animation-motion-design.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/animation.md` — invoke with `/animation`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/animation-engineer.md` (parent: Design Engineer)

## How to follow this rule:
1. When implementing animations, invoke the `/animation` workflow
2. Follow the workflow steps in order: Read Context → Motion Language → Tools → Micro-Interactions → Page Transitions → Scroll → Layout → Reduced Motion → Performance → Test → Document
3. Always respect `prefers-reduced-motion` — provide reduced-motion alternatives for all animations
4. Always animate only compositor-friendly properties (transform, opacity) — never width, height, margin
5. Always define a motion language with consistent durations and easings
6. Always use Framer Motion for React apps, CSS for simple transitions, WAAPI for imperative control
7. Always test animations at 60fps on mobile devices
8. Never use animation that delays user tasks or blocks interaction

## When this rule applies:
- Implementing animations in a web project
- Building micro-interactions, page transitions, or scroll animations
- Setting up Framer Motion or CSS animation system
- Ensuring reduced-motion accessibility
- User asks about animation or motion design

## When this rule does NOT apply:
- Projects with no animation needs (static content sites)
- User explicitly says to skip animation
