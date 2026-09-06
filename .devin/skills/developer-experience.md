---
name: Developer Experience (DX) Skill
description: Comprehensive methodology for developer experience — 2025-2026 practices with project setup, tooling, error messages, debugging, fast feedback, onboarding, and productivity
version: 1.0.0
tags: [developer-experience, dx, tooling, onboarding, error-messages, debugging, fast-feedback, vscode, eslint, prettier, productivity]
---

# Developer Experience (DX) Skill

## Purpose
This skill provides a comprehensive methodology for optimizing developer experience across any kind of web project. It reflects **modern 2025-2026 practices** — instant dev server startup, fast HMR, clear error messages, automated formatting, editor integration, one-command setup, comprehensive tooling, and frictionless onboarding.

## Core Philosophy

**Developer experience is user experience for developers.** Every friction point — slow builds, confusing errors, missing documentation, broken setup — costs time and morale. Invest in DX the same way you invest in UX: identify pain points, measure friction, and eliminate it systematically. A developer who can go from `git clone` to running app in under 5 minutes is a productive developer.

**The #1 rule:** Optimize for the cold start. The most important DX metric is time-to-first-success — how long does it take a new developer to clone the repo, install dependencies, and see the app running? If it's more than 5 minutes, you have a DX problem. Every minute of setup friction compounds across team size and time.

---

## Part 1: Project Setup

### 1.1 One-Command Setup
```json
// package.json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "test": "vitest",
    "lint": "eslint .",
    "format": "prettier --write .",
    "typecheck": "tsc --noEmit",
    "setup": "npm install && npm run db:setup && npm run dev",
    "db:setup": "prisma migrate dev && prisma db seed",
    "db:seed": "tsx prisma/seed.ts",
    "check": "npm run lint && npm run typecheck && npm run test"
  }
}
```

### 1.2 README Quick Start
```markdown
## Quick Start

```bash
# 1. Clone
git clone https://github.com/org/project.git
cd project

# 2. Install
npm install

# 3. Environment
cp .env.example .env.local
# Edit .env.local with your values

# 4. Database (if applicable)
npm run db:setup

# 5. Run
npm run dev
```

That's it. App runs at http://localhost:3000
```

### 1.3 .env.example
```bash
# .env.example — safe to commit, documents required variables
# API
VITE_API_URL=http://localhost:3000/api

# Database
DATABASE_URL=postgresql://user:password@localhost:5432/mydb

# Auth
JWT_SECRET=your-secret-here

# External Services
STRIPE_SECRET_KEY=sk_test_xxx
SENDGRID_API_KEY=SG.xxx
```

### 1.4 Node Version Management
```json
// package.json
{
  "engines": {
    "node": ">=20.0.0"
  }
}
```
```bash
# .nvmrc
20
```
```bash
# .tool-versions (asdf)
nodejs 20.11.0
```

---

## Part 2: Editor Integration

### 2.1 VSCode Settings
```json
// .vscode/settings.json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": "explicit",
    "source.organizeImports": "explicit"
  },
  "typescript.tsdk": "node_modules/typescript/lib",
  "typescript.enablePromptUseWorkspaceTsdk": true,
  "files.associations": {
    "*.css": "tailwindcss"
  },
  "tailwindCSS.experimental.classRegex": [
    ["cva\\(([^)]*)\\)", "[\"'`]([^\"'`]*).*?[\"'`]"],
    ["cn\\(([^)]*)\\)", "[\"'`]([^\"'`]*).*?[\"'`]"]
  ],
  "files.exclude": {
    "**/node_modules": true,
    "**/.next": true,
    "**/dist": true
  }
}
```

### 2.2 VSCode Extensions
```json
// .vscode/extensions.json
{
  "recommendations": [
    "dbaeumer.vscode-eslint",
    "esbenp.prettier-vscode",
    "bradlc.vscode-tailwindcss",
    "ms-vscode.vscode-typescript-next",
    "firsttris.vscode-jest-runner",
    "mikestead.dotenv",
    "eamodio.gitlens",
    "streetsidesoftware.code-spell-checker"
  ]
}
```

### 2.3 Workspace Snippets
```json
// .vscode/react.code-snippets
{
  "React Component": {
    "prefix": "rc",
    "body": [
      "interface ${1:ComponentName}Props {",
      "  ${2:prop}: ${3:string};",
      "}",
      "",
      "export function ${1:ComponentName}({ ${2:prop} }: ${1:ComponentName}Props) {",
      "  return (",
      "    <div>",
      "      $0",
      "    </div>",
      "  );",
      "}"
    ]
  },
  "React Hook": {
    "prefix": "rh",
    "body": [
      "function use${1:HookName}() {",
      "  $0",
      "}",
      "",
      "export { use${1:HookName} };"
    ]
  }
}
```

---

## Part 3: Code Quality Automation

### 3.1 ESLint + Prettier
```javascript
// eslint.config.js
import tseslint from 'typescript-eslint';
import eslintPluginReact from 'eslint-plugin-react';
import eslintPluginReactHooks from 'eslint-plugin-react-hooks';

