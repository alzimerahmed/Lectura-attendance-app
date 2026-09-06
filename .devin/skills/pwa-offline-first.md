---
name: PWA & Offline-First Skill
description: Comprehensive methodology for building Progressive Web Apps and offline-first web applications — 2025-2026 practices with service workers, web app manifests, background sync, push notifications, and IndexedDB
version: 1.0.0
tags: [pwa, offline-first, service-worker, web-app-manifest, push-notifications, background-sync, indexeddb, installable, app-like]
---

# PWA & Offline-First Skill

## Purpose
This skill provides a comprehensive methodology for building Progressive Web Apps (PWAs) and offline-first web applications. It reflects **modern 2025-2026 practices** — service workers with Workbox, web app manifests for installability, background sync for resilient updates, push notifications for re-engagement, and IndexedDB for offline data persistence.

## Core Philosophy

**Offline-first means the app works without a network connection, not just degrades gracefully.** Design for the worst case (no network) and enhance when connectivity returns. The app should never show a blank screen or a useless error — it should always show something useful, even if it's cached or stale data.

**The #1 rule:** The network is unreliable. Treat it as an enhancement, not a requirement. Every critical user flow should work offline or handle network failure gracefully. Cache aggressively, sync when connected, and never block the user on a network request that might fail.

---

## Part 1: PWA Fundamentals

### 1.1 What Makes a PWA
- **Installable:** Users can install it to their home screen / desktop
- **Offline-capable:** Works without network connection
- **App-like:** Full-screen, no browser chrome, splash screen
- **Responsive:** Works on all device sizes
- **Secure:** Served over HTTPS (required for service workers)
- **Fast:** Loads quickly, responds to input immediately

### 1.2 PWA Requirements (2025-2026)
1. **Web App Manifest:** Valid `manifest.json` with name, icons, start_url, display
2. **Service Worker:** Registered service worker that handles fetch events
3. **HTTPS:** Served over HTTPS (localhost exempt for development)
4. **Icons:** At least 192px and 512px icons (maskable recommended)
5. **Responsive:** Mobile-friendly viewport meta tag
6. **No browser errors:** No console errors on page load

### 1.3 PWA vs Native
| Feature | PWA | Native |
|---|---|---|
| **Installation** | Add to home screen | App store |
| **Offline** | Service worker caching | Full offline |
| **Push notifications** | Web Push API | Platform-specific |
| **Background tasks** | Service Worker (limited) | Full background |
| **Hardware access** | Web APIs (growing) | Full access |
| **Distribution** | Web (no store) | App store |
| **Updates** | Instant (next visit) | App store review |
| **Size** | Small (web) | Large (binary) |
| **Discovery** | Search engines | App store search |

---

## Part 2: Web App Manifest

### 2.1 Basic Manifest
```json
{
  "name": "My App",
  "short_name": "MyApp",
  "description": "A description of my app",
  "start_url": "/",
  "display": "standalone",
  "background_color": "#ffffff",
  "theme_color": "#000000",
  "orientation": "portrait-primary",
  "scope": "/",
  "lang": "en",
  "dir": "ltr",
  "categories": ["productivity", "utilities"],
  "icons": [
    {
      "src": "/icons/icon-192.png",
      "sizes": "192x192",
      "type": "image/png",
      "purpose": "any"
    },
    {
      "src": "/icons/icon-512.png",
      "sizes": "512x512",
      "type": "image/png",
      "purpose": "any"
    },
    {
      "src": "/icons/icon-maskable-512.png",
      "sizes": "512x512",
      "type": "image/png",
      "purpose": "maskable"
    }
  ],
  "screenshots": [
    {
      "src": "/screenshots/home.png",
      "sizes": "1280x720",
      "type": "image/png",
      "form_factor": "wide"
    },
    {
      "src": "/screenshots/home-mobile.png",
      "sizes": "390x844",
      "type": "image/png",
      "form_factor": "narrow"
    }
  ],
  "shortcuts": [
    {
      "name": "New Post",
      "short_name": "New",
      "url": "/posts/new",
      "icons": [{ "src": "/icons/new.png", "sizes": "96x96" }]
    }
  ],
  "share_target": {
    "action": "/share",
    "method": "POST",
    "enctype": "multipart/form-data",
    "params": {
      "title": "title",
      "text": "text",
      "url": "url",
      "files": [{ "name": "file", "accept": ["image/*"] }]
    }
  }
}
```

