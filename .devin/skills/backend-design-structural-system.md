---
name: Backend Design Structural System Skill
description: Architecture patterns, structural design principles, and engineering best practices for building robust, scalable, and maintainable backends — modern 2025-2026 standards, not legacy patterns or AI-generated boilerplate
version: 1.0.0
tags: [backend, architecture, api, database, security, performance, testing, devops, anti-patterns]
---

# Backend Design Structural System Skill

## Purpose
This skill encodes the architecture patterns, structural design principles, and engineering best practices for building **robust, scalable, and maintainable backends** for any website or web application. It reflects modern 2025-2026 backend engineering standards — not legacy patterns or AI-generated boilerplate. Every choice should be opinionated and deliberate, grounded in the specific project's needs.

## Core Philosophy

**Model for the reader. Model for the operator. Model for the next developer.** A backend that is correct but impossible to operate, debug, or extend is a failed backend. Every architectural decision should consider three stakeholders: the client consuming the API, the operator running the system, and the developer maintaining the code.

**The #1 rule:** Start simple, add complexity only when measured evidence demands it. Premature microservices, premature optimization, and premature abstraction are the three most expensive mistakes in backend engineering.

---

## Part 1: API Design

### 1.1 RESTful Conventions

**Resource-oriented design:**
- Use nouns, not verbs in paths: `/users`, `/orders`, `/orders/{id}/items`
- Standard HTTP methods: `GET` (read), `POST` (create), `PUT` (full replace), `PATCH` (partial update), `DELETE` (remove)
- HTTP status codes: `200` (OK), `201` (Created), `204` (No Content), `400` (Bad Request), `401` (Unauthorized), `403` (Forbidden), `404` (Not Found), `409` (Conflict), `422` (Unprocessable Entity), `429` (Too Many Requests), `500` (Internal Error), `503` (Service Unavailable)
- Use `PUT` for idempotent updates, `PATCH` for partial non-idempotent updates
- Never use `GET` for state-changing operations

**Naming:**
- `snake_case` or `camelCase` consistently (pick one, enforce everywhere)
- Plural nouns for collections: `/users` not `/user`
- Query params for filtering, sorting, field selection: `?status=active&sort=-created_at&fields=id,name`

### 1.2 GraphQL Schemas

- Use schema-first design — define types before resolvers
- Keep resolvers thin — delegate to service layer
- Use DataLoader for N+1 elimination
- Implement query complexity analysis and depth limiting
- Version schema with `@deprecated` directives, not URL versioning
- Use connections/edges for paginated lists (Relay-style)

### 1.3 Versioning Strategies

| Strategy | When to Use | Example |
|---|---|---|
| **URL versioning** | Public APIs, breaking changes expected | `/api/v2/users` |
| **Header versioning** | Internal APIs, clean URLs | `Accept: application/vnd.api+json;version=2` |
| **Query param** | Quick patches, not recommended long-term | `?version=2` |
| **No versioning** | Internal, rapidly iterating, few consumers | — |

**Rule:** Version from day one for public APIs. Use additive (non-breaking) changes by default. Reserve new versions for truly breaking changes.

### 1.4 Pagination

**Always paginate list endpoints. No exceptions.**

| Method | When | Example |
|---|---|---|
| **Cursor-based** | Large datasets, real-time data, stable ordering | `?cursor=abc123&limit=20` |
| **Offset-based** | Small datasets, need to jump to page N | `?page=3&limit=20` |
| **Keyset** | Ordered, stable sort key | `?after_id=500&limit=20` |

**Cursor tokens must be:**
- Opaque (encrypted, not readable base64 JSON)
- Versioned (include format version for forward compatibility)
- Time-bound (expire within 24 hours)
- Bound to the caller (include account/user ID)
- Hash-validated (reject if filter params changed)

```json
{
  "data": [...],
  "pagination": {
    "next_cursor": "eyJ2IjoxLCJjIjoi...",
    "has_more": true,
    "limit": 20
  }
}
```

### 1.5 Error Handling & Response Envelopes

**Consistent error format:**
```json
{
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "The email field is required",
    "field": "email",
    "details": {...},
    "request_id": "req_abc123"
  }
}
```

**Rules:**
- Never leak internal error details (stack traces, SQL errors, table names) to clients
- Use error codes (strings, not numbers) for programmatic handling
- Include `request_id` for traceability
- Map domain errors to HTTP status codes in a global error handler
- Use custom error classes that carry status code + code + message

