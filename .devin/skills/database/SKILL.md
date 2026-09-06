---
name: database
description: Comprehensive database design & optimization workflow — selection, schema, indexing, query optimization, scaling, backups, monitoring, and security
---

# Database Design & Optimization Workflow

This workflow applies the **Database Design & Optimization Skill** (`~/.codeium/windsurf/skills/database-design-optimization.md`) to design and optimize databases.

## When to Run
- When designing a new database schema
- When the user says `/database` or asks about database optimization
- When optimizing slow queries
- When planning database scaling
- When setting up database monitoring and backups

---

## Step 1: Assess Database Needs

1. Read the project context — data requirements, expected scale, query patterns
2. Identify data entities and their relationships
3. Estimate data volume — current and projected
4. Identify query patterns — what will be queried frequently?
5. Determine consistency requirements — ACID? eventual? 
6. Check for special needs — full-text search, time-series, vector/AI, graph

## Step 2: Choose Database(s)

1. Select primary database: PostgreSQL (default for most projects)
2. Select supplementary databases if needed:
   - Redis: caching, sessions, rate limiting, queues
   - Meilisearch/Typesense: full-text search
   - TimescaleDB: time-series data
   - pgvector: vector/AI applications
3. Document the rationale for each choice
4. Set up database instances (local dev, staging, production)

## Step 3: Design Schema

1. Create entity-relationship diagram (ERD) for all entities
2. Define tables with naming conventions (snake_case, plural table names)
3. Choose primary keys: UUID v7 (recommended) or BIGSERIAL
4. Add standard columns: `id`, `created_at`, `updated_at`, `deleted_at`
5. Define data types: TEXT, BIGINT, NUMERIC for money, TIMESTAMPTZ, JSONB, BOOLEAN
6. Define foreign keys with appropriate ON DELETE actions
7. Add constraints: UNIQUE, CHECK, NOT NULL
8. Normalize to 3NF — denormalize only when measured performance demands
9. Use JSONB for flexible/variable structure data (metadata, settings)
10. Use enums for status fields with known values

## Step 4: Define Indexes

1. Index all foreign keys — not automatic in PostgreSQL
2. Index frequently filtered columns (WHERE clause columns)
3. Index frequently sorted columns (ORDER BY columns)
4. Create composite indexes with correct column order (most selective first)
5. Create partial indexes for common filtered subsets (WHERE deleted_at IS NULL)
6. Create covering indexes (INCLUDE) for index-only scans
7. Create expression indexes for case-insensitive lookups (LOWER(email))
8. Create GIN indexes for JSONB, array, and full-text search columns
9. Document each index and the query it serves
10. Don't over-index — every index slows writes

## Step 5: Implement Migrations

1. Set up migration tool (Prisma Migrate, Drizzle Kit, node-pg-migrate, Flyway)
2. Create initial schema migration
3. Ensure migrations are versioned, reversible, and tested
4. Never apply migrations directly to production — test in staging first
5. Use `CREATE INDEX CONCURRENTLY` for production index creation (no lock)
6. Document migration process and rollback procedures

## Step 6: Optimize Queries

1. Run `EXPLAIN (ANALYZE, BUFFERS)` on key queries
2. Identify sequential scans on large tables — add indexes
3. Eliminate N+1 queries — use JOINs or eager loading
4. Replace OFFSET pagination with cursor (keyset) pagination
5. Select only needed columns — avoid `SELECT *`
6. Add WHERE clauses to reduce result sets before aggregation
7. Use materialized views for expensive aggregations
8. Avoid functions on indexed columns in WHERE clauses
9. Use IN instead of OR for multiple values
10. Monitor with `pg_stat_statements` — find and optimize slowest queries

## Step 7: Set Up Connection Management

1. Configure connection pooling (PgBouncer for serverless, app-level pool otherwise)
2. Set appropriate pool size (10-20 per instance)
3. Set `statement_timeout` (30s) and `idle_in_transaction_session_timeout` (60s)
4. For serverless: use PgBouncer transaction mode or HTTP-based proxy
5. Monitor connection usage — watch for connection exhaustion

## Step 8: Implement Caching

1. Set up Redis for caching hot data
2. Cache expensive query results with TTL
3. Cache session data and rate limiting counters
4. Create materialized views for expensive aggregations
5. Implement cache invalidation strategy (TTL, event-based, write-through)
6. Don't cache everything — cache what's expensive and frequently accessed

## Step 9: Plan Scaling Strategy

1. **Vertical:** Upgrade CPU/RAM/disk when approaching limits
2. **Read replicas:** Set up for read-heavy workloads — route reads to replica
3. **Partitioning:** Partition large tables by date (range) or category (list)
4. **Sharding:** Plan sharding strategy for very large datasets — choose shard key carefully
5. **CDN/Edge:** Cache static data at edge for global users
6. Document scaling plan and triggers for each scaling action

## Step 10: Set Up Backups & Recovery

1. Configure automated daily backups (pg_dump or cloud-managed)
2. Enable WAL archiving for point-in-time recovery
3. Set backup retention policy (e.g., 30 days)
4. Test backup restoration regularly — in staging environment
5. Document recovery procedures and RTO/RPO targets
6. Set up cross-region backup replication for disaster recovery

## Step 11: Configure Monitoring

1. Enable `pg_stat_statements` for query performance tracking
2. Set up slow query logging
3. Monitor cache hit ratio (target > 99%)
4. Monitor connection usage (active vs max)
5. Monitor replication lag (if using replicas)
6. Monitor table bloat and vacuum status
7. Set up alerts: connection exhaustion, slow queries, disk space, replication lag
8. Create database dashboard with key metrics

## Step 12: Secure the Database

1. Create separate database users: app (read-write), analytics (read-only), admin (migrations)
2. Grant least privilege — app user should not have DDL permissions
3. Require SSL for all connections
4. Enable encryption at rest (disk encryption, cloud-managed)
5. Use `pgcrypto` for column-level encryption if needed
6. Always use parameterized queries — prevent SQL injection
7. Store credentials in secrets manager — not in source code
8. Audit access logs periodically

## Step 13: Document & Maintain

1. Create schema diagram (ERD)
2. Document index strategy — which indexes serve which queries
3. Document scaling plan and triggers
4. Document backup and recovery procedures
5. Create database style guide (naming conventions, data types, patterns)
6. Schedule regular maintenance: vacuum, analyze, index review, slow query audit
