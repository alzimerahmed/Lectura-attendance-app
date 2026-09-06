---
name: Monorepo Management Skill
description: Comprehensive methodology for monorepo management — 2025-2026 practices with Turborepo, pnpm workspaces, package versioning, cross-package dependencies, caching, and CI optimization
version: 1.0.0
tags: [monorepo, turborepo, pnpm, workspaces, package-management, caching, ci, changesets, versioning, cross-package]
---

# Monorepo Management Skill

## Purpose
This skill provides a comprehensive methodology for managing monorepos across any kind of web project. It reflects **modern 2025-2026 practices** — Turborepo for task orchestration and caching, pnpm workspaces for efficient dependency management, Changesets for versioning, shared configurations for consistency, and CI optimization with remote caching.

## Core Philosophy

**A monorepo should feel like a single project, not a collection of projects.** Shared tooling, shared configs, shared types, and shared components should flow naturally between packages. The developer should be able to run one command to build, test, and deploy any package — without manually managing dependencies or execution order.

**The #1 rule:** Use pnpm workspaces, not npm or yarn. pnpm's content-addressable storage saves disk space (one copy of each package version globally), its strict node_modules prevents phantom dependencies, and its workspace protocol (`workspace:*`) makes cross-package linking explicit and safe. npm and yarn workspaces are slower, less disk-efficient, and more error-prone.

---

## Part 1: Monorepo Structure

### 1.1 Standard Structure
```
my-monorepo/
├── apps/
│   ├── web/              — Next.js web app
│   ├── admin/            — Admin dashboard
│   ├── api/              — API server
│   └── mobile/           — React Native app
├── packages/
│   ├── ui/               — Shared UI components
│   ├── config/           — Shared configs (eslint, tsconfig, tailwind)
│   ├── types/            — Shared TypeScript types
│   ├── utils/            — Shared utilities
│   ├── api-client/       — API client SDK
│   └── database/         — Database schemas and migrations
├── turbo.json            — Turborepo configuration
├── pnpm-workspace.yaml   — pnpm workspace config
├── package.json          — Root package.json
├── .changeset/           — Changesets config
└── .github/
    └── workflows/        — CI/CD
```

### 1.2 pnpm Workspace Config
```yaml
# pnpm-workspace.yaml
packages:
  - "apps/*"
  - "packages/*"
```

### 1.3 Root package.json
```json
{
  "name": "my-monorepo",
  "private": true,
  "scripts": {
    "dev": "turbo dev",
    "build": "turbo build",
    "test": "turbo test",
    "lint": "turbo lint",
    "typecheck": "turbo typecheck",
    "clean": "turbo clean && rm -rf node_modules",
    "changeset": "changeset",
    "version-packages": "changeset version",
    "release": "turbo build && changeset publish"
  },
  "devDependencies": {
    "turbo": "^2.3",
    "changeset": "^0.2"
  },
  "packageManager": "pnpm@9.15.0",
  "engines": {
    "node": ">=20.0.0"
  }
}
```

---

## Part 2: Turborepo

### 2.1 turbo.json
```json
{
  "$schema": "https://turbo.build/schema.json",
  "tasks": {
    "build": {
      "dependsOn": ["^build"],
      "outputs": ["dist/**", ".next/**", "!.next/cache/**"],
      "env": ["NODE_ENV", "DATABASE_URL"]
    },
    "dev": {
      "cache": false,
      "persistent": true
    },
    "test": {
      "dependsOn": ["^build"],
      "outputs": ["coverage/**"],
      "inputs": ["src/**/*.tsx", "src/**/*.ts", "test/**/*.ts"]
    },
    "lint": {
      "outputs": []
    },
    "typecheck": {
      "dependsOn": ["^build"],
      "outputs": []
    },
    "clean": {
      "cache": false
    }
  }
}
```

### 2.2 Task Dependencies
```json
{
  "tasks": {
    "build": {
      "dependsOn": ["^build"],
      "outputs": ["dist/**"]
    }
  }
}
```
- **`^build`** — Build dependencies first (packages that this package imports)
- **`build`** — Build this package's dependencies and itself
- **Topological order:** Turborepo automatically runs tasks in dependency order
- **Parallel execution:** Independent tasks run in parallel