**Consistent success envelope:**
```json
{
  "data": {...},
  "meta": {
    "request_id": "req_abc123",
    "timestamp": "2026-07-12T09:30:00Z"
  }
}
```

### 1.6 Idempotency

- `POST` endpoints that create resources or trigger side effects MUST support idempotency keys
- Accept `Idempotency-Key` header
- Store request + response for 24 hours
- Return cached response for duplicate keys
- Use for: payments, order creation, email sends, any financial operation

### 1.7 Rate Limiting

- Implement at the API gateway or middleware layer
- Use token bucket algorithm
- Return `429` with `Retry-After` header
- Rate limit by: API key, user ID, IP address (in order of preference)
- Different limits for different endpoints (auth: strict, read: generous)

---

## Part 2: Data Modeling

### 2.1 Schema Design Principles

1. **Normalize first (3NF), denormalize with intent** — Start at Third Normal Form. Denormalize a specific column only after `EXPLAIN ANALYZE` proves a join is the bottleneck. Document why in a comment.
2. **Model for the reader** — OLTP gets normalized. Analytics gets dimensional. Feature stores get flattened. Design the schema that makes the consumer's query simplest.
3. **Choose the right primary key** — `BIGSERIAL`/identity columns for single-cluster (20-40x faster reads, 8 bytes). `UUIDv7` for distributed/multi-database (timestamp-ordered, preserves B-tree locality). Avoid random `UUIDv4`.
4. **Use consistent naming** — `snake_case` table names (plural), `snake_case` columns (singular), `idx_{table}_{columns}` for indexes, `{referenced_table_singular}_id` for foreign keys
5. **Define foreign keys** — Not optional. They prevent orphaned records, document relationships, and help the query planner
6. **Choose correct data types** — Smallest sufficient type. `BIGINT` over `INT` for growing IDs. `TIMESTAMPTZ` over `TIMESTAMP`. `VARCHAR(n)` over `TEXT` for constrained fields

### 2.2 Indexing Strategy

**Index the columns you filter, join, and sort on. No more, no less.**

| Index Type | When |
|---|---|
| **B-tree** | Default. Equality, range, sort. Primary keys, foreign keys |
| **GIN** | JSONB containment, full-text search, array membership |
| **BRIN** | Append-only time-series, naturally clustered data |
| **GIST** | Geometric, ranges, custom types |
| **Partial** | Skewed distributions where queries always touch a hot subset (`WHERE status = 'pending'`) |
| **Composite** | Multiple columns in same query. Order: equality first, range last |

**Rules:**
- Every foreign key should have an index
- Composite index column order: highest selectivity first (for equality), range column last
- Drop unused indexes — they slow writes
- Use `CREATE INDEX CONCURRENTLY` in production (no table lock)
- Monitor index usage statistics

### 2.3 Relationship Modeling

- **One-to-one:** Shared primary key or unique foreign key
- **One-to-many:** Foreign key on the "many" side + index
- **Many-to-many:** Junction table with composite primary key
- **Polymorphic:** Avoid. Use explicit relationships or table-per-type. Polymorphic associations break referential integrity and indexing
- **Soft deletes:** Avoid. Use a `deleted_at` timestamp column with partial indexes. Or better: event sourcing / audit log

### 2.4 JSON Columns

- Use `JSONB` (PostgreSQL) for semi-structured data that varies per record
- Don't use JSONB for data that should be queryable columns
- Add GIN indexes on JSONB fields you query
- Validate JSON structure at the application layer
- Use generated columns to extract frequently-queried JSONB fields into queryable columns

### 2.5 Migration Patterns

**Expand-and-contract (zero-downtime):**
1. **Expand:** Add new column/table (backward compatible)
2. **Migrate:** Backfill data, dual-write to old + new
3. **Switch:** Read from new, verify consistency
4. **Contract:** Remove old column (after verification)

**Rules:**
- Never rename columns in a single migration — add new, backfill, switch, drop old
- Never change column types in-place — add new, backfill, switch, drop old
- Always test migrations on production-like data volumes
- Use migration tools that support rollbacks
- Migrations should be idempotent

### 2.6 ORM vs Raw SQL