### 2.2 Display Modes
| Mode | Description |
|---|---|
| `fullscreen` | No browser UI at all |
| `standalone` | No browser chrome, status bar visible |
| `minimal-ui` | Minimal browser controls (back, forward, reload) |
| `browser` | Standard browser tab |

### 2.3 Link the Manifest
```html
<link rel="manifest" href="/manifest.json" />
<meta name="theme-color" content="#000000" />
<link rel="apple-touch-icon" href="/icons/icon-192.png" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="default" />
```

### 2.4 Maskable Icons
- **Problem:** Android adaptive icons crop icons differently per device
- **Solution:** Maskable icons have a "safe zone" — keep important content within inner 80%
- **Purpose:** `"purpose": "maskable"` in manifest
- **Design:** Full-bleed background with logo in center 80%

---

## Part 3: Service Workers

### 3.1 Service Worker Lifecycle
```
Install → Activate → Fetch/Message/Sync/Push
```
1. **Install:** Pre-cache critical assets (app shell)
2. **Activate:** Clean up old caches, take control
3. **Fetch:** Intercept network requests, serve from cache
4. **Sync:** Handle background sync when connectivity returns
5. **Push:** Handle push notifications

### 3.2 Basic Service Worker
```javascript
// sw.js
const CACHE_NAME = 'my-app-v1';
const PRECACHE_URLS = [
  '/',
  '/index.html',
  '/styles.css',
  '/app.js',
  '/offline.html',
];

// Install — pre-cache app shell
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(PRECACHE_URLS))
  );
  self.skipWaiting(); // Activate immediately
});

// Activate — clean old caches
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.filter((key) => key !== CACHE_NAME).map((key) => caches.delete(key))
      );
    })
  );
  self.clients.claim(); // Take control immediately
});

// Fetch — cache-first for app shell, network-first for data
self.addEventListener('fetch', (event) => {
  const { request } = event;

  // Skip non-GET requests
  if (request.method !== 'GET') return;

  // Skip cross-origin requests
  if (!request.url.startsWith(self.location.origin)) return;

  // App shell — cache-first
  if (PRECACHE_URLS.includes(new URL(request.url).pathname)) {
    event.respondWith(
      caches.match(request).then((cached) => cached || fetch(request))
    );
    return;
  }

  // API/data — network-first with cache fallback
  if (request.url.includes('/api/')) {
    event.respondWith(
      fetch(request)
        .then((response) => {
          const clone = response.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(request, clone));
          return response;
        })
        .catch(() => caches.match(request))
    );
    return;
  }

  // Other — stale-while-revalidate
  event.respondWith(
    caches.match(request).then((cached) => {
      const fetchPromise = fetch(request).then((response) => {
        const clone = response.clone();
        caches.open(CACHE_NAME).then((cache) => cache.put(request, clone));
        return response;
      });
      return cached || fetchPromise;
    })
  );
});
```

### 3.3 Register Service Worker
```typescript
// Register in app
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js')
      .then((registration) => {
        console.log('SW registered:', registration.scope);
      })
      .catch((error) => {
        console.log('SW registration failed:', error);
      });
  });
}
```

### 3.4 Update Flow
```javascript
// sw.js — new version
const CACHE_NAME = 'my-app-v2'; // Bump version

// Listen for updates
self.addEventListener('message', (event) => {
  if (event.data === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});

// In app — detect update and notify user
navigator.serviceWorker.addEventListener('controllerchange', () => {
  // New service worker took control — reload
  window.location.reload();
});

// Check for updates
navigator.serviceWorker.register('/sw.js').then((registration) => {
  registration.addEventListener('updatefound', () => {
    const newWorker = registration.installing;
    newWorker.addEventListener('statechange', () => {
      if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
        // New version available — show update prompt
        showUpdatePrompt(() => {
          newWorker.postMessage('SKIP_WAITING');
        });
      }
    });
  });
});
```

---

## Part 4: Workbox

### 4.1 Why Workbox
- **Higher-level API:** Easier than raw service worker
- **Strategies:** Pre-built caching strategies
- **Routing:** Pattern-based request routing
- **Background sync:** Built-in queue for failed requests
- **Google-maintained:** Well-tested, widely used

