# Rule: PWA & Offline-First for All Projects

**ALWAYS** apply the PWA & Offline-First skill and workflow when building Progressive Web Apps or offline-capable web applications. The network is unreliable — design for the worst case and enhance when connectivity returns.

## Skill
`~/.codeium/windsurf/skills/pwa-offline-first.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/pwa.md` — invoke with `/pwa`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/pwa-engineer.md` (parent: Feature Engineer)

## How to follow this rule:
1. When building PWAs, invoke the `/pwa` workflow
2. Follow the workflow steps in order: Assess → Manifest → Service Worker → Caching → App Shell → IndexedDB → Background Sync → Push → Offline UI → Updates → Test → Document
3. Always create a valid web app manifest with maskable icons (192px, 512px)
4. Always use Workbox for service worker management — not raw service workers
5. Always implement the app shell model — pre-cache HTML/CSS/JS for instant offline loading
6. Always use IndexedDB (via `idb`) for offline data persistence — not localStorage
7. Always implement background sync for resilient data submission when offline
8. Always handle service worker updates gracefully — detect, prompt, reload

## When this rule applies:
- Building a Progressive Web App
- Implementing offline support or service workers
- Setting up push notifications or background sync
- Making a web app installable
- User asks about PWA or offline-first

## When this rule does NOT apply:
- Server-rendered sites with no offline needs
- User explicitly says to skip PWA setup