| Use ORM For | Use Raw SQL For |
|---|---|
| CRUD operations | Complex queries with multiple joins |
| Simple relationships | Bulk inserts/updates |
| Schema migrations | Performance-critical queries |
| Rapid prototyping | Reporting/aggregation queries |
| Type safety | Queries ORM can't express cleanly |

**Always understand what SQL your ORM generates.** Use query logging in development. Profile slow queries with `EXPLAIN ANALYZE`.

---

## Part 3: Authentication & Authorization

### 3.1 Session-Based vs Token-Based

| Aspect | Session-Based | Token-Based (JWT) |
|---|---|---|
| **Best for** | Server-rendered apps, same-domain | SPA, mobile, cross-domain APIs |
| **Storage** | Server-side (DB or Redis) | Client-side (httpOnly cookie or memory) |
| **Revocation** | Immediate (delete session) | Requires token blacklist or short expiry |
| **Scalability** | Requires session store | Stateless, no shared store needed |
| **Security** | httpOnly + Secure + SameSite cookies | Short-lived + refresh token rotation |

**2026 recommendation:**
- For web apps (same domain): **session-based with httpOnly, Secure, SameSite cookies**
- For SPAs/mobile: **JWT access tokens (short-lived, 15min) + refresh token rotation**
- For third-party API access: **OAuth 2.0 / OIDC**

### 3.2 JWT Best Practices

- **Short-lived access tokens** (15 minutes max)
- **Refresh token rotation** — issue new refresh token on each use, invalidate old one
- **Store in httpOnly cookies** — never localStorage (XSS vulnerable)
- **Use strong signing** — `RS256` or `ES256`, not `HS256` (shared secret)
- **Include `iss`, `aud`, `exp`, `iat`, `jti`** claims
- **Validate all claims** on every request
- **Implement token revocation** — blacklist `jti` for revoked tokens
- **Don't put sensitive data in JWT** — it's base64, not encrypted

### 3.3 OAuth 2.0 / OIDC Flows

| Flow | Use Case |
|---|---|
| **Authorization Code + PKCE** | SPAs, mobile apps (the only correct flow for these) |
| **Authorization Code** | Server-side web apps |
| **Client Credentials** | Service-to-service communication |
| **Resource Owner Password** | Deprecated. Don't use. |
| **Implicit** | Deprecated. Don't use. |

### 3.4 RBAC vs ABAC

| Model | When |
|---|---|
| **RBAC (Role-Based)** | Simple, stable permission structure (admin, user, guest) |
| **ABAC (Attribute-Based)** | Complex, dynamic permissions based on user/resource/context attributes |
| **ReBAC (Relationship-Based)** | Permissions based on relationships between entities (Google Zanzibar / SpiceDB) |

**Start with RBAC. Move to ABAC only when RBAC can't express your rules.**

### 3.5 API Key Management

- Hash API keys at rest (like passwords) — never store plaintext
- Use separate keys for separate purposes (read-only, write, admin)
- Include key prefix for identification (e.g., `sk_live_abc123...`)
- Support key rotation with grace period
- Rate limit per key
- Log all key usage
- Auto-expire unused keys

---

## Part 4: Security

### 4.1 Input Validation

- **Validate at the boundary** — all incoming data (body, query, params, headers)
- **Use schema validation** — Zod, Joi, Pydantic, Valibind — never manual `if` checks
- **Whitelist, don't blacklist** — define what's allowed, reject everything else
- **Validate type, format, length, range, and business rules**
- **Sanitize output, not input** — encode for the correct context (HTML, URL, JS, SQL)

### 4.2 SQL Injection Prevention

- **Always use parameterized queries / prepared statements** — no exceptions
- Never string-concatenate SQL
- Use ORM query builders or parameterized raw SQL
- Validate and constrain identifiers (table/column names) if dynamic

### 4.3 XSS / XSRF Protection

- **XSS:** Output-encode all user content. Use CSP headers. httpOnly cookies. Don't use `dangerouslySetInnerHTML` without sanitization
- **XSRF:** SameSite cookies (`SameSite=Strict` or `SameSite=Lax`). XSRF tokens for state-changing requests. `Origin` / `Referer` header validation

### 4.4 Rate Limiting

- Apply at multiple layers: API gateway, application, per-endpoint
- Token bucket or sliding window algorithms
- Different limits: auth endpoints (5/min), read endpoints (100/min), write endpoints (30/min)
- Return `429` with `Retry-After` header
- Consider adaptive rate limiting based on system load

