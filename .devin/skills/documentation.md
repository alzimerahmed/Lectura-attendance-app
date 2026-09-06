---
name: Documentation Skill
description: Comprehensive methodology for software documentation — 2025-2026 practices with README, API docs, architecture decisions, inline comments, component docs, changelogs, and doc-as-code
version: 1.0.0
tags: [documentation, readme, api-docs, adr, inline-comments, changelog, jsdoc, storybook, doc-as-code, developer-docs]
---

# Documentation Skill

## Purpose
This skill provides a comprehensive methodology for software documentation across any kind of web project. It reflects **modern 2025-2026 practices** — doc-as-code with Markdown, automated API docs from code, Architecture Decision Records (ADRs), Storybook for component documentation, changelogs from Conventional Commits, and zero-stale-documentation through CI checks.

## Core Philosophy

**Documentation is code.** Treat docs with the same rigor as code: version control, review in PRs, test for accuracy, automate where possible, and delete what's stale. The best documentation lives next to the code it describes, is written by the people who wrote the code, and is updated in the same PR as the code change.

**The #1 rule:** Write docs for the reader, not the writer. The person reading your docs doesn't have your context — they don't know why you made a decision, how the system works, or where to start. Write for the newcomer, the contributor, and the future maintainer who will read this six months from now and has no idea what you were thinking.

---

## Part 1: Documentation Types

### 1.1 Documentation Pyramid

```
        /\
       /  \     Tutorials (learning-oriented)
      /----\
     /      \   How-to Guides (task-oriented)
    /--------\
   /          \ Reference (information-oriented)
  /------------\
 /              \ Explanation (understanding-oriented)
/----------------\
```

| Type | Purpose | Audience | Example |
|---|---|---|---|
| **Tutorials** | Learn from scratch | New users | "Getting Started with the API" |
| **How-to Guides** | Solve specific problems | Practitioners | "How to add OAuth authentication" |
| **Reference** | Look up details | All users | API reference, type definitions |
| **Explanation** | Understand why | All readers | Architecture decisions, design docs |

### 1.2 Project Documentation Files

| File | Purpose | Who Reads It |
|---|---|---|
| `README.md` | First impression, quick start | Everyone |
| `CONTRIBUTING.md` | How to contribute | Contributors |
| `ARCHITECTURE.md` | System design and decisions | Maintainers |
| `CHANGELOG.md` | What changed and when | Users, maintainers |
| `LICENSE` | Legal terms | Everyone |
| `docs/` | Detailed documentation | All readers |
| `adr/` | Architecture Decision Records | Maintainers |
| `API.md` or `/docs/api` | API reference | API consumers |

---

## Part 2: README.md

### 2.1 Structure
```markdown
# Project Name

> One-line description of what this project does.

[![CI](https://github.com/org/repo/actions/workflows/ci.yml/badge.svg)](https://github.com/org/repo/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

## Features

- Feature 1 — brief description
- Feature 2 — brief description
- Feature 3 — brief description

## Quick Start

### Prerequisites

- Node.js 20+
- npm 10+
- PostgreSQL 16+ (if applicable)

### Installation

```bash
git clone https://github.com/org/repo.git
cd repo
npm install
cp .env.example .env.local
# Edit .env.local with your values
npm run dev
```

App runs at http://localhost:3000

## Usage

### Basic Example

```bash
npm run build
npm run preview
```

### Advanced Configuration

See [docs/configuration.md](docs/configuration.md) for all options.

## Project Structure

```
src/
  app/         — Pages and routes
  components/  — React components
  lib/         — Utilities and helpers
  hooks/       — Custom hooks
  types/       — TypeScript types
  test/        — Test utilities