### 4.2 Workbox Setup (with Vite)
```typescript
// vite.config.ts
import { VitePWA } from 'vite-plugin-pwa';

export default {
  plugins: [
    VitePWA({
      registerType: 'autoUpdate',
      workbox: {
        globPatterns: ['**/*.{js,css,html,svg,png,woff2}'],
        navigateFallback: '/index.html',
        navigateFallbackDenylist: [/^\/api\//],
      },
      manifest: {
        name: 'My App',
        short_name: 'MyApp',
        theme_color: '#000000',
        icons: [
          { src: '/icons/icon-192.png', sizes: '192x192', type: 'image/png' },
          { src: '/icons/icon-512.png', sizes: '512x512', type: 'image/png' },
        ],
      },
    }),
  ],
};
```

### 4.3 Workbox Strategies
```javascript
import { registerRoute } from 'workbox-routing';
import { CacheFirst, NetworkFirst, StaleWhileRevalidate } from 'workbox-strategies';

// Cache-first: for static assets (images, fonts)
registerRoute(
  ({ request }) => request.destination === 'image',
  new CacheFirst({ cacheName: 'images' })
);

// Network-first: for fresh data with cache fallback
registerRoute(
  ({ url }) => url.pathname.startsWith('/api/'),
  new NetworkFirst({
    cacheName: 'api-cache',
    networkTimeoutSeconds: 3,
  })
);

// Stale-while-revalidate: for non-critical resources
registerRoute(
  ({ request }) => request.destination === 'script' || request.destination === 'style',
  new StaleWhileRevalidate({ cacheName: 'static-resources' })
);
```

### 4.4 Workbox with Next.js
```javascript
// next.config.js
const withPWA = require('next-pwa')({
  dest: 'public',
  register: true,
  skipWaiting: true,
  disable: process.env.NODE_ENV === 'development',
});

module.exports = withPWA({});
```

---

## Part 5: Caching Strategies

### 5.1 Strategy Comparison

| Strategy | When to Use | Behavior |
|---|---|---|
| **Cache-first** | Static assets, images, fonts | Serve from cache, never hit network |
| **Network-first** | Fresh data (API, news) | Try network, fall back to cache |
| **Stale-while-revalidate** | Non-critical resources | Serve cache, update in background |
| **Network-only** | Non-cacheable data | Always network, fail if offline |
| **Cache-only** | Offline-only content | Only cache, fail if not cached |

### 5.2 App Shell Model
```
App Shell (cached) = HTML + CSS + JS (the "frame")
Content (dynamic) = API data, images, user content

1. Load app shell from cache (instant)
2. Fetch content from network (or cache)
3. If offline: show cached content or offline message
```

### 5.3 Cache Storage API
```javascript
// Open cache
const cache = await caches.open('my-cache');

// Add to cache
await cache.add('/page.html');
await cache.addAll(['/page1.html', '/page2.html']);

// Match from cache
const response = await cache.match('/page.html');

// Delete from cache
await cache.delete('/page.html');

// Keys (list all cached URLs)
const keys = await cache.keys();
```

### 5.4 Cache Versioning
```javascript
const CACHE_VERSION = 'v2';
const CACHE_NAME = `my-app-${CACHE_VERSION}`;

// On activate — delete old versions
caches.keys().then((keys) => {
  return Promise.all(
    keys
      .filter((key) => key.startsWith('my-app-') && key !== CACHE_NAME)
      .map((key) => caches.delete(key))
  );
});
```

---

## Part 6: IndexedDB (Offline Data)

### 6.1 Why IndexedDB
- **Large storage:** Much larger than localStorage (50MB+ vs 5MB)
- **Structured data:** Stores objects, not just strings
- **Asynchronous:** Non-blocking, unlike localStorage
- **Indexed:** Fast queries with indexes
- **Transactional:** ACID guarantees

### 6.2 IndexedDB with idb (simpler API)
```typescript
import { openDB } from 'idb';

const db = await openDB('my-app', 1, {
  upgrade(db) {
    const store = db.createObjectStore('articles', { keyPath: 'id' });
    store.createIndex('by-date', 'publishedAt');
    store.createIndex('by-category', 'category');
  },
});

// Add
await db.add('articles', { id: 1, title: 'Hello', category: 'tech', publishedAt: new Date() });

// Get
const article = await db.get('articles', 1);

// Get all
const allArticles = await db.getAll('articles');

// Query by index
const techArticles = await db.getAllFromIndex('articles', 'by-category', 'tech');

// Update
await db.put('articles', { id: 1, title: 'Updated', category: 'tech', publishedAt: new Date() });

// Delete
await db.delete('articles', 1);
```