### 4.5 CORS Policies

- **Never use `Access-Control-Allow-Origin: *` with credentials**
- Whitelist specific origins
- Restrict methods to those actually used
- Restrict headers to those actually used
- Set appropriate `Access-Control-Max-Age` to reduce preflight requests

### 4.6 Secrets Management

- **Never hardcode secrets** in source code, config files, or environment files that are committed
- Use environment variables for local dev, secrets manager for production (AWS Secrets Manager, Doppler, HashiCorp Vault)
- Rotate secrets regularly
- Use separate secrets per environment
- Log secret access events
- Scan for leaked secrets in CI (GitGuardian, TruffleHog)

### 4.7 OWASP Top 10 (2026)

1. **Broken Access Control** — enforce authorization on every request, verify ownership
2. **Cryptographic Failures** — use modern algorithms, encrypt at rest and in transit
3. **Injection** — parameterized queries, input validation
4. **Insecure Design** — threat model from the start, abuse cases
5. **Security Misconfiguration** — disable defaults, remove unused features, secure headers
6. **Vulnerable Components** — dependency scanning, SCA tools, regular updates
7. **Auth Failures** — MFA, strong password policies, session management
8. **Software/Data Integrity** — code signing, CI/CD integrity, verified dependencies
9. **Logging/Monitoring Failures** — structured logging, alerting, audit trails
10. **SSRF** — validate URLs, restrict internal network access, use allowlists

### 4.8 Security Headers

```
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
Content-Security-Policy: default-src 'self'; script-src 'self' 'nonce-abc123'
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Referrer-Policy: strict-origin-when-cross-origin
Permissions-Policy: geolocation=(), microphone=(), camera=()
```

---

## Part 5: Performance & Scalability

### 5.1 Caching Layers

| Layer | When | Tools |
|---|---|---|
| **In-memory (app-level)** | Frequent reads, session data, computed results | LRU cache, Map |
| **Distributed cache** | Multi-instance apps, shared state | Redis, Memcached |
| **CDN** | Static assets, cached API responses, edge logic | Cloudflare, Vercel Edge, Fastly |
| **Query cache** | Expensive database queries | Materialized views, Redis |
| **HTTP cache** | Public, cacheable responses | `Cache-Control`, `ETag`, `Vary` |

**Cache invalidation rules:**
- Cache with TTL — don't expect perfect consistency
- Use cache-aside pattern: check cache → miss → fetch → populate cache
- Invalidate on write (write-through) for critical data
- Use event-driven invalidation for complex dependencies
- **Never cache authenticated personalized data without per-user keys**

### 5.2 N+1 Query Elimination

**The most common backend performance killer.**

**Detection:**
- Enable ORM query logging in development
- Look for queries inside loops
- Monitor database connection count per request

**Solutions:**
- Use eager loading (`include`, `with`, `select_related`)
- Use DataLoader pattern (batch + cache)
- Use JOINs in raw SQL
- Use batch loading by ID array

### 5.3 Connection Pooling

- Always use connection pooling — never open a new connection per request
- Pool size: `(CPU cores * 2) + effective_spindle_count` as a starting point
- Use PgBouncer for PostgreSQL connection pooling at scale
- Set appropriate `idle_timeout`, `max_lifetime`, `connection_timeout`
- Monitor pool utilization and wait times

### 5.4 Background Jobs & Queues

- Move slow work out of the request cycle: email sending, image processing, report generation, third-party API calls
- Use a job queue: BullMQ, Sidekiq, Celery, Temporal
- Implement job retries with exponential backoff
- Use dead letter queues for permanently failed jobs
- Make jobs idempotent (they may run multiple times)
- Set job timeouts
- Monitor queue depth and job duration

### 5.5 Scaling Strategies

| Strategy | When |
|---|---|
| **Vertical scaling** | First. Bigger server. Simple, no code changes |
| **Horizontal scaling** | When single server can't keep up. Requires stateless app servers |
| **Read replicas** | Read-heavy workloads. Route reads to replicas, writes to primary |
| **Sharding** | Write-heavy or data-volume exceeds single node. Partition by tenant, geography, or time |
| **Caching** | Before scaling database. Cheapest performance win |
| **Async processing** | Before scaling workers. Move work to background |
| **CDN/Edge** | Before scaling origin servers. Serve from edge |