export default tseslint.config(
  ...tseslint.configs.strict,
  ...tseslint.configs.stylistic,
  {
    files: ['**/*.{ts,tsx}'],
    plugins: {
      react: eslintPluginReact,
      'react-hooks': eslintPluginReactHooks,
    },
    rules: {
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'error',
    },
  },
);
```
```json
// .prettierrc
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "all",
  "printWidth": 100,
  "plugins": ["prettier-plugin-tailwindcss"]
}
```

### 3.2 lint-staged + Husky
```json
// package.json
{
  "lint-staged": {
    "*.{ts,tsx}": ["eslint --fix", "prettier --write"],
    "*.{css,scss}": ["prettier --write"],
    "*.{md,json}": ["prettier --write"]
  }
}
```
```bash
# .husky/pre-commit
npx lint-staged

# .husky/pre-push
npm run typecheck && npm run test
```

### 3.3 EditorConfig
```ini
# .editorconfig
root = true

[*]
charset = utf-8
end_of_line = lf
indent_style = space
indent_size = 2
insert_final_newline = true
trim_trailing_whitespace = true

[*.md]
trim_trailing_whitespace = false
```

---

## Part 4: Error Messages & Debugging

### 4.1 Clear Error Messages
```typescript
// BAD — unhelpful error
throw new Error('Failed');

// GOOD — actionable error
throw new Error(
  `Failed to fetch user ${userId}: ${response.status} ${response.statusText}. ` +
  `Check if the user exists and you have permission to access it.`
);

// With error codes
class AppError extends Error {
  constructor(
    message: string,
    public code: string,
    public statusCode: number = 500,
    public context?: Record<string, unknown>
  ) {
    super(message);
    this.name = 'AppError';
  }
}

throw new AppError(
  'User not found',
  'USER_NOT_FOUND',
  404,
  { userId, requestId: crypto.randomUUID() }
);
```

### 4.2 Development Error Overlay
```typescript
// Next.js — error boundary with dev details
'use client';

import { useEffect } from 'react';

export default function GlobalError({ error, reset }) {
  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <html>
      <body>
        <div style={{ padding: '2rem', fontFamily: 'monospace' }}>
          <h2>Something went wrong!</h2>
          {process.env.NODE_ENV === 'development' && (
            <pre style={{ overflow: 'auto' }}>
              {error.message}
              {'\n\n'}
              {error.stack}
            </pre>
          )}
          <button onClick={reset}>Try again</button>
        </div>
      </body>
    </html>
  );
}
```

### 4.3 Structured Logging
```typescript
// Development — pretty print
const logger = {
  dev: (message: string, context?: object) => {
    if (process.env.NODE_ENV === 'development') {
      console.log(`[${new Date().toISOString()}] ${message}`, context ?? '');
    }
  },
  error: (message: string, error?: Error, context?: object) => {
    console.error(`[ERROR] ${message}`, {
      error: error?.message,
      stack: error?.stack,
      ...context,
    });
  },
};

// Usage
logger.dev('User logged in', { userId: user.id });
logger.error('Payment failed', error, { orderId, amount });
```

---

## Part 5: Fast Feedback Loop

### 5.1 Fast Dev Server
- **Vite:** Instant cold start, lightning-fast HMR
- **Turbopack:** Instant for Next.js 15+
- **esbuild:** Extremely fast transpilation
- **SWC:** Fast Rust-based compiler (Next.js)

### 5.2 Fast Tests
```json
// package.json
{
  "scripts": {
    "test": "vitest run",
    "test:watch": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest run --coverage"
  }
}
```
```typescript
// vitest.config.ts
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    css: true,
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
    },
  },
});
```

### 5.3 Fast Type Checking
```bash
# Use tsc --noEmit --incremental for faster checks
tsc --noEmit --incremental --tsBuildInfoFile node_modules/.cache/tsbuildinfo.json
```

### 5.4 Hot Module Replacement (HMR)
- **React Fast Refresh:** Preserves state on component edits
- **CSS HMR:** Styles update without page reload
- **Error Overlay:** Show build errors in browser, not terminal
- **Partial Acceptance:** Only reload what changed

---

## Part 6: Onboarding

### 6.1 CONTRIBUTING.md
```markdown
# Contributing

## Development Setup

1. Fork and clone the repository
2. Run `npm install`
3. Copy `.env.example` to `.env.local` and fill in values
4. Run `npm run db:setup` (if database needed)
5. Run `npm run dev`

## Code Style

- TypeScript strict mode
- ESLint + Prettier (runs on save and pre-commit)
- Conventional Commits: `type(scope): description`
- All PRs require review and passing CI

## Testing

- Write tests for new features
- Run `npm run test` before pushing
- Aim for > 80% coverage on critical paths

## Pull Request Process

1. Create branch: `feature/description`
2. Make changes, commit with Conventional Commits
3. Push and create PR using template
4. Address review feedback
5. Squash and merge after approval

## Project Structure

src/
  app/        — Pages and layouts
  components/ — React components
  lib/        — Utilities and helpers
  hooks/      — Custom hooks
  types/      — TypeScript types
  test/       — Test utilities