### 2.3 Caching
```json
{
  "tasks": {
    "build": {
      "inputs": ["src/**/*", "package.json", "tsconfig.json"],
      "outputs": ["dist/**"],
      "cache": true
    }
  }
}
```
- **Local cache:** `.turbo/cache/` — speeds up repeated builds
- **Remote cache:** Vercel Remote Cache — shared across team and CI
- **Cache keys:** Based on inputs (source files, configs, environment variables)
- **Cache hits:** Skip task entirely, restore outputs from cache

### 2.4 Remote Cache
```bash
# Enable Vercel Remote Cache
npx turbo login
npx turbo link

# Now cache is shared across:
# - Local development
# - CI pipelines
# - Team members
```

### 2.5 Filtering
```bash
# Run task for specific package
turbo build --filter=web

# Run for packages that depend on ui
turbo build --filter=...@repo/ui

# Run for packages that ui depends on
turbo build --filter=@repo/ui...

# Run for changed packages since main
turbo build --filter=...[main]

# Run for changed packages and their dependents
turbo build --filter=...[main]...
```

---

## Part 3: Package Management

### 3.1 Cross-Package Dependencies
```json
// apps/web/package.json
{
  "name": "web",
  "dependencies": {
    "@repo/ui": "workspace:*",
    "@repo/utils": "workspace:*",
    "@repo/api-client": "workspace:*"
  }
}
```
- **`workspace:*`** — Links to local package, not published version
- **Explicit dependencies:** Always declare what you import — no phantom deps
- **Version sync:** When a package is published, `workspace:*` is replaced with actual version

### 3.2 Shared Dependencies (Hoisting)
```json
// Root package.json — shared dev dependencies
{
  "devDependencies": {
    "typescript": "^5.7",
    "eslint": "^9.18",
    "prettier": "^3.4",
    "vitest": "^2.1"
  }
}
```
- **pnpm strict:** Each package only sees its declared dependencies
- **No hoisting by default:** Prevents phantom dependencies
- **Shared dev deps:** Install at root for consistency — TypeScript, ESLint, Prettier

### 3.3 Package.json for a Package
```json
// packages/ui/package.json
{
  "name": "@repo/ui",
  "version": "0.1.0",
  "private": true,
  "main": "./dist/index.js",
  "module": "./dist/index.mjs",
  "types": "./dist/index.d.ts",
  "exports": {
    ".": {
      "import": "./dist/index.mjs",
      "require": "./dist/index.js",
      "types": "./dist/index.d.ts"
    },
    "./button": {
      "import": "./dist/button.mjs",
      "require": "./dist/button.js",
      "types": "./dist/button.d.ts"
    }
  },
  "scripts": {
    "build": "tsup src/index.ts --format esm,cjs --dts",
    "dev": "tsup src/index.ts --format esm,cjs --dts --watch",
    "lint": "eslint .",
    "typecheck": "tsc --noEmit",
    "test": "vitest run"
  },
  "dependencies": {
    "react": "^19.0",
    "class-variance-authority": "^0.7"
  },
  "peerDependencies": {
    "react": "^19.0"
  }
}
```

---

## Part 4: Shared Configurations

### 4.1 Shared ESLint Config
```typescript
// packages/config/eslint/index.ts
import tseslint from 'typescript-eslint';

export const config = tseslint.config(
  ...tseslint.configs.strict,
  ...tseslint.configs.stylistic,
  {
    rules: {
      '@typescript-eslint/no-explicit-any': 'error',
      '@typescript-eslint/no-non-null-assertion': 'error',
    },
  },
);

export default config;
```
```json
// apps/web/package.json
{
  "devDependencies": {
    "@repo/eslint-config": "workspace:*"
  }
}
```
```javascript
// apps/web/eslint.config.js
import { config } from '@repo/eslint-config';

export default config;
```

