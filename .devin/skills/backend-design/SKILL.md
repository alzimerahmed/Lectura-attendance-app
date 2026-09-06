---
name: backend-design
description: Apply backend architecture patterns, structural design principles, and engineering best practices to any backend project — ensures robust, scalable, maintainable systems
---

# Backend Design Structural System Workflow

This workflow applies the **Backend Design Structural System Skill** (`~/.codeium/windsurf/skills/backend-design-structural-system.md`) to ensure every backend is architected with modern 2025-2026 standards — robust, scalable, maintainable, and free of legacy patterns or AI-generated boilerplate.

## When to Run
- When building any new backend or API
- When designing database schema for a new project
- When restructuring or refactoring an existing backend
- When the user says `/backend-design` or asks about backend architecture
- After the Website Research workflow completes, before writing backend code
- When adding significant new backend features (auth, payments, real-time, etc.)

---

## Step 1: Read Context

1. Read the project's `research.md` if it exists — it contains the tech stack and architecture decisions
2. Read existing `package.json`, `requirements.txt`, or equivalent to understand the stack
3. Read any existing database schema, migrations, or ORM models
4. Read existing API routes/controllers to understand current structure
5. Identify: framework, database, ORM, auth system, deployment target

## Step 2: Assess Project Complexity

Determine the architectural approach based on actual needs:

1. **Simple CRUD app?** → Layered architecture (controller → service → repository). Single database. Monolith.
2. **Complex domain logic?** → Clean/Hexagonal architecture. Rich domain models. Bounded contexts.
3. **Multiple teams?** → Modular monolith with clear module boundaries. Plan for future extraction.
4. **Distributed system?** → Event-driven architecture. Message broker. Database per service.
5. **Real-time requirements?** → WebSockets or SSE. Pub/sub backend. Connection management.

Document the architectural decision and justify it. If you can't justify the complexity, choose the simpler option.

## Step 3: Design API Surface

1. **List all resources** the API will expose
2. **Define endpoints** using RESTful conventions (or GraphQL schema if appropriate):
   - Resource paths (nouns, plural)
   - HTTP methods
   - Request/response shapes
   - Status codes
3. **Design pagination** for all list endpoints (cursor-based for large datasets)
4. **Design error format** — consistent envelope with code, message, field, request_id
5. **Plan idempotency** for all state-changing POST endpoints
6. **Plan rate limiting** strategy (per endpoint, per user, per IP)
7. **Plan versioning** strategy (URL for public APIs, additive changes by default)

## Step 4: Design Data Model

1. **Identify entities** and their relationships
2. **Choose primary key strategy:**
   - `BIGSERIAL`/identity for single-cluster (default, faster)
   - `UUIDv7` for distributed/multi-database
3. **Normalize to 3NF** — one entity per table, clear relationships
4. **Plan indexes** based on query patterns:
   - Index every foreign key
   - Index columns used in WHERE, JOIN, ORDER BY
   - Use composite indexes with correct column order (equality first, range last)
   - Use partial indexes for skewed distributions
5. **Plan migrations** using expand-and-contract pattern (zero-downtime)
6. **Decide ORM vs raw SQL** — ORM for CRUD, raw SQL for complex queries
7. **Identify N+1 risks** and plan eager loading strategy

## Step 5: Design Auth & Security

1. **Choose auth strategy:**
   - Web app (same domain): session-based with httpOnly, Secure, SameSite cookies
   - SPA/mobile: JWT access tokens (15min) + refresh token rotation
   - Third-party: OAuth 2.0 Authorization Code + PKCE
2. **Choose authorization model:**
   - Start with RBAC (admin, user, guest)
   - Move to ABAC only when RBAC can't express rules
3. **Plan security measures:**
   - Input validation at boundary (schema validation, not manual if-checks)
   - Parameterized queries (no string-concatenated SQL)
   - CORS whitelist (never `*` with credentials)
   - Rate limiting at gateway/middleware
   - Security headers (HSTS, CSP, X-Content-Type-Options, etc.)
   - Secrets in environment variables / secrets manager (never in code)
4. **Plan OWASP Top 10 mitigation** for each vulnerability

## Step 6: Design Code Structure

1. **Choose architecture pattern** (layered by default, hexagonal for swappable infra, clean for complex domain)
2. **Define folder structure** — modular by domain:
   ```
   src/
   ├── modules/
   │   ├── auth/
   │   ├── billing/
   │   └── inventory/
   ├── shared/
   │   ├── middleware/
   │   ├── utils/
   │   └── database/
   └── app.ts
   ```
3. **Enforce separation of concerns:**
   - Controllers: HTTP only (req/res, status codes, validation)
   - Services: Business logic (no HTTP, throw errors)
   - Repositories: Data access (return domain objects)