```

### 6.2 Architecture Decision Records (ADR)
```markdown
# ADR-001: Use Vite as Build Tool

## Status
Accepted

## Context
Need a fast, modern build tool for the React SPA.

## Decision
Use Vite for development and production builds.

## Consequences
- Fast cold start and HMR
- Native ESM support
- Rich plugin ecosystem
- No Webpack compatibility
```

---

## Part 7: Developer Tools

### 7.1 Package.json Scripts
```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "test": "vitest run",
    "test:watch": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest run --coverage",
    "lint": "eslint .",
    "lint:fix": "eslint . --fix",
    "format": "prettier --write .",
    "format:check": "prettier --check .",
    "typecheck": "tsc --noEmit",
    "check": "npm run lint && npm run typecheck && npm run test",
    "clean": "rm -rf dist node_modules/.cache",
    "fresh": "rm -rf node_modules package-lock.json && npm install"
  }
}
```

### 7.2 Debug Configuration
```json
// .vscode/launch.json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "Debug Dev Server",
      "type": "node",
      "request": "launch",
      "runtimeExecutable": "npm",
      "runtimeArgs": ["run", "dev"],
      "console": "integratedTerminal",
      "skipFiles": ["<node_internals>/**"]
    },
    {
      "name": "Debug Tests",
      "type": "node",
      "request": "launch",
      "runtimeExecutable": "npm",
      "runtimeArgs": ["run", "test:watch"],
      "console": "integratedTerminal"
    }
  ]
}
```

### 7.3 Path Aliases
```typescript
// vite.config.ts
export default defineConfig({
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@components': path.resolve(__dirname, './src/components'),
      '@lib': path.resolve(__dirname, './src/lib'),
      '@hooks': path.resolve(__dirname, './src/hooks'),
    },
  },
});
```
```json
// tsconfig.json
{
  "compilerOptions": {
    "paths": {
      "@/*": ["./src/*"],
      "@components/*": ["./src/components/*"],
      "@lib/*": ["./src/lib/*"],
      "@hooks/*": ["./src/hooks/*"]
    }
  }
}
```

---

## Part 8: CI/CD for DX

### 8.1 Fast CI Pipeline
```yaml
# .github/workflows/ci.yml
name: CI

on: [push, pull_request]

jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
          cache: 'npm'
      - run: npm ci
      - run: npm run lint
      - run: npm run typecheck
      - run: npm run test -- --reporter=dot
      - run: npm run build
```

### 8.2 CI Caching
- **npm cache:** `actions/setup-node` with `cache: 'npm'`
- **Build cache:** Cache `dist/` or `.next/` between runs
- **Test cache:** Cache Vitest results
- **Turbo cache:** `actions/cache` with Turborepo

### 8.3 Parallel Jobs
```yaml
jobs:
  lint:
    runs-on: ubuntu-latest
    steps: [checkout, setup-node, npm ci, npm run lint]
  typecheck:
    runs-on: ubuntu-latest
    steps: [checkout, setup-node, npm ci, npm run typecheck]
  test:
    runs-on: ubuntu-latest
    steps: [checkout, setup-node, npm ci, npm run test]
  build:
    needs: [lint, typecheck, test]
    runs-on: ubuntu-latest
    steps: [checkout, setup-node, npm ci, npm run build]
```

---

## Part 9: DX Metrics

### 9.1 Key Metrics
| Metric | Target | How to Measure |
|---|---|---|
| **Time to first success** | < 5 min | From `git clone` to running app |
| **Cold start time** | < 2 sec | `npm run dev` to browser ready |
| **HMR speed** | < 200ms | Save to browser update |
| **Test suite time** | < 30 sec | `npm run test` |
| **Build time** | < 30 sec | `npm run build` |
| **Type check time** | < 10 sec | `npm run typecheck` |
| **CI time** | < 5 min | Push to CI pass |

### 9.2 Regular DX Audits
- **Onboarding test:** Have a new team member clone and set up — time it
- **Friction log:** Document every annoyance, no matter how small
- **Tool survey:** Ask team what tools they love and hate
- **Build time trend:** Track build time over time — catch regressions
- **Test speed trend:** Track test suite time — catch slow tests

---

## Execution Instructions for Cascade

When this skill is activated for developer experience:

1. **Read the project context** — team size, existing tooling, pain points
2. **Set up one-command setup** — `npm install && npm run dev` should work
3. **Create .env.example** — document all required environment variables
4. **Set up VSCode integration** — settings, extensions, snippets, launch config
5. **Set up code quality automation** — ESLint, Prettier, Husky, lint-staged, EditorConfig
6. **Set up fast feedback loop** — Vite (fast dev server), Vitest (fast tests), incremental tsc
7. **Write clear error messages** — actionable, with context and suggested fixes
8. **Create CONTRIBUTING.md** — setup steps, code style, PR process, project structure
9. **Set up path aliases** — `@/` for src, configured in both Vite and tsconfig
10. **Optimize CI** — caching, parallel jobs, fast feedback
11. **Track DX metrics** — cold start, HMR, test speed, build time, CI time
12. **Conduct regular DX audits** — onboarding tests, friction logs, tool surveys
