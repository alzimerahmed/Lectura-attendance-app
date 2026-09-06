---
name: Build Tools & Bundlers Skill
description: Comprehensive methodology for configuring build tools and bundlers — 2025-2026 practices with Vite, Turbopack, esbuild, code splitting, tree shaking, source maps, and bundle analysis
version: 1.0.0
tags: [build-tools, bundlers, vite, turbopack, esbuild, webpack, code-splitting, tree-shaking, source-maps, bundle-analysis, performance]
---

# Build Tools & Bundlers Skill

## Purpose
This skill provides a comprehensive methodology for configuring build tools and bundlers across any kind of web project. It reflects **modern 2025-2026 practices** — Vite as the default bundler for SPAs, Turbopack for Next.js, esbuild for library builds, code splitting for optimal loading, tree shaking for smaller bundles, and bundle analysis for continuous optimization.

## Core Philosophy

**The best bundle is the smallest bundle that loads the fastest.** Every kilobyte of JavaScript must be downloaded, parsed, and executed. Smaller bundles mean faster page loads, better Core Web Vitals, and happier users. Optimize relentlessly — code split, tree shake, lazy load, and analyze your bundle regularly.

**The #1 rule:** Ship less JavaScript. The fastest code is code that's never sent. Code split by route, lazy load heavy components, and only load what the user needs right now. A 500KB bundle that's split into five 100KB chunks loaded on demand is better than one monolithic 500KB bundle.

---

## Part 1: Bundler Selection

### 1.1 Comparison (2025-2026)

| Bundler | Speed | Use Case | Ecosystem |
|---|---|---|---|
| **Vite** | Very fast (esbuild + Rollup) | SPAs, libraries, most projects | Excellent |
| **Turbopack** | Very fast (Rust) | Next.js (built-in) | Next.js only |
| **esbuild** | Extremely fast (Go) | Libraries, quick builds | Good |
| **Rollup** | Fast (Rust-based Rollup 4) | Libraries, ESM output | Excellent |
| **Webpack** | Slow (but mature) | Legacy, complex configs | Excellent |
| **Rspack** | Very fast (Rust, Webpack-compatible) | Webpack migration | Growing |
| **Bun** | Very fast (Zig) | Bun runtime projects | Growing |

### 1.2 Decision Matrix

| Project Type | Recommended Bundler |
|---|---|
| **Next.js app** | Turbopack (built-in, default in Next 15+) |
| **React SPA** | Vite |
| **Vue/Svelte SPA** | Vite |
| **Library/package** | tsup (esbuild) or Rollup |
| **Monorepo** | Turborepo + Vite (apps), tsup (packages) |
| **Legacy Webpack** | Rspack (drop-in replacement) or migrate to Vite |
| **Static site** | Vite or Astro |

---

## Part 2: Vite Configuration

### 2.1 Basic Vite Config
```typescript
// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    open: true,
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
    target: 'es2022',
    rollupOptions: {
      output: {
        manualChunks: {
          'react-vendor': ['react', 'react-dom'],
          'ui-vendor': ['framer-motion', 'lucide-react'],
        },
      },
    },
  },
});
```

### 2.2 Vite Plugins
```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { imagetools } from 'vite-imagetools';
import { VitePWA } from 'vite-plugin-pwa';
import { vanillaExtractPlugin } from '@vanilla-extract/vite-plugin';

export default defineConfig({
  plugins: [
    react(),
    imagetools({ defaultDirectives: (url) => ({ format: 'avif', quality: 80 }) }),
    VitePWA({ registerType: 'autoUpdate' }),
    vanillaExtractPlugin(),
  ],
});
```

### 2.3 Environment Variables
```bash
# .env
VITE_API_URL=https://api.example.com
VITE_ANALYTICS_ID=GA-XXXXXXX
```
```typescript
// Access in code
const apiUrl = import.meta.env.VITE_API_URL;
```
- **`VITE_` prefix:** Required for client-side exposure
- **`.env.local`:** Local overrides (gitignored)
- **`.env.production`:** Production values
- **Never expose secrets:** Anything with `VITE_` prefix is visible to clients

### 2.4 Dev Server Proxy
```typescript
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
```

---

## Part 3: Code Splitting

### 3.1 Route-Based Splitting (React)
```tsx
import { lazy, Suspense } from 'react';

// Lazy load route components
const Home = lazy(() => import('./pages/Home'));
const About = lazy(() => import('./pages/About'));
const Dashboard = lazy(() => import('./pages/Dashboard'));

function App() {
  return (
    <Suspense fallback={<PageSkeleton />}>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route path="/dashboard" element={<Dashboard />} />
      </Routes>
    </Suspense>
  );
}
```

### 3.2 Component-Based Splitting
```tsx
// Lazy load heavy components
const Chart = lazy(() => import('./components/Chart'));
const PDFViewer = lazy(() => import('./components/PDFViewer'));
const RichEditor = lazy(() => import('./components/RichEditor'));

function ReportPage() {
  const [showChart, setShowChart] = useState(false);

  return (
    <div>
      <button onClick={() => setShowChart(true)}>Show Chart</button>
      {showChart && (
        <Suspense fallback={<Spinner />}>
          <Chart />
        </Suspense>
      )}
    </div>
  );
}
```