**Order of operations:** Cache → Async → Optimize queries → Read replicas → Vertical scale → Shard. Never shard first.

---

## Part 6: Code Organization

### 6.1 Architecture Patterns

| Pattern | When | Trade-off |
|---|---|---|
| **Layered (3-tier)** | Default for most apps. Controller → Service → Repository | Simple, well-understood, can become rigid |
| **Hexagonal (Ports & Adapters)** | When infrastructure may change (swap DB, add queue) | More interfaces, more flexibility |
| **Clean Architecture** | Complex domain logic, long-lived projects | Most boilerplate, most decoupled |
| **Modular Monolith** | Large team, future microservices migration path | Modules within a monolith, clear boundaries |
| **Microservices** | Multiple teams, independently deployable services | High operational complexity, network failures |

**2026 recommendation:** Start with a **modular monolith** using layered architecture. Extract to microservices only when you have a proven need (team size > 8, independent deployment requirements, different scaling profiles).

### 6.2 Separation of Concerns

**The three-layer rule:**

```
┌─────────────────────────────────┐
│ Controller / Route Handler       │  HTTP stuff only: req/res, status codes, validation
├─────────────────────────────────┤
│ Service / Domain Logic           │  Business rules. Doesn't know what HTTP is.
├─────────────────────────────────┤
│ Repository / Data Access         │  Database queries. Doesn't know business rules.
└─────────────────────────────────┘
```

- **Controllers** are traffic cops — take request, hand off to service, return result
- **Services** take data in, return data out — no `req`/`res`, throw errors on failure
- **Repositories** abstract data access — return domain objects, not ORM entities

### 6.3 Domain-Driven Design Boundaries

- **Bounded contexts:** Each domain (auth, billing, inventory) has its own models, rules, and vocabulary
- **Aggregates:** Consistency boundaries — one entity is the root, others belong to it
- **Value objects:** Immutable, compared by value (Money, Address, Email)
- **Domain events:** Signal that something happened in the domain (OrderPlaced, PaymentFailed)
- **Anti-corruption layer:** Translate between bounded contexts

### 6.4 Folder Structure (Modular Monolith)

```
src/
├── modules/
│   ├── auth/
│   │   ├── auth.controller.ts
│   │   ├── auth.service.ts
│   │   ├── auth.repository.ts
│   │   ├── auth.schema.ts        # validation schemas
│   │   ├── auth.types.ts         # types/interfaces
│   │   └── auth.routes.ts
│   ├── billing/
│   │   ├── billing.controller.ts
│   │   ├── billing.service.ts
│   │   └── ...
│   └── inventory/
│       └── ...
├── shared/
│   ├── middleware/               # error handler, rate limiter, auth
│   ├── utils/                    # logger, date, crypto
│   ├── database/                 # connection, migrations
│   └── types/                    # shared types
└── app.ts                        # composition root
```

---

## Part 7: Error Handling & Observability

### 7.1 Structured Logging

**Always use structured (JSON) logging in production.**

```json
{
  "timestamp": "2026-07-12T09:30:00.123Z",
  "level": "error",
  "service": "order-service",
  "message": "Payment processing failed",
  "request_id": "req_abc123",
  "trace_id": "trace_xyz789",
  "user_id": "usr_456",
  "order_id": "ord_789",
  "error_code": "PAYMENT_DECLINED",
  "duration_ms": 1250
}
```

**Rules:**
- Include `request_id` and `trace_id` on every log entry for correlation
- Use log levels correctly: `debug` (dev only), `info` (lifecycle), `warn` (degraded), `error` (failures), `fatal` (crash)
- Never log secrets, tokens, passwords, PII
- Log at service boundaries (request in/out, job start/end)
- Use a structured logger (pino, zerolog, slog, structlog, Winston with JSON format)

### 7.2 Error Propagation

- **Custom error classes** with `code`, `statusCode`, `message`, `details`
- **Throw in services, catch in controllers** — global error middleware maps to HTTP response
- **Never swallow errors** — `catch (e) {}` is a bug. At minimum, log the error
- **Wrap errors with context** — `throw new DatabaseError('Failed to create order', { cause: e })`
- **Distinguish operational errors** (network failure, bad input) from programmer errors** (null reference, type error)

### 7.3 Health Checks