### 6.3 Offline Data Sync Pattern
```typescript
// Save data offline when created
async function createArticleOffline(article) {
  const db = await openDB('my-app', 1);
  await db.add('articles', { ...article, synced: false });
  // Register for background sync
  if ('sync' in serviceWorkerRegistration) {
    await serviceWorkerRegistration.sync.register('sync-articles');
  }
}

// In service worker — sync when online
self.addEventListener('sync', (event) => {
  if (event.tag === 'sync-articles') {
    event.waitUntil(syncArticles());
  }
});

async function syncArticles() {
  const db = await openDB('my-app', 1);
  const unsynced = await db.getAllFromIndex('articles', 'by-synced', false);
  for (const article of unsynced) {
    try {
      await fetch('/api/articles', {
        method: 'POST',
        body: JSON.stringify(article),
      });
      await db.put('articles', { ...article, synced: true });
    } catch (error) {
      // Will retry on next sync event
      throw error;
    }
  }
}
```

---

## Part 7: Background Sync

### 7.1 How Background Sync Works
1. **User action:** User submits form while offline
2. **Queue:** Service worker queues the request
3. **Sync event:** Browser fires `sync` event when connectivity returns
4. **Retry:** Service worker retries the queued request
5. **Success:** Data is synced, user is notified

### 7.2 Implementation
```javascript
// In app — register sync
async function submitForm(data) {
  if ('serviceWorker' in navigator && 'SyncManager' in window) {
    const reg = await navigator.serviceWorker.ready;
    // Store data in IndexedDB
    await storePendingRequest(data);
    // Register sync
    await reg.sync.register('sync-form');
  } else {
    // Fallback — try immediately
    await fetch('/api/submit', { method: 'POST', body: JSON.stringify(data) });
  }
}

// In service worker
self.addEventListener('sync', (event) => {
  if (event.tag === 'sync-form') {
    event.waitUntil(processPendingRequests());
  }
});

async function processPendingRequests() {
  const requests = await getPendingRequests();
  for (const req of requests) {
    try {
      await fetch('/api/submit', {
        method: 'POST',
        body: JSON.stringify(req.data),
      });
      await markRequestSynced(req.id);
    } catch (error) {
      // Will retry
      throw error;
    }
  }
}
```

### 7.3 Periodic Background Sync
```javascript
// Register periodic sync (requires permission)
const reg = await navigator.serviceWorker.ready;
if ('periodicSync' in reg) {
  const status = await navigator.permissions.query({ name: 'periodic-background-sync' });
  if (status.state === 'granted') {
    await reg.periodicSync.register('update-content', {
      minInterval: 24 * 60 * 60 * 1000, // 24 hours
    });
  }
}

// In service worker
self.addEventListener('periodicsync', (event) => {
  if (event.tag === 'update-content') {
    event.waitUntil(updateContent());
  }
});
```

---

## Part 8: Push Notifications

### 8.1 Push Notification Flow
```
Server → Push Service (FCM/APNs) → Service Worker → Notification
```
1. **Subscribe:** User grants permission, browser generates subscription
2. **Send subscription to server:** Store for later use
3. **Server sends push:** Server sends message to push service
4. **Push service delivers:** Browser receives push, wakes service worker
5. **Service worker shows notification:** Display notification to user

### 8.2 Subscribe to Push
```typescript
async function subscribeToPush() {
  const permission = await Notification.requestPermission();
  if (permission !== 'granted') return;

  const reg = await navigator.serviceWorker.ready;
  const subscription = await reg.pushManager.subscribe({
    userVisibleOnly: true,
    applicationServerKey: urlBase64ToUint8Array(VAPID_PUBLIC_KEY),
  });

  // Send subscription to server
  await fetch('/api/push/subscribe', {
    method: 'POST',
    body: JSON.stringify(subscription),
  });
}

function urlBase64ToUint8Array(base64String) {
  const padding = '='.repeat((4 - base64String.length % 4) % 4);
  const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
  const rawData = atob(base64);
  return Uint8Array.from(rawData.split('').map(c => c.charCodeAt(0)));
}
```

### 8.3 Handle Push in Service Worker
```javascript
self.addEventListener('push', (event) => {
  const data = event.data ? event.data.json() : {};

  const options = {
    body: data.body,
    icon: '/icons/icon-192.png',
    badge: '/icons/badge-72.png',
    data: { url: data.url },
    actions: [
      { action: 'open', title: 'Open' },
      { action: 'close', title: 'Close' },
    ],
    tag: data.tag || 'default',
    renotify: true,
    vibrate: [200, 100, 200],
  };

  event.waitUntil(
    self.registration.showNotification(data.title || 'Notification', options)
  );
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  if (event.action === 'open' || !event.action) {
    event.waitUntil(
      clients.openWindow(event.notification.data.url || '/')
    );
  }
});
```

