---
name: pwa
description: Comprehensive PWA & offline-first workflow — manifest, service worker, caching, IndexedDB, background sync, push notifications, offline UI, and testing
---

# PWA & Offline-First Workflow

This workflow applies the **PWA & Offline-First Skill** (`~/.codeium/windsurf/skills/pwa-offline-first.md`) to build installable, offline-capable web applications.

## When to Run
- When building a Progressive Web App
- When the user says `/pwa` or asks about offline support
- When implementing service workers or caching strategies
- When setting up push notifications or background sync
- When making a web app installable

---

## Step 1: Assess PWA Needs

1. Read the project context — framework, offline requirements, push notification needs
2. Identify critical user flows that must work offline
3. Determine what data needs to be cached for offline use
4. Check if push notifications are needed for re-engagement
5. Check if background sync is needed for resilient data submission
6. Verify HTTPS is available (required for service workers)

## Step 2: Create Web App Manifest

1. Create `manifest.json` (or `public/manifest.json` in Next.js)
2. Set `name`, `short_name`, `description`, `start_url`, `scope`
3. Set `display: "standalone"` for app-like experience
4. Set `theme_color` and `background_color` to match brand
5. Generate icons: 192px, 512px (any purpose), 512px (maskable purpose)
6. Add screenshots for install prompt (wide and narrow form factors)
7. Add app shortcuts for quick actions
8. Add `share_target` if app should receive shared content
9. Link manifest in HTML: `<link rel="manifest" href="/manifest.json">`
10. Add iOS-specific meta tags: `apple-touch-icon`, `apple-mobile-web-app-capable`

## Step 3: Set Up Service Worker

1. Choose approach: Workbox (recommended) or custom service worker
2. **Workbox (Vite):** Use `vite-plugin-pwa` with autoUpdate
3. **Workbox (Next.js):** Use `next-pwa` plugin
4. **Custom:** Write `sw.js` with install, activate, and fetch handlers
5. Register service worker in app entry point
6. Set up update flow: detect new version, prompt user, reload
7. Configure `skipWaiting` and `clients.claim` as appropriate

## Step 4: Implement Caching Strategies

1. **App shell (HTML/CSS/JS):** Cache-first — pre-cache on install, serve from cache
2. **Static assets (images, fonts):** Cache-first with expiration — don't grow unbounded
3. **API data (fresh):** Network-first with cache fallback — try network, fall back to cache
4. **API data (non-critical):** Stale-while-revalidate — serve cache, update in background
5. **Images (content):** Stale-while-revalidate — serve cached, update when possible
6. Set cache expiration: max entries, max age
7. Set up cache versioning — clean old caches on activate
8. Configure navigation fallback to `index.html` for SPA routing

## Step 5: Implement App Shell Model

1. Identify app shell: HTML structure, CSS, JS bundle, critical fonts/images
2. Pre-cache app shell on service worker install
3. App loads instantly from cache, then fetches dynamic content
4. Set up navigation fallback — all navigations serve cached `index.html`
5. Exclude API routes from navigation fallback
6. Test: load app, go offline, refresh — app shell should load

## Step 6: Set Up IndexedDB for Offline Data

1. Install `idb` library for simpler IndexedDB API
2. Create database with object stores for each data type
3. Create indexes for common queries (by date, by category, etc.)
4. Implement CRUD operations: add, get, getAll, put, delete
5. Add `synced` flag to track which records need syncing
6. Implement offline data access — read from IndexedDB when network fails
7. Implement offline data creation — save with `synced: false`

## Step 7: Implement Background Sync

1. Check browser support: `'SyncManager' in window`
2. Store pending requests in IndexedDB when offline
3. Register sync event: `reg.sync.register('sync-tag')`
4. In service worker: listen for `sync` event, process pending requests
5. Retry failed requests when connectivity returns
6. Mark requests as synced on success
7. Fallback: if background sync unsupported, retry on `online` event
8. Set up periodic background sync for content updates (if needed)

## Step 8: Set Up Push Notifications

1. Generate VAPID keys (server-side)
2. Request notification permission from user (with clear explanation)
3. Subscribe to push: `pushManager.subscribe` with VAPID public key
4. Send subscription to server for storage
5. In service worker: listen for `push` event, show notification
6. Configure notification: title, body, icon, badge, actions, tag, vibrate
7. Handle notification click: open correct URL
8. Server-side: use `web-push` library to send notifications
9. Handle unsubscribe: clean up subscription on server

## Step 9: Implement Offline UI

1. **Connection status:** Use `navigator.onLine` and `online`/`offline` events
2. **Offline banner:** Show banner when offline — "You're offline. Some features may be unavailable."
3. **Stale data indicator:** Show when data is from cache and may be outdated
4. **Sync status:** Show count of pending changes waiting to sync
5. **Offline fallback page:** Create `offline.html` with cached content links
6. **Graceful degradation:** Disable features that require network, show helpful messages
7. **Retry UI:** Allow user to manually retry failed operations

## Step 10: Handle Service Worker Updates

1. Detect when new service worker is installed
2. Show update prompt: "A new version is available. Update now?"
3. On user confirmation: `postMessage('SKIP_WAITING')` to new service worker
4. Listen for `controllerchange` event — reload page
5. Or use `autoUpdate` mode with Workbox — updates automatically
6. Test update flow: deploy new version, verify update prompt appears

## Step 11: Test PWA

1. **Lighthouse:** Run PWA audit — fix all issues
2. **Offline testing:** DevTools → Application → Service Workers → Offline
   - Refresh page — app shell should load
   - Navigate to cached pages — should work
   - Test API calls — should fall back to cache
3. **Installation testing:**
   - Chrome: Install icon in address bar
   - Android: Add to Home screen
   - iOS: Safari → Share → Add to Home Screen
   - Desktop: Install app, verify standalone mode
4. **Push notification testing:**
   - Verify permission prompt
   - Send test push from server
   - Verify notification appears when app is closed
   - Verify notification click opens correct URL
5. **Background sync testing:**
   - Go offline, submit form
   - Go back online, verify data syncs
6. **Cross-browser:** Test in Chrome, Firefox, Safari, Edge

## Step 12: Document & Maintain

1. Document offline capabilities — what works offline, what doesn't
2. Document caching strategy — which resources use which strategy
3. Document sync behavior — how pending changes are handled
4. Document push notification setup — VAPID keys, server configuration
5. Document update flow — how users get new versions
6. Monitor service worker errors in production
7. Regularly test offline functionality after updates