### 3.3 Manual Chunks (Vite/Rollup)
```typescript
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          // Split vendor libraries
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'ui-vendor': ['framer-motion', 'lucide-react', 'sonner'],
          'chart-vendor': ['recharts', 'd3'],
          'utils-vendor': ['date-fns', 'zod', 'lodash-es'],
        },
      },
    },
  },
});
```

### 3.4 Dynamic Imports
```typescript
// Load module on demand
async function loadAndProcess(file: File) {
  // Only load image processing library when needed
  const { default: sharp } = await import('sharp');
  // Process image...
}

// Conditional loading
if (process.env.NODE_ENV === 'production') {
  const { initAnalytics } = await import('./analytics');
  initAnalytics();
}
```

---

## Part 4: Tree Shaking

### 4.1 What is Tree Shaking
- **Dead code elimination:** Remove unused exports from the final bundle
- **ESM required:** Tree shaking works with ES modules (`import`/`export`), not CommonJS
- **Side effects:** Packages with side effects may not be tree-shaken

### 4.2 Enabling Tree Shaking
```json
// package.json — mark package as side-effect free
{
  "sideEffects": false
}

// Or specify files with side effects
{
  "sideEffects": ["*.css", "*.scss", "./src/polyfills.js"]
}
```

### 4.3 Import Best Practices
```typescript
// GOOD — named imports (tree-shakeable)
import { debounce } from 'lodash-es';
import { format } from 'date-fns';

// BAD — default import of entire library (not tree-shakeable)
import _ from 'lodash';
import * as dateFns from 'date-fns';

// GOOD — specific imports
import debounce from 'lodash-es/debounce';
import { format } from 'date-fns/format';

// BAD — barrel imports (may prevent tree shaking)
import { debounce, throttle, cloneDeep } from 'lodash-es';
// If only debounce is used, throttle and cloneDeep may still be included
```

### 4.4 Verify Tree Shaking
```bash
# Analyze bundle to verify tree shaking
npx vite-bundle-visualizer

# Or with source-map-explorer
npx source-map-explorer dist/assets/*.js
```

---

## Part 5: Bundle Analysis

### 5.1 Vite Bundle Visualizer
```bash
# Install and run
npm i -D rollup-plugin-visualizer
```
```typescript
// vite.config.ts
import { visualizer } from 'rollup-plugin-visualizer';

export default defineConfig({
  plugins: [
    visualizer({
      open: true,
      filename: 'dist/stats.html',
      gzipSize: true,
      brotliSize: true,
    }),
  ],
});
```

### 5.2 What to Look For
- **Large dependencies:** Any single dependency > 50KB gzipped — can it be replaced?
- **Duplicate packages:** Same package included multiple times — deduplicate
- **Unused code:** Code that's included but never used — tree shake or remove
- **Large chunks:** Any chunk > 200KB gzipped — split further
- **CSS size:** Large CSS files — purge unused styles
- **Font size:** Embedded fonts — use `font-display: swap` and subset

### 5.3 Bundle Budget
```json
// package.json or .bundlebudgetrc
{
  "bundleBudget": {
    "default": "200kb",
    "initial": "150kb",
    "anyChunk": "100kb",
    "anyAsset": "500kb"
  }
}
```
- **Initial bundle:** < 150KB gzipped — what loads on first page
- **Any chunk:** < 100KB gzipped — no single lazy chunk should be huge
- **Total:** < 500KB gzipped — all chunks combined
- **Fail CI:** If budget exceeded, fail the build

---

## Part 6: Source Maps

### 6.1 Source Map Configuration
```typescript
// Vite
export default defineConfig({
  build: {
    sourcemap: true, // Generate source maps
    // 'hidden' — generate but don't reference in output (for error tracking)
    // 'inline' — inline source maps (larger files, no separate request)
  },
});
```

### 6.2 Source Map Strategies

| Environment | Strategy | Why |
|---|---|---|
| **Development** | `sourcemap: true` | Full source maps for debugging |
| **Staging** | `sourcemap: true` | Full source maps for testing |
| **Production** | `sourcemap: 'hidden'` | Source maps for error tracking, not exposed to users |

### 6.3 Error Tracking with Source Maps
```bash
# Upload source maps to Sentry/Vercel/etc after build
npx @sentry/cli sourcemaps upload dist/

# Or in CI
- name: Upload source maps
  run: npx @sentry/cli sourcemaps upload dist/
  env:
    SENTRY_AUTH_TOKEN: ${{ secrets.SENTRY_AUTH_TOKEN }}
```

---

## Part 7: CSS Bundling

### 7.1 CSS in Vite
```typescript
// Vite handles CSS automatically
import './styles.css'; // Injected into <style> or .css file
import './styles.module.css'; // CSS Modules
import './styles.scss'; // Sass (install sass)
```