```
GET /health     → 200 { "status": "ok" }          (liveness — is the process alive?)
GET /ready      → 200 { "status": "ready" }       (readiness — can I handle requests?)
```

**Readiness checks should verify:**
- Database connection
- Cache connection
- Critical external dependencies
- Not currently shutting down

**Rules:**
- Health checks must be fast (< 1 second)
- Don't include sensitive information
- Use for orchestrator (Kubernetes, load balancer) decisions

### 7.4 Distributed Tracing

- Use **OpenTelemetry** (the CNCF standard) — not vendor-specific instrumentation
- Instrument at: HTTP entry, database calls, external API calls, message queue publish/consume
- Propagate trace context via W3C Trace Context headers (`traceparent`, `tracestate`)
- Use OTel Collector as a telemetry pipeline between app and backend
- Sample in production (1-10% of requests) to manage cost
- Use tail-based sampling for error capture (keep all error traces)

### 7.5 Metrics

**RED method (for services):**
- **R**ate — requests per second
- **E**rrors — error rate
- **D**uration — latency distribution (p50, p90, p99)

**USE method (for resources):**
- **U**tilization — CPU, memory, disk, network
- **S**aturation — queue depth, connection pool usage
- **E**rrors — disk errors, OOM kills

**Rules:**
- Use Prometheus exposition format (`/metrics` endpoint)
- Control cardinality — don't use high-cardinality labels (user IDs, request IDs)
- Alert on symptoms (error rate, latency) not causes (CPU, disk)
- Set SLOs and alert on SLO burn rate, not raw thresholds

### 7.6 Alerting

- Alert on: error rate > threshold for X minutes, p99 latency > SLO, health check failing
- Don't alert on: individual errors, high CPU (unless sustained), slow single request
- Include runbook link in every alert
- Route alerts to the right team (not a global Slack channel)
- Implement escalation policies (PagerDuty, Opsgenie)

---

## Part 8: Testing

### 8.1 Test Pyramid

```
        /\
       /e2e\        Few — slow, expensive, brittle
      /------\
     /integration\  Some — test service + DB together
    /------------\
   /    unit      \  Many — fast, isolated, deterministic
  /----------------\
```

### 8.2 Unit Tests

- Test business logic in isolation
- Mock external dependencies (database, APIs, queues)
- Test behavior, not implementation
- One assertion concept per test
- Name tests descriptively: `it('throws when email is already registered')`
- Use factories/builders for test data, not hardcoded fixtures
- Target: 80%+ coverage for domain logic, 40-60% for controllers

### 8.3 Integration Tests

- Test service + real database (use test database or testcontainers)
- Test API endpoints end-to-end through HTTP
- Test database migrations
- Test cache interactions
- Use transactions with rollback for isolation
- Target: cover all critical user flows

### 8.4 E2E Tests

- Test through the actual UI or API as a real user would
- Few in number — focus on critical paths (signup, purchase, key workflow)
- Run against a staging environment
- Use Playwright, Cypress, or similar
- Don't test edge cases here — that's unit/integration territory

### 8.5 Contract Testing

- For microservices: consumer-driven contract testing (Pact)
- For APIs: OpenAPI schema validation in CI
- Test that API responses match documented schema
- Run on every PR

### 8.6 Test Database Management

- Use a separate test database — never run tests against dev/prod
- Use testcontainers for integration tests (spin up real PostgreSQL in Docker)
- Run migrations before tests
- Clean state between tests (truncate or transaction rollback)
- Seed with factories, not SQL files

### 8.7 Load Testing

- Test before major releases and infrastructure changes
- Use k6, Locust, or Artillery
- Test normal load, peak load, and spike scenarios
- Measure: throughput, latency percentiles, error rate, resource utilization
- Find the breaking point and understand what breaks first

---

## Part 9: Deployment & DevOps

### 9.1 Environment Management

| Environment | Purpose | Data |
|---|---|---|
| **Local** | Development | Seeded mock data |
| **CI** | Automated tests | Ephemeral, testcontainers |
| **Staging** | Pre-production verification | Anonymized prod-like data |
| **Production** | Real users | Real data |

**Rules:**
- Staging should mirror production as closely as possible (same infra, same config, same deps)
- Never use production data in non-production environments without anonymization
- Use infrastructure as code for all environments

### 9.2 CI/CD Pipeline

