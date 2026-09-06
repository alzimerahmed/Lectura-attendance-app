---
name: build-tools
description: Comprehensive build tools & bundlers workflow — bundler selection, Vite config, code splitting, tree shaking, bundle analysis, source maps, CSS, and performance optimization
---

# Build Tools & Bundlers Workflow

This workflow applies the **Build Tools & Bundlers Skill** (`~/.codeium/windsurf/skills/build-tools-bundlers.md`) to configure optimal build pipelines and minimize bundle size.

## When to Run
- When setting up a build tool for a new project
- When the user says `/build-tools` or asks about bundlers
- When optimizing bundle size or build performance
- When configuring code splitting or tree shaking
- When migrating from Webpack to Vite

---

## Step 1: Choose Bundler

1. Read the project context — framework, project type, existing bundler
2. **Next.js:** Use Turbopack (built-in, default in Next 15+)
3. **React/Vue/Svelte SPA:** Use Vite (fastest, best DX)
4. **Library/package:** Use tsup (esbuild-based, simple config)
5. **Monorepo:** Turborepo for orchestration + Vite (apps) + tsup (packages)
6. **Legacy Webpack:** Migrate to Vite or use Rspack (drop-in replacement)
7. **Static site:** Vite or Astro
8. Install and initialize chosen bundler

## Step 2: Configure Bundler

1. Create config file: `vite.config.ts`, `tsup.config.ts`, etc.
2. Set up path aliases: `@/` → `./src/`
3. Configure dev server: port, proxy, auto-open
4. Set build target: `es2022` for modern browsers (less transpilation)
5. Set up environment variables: `VITE_` prefix for client-exposed vars
6. Install and configure plugins: React, image optimization, PWA, etc.
7. Configure output: directory, file naming with content hashes

## Step 3: Set Up Code Splitting

1. **Route-based splitting:** Use `React.lazy()` + `Suspense` for each route
2. **Component splitting:** Lazy load heavy components (charts, editors, PDF viewers)
3. **Manual vendor chunks:** Split large vendor libraries into separate chunks
4. **Dynamic imports:** Use `import()` for on-demand loading
5. **Prefetch:** Add `<link rel="prefetch">` for likely-next routes
6. Set up Suspense fallbacks: skeletons, spinners, or shimmer placeholders
7. Test: verify each route loads only its own chunk

## Step 4: Enable Tree Shaking

1. Use ESM imports (`import`/`export`) — not CommonJS (`require`)
2. Set `"sideEffects": false` in `package.json` (or list files with side effects)
3. Use named imports: `import { debounce } from 'lodash-es'` (not `import _ from 'lodash'`)
4. Avoid barrel imports if they prevent tree shaking
5. Use `lodash-es` instead of `lodash`, `date-fns` v3+ (ESM)
6. Verify with bundle visualizer — check for unused code in output
7. Check third-party packages: ensure they have `"sideEffects": false` and ESM output

## Step 5: Configure Source Maps

1. **Development:** `sourcemap: true` — full source maps for debugging
2. **Staging:** `sourcemap: true` — full source maps for testing
3. **Production:** `sourcemap: 'hidden'` — source maps for error tracking, not exposed to users
4. Set up error tracking integration: upload source maps to Sentry/Vercel
5. Never expose production source maps to users (security risk)
6. Test: verify stack traces in error tracking show original source

## Step 6: Set Up Bundle Analysis

1. Install bundle visualizer: `rollup-plugin-visualizer` (Vite) or `webpack-bundle-analyzer`
2. Generate visualization: `npx vite-bundle-visualizer`
3. Review for:
   - Large dependencies (> 50KB gzipped) — can they be replaced?
   - Duplicate packages — same package included multiple times
   - Unused code — included but never imported
   - Large chunks (> 200KB) — split further
4. Set up bundle budget in CI — fail build if exceeded
5. Track bundle size over time — catch regressions early

## Step 7: Set Bundle Budget

1. **Initial bundle:** < 150KB gzipped (first page load)
2. **Any single chunk:** < 100KB gzipped
3. **Total bundle:** < 500KB gzipped (all chunks combined)
4. **CSS:** < 50KB gzipped (initial)
5. **Any asset:** < 500KB (images, fonts)
6. Configure CI to fail build if budget exceeded
7. Use `size-limit` or custom check in GitHub Actions
8. Review budget regularly — adjust as features are added

## Step 8: Optimize CSS

1. Vite automatically code-splits CSS per lazy-loaded component
2. Configure CSS minification: `lightningcss` (faster than esbuild)
3. Use CSS Modules for scoped styles: `*.module.css`
4. Purge unused CSS: TailwindCSS does this automatically
5. Avoid large CSS frameworks — use utility-first (Tailwind) or CSS-in-JS
6. Inline critical CSS for above-the-fold content
7. Use `font-display: swap` for custom fonts

## Step 9: Optimize Images at Build

1. Install `vite-imagetools` for build-time image optimization
2. Configure default directives: AVIF format, quality 80
3. Generate responsive sizes: 400w, 800w, 1200w, 1600w
4. Generate WebP fallbacks
5. Optimize SVGs with SVGO
6. Use `import.meta.glob` for dynamic image imports
7. Set up lazy loading for below-fold images

## Step 10: Optimize Build Performance

1. Set `target: 'es2022'` — less transpilation for modern browsers
2. Use `esbuild` for minification (faster than terser)
3. Use `lightningcss` for CSS minification
4. Pre-bundle dependencies in dev: `optimizeDeps.include`
5. Use `npm ci` in CI (faster, reproducible)
6. Enable build caching: Vite cache, CI cache
7. Parallelize CI jobs: lint, test, build in parallel
8. Use Turborepo for monorepo build caching

## Step 11: Test Production Build

1. Run `npm run build` — verify no errors
2. Run `npm run preview` — serve production build locally
3. Test all routes — verify code splitting works
4. Check bundle size — compare against budget
5. Run Lighthouse — check performance score
6. Verify source maps — check error tracking
7. Test in production-like environment — staging
8. Check for console errors — no missing modules

## Step 12: Document & Maintain

1. Document build configuration — config file, plugins, environment variables
2. Document chunking strategy — vendor chunks, route chunks, dynamic imports
3. Document bundle budget — limits and CI enforcement
4. Document deployment process — build command, output directory, deploy command
5. Regularly review bundle analysis — remove unused dependencies
6. Update dependencies regularly — security patches, performance improvements
7. Monitor bundle size trend — catch regressions in CI