### 4.2 Shared TypeScript Config
```json
// packages/config/tsconfig/base.json
{
  "compilerOptions": {
    "target": "ES2022",
    "lib": ["ES2022", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "moduleResolution": "bundler",
    "strict": true,
    "noUncheckedIndexedAccess": true,
    "exactOptionalPropertyTypes": true,
    "skipLibCheck": true,
    "isolatedModules": true
  }
}
```
```json
// apps/web/tsconfig.json
{
  "extends": "@repo/tsconfig/base.json",
  "compilerOptions": {
    "jsx": "preserve",
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["src"]
}
```

### 4.3 Shared Tailwind Config
```typescript
// packages/config/tailwind/index.ts
export const sharedConfig = {
  theme: {
    extend: {
      colors: {
        brand: {
          500: 'oklch(0.62 0.19 250)',
          900: 'oklch(0.28 0.09 250)',
        },
      },
    },
  },
};
```

---

## Part 5: Versioning with Changesets

### 5.1 Setup
```bash
# Install changesets
pnpm add -D -w changeset

# Initialize
npx changeset init
```

### 5.2 Configuration
```json
// .changeset/config.json
{
  "changelog": "@changesets/cli/changelog",
  "commit": false,
  "fixed": [],
  "linked": [],
  "access": "restricted",
  "baseBranch": "main",
  "updateInternalDependencies": "patch",
  "ignore": ["web", "admin"]
}
```

### 5.3 Workflow
```bash
# 1. Make changes in a package
# 2. Add a changeset
npx changeset
# Select packages that changed
# Select bump type (major, minor, patch)
# Write summary of changes

# This creates .changeset/xyz.md:
# ---
# "@repo/ui": minor
# ---
# Added new Button component with variants

# 3. Commit changeset with code changes
git add . && git commit -m "feat(ui): add Button component"

# 4. When ready to release, version packages
npx changeset version
# This:
# - Bumps versions in package.json files
# - Updates CHANGELOG.md in each package
# - Consumes the changeset files

# 5. Publish
npx changeset publish
# This publishes changed packages to npm
```

### 5.4 Automated Releases (GitHub Action)
```yaml
# .github/workflows/release.yml
name: Release

on:
  push:
    branches: [main]

jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
          cache: 'pnpm'
          registry-url: 'https://registry.npmjs.org'

      - run: pnpm install --frozen-lockfile
      - run: pnpm build

      - name: Create Release Pull Request or Publish
        uses: changesets/action@v1
        with:
          publish: pnpm release
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          NPM_TOKEN: ${{ secrets.NPM_TOKEN }}
```

---

## Part 6: CI/CD for Monorepos

### 6.1 Optimized CI Pipeline
```yaml
# .github/workflows/ci.yml
name: CI

on: [push, pull_request]

jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 2  # Needed for change detection

      - uses: pnpm/action-setup@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
          cache: 'pnpm'

      - run: pnpm install --frozen-lockfile

      # Use Turbo Remote Cache
      - name: Turbo Remote Cache
        uses: actions/cache@v4
        with:
          path: .turbo
          key: turbo-${{ runner.os }}-${{ github.sha }}
          restore-keys: |
            turbo-${{ runner.os }}-

      # Run only affected packages
      - run: pnpm turbo lint typecheck test build --filter=...[main]

  deploy:
    needs: quality
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
          cache: 'pnpm'

      - run: pnpm install --frozen-lockfile
      - run: pnpm turbo build --filter=web
      - run: npx vercel --prod --token ${{ secrets.VERCEL_TOKEN }}
```

### 6.2 Change Detection
```bash
# Only run tasks for changed packages and their dependents
turbo build --filter=...[main]

# Only run tasks for changed packages
turbo build --filter=[main]

# Run for specific app and its dependencies
turbo build --filter=web...
```

---

## Part 7: Development Workflow

### 7.1 Running Multiple Dev Servers
```bash
# Run all dev servers simultaneously
pnpm dev

# Run specific app
turbo dev --filter=web

# Run web and its dependencies in dev mode
turbo dev --filter=web...
```