docs/          — Documentation
adr/           — Architecture Decision Records
```

## Scripts

| Script | Description |
|---|---|
| `npm run dev` | Start dev server |
| `npm run build` | Production build |
| `npm run test` | Run tests |
| `npm run lint` | Run ESLint |
| `npm run typecheck` | Run TypeScript check |

## Tech Stack

- **Framework:** Next.js 15
- **Language:** TypeScript 5.7
- **Styling:** TailwindCSS v4
- **Database:** PostgreSQL with Prisma
- **Testing:** Vitest + Playwright

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup and guidelines.

## License

[MIT](LICENSE) © 2025 Organization Name
```

### 2.2 README Best Practices
- **Badges:** CI status, license, PRs welcome — at the top
- **Quick Start:** 3-5 commands to running app — most important section
- **Examples:** Show, don't tell — code snippets that work
- **Structure:** Visual directory tree for project layout
- **Scripts table:** All npm scripts with descriptions
- **Tech stack:** List key technologies and versions
- **Keep it current:** Review in every PR that changes setup

---

## Part 3: API Documentation

### 3.1 OpenAPI/Swagger
```typescript
// For REST APIs — generate OpenAPI spec from code
import { openapi } from '@hono/zod-openapi';

const app = openapi({
  openapi: {
    info: {
      title: 'My API',
      version: '1.0.0',
      description: 'API for managing resources',
    },
  },
});

// Define route with schema
app.openapi(
  createRoute({
    method: 'get',
    path: '/users/{id}',
    request: {
      params: z.object({ id: z.string().uuid() }),
    },
    responses: {
      200: {
        description: 'User found',
        content: { 'application/json': { schema: userSchema } },
      },
      404: {
        description: 'User not found',
        content: { 'application/json': { schema: errorSchema } },
      },
    },
  }),
  handler
);

// Serve API docs at /docs
app.doc('/doc', (c) => ({ openapi: '3.0.0', info: {...}, paths: {...} }));
```

### 3.2 API.md (Simple)
```markdown
# API Reference

## Authentication

All requests require a Bearer token:
```
Authorization: Bearer <token>
```

## Endpoints

### GET /api/users

List all users with pagination.

**Query Parameters:**
| Param | Type | Default | Description |
|---|---|---|---|
| `page` | number | 1 | Page number |
| `limit` | number | 20 | Items per page (max 100) |
| `search` | string | — | Search by name or email |

**Response:**
```json
{
  "data": [{ "id": "uuid", "name": "Alice", "email": "alice@example.com" }],
  "pagination": { "page": 1, "limit": 20, "total": 42, "totalPages": 3 }
}
```

**Example:**
```bash
curl -H "Authorization: Bearer $TOKEN" \
  "https://api.example.com/api/users?page=1&limit=20"
```

### POST /api/users

Create a new user.

**Request Body:**
```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "role": "user"
}
```

**Response (201):**
```json
{
  "id": "uuid",
  "name": "Alice",
  "email": "alice@example.com",
  "role": "user",
  "createdAt": "2025-01-15T10:00:00Z"
}
```

**Errors:**
| Status | Code | Description |
|---|---|---|
| 400 | VALIDATION_ERROR | Invalid request body |
| 409 | EMAIL_EXISTS | Email already registered |
```

---

## Part 4: Architecture Decision Records (ADRs)

### 4.1 ADR Template
```markdown
# ADR-001: Use PostgreSQL for Primary Database

## Status
Accepted — 2025-01-15

## Context
We need a relational database for the application. The team has experience with
PostgreSQL and MySQL. The application requires complex queries, JSON columns,
and full-text search.

## Decision
Use PostgreSQL 16 as the primary database.

## Consequences

### Positive
- Advanced features: JSONB, full-text search, arrays, custom types
- Excellent performance for complex queries
- Strong ecosystem: Prisma, Drizzle, pgAdmin
- Built-in replication and failover

### Negative
- No managed serverless option (unlike DynamoDB)
- Requires connection pooling for serverless deployments

### Neutral
- Team needs to learn PostgreSQL-specific optimizations
```