```
Push → Lint → Type Check → Unit Tests → Build → Integration Tests → 
  → Security Scan → E2E Tests → Deploy to Staging → Smoke Tests → Deploy to Prod
```

- Run linting and type checking on every push
- Run unit tests on every push
- Run integration tests on PR merge
- Run E2E tests on staging deploy
- Run security scans (SAST, SCA, secrets) on every build
- Require green pipeline for merge to main
- Deploy automatically to staging on merge to main
- Deploy to production on tag/release or manual approval

### 9.3 Containerization

- Use multi-stage builds (build deps → compile → copy artifacts to slim runtime image)
- Use `.dockerignore` to exclude node_modules, .git, test files
- Pin base image versions (don't use `latest`)
- Run as non-root user
- Use distroless or alpine base images for smaller attack surface
- Set memory and CPU limits
- Use health checks in Dockerfile

### 9.4 Zero-Downtime Deployments

| Strategy | How | When |
|---|---|---|
| **Rolling** | Replace instances gradually | Default for stateless services |
| **Blue-Green** | Switch traffic between two environments | When you need instant rollback |
| **Canary** | Route small % of traffic to new version, monitor, ramp up | High-risk changes |
| **Feature flags** | Deploy code off, enable gradually | Decouple deploy from release |

**Rules:**
- Always support rollback
- Database migrations must be backward-compatible (expand-and-contract)
- Don't deploy on Fridays (unless you have weekend on-call)
- Monitor error rate and latency after every deploy
- Implement automatic rollback on error rate spike

### 9.5 Infrastructure as Code

- Use Terraform, Pulumi, or CDK for all infrastructure
- Version control all infrastructure definitions
- Review infrastructure changes via PRs
- Use separate state per environment
- Never make manual infrastructure changes in production

---

## Part 10: Modern Patterns

### 10.1 Serverless Functions

- Best for: event-driven, sporadic traffic, simple request-response
- Not for: long-running tasks, persistent connections, high-frequency
- Be aware of: cold starts, execution time limits, vendor lock-in
- Use for: webhooks, scheduled jobs, API endpoints with variable load

### 10.2 Edge Computing

- Run code close to users: Cloudflare Workers, Vercel Edge Functions, Deno Deploy
- Best for: auth, redirects, A/B testing, personalization, geolocation logic
- Limitations: limited runtime, no filesystem, execution time limits
- Use edge for middleware, origin for business logic

### 10.3 Event-Driven Architecture

- Services communicate via events (not direct calls)
- Use message broker: Kafka, RabbitMQ, NATS, EventBridge
- Events are immutable, append-only log
- Consumers are independent — failure in one doesn't block others
- Implement event versioning from day one
- Use schema registry for event contracts

### 10.4 Microservices vs Monolith

| Aspect | Monolith | Microservices |
|---|---|---|
| **Team size** | < 8 developers | > 8 developers, multiple teams |
| **Deployment** | Single unit | Independent per service |
| **Scaling** | Whole app | Per service |
| **Complexity** | Low operational, high code | High operational, lower code per service |
| **Data** | Shared database | Database per service |
| **Testing** | Simpler | Contract tests needed |
| **Observability** | Simpler | Distributed tracing required |

**2026 rule:** Start with a modular monolith. Extract services when you have a proven need. The cost of microservices (operational complexity, network failures, distributed transactions) is enormous — only pay it when the benefits clearly outweigh.

### 10.5 Real-Time (WebSockets, SSE)

| Protocol | When |
|---|---|
| **WebSockets** | Bidirectional real-time (chat, collaboration, gaming) |
| **SSE (Server-Sent Events)** | Server-to-client only (notifications, live updates) |
| **Long polling** | Fallback for environments that block WebSockets |

**Rules:**
- Authenticate the upgrade request
- Implement heartbeat/ping-pong for connection health
- Handle reconnection on client side
- Scale with a pub/sub backend (Redis Pub/Sub, NATS)
- Set connection limits per user

### 10.6 Streaming Responses

- Use for: large responses, LLM token streaming, progress updates
- Use `Transfer-Encoding: chunked` or SSE
- Set appropriate `Content-Type` and buffer sizes
- Handle client disconnection gracefully
- Backpressure: don't produce faster than consumer can handle

---

## Part 11: Anti-Patterns to Avoid

### Architecture Anti-Patterns

| Anti-Pattern | Problem | Fix |
|---|---|---|
| **God Object/Class** | One class does everything (>500 lines, >20 methods, >10 deps) | Extract by domain boundary, not by layer |
| **Fat Controllers** | Business logic in route handlers, coupled to req/res | Move to service layer. Controller = traffic cop only |
| **Leaky Abstractions** | ORM errors, internal table names, pagination cursors exposed to clients | Map domain to API, not implementation. Opaque tokens |
| **Premature Microservices** | Splitting monolith before proving the need | Start with modular monolith, extract when justified |
| **Premature Optimization** | Caching/sharding before measuring | Measure first with EXPLAIN ANALYZE, optimize the proven bottleneck |
| **Anemic Domain Model** | Domain objects are just data bags, all logic in services | Move behavior into domain entities. Rich domain models |
| **Circular Dependency** | ServiceA → ServiceB → ServiceA | Extract shared dependency, use events, or restructure boundaries |

### API Anti-Patterns

| Anti-Pattern | Problem | Fix |
|---|---|---|
| **Chatty APIs** | Client makes 10 calls for one screen | Batch endpoints, GraphQL, embed related resources |
| **Missing Pagination** | `GET /users` returns 500K records | Always paginate. Cursor-based for large datasets |
| **Inconsistent Error Format** | Different error shapes per endpoint | Global error handler with consistent envelope |
| **Magic Strings/Numbers** | `status === 'pending'` scattered everywhere | Use enums/constants. Single source of truth |
| **Hardcoded Config** | URLs, limits, timeouts in code | Environment variables, config files, secrets manager |
| **Synchronous Blocking** | Email send in request handler (3s response time) | Move to background job queue |
| **No Idempotency** | Duplicate payment on retry | Accept Idempotency-Key header, cache response |
| **Versioning by URL only** | `v1` in URL but no real versioning strategy | Plan versioning from day one, use additive changes |
| **Exposing Internal IDs** | Auto-increment IDs leak record count | Use UUIDs or opaque identifiers externally |

### Data Anti-Patterns

| Anti-Pattern | Problem | Fix |
|---|---|---|
| **N+1 Queries** | 1 query for list + N queries for relations | Eager loading, DataLoader, JOINs |
| **SELECT *** | Transfers unnecessary data | Select only required columns |
| **Missing Indexes** | Full table scans on common queries | Index filter/join/sort columns |
| **Over-Indexing** | Every column indexed, writes slow | Index only what queries need. Drop unused |
| **EAV Pattern** | Entity-Attribute-Value table, unqueryable | JSONB columns or proper normalization |
| **God Table** | 50+ column table | Vertical partition by access pattern |
| **Polymorphic Associations** | Breaks referential integrity | Explicit relationships or table-per-type |
| **No Migration Strategy** | Schema changes cause downtime | Expand-and-contract pattern |

### Security Anti-Patterns

| Anti-Pattern | Problem | Fix |
|---|---|---|
| **Storing Secrets in Code** | API keys in source, committed to git | Environment variables, secrets manager |
| **`*` CORS with Credentials** | Any origin can make authenticated requests | Whitelist specific origins |
| **JWT in localStorage** | XSS can steal tokens | httpOnly cookies |
| **No Rate Limiting** | Brute force, DoS, resource exhaustion | Rate limit at gateway/middleware |
| **Unvalidated Input** | Injection, data corruption, unexpected behavior | Schema validation at boundary |
| **String-Concatenated SQL** | SQL injection | Parameterized queries always |

---

## Execution Instructions for Cascade

When this skill is activated during backend development:

1. **Read the project's `research.md`** if it exists — it contains the tech stack and architecture decisions
2. **Assess project complexity** — is this a simple CRUD app, a complex domain, or a distributed system?
3. **Choose architecture based on project needs** — not on what's trendy. Modular monolith by default.
4. **Follow the three-layer rule** — controllers, services, repositories. Never mix concerns.
5. **Check every decision against the anti-patterns catalog** in Part 11
6. **Implement observability from day one** — structured logging, health checks, metrics
7. **Write tests at the right level** — unit for domain logic, integration for API + DB, e2e for critical paths
8. **Design APIs for the client** — consistent, paginated, well-documented, idempotent where needed
9. **Security is not a feature** — it's a baseline. Input validation, parameterized queries, proper auth, security headers.
10. **When in doubt, choose the simpler solution** — you can always add complexity later. Removing it is much harder.