### 8.4 Server-Side Push (web-push)
```javascript
const webpush = require('web-push');

webpush.setVapidDetails(
  'mailto:contact@example.com',
  VAPID_PUBLIC_KEY,
  VAPID_PRIVATE_KEY
);

await webpush.sendNotification(subscription, JSON.stringify({
  title: 'New message',
  body: 'You have a new message',
  url: '/messages',
}));
```

---

## Part 9: Offline UI Patterns

### 9.1 Connection Status
```typescript
function useOnlineStatus() {
  const [online, setOnline] = useState(navigator.onLine);

  useEffect(() => {
    const handleOnline = () => setOnline(true);
    const handleOffline = () => setOnline(false);
    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);
    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  return online;
}

// Usage
function ConnectionBanner() {
  const online = useOnlineStatus();
  if (online) return null;
  return (
    <div className="bg-yellow-500 text-white p-2 text-center">
      You're offline. Some features may be unavailable.
    </div>
  );
}
```

### 9.2 Offline Fallback Page
```html
<!-- offline.html -->
<!DOCTYPE html>
<html>
<head>
  <title>Offline</title>
</head>
<body>
  <h1>You're offline</h1>
  <p>Check your internet connection and try again.</p>
  <p>Cached content is still available:</p>
  <nav>
    <a href="/">Home</a>
    <a href="/articles">Articles</a>
  </nav>
</body>
</html>
```

### 9.3 Stale Data Indicator
```tsx
function Article({ data, isStale }) {
  return (
    <article>
      {isStale && (
        <div className="stale-warning">
          Showing cached content — may be outdated
        </div>
      )}
      <h1>{data.title}</h1>
      <p>{data.body}</p>
    </article>
  );
}
```

### 9.4 Sync Status
```tsx
function SyncIndicator() {
  const [pendingCount, setPendingCount] = useState(0);

  // Check IndexedDB for unsynced items
  useEffect(() => {
    checkPendingSyncs().then(setPendingCount);
  }, []);

  if (pendingCount === 0) return null;
  return (
    <div className="sync-pending">
      {pendingCount} changes pending sync
    </div>
  );
}
```

---

## Part 10: Testing PWAs

### 10.1 Lighthouse PWA Audit
- **Run Lighthouse:** Chrome DevTools → Lighthouse → PWA category
- **Check all criteria:** Manifest, service worker, HTTPS, icons, etc.
- **Fix issues:** Address all Lighthouse PWA recommendations

### 10.2 Testing Offline
1. **DevTools:** Application → Service Workers → Offline checkbox
2. **DevTools:** Network → Offline
3. **Refresh:** Verify app still works
4. **Navigate:** Test all critical flows offline
5. **Reconnect:** Verify data syncs when back online

### 10.3 Testing Installation
1. **Chrome:** Address bar → Install icon
2. **Android:** Chrome menu → Add to Home screen
3. **iOS:** Safari → Share → Add to Home Screen
4. **Desktop:** Chrome menu → Install app
5. **Verify:** Opens in standalone mode, correct icon, splash screen

### 10.4 Testing Push Notifications
1. **Permission:** Verify permission prompt appears
2. **Granted:** Verify notification is shown
3. **Denied:** Verify graceful degradation
4. **Background:** Verify push works when app is closed
5. **Click:** Verify notification click opens correct URL

---

## Execution Instructions for Cascade

When this skill is activated for PWA & offline-first:

1. **Read the project context** — framework, offline requirements, push notification needs
2. **Create web app manifest** — name, icons (192px, 512px, maskable), display mode, theme color
3. **Set up service worker** — Workbox (recommended) or custom, with caching strategies
4. **Implement caching strategies** — cache-first (static), network-first (API), SWR (other)
5. **Implement app shell** — pre-cache HTML/CSS/JS for instant offline loading
6. **Set up IndexedDB** — for offline data persistence with idb library
7. **Implement background sync** — queue failed requests, retry when online
8. **Set up push notifications** — VAPID keys, subscription, server-side push
9. **Implement offline UI** — connection status, stale data indicator, sync status, offline fallback
10. **Handle service worker updates** — detect new version, prompt user, reload
11. **Test** — Lighthouse PWA audit, offline testing, installation testing, push testing
12. **Document** — offline capabilities, sync behavior, notification setup