### 4.2 ADR Guidelines
- **Number sequentially:** ADR-001, ADR-002, etc.
- **One decision per ADR:** Don't combine unrelated decisions
- **Status:** Proposed, Accepted, Deprecated, Superseded
- **Context first:** Explain why this decision is being made
- **List alternatives:** What was considered and rejected
- **Date:** When the decision was made
- **Link superseded:** If this ADR supersedes another, link it
- **Store in repo:** `adr/` directory or `docs/adr/`

---

## Part 5: Inline Code Comments

### 5.1 When to Comment
```typescript
// GOOD — explain WHY, not WHAT
// Using setTimeout instead of requestAnimationFrame because
// we need this to run even when the tab is not visible
setTimeout(syncData, 5000);

// GOOD — document non-obvious business logic
// Tax is calculated on the shipping address, not billing
// This is required by EU VAT rules for digital goods
const taxRate = getTaxRate(order.shippingAddress.country);

// GOOD — link to context
// See ADR-003 for why we chose Redis over Memcached
const cache = new Redis(process.env.REDIS_URL);

// BAD — explains what the code does (obvious)
// Increment counter by 1
counter++;

// BAD — outdated comment
// TODO: remove this after migration (added 2023)
```

### 5.2 JSDoc for Public APIs
```typescript
/**
 * Fetches a user by ID with their associated orders.
 *
 * @param userId - The unique identifier of the user
 * @param options - Optional configuration
 * @param options.includeOrders - Whether to include the user's orders
 * @param options.cache - Cache strategy ('force-cache' | 'no-store')
 * @returns The user with optional orders, or null if not found
 * @throws {AppError} When userId is invalid or database connection fails
 *
 * @example
 * ```ts
 * const user = await fetchUser('123e4567-e89b-12d3-a456-426614174000', {
 *   includeOrders: true,
 * });
 * ```
 */
async function fetchUser(
  userId: string,
  options?: { includeOrders?: boolean; cache?: RequestCache }
): Promise<User | null> {
  // ...
}
```

### 5.3 Component Documentation
```typescript
/**
 * Button component with variants and sizes.
 *
 * @example
 * ```tsx
 * <Button variant="primary" size="md" onClick={handleClick}>
 *   Click me
 * </Button>
 * ```
 */
interface ButtonProps {
  /** Visual style of the button */
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
  /** Size of the button */
  size?: 'sm' | 'md' | 'lg';
  /** Click handler */
  onClick?: (event: React.MouseEvent<HTMLButtonElement>) => void;
  /** Disable the button */
  disabled?: boolean;
  /** Button content */
  children: React.ReactNode;
}
```

---

## Part 6: Component Documentation (Storybook)

### 6.1 Story Setup
```tsx
// Button.stories.tsx
import type { Meta, StoryObj } from '@storybook/react';
import { Button } from './Button';

const meta: Meta<typeof Button> = {
  title: 'UI/Button',
  component: Button,
  parameters: {
    layout: 'centered',
    docs: {
      description: {
        component: 'Primary action button with multiple variants and sizes.',
      },
    },
  },
  tags: ['autodocs'],
  argTypes: {
    variant: {
      control: 'select',
      options: ['primary', 'secondary', 'outline', 'ghost'],
      description: 'Visual style',
    },
    size: {
      control: 'select',
      options: ['sm', 'md', 'lg'],
      description: 'Button size',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Primary: Story = {
  args: {
    variant: 'primary',
    size: 'md',
    children: 'Click me',
  },
};

export const Secondary: Story = {
  args: {
    variant: 'secondary',
    children: 'Secondary',
  },
};

export const Disabled: Story = {
  args: {
    variant: 'primary',
    disabled: true,
    children: 'Disabled',
  },
};
```

---

## Part 7: Changelog

### 7.1 Keep a Changelog Format
```markdown
# Changelog

All notable changes to this project are documented in this file.
Format based on [Keep a Changelog](https://keepachangelog.com/).
This project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- OAuth2 authentication with Google
- User avatar upload with image processing

### Changed
- Updated API response format for pagination
- Upgraded Next.js to 15.2

### Fixed
- Fixed race condition in cart checkout
- Resolved memory leak in WebSocket handler

### Security
- Patched XSS vulnerability in user bio rendering

## [1.2.0] - 2025-01-15

### Added
- Dark mode support
- Search functionality with Meilisearch

### Changed
- Migrated from Webpack to Vite

### Fixed
- Fixed hydration mismatch on product pages
```