4. **Identify bounded contexts** and module boundaries
5. **Plan domain events** for cross-module communication

## Step 7: Plan Observability

1. **Structured logging** — JSON format with request_id, trace_id, service name
2. **Health checks** — `/health` (liveness), `/ready` (readiness with dependency checks)
3. **Metrics** — RED method (Rate, Errors, Duration) per endpoint
4. **Distributed tracing** — OpenTelemetry instrumentation at HTTP, DB, external call boundaries
5. **Alerting** — alert on error rate, p99 latency, health check failure. Include runbook links.
6. **Plan log levels:** debug (dev), info (lifecycle), warn (degraded), error (failures)

## Step 8: Plan Testing Strategy

1. **Unit tests** — domain logic in isolation, mock dependencies. Target 80%+ for domain logic.
2. **Integration tests** — service + real database (testcontainers), API endpoints through HTTP
3. **E2E tests** — critical user flows only (signup, purchase, key workflow)
4. **Contract tests** — OpenAPI schema validation in CI, consumer-driven for microservices
5. **Load tests** — before major releases, find breaking point
6. **Test database** — separate test DB, testcontainers, factories for test data

## Step 9: Plan Deployment

1. **Environment strategy** — local, CI, staging (prod-like), production
2. **CI/CD pipeline** — lint → type check → unit tests → build → integration tests → security scan → deploy staging → e2e → deploy prod
3. **Containerization** — multi-stage builds, non-root user, pinned versions, health checks
4. **Deployment strategy** — rolling (default), blue-green (instant rollback), canary (high-risk)
5. **Database migrations** — expand-and-contract, backward-compatible, tested on prod-like data
6. **Rollback plan** — automatic rollback on error rate spike, backward-compatible migrations

## Step 10: Check Against Anti-Patterns

Before building, verify NONE of these are present:

### Architecture
- [ ] God objects/classes (>500 lines, >20 methods, >10 dependencies)
- [ ] Fat controllers (business logic in route handlers)
- [ ] Leaky abstractions (ORM errors, internal table names exposed to clients)
- [ ] Premature microservices (splitting before proving the need)
- [ ] Premature optimization (caching/sharding before measuring)
- [ ] Anemic domain models (data bags with no behavior)
- [ ] Circular dependencies (ServiceA → ServiceB → ServiceA)

### API
- [ ] Chatty APIs (client needs 10 calls for one screen)
- [ ] Missing pagination on list endpoints
- [ ] Inconsistent error formats across endpoints
- [ ] Magic strings/numbers instead of enums/constants
- [ ] Hardcoded config (URLs, limits, timeouts in code)
- [ ] Synchronous blocking where async is needed (email in request handler)
- [ ] No idempotency on payment/order endpoints
- [ ] Exposing internal auto-increment IDs

### Data
- [ ] N+1 queries (queries inside loops)
- [ ] SELECT * (unnecessary data transfer)
- [ ] Missing indexes on foreign keys or filter columns
- [ ] Over-indexing (every column indexed)
- [ ] EAV pattern (entity-attribute-value table)
- [ ] God table (50+ columns)
- [ ] Polymorphic associations (breaks referential integrity)
- [ ] No migration strategy (schema changes cause downtime)

### Security
- [ ] Secrets in source code
- [ ] CORS `*` with credentials
- [ ] JWT in localStorage
- [ ] No rate limiting
- [ ] Unvalidated input
- [ ] String-concatenated SQL

If any check fails, revise the design before building.

## Step 11: Build

Implement the backend following the design plan:

1. Set up project structure (modules, shared, database)
2. Implement database schema and migrations (expand-and-contract)
3. Implement repositories (data access layer)
4. Implement services (business logic, domain rules)
5. Implement controllers (thin — HTTP only)
6. Implement middleware (auth, error handling, rate limiting, validation)
7. Implement observability (logging, health checks, metrics, tracing)
8. Write tests (unit → integration → e2e)
9. Set up CI/CD pipeline
10. Deploy to staging, verify, deploy to production

---

## Quick Reference: Architecture Decision Checklist

- [ ] Architecture pattern chosen and justified (layered/hexagonal/clean/modular monolith)
- [ ] API design: RESTful, paginated, consistent errors, idempotent, versioned
- [ ] Data model: normalized, indexed, correct PK strategy, migration plan
- [ ] Auth: correct strategy for app type, secure token handling
- [ ] Security: input validation, parameterized queries, CORS, rate limiting, headers, secrets
- [ ] Code structure: modular by domain, separation of concerns enforced
- [ ] Observability: structured logging, health checks, metrics, tracing, alerting
- [ ] Testing: unit (domain), integration (API+DB), e2e (critical paths), contract, load
- [ ] Deployment: CI/CD, containerization, zero-downtime, rollback plan
- [ ] Anti-patterns: all checks passed