### 7.2 Adding a New Package
```bash
# Create package directory
mkdir -p packages/new-package/src

# Create package.json
cat > packages/new-package/package.json << 'EOF'
{
  "name": "@repo/new-package",
  "version": "0.1.0",
  "private": true,
  "main": "./dist/index.js",
  "types": "./dist/index.d.ts",
  "scripts": {
    "build": "tsup src/index.ts --format esm,cjs --dts",
    "dev": "tsup src/index.ts --format esm,cjs --dts --watch"
  }
}
EOF

# Create source file
echo "export const hello = () => 'Hello';" > packages/new-package/src/index.ts

# Install dependencies
pnpm install

# Use in another package
# Add "@repo/new-package": "workspace:*" to dependencies
```

### 7.3 Adding a New App
```bash
# Create Next.js app in monorepo
pnpm create next-app apps/new-app --ts --tailwind --eslint --app --src-dir

# Add to workspace (automatic with pnpm-workspace.yaml glob)

# Link shared packages
cd apps/new-app
pnpm add @repo/ui @repo/utils @repo/config
```

---

## Part 8: Common Patterns

### 8.1 Shared Types Package
```typescript
// packages/types/src/index.ts
export type User = {
  id: string;
  name: string;
  email: string;
  role: 'admin' | 'user';
};

export type Product = {
  id: string;
  name: string;
  price: number;
  currency: string;
};

// Export Zod schemas for runtime validation
export { userSchema, productSchema } from './schemas';
```

### 8.2 Shared API Client
```typescript
// packages/api-client/src/index.ts
import type { User, Product } from '@repo/types';

export class ApiClient {
  constructor(private baseUrl: string) {}

  async getUser(id: string): Promise<User> {
    const res = await fetch(`${this.baseUrl}/users/${id}`);
    return res.json();
  }

  async getProducts(): Promise<Product[]> {
    const res = await fetch(`${this.baseUrl}/products`);
    return res.json();
  }
}
```

### 8.3 Shared Database Package
```typescript
// packages/database/src/index.ts
import { PrismaClient } from '@prisma/client';

const globalForPrisma = globalThis as unknown as { prisma: PrismaClient };

export const prisma = globalForPrisma.prisma || new PrismaClient();

if (process.env.NODE_ENV !== 'production') globalForPrisma.prisma = prisma;

// Export types
export type { User, Product } from '@prisma/client';
```

---

## Part 9: Anti-Patterns

### 9.1 Don't Use npm or yarn for Monorepos
```bash
# BAD — npm/yarn workspaces are slow and less disk-efficient
npm install
yarn install

# GOOD — pnpm is fast, disk-efficient, and strict
pnpm install
```

### 9.2 Don't Use Lerna (Deprecated)
```bash
# BAD — Lerna is deprecated, use Turborepo
npx lerna run build

# GOOD — Turborepo is modern, fast, and actively maintained
turbo build
```

### 9.3 Don't Hoist All Dependencies
```json
// BAD — hoisting causes phantom dependencies
{
  "pnpm": {
    "hoist-pattern": ["*"]
  }
}

// GOOD — let pnpm's strict mode prevent phantom deps
// Only declare what you use in each package.json
```

### 9.4 Don't Version All Packages Together
```bash
# BAD — bumping all packages at once
# If ui changes, why should api-client get a new version?

# GOOD — use Changesets for independent versioning
npx changeset  # Only bump changed packages
```

---

## Execution Instructions for Cascade

When this skill is activated for monorepo management:

1. **Read the project context** — number of packages, apps, shared code, team structure
2. **Set up pnpm workspaces** — `pnpm-workspace.yaml`, root `package.json` with `packageManager`
3. **Set up Turborepo** — `turbo.json` with task pipeline, dependencies, caching
4. **Create package structure** — `apps/` for deployable apps, `packages/` for shared libraries
5. **Set up shared configs** — ESLint, TypeScript, Tailwind in `packages/config/`
6. **Configure cross-package dependencies** — `workspace:*` protocol, explicit declarations
7. **Set up Changesets** — for independent package versioning and publishing
8. **Set up CI/CD** — Turbo Remote Cache, change detection, parallel jobs
9. **Set up development workflow** — `pnpm dev` runs all dev servers, filtering for specific packages
10. **Create shared packages** — types, utils, UI components, API client, database
11. **Enable remote caching** — Vercel Remote Cache for shared cache across team and CI
12. **Document** — monorepo structure, adding packages, release process, CI pipeline