### 7.2 Automated Changelog from Conventional Commits
```bash
# standard-version generates changelog from commit messages
npx standard-version

# Or use semantic-release for full automation
# It analyzes commits, bumps version, generates changelog, creates GitHub release
```

---

## Part 8: CONTRIBUTING.md

### 8.1 Template
```markdown
# Contributing to Project Name

Thank you for your interest in contributing! This guide will help you get started.

## Development Setup

1. Fork and clone the repository
2. Run `npm install`
3. Copy `.env.example` to `.env.local` and fill in values
4. Run `npm run db:setup` (if database needed)
5. Run `npm run dev`
6. App runs at http://localhost:3000

## Code Style

- **TypeScript:** Strict mode, no `any`, no `as` assertions
- **Formatting:** Prettier (runs on save and pre-commit)
- **Linting:** ESLint with typescript-eslint strict config
- **Commits:** Conventional Commits — `type(scope): description`
- **Branches:** `feature/`, `fix/`, `chore/`, `docs/`

## Testing

- Write tests for all new features
- Run `npm run test` before pushing
- Minimum 80% coverage on critical paths
- Use Vitest for unit tests, Playwright for E2E

## Pull Request Process

1. Create a branch from `main`: `git checkout -b feature/description`
2. Make changes, commit with Conventional Commits
3. Push and create a PR using the template
4. Ensure all CI checks pass
5. Request review from a maintainer
6. Address feedback by pushing additional commits
7. Squash and merge after approval

## Project Structure

See [README.md](README.md) for directory structure.

## Architecture Decisions

See [adr/](adr/) for Architecture Decision Records.

## Reporting Issues

- Use GitHub Issues for bug reports and feature requests
- Include: steps to reproduce, expected vs actual, environment info
- For security issues, email security@example.com (don't open public issues)
```

---

## Part 9: Documentation CI Checks

### 9.1 Check for Stale Docs
```yaml
# .github/workflows/docs.yml
name: Documentation

on: [push, pull_request]

jobs:
  check-docs:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
      - run: npm ci

      # Check for broken links
      - run: npx linkinator docs/ README.md

      # Check for TODOs in docs
      - run: npx leasot 'docs/**/*.md' README.md

      # Type check code examples in docs
      - run: npx documentation lint 'src/**/*.ts'

      # Build Storybook
      - run: npm run build-storybook
```

### 9.2 Documentation Tests
```typescript
// Test that code examples in docs actually work
import { describe, it, expect } from 'vitest';
import { readFileSync } from 'fs';

describe('README examples', () => {
  it('quick start commands work', () => {
    const readme = readFileSync('README.md', 'utf-8');
    // Extract and verify code blocks
    const codeBlocks = readme.match(/```bash\n([\s\S]*?)```/g);
    // Verify commands are valid
  });
});
```

---

## Execution Instructions for Cascade

When this skill is activated for documentation:

1. **Read the project context** — existing docs, audience, project type
2. **Create README.md** — quick start, features, structure, scripts, tech stack
3. **Create CONTRIBUTING.md** — setup, code style, testing, PR process
4. **Create API documentation** — OpenAPI spec or API.md with all endpoints
5. **Set up ADRs** — `adr/` directory with template and initial decisions
6. **Add inline comments** — explain WHY not WHAT, JSDoc for public APIs
7. **Set up Storybook** — for component documentation with live examples
8. **Create CHANGELOG.md** — Keep a Changelog format, automate from commits
9. **Set up doc CI checks** — broken links, stale TODOs, code example validation
10. **Create docs/ directory** — for detailed guides, tutorials, explanations
11. **Review docs in PRs** — require doc updates with code changes
12. **Automate where possible** — API docs from code, changelog from commits