### 7.2 CSS Code Splitting
```typescript
// Vite automatically code-splits CSS
// Each lazy-loaded component's CSS is in a separate file
const Dashboard = lazy(() => import('./Dashboard'));
// Dashboard.css is loaded only when Dashboard is loaded
```

### 7.3 CSS Minification
```typescript
export default defineConfig({
  build: {
    cssMinify: 'lightningcss', // Faster than esbuild
    // or 'esbuild' (default)
  },
});
```

---

## Part 8: Library Building

### 8.1 tsup (esbuild-based, Recommended)
```typescript
// tsup.config.ts
import { defineConfig } from 'tsup';

export default defineConfig({
  entry: ['src/index.ts'],
  format: ['esm', 'cjs'],
  dts: true, // Generate .d.ts files
  splitting: false,
  sourcemap: true,
  clean: true,
  treeshake: true,
  minify: true,
});
```

### 8.2 Package.json for Library
```json
{
  "name": "my-library",
  "version": "1.0.0",
  "type": "module",
  "main": "./dist/index.cjs",
  "module": "./dist/index.js",
  "types": "./dist/index.d.ts",
  "exports": {
    ".": {
      "import": "./dist/index.js",
      "require": "./dist/index.cjs",
      "types": "./dist/index.d.ts"
    },
    "./utils": {
      "import": "./dist/utils.js",
      "require": "./dist/utils.cjs",
      "types": "./dist/utils.d.ts"
    }
  },
  "sideEffects": false,
  "files": ["dist"]
}
```

### 8.3 Multiple Entry Points
```typescript
// tsup.config.ts
export default defineConfig({
  entry: {
    index: 'src/index.ts',
    utils: 'src/utils.ts',
    components: 'src/components/index.ts',
  },
  format: ['esm', 'cjs'],
  dts: true,
});
```

---

## Part 9: Performance Optimization

### 9.1 Build Performance
```typescript
export default defineConfig({
  // Parallelize processing
  build: {
    target: 'es2022', // Modern browsers only — less transpilation
    minify: 'esbuild', // Faster than terser
    cssMinify: 'lightningcss',
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        // Compress assets
        assetFileNames: 'assets/[name].[hash][extname]',
        chunkFileNames: 'chunks/[name].[hash].js',
        entryFileNames: 'entry/[name].[hash].js',
      },
    },
  },
  // Optimize dependencies
  optimizeDeps: {
    include: ['react', 'react-dom', 'framer-motion'], // Pre-bundle
    exclude: ['@api/client'], // Don't pre-bundle
  },
});
```

### 9.2 Dependency Optimization
```typescript
export default defineConfig({
  optimizeDeps: {
    // Pre-bundle large dependencies for faster dev server
    include: [
      'react',
      'react-dom',
      'react-router-dom',
      'framer-motion',
      'date-fns',
      'zod',
    ],
  },
});
```

### 9.3 Image Optimization at Build
```typescript
import { imagetools } from 'vite-imagetools';

export default defineConfig({
  plugins: [
    imagetools({
      defaultDirectives: (url) => {
        if (url.searchParams.has('avatar')) {
          return { w: 200, h: 200, format: 'avif', quality: 70 };
        }
        return { format: 'avif', quality: 80 };
      },
    }),
  ],
});
```

---

## Part 10: Turbopack (Next.js)

### 10.1 Turbopack in Next.js 15+
```javascript
// next.config.js
module.exports = {
  // Turbopack is default for dev in Next 15+
  // For build:
  // npx next build --turbo
};
```

### 10.2 Turbopack vs Webpack
- **Speed:** 10-700x faster than Webpack (Rust-based)
- **Dev server:** Instant cold start, lightning-fast HMR
- **Build:** Faster production builds
- **Compatibility:** Webpack-compatible config, most plugins work
- **Status:** Production-ready in Next.js 15+ for dev, stable for build in 15.2+

---

## Execution Instructions for Cascade

When this skill is activated for build tools & bundlers:

1. **Read the project context** — framework, existing bundler, bundle size, performance needs
2. **Choose bundler** — Vite (SPAs), Turbopack (Next.js), tsup (libraries)
3. **Configure bundler** — entry points, output format, aliases, plugins
4. **Set up code splitting** — route-based lazy loading, manual vendor chunks, dynamic imports
5. **Enable tree shaking** — ESM imports, `sideEffects: false`, named imports
6. **Configure source maps** — `true` for dev, `'hidden'` for production
7. **Set up bundle analysis** — visualizer plugin, check for large deps and duplicates
8. **Set bundle budget** — initial < 150KB, any chunk < 100KB, fail CI if exceeded
9. **Optimize CSS** — code splitting, minification, purge unused styles
10. **Optimize images** — build-time AVIF/WebP conversion
11. **Set up environment variables** — `VITE_` prefix, separate dev/prod
12. **Test production build** — `npm run build`, verify bundle size, test in production mode
13. **Document** — build configuration, chunking strategy, bundle budget
