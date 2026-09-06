---
name: Database Design & Optimization Skill
description: Comprehensive methodology for designing and optimizing databases — 2025-2026 practices with PostgreSQL focus, schema design, indexing strategies, query optimization, scaling, and NoSQL selection
version: 1.0.0
tags: [database, postgresql, schema-design, indexing, query-optimization, scaling, nosql, migrations]
---

# Database Design & Optimization Skill

## Purpose
This skill provides a comprehensive methodology for designing and optimizing databases across any kind of web project. It reflects **modern 2025-2026 practices** — PostgreSQL as the default relational database, thoughtful schema design, evidence-based indexing, query optimization with EXPLAIN ANALYZE, and scaling strategies from read replicas to sharding.

## Core Philosophy

**Schema design is the foundation.** A well-designed schema prevents bugs, enables performance, and supports evolution. A poorly designed schema creates technical debt that compounds over time. Get the schema right early — it's the most expensive thing to change later.

**The #1 rule:** Measure before optimizing. EXPLAIN ANALYZE tells you what's actually slow. Don't add indexes based on guesses — add them based on query patterns. Don't denormalize based on assumptions — denormalize based on measured bottlenecks.

---

## Part 1: Database Selection

### 1.1 Relational (PostgreSQL)
- **Default choice:** ACID compliance, SQL, mature ecosystem
- **PostgreSQL 17/18:** JSONB, full-text search, partitioning, logical replication
- **Use for:** Most applications — user data, transactions, relationships, complex queries
- **Alternatives:** MySQL/MariaDB (simpler, less features), SQLite (embedded, local)

### 1.2 Document (MongoDB, CouchDB)
- **Use for:** CMS, content-heavy apps, flexible schemas, rapid prototyping
- **Trade-off:** No joins, no transactions (multi-document), eventual consistency
- **When to choose over PostgreSQL:** Schema is truly flexible and relationships are minimal

### 1.3 Key-Value (Redis, DynamoDB)
- **Redis:** In-memory — caching, sessions, rate limiting, queues, real-time
- **DynamoDB:** Managed NoSQL — simple key-value lookups at massive scale
- **Use for:** Caching, sessions, leaderboards, real-time features

### 1.4 Search (Elasticsearch, Meilisearch, Typesense)
- **Use for:** Full-text search, faceted search, typo-tolerance, relevance scoring
- **PostgreSQL FTS:** Good enough for many use cases — use external search for scale
- **Meilisearch/Typesense:** Lighter alternatives to Elasticsearch

### 1.5 Time-Series (TimescaleDB, InfluxDB)
- **TimescaleDB:** PostgreSQL extension — time-series on relational data
- **Use for:** IoT, monitoring, analytics, metrics
- **InfluxDB:** Purpose-built time-series — simpler but less flexible

### 1.6 Graph (Neo4j, Amazon Neptune)
- **Use for:** Social networks, recommendation engines, fraud detection
- **When relationships are the data:** When traversing relationships is the primary query

### 1.7 Vector (pgvector, Pinecone, Weaviate)
- **pgvector:** PostgreSQL extension — vector similarity search in relational database
- **Use for:** AI/ML applications, semantic search, RAG, recommendations
- **Pinecone/Weaviate:** Managed vector databases for large-scale AI applications

### 1.8 Decision Matrix

| Need | Database |
|---|---|
| General purpose | PostgreSQL |
| Caching/sessions | Redis |
| Full-text search | PostgreSQL FTS → Meilisearch → Elasticsearch |
| Time-series | TimescaleDB (PostgreSQL) |
| Flexible schema | PostgreSQL JSONB → MongoDB |
| Graph relationships | PostgreSQL (with recursive CTEs) → Neo4j |
| Vector/AI | pgvector (PostgreSQL) → Pinecone |
| Massive key-value | DynamoDB |

---

## Part 2: Schema Design Principles

### 2.1 Naming Conventions
```sql
-- Tables: snake_case, plural
CREATE TABLE users ();
CREATE TABLE order_items ();

-- Columns: snake_case, singular
id, email, created_at, updated_at

-- Foreign keys: <table_singular>_id
user_id, order_id, product_id

-- Indexes: idx_<table>_<columns>
idx_users_email
idx_order_items_order_id_product_id

-- Constraints: uq_<table>_<columns>, fk_<table>_<ref_table>
uq_users_email
fk_order_items_orders
```

### 2.2 Primary Keys
```sql
-- UUID v7 (recommended — sortable + unique)
id UUID PRIMARY KEY DEFAULT uuid_generate_v7()

-- Bigserial (auto-increment — simpler, smaller)
id BIGSERIAL PRIMARY KEY

-- Don't use: SERIAL (int — only 2B rows), UUID v4 (random — index fragmentation)
```

### 2.3 Standard Columns
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
  email TEXT NOT NULL,
  -- ... other columns
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ -- soft delete
);
```
- **`created_at`:** When the row was created — never changes
- **`updated_at`:** When the row was last modified — update via trigger
- **`deleted_at`:** Soft delete — NULL means not deleted
- **`TIMESTAMPTZ`:** Always use timestamp with time zone — never `TIMESTAMP` without TZ

### 2.4 Data Types
```sql
-- Strings
TEXT -- preferred over VARCHAR(n) — no length limit, same performance
VARCHAR(255) -- only when you need a constraint

-- Numbers
BIGINT -- for IDs and counters (int overflow is real)
INTEGER -- for small bounded numbers
NUMERIC(10,2) -- for money (exact decimal)
DECIMAL -- same as NUMERIC

-- Booleans
BOOLEAN -- true/false

-- Dates/Times
DATE -- date only
TIME -- time only
TIMESTAMPTZ -- date + time + timezone (always use this)

-- JSON
JSONB -- binary JSON, indexable, efficient (NOT JSON — text-based, slow)

-- Arrays
TEXT[] -- array of text (PostgreSQL-specific)
INTEGER[] -- array of integers

-- Enums
CREATE TYPE order_status AS ENUM ('pending', 'paid', 'shipped', 'delivered', 'cancelled');
```

### 2.5 Foreign Keys
```sql
CREATE TABLE orders (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  status order_status NOT NULL DEFAULT 'pending',
  total NUMERIC(10,2) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
```
- **Always use foreign keys:** Enforce referential integrity at the database level
- **ON DELETE:** `CASCADE` (delete children), `SET NULL` (keep children), `RESTRICT` (prevent)
- **ON UPDATE:** `CASCADE` (update children's FKs) — usually default
- **Index FKs:** Foreign keys are NOT automatically indexed — add an index

### 2.6 Constraints
```sql
-- Unique constraint
CREATE UNIQUE INDEX uq_users_email ON users(email);

-- Check constraint
ALTER TABLE products ADD CONSTRAINT check_price_positive CHECK (price > 0);

-- Not null
ALTER TABLE users ALTER COLUMN email SET NOT NULL;

-- Exclusion constraint (prevent overlapping ranges)
ALTER TABLE bookings ADD EXCLUDE USING gist (room_id WITH =, time_range WITH &&);
```

### 2.7 Normalization (3NF)
- **1NF:** Atomic values, no repeating groups
- **2NF:** No partial dependencies (all non-key columns depend on the whole key)
- **3NF:** No transitive dependencies (non-key columns don't depend on other non-key columns)
- **When to denormalize:** Only when measured performance demands it — not preemptively

### 2.8 JSONB Columns (Semi-Structured Data)
```sql
-- Store flexible/variable structure data
ALTER TABLE events ADD COLUMN metadata JSONB DEFAULT '{}';

-- Query JSONB
SELECT * FROM events WHERE metadata->>'source' = 'web';
SELECT * FROM events WHERE metadata @> '{"type": "click"}';

-- Index JSONB
CREATE INDEX idx_events_metadata ON events USING gin(metadata);
```
- **Use for:** Metadata, settings, variable attributes, API payloads
- **Don't use for:** Data that should be queried/related normally — use columns
- **Index:** GIN index for JSONB key lookups

---

## Part 3: Indexing Strategy

### 3.1 Index Types

| Type | Use Case | Example |
|---|---|---|
| **B-tree** | Default — equality, range, sort | `WHERE email = 'x'`, `WHERE created_at > '2025-01-01'` |
| **GIN** | Array, JSONB, full-text search | `WHERE tags @> ARRAY['tag1']`, `WHERE metadata @> '{}'` |
| **GiST** | Geometric, range, nearest-neighbor | `WHERE location <-> point < 1000` |
| **BRIN** | Large tables with natural ordering | Time-series data ordered by time |
| **Hash** | Equality only (rarely needed) | `WHERE id = 123` |
| **Partial** | Index subset of rows | `WHERE deleted_at IS NULL` |
| **Covering** | Include extra columns | `INCLUDE (name, email)` — index-only scan |
| **Expression** | Index on expression | `LOWER(email)` |
| **Unique** | Enforce uniqueness | `CREATE UNIQUE INDEX` |

### 3.2 When to Index
- **Foreign keys:** Always index FKs — joins and cascading deletes need them
- **Frequently filtered columns:** Columns in WHERE clauses
- **Frequently sorted columns:** Columns in ORDER BY clauses
- **Frequently joined columns:** Columns in JOIN conditions
- **Unique constraints:** Email, slug, etc.
- **Don't over-index:** Every index slows writes — index what you query

### 3.3 Composite Index Column Order
```sql
-- Good: most selective first, or match query order
CREATE INDEX idx_orders_user_status_created ON orders(user_id, status, created_at);

-- Query that uses this index:
SELECT * FROM orders WHERE user_id = 123 AND status = 'paid' ORDER BY created_at DESC;

-- This index also serves:
SELECT * FROM orders WHERE user_id = 123; -- uses first column
SELECT * FROM orders WHERE user_id = 123 AND status = 'paid'; -- uses first two
-- But NOT:
SELECT * FROM orders WHERE status = 'paid'; -- can't use this index (leftmost prefix)
```

### 3.4 Partial Indexes
```sql
-- Only index active users — smaller, faster
CREATE INDEX idx_users_active_email ON users(email) WHERE deleted_at IS NULL;

-- Only index unpaid orders
CREATE INDEX idx_orders_unpaid ON orders(created_at) WHERE status = 'pending';
```

### 3.5 Covering Indexes
```sql
-- Include columns to enable index-only scan
CREATE INDEX idx_users_email ON users(email) INCLUDE (name, avatar_url);

-- Query can be served entirely from index:
SELECT email, name, avatar_url FROM users WHERE email = 'x@example.com';
```

### 3.6 Expression Indexes
```sql
-- Case-insensitive email lookup
CREATE INDEX idx_users_email_lower ON users(LOWER(email));

SELECT * FROM users WHERE LOWER(email) = 'user@example.com';
```

### 3.7 Index Maintenance
```sql
-- Find unused indexes
SELECT * FROM pg_stat_user_indexes WHERE idx_scan = 0;

-- Find duplicate indexes
SELECT * FROM pg_stat_user_indexes WHERE idx_scan = 0 AND indexname LIKE 'idx_%';

-- Rebuild fragmented indexes
REINDEX INDEX CONCURRENTLY idx_users_email;

-- Analyze table statistics
ANALYZE users;
```

---

## Part 4: Query Optimization

### 4.1 EXPLAIN ANALYZE
```sql
EXPLAIN (ANALYZE, BUFFERS) SELECT * FROM orders WHERE user_id = 123 ORDER BY created_at DESC LIMIT 10;
```
- **Seq Scan:** Full table scan — bad for large tables, needs index
- **Index Scan:** Using index — good
- **Index Only Scan:** All data from index — best
- **Bitmap Scan:** Index + filter — good for selective queries
- **Sort:** Sorting in memory — bad if not using index for sort
- **Hash Join:** Good for equality joins on large datasets
- **Nested Loop:** Good for small datasets, bad for large
- **Buffers:** Shows shared/disk I/O — high disk reads = cache miss

### 4.2 Common Query Anti-Patterns
```sql
-- Bad: SELECT * (fetches unnecessary columns)
SELECT * FROM users WHERE active = true;
-- Good: Select only needed columns
SELECT id, name, email FROM users WHERE active = true;

-- Bad: OFFSET pagination (gets slower as offset grows)
SELECT * FROM items ORDER BY id OFFSET 10000 LIMIT 20;
-- Good: Cursor (keyset) pagination
SELECT * FROM items WHERE id > 10000 ORDER BY id LIMIT 20;

-- Bad: N+1 queries (1 + N queries)
users = SELECT * FROM users;
for each user: SELECT * FROM posts WHERE user_id = user.id;
-- Good: Single query with JOIN or eager loading
SELECT u.*, p.* FROM users u LEFT JOIN posts p ON p.user_id = u.id;

-- Bad: OR in WHERE (may not use index)
SELECT * FROM orders WHERE user_id = 1 OR user_id = 2;
-- Good: IN
SELECT * FROM orders WHERE user_id IN (1, 2);

-- Bad: Function on indexed column (prevents index use)
SELECT * FROM users WHERE EXTRACT(YEAR FROM created_at) = 2025;
-- Good: Range query (uses index)
SELECT * FROM users WHERE created_at >= '2025-01-01' AND created_at < '2026-01-01';

-- Bad: LIKE with leading wildcard (can't use index)
SELECT * FROM users WHERE name LIKE '%john%';
-- Good: Full-text search or trigram index
SELECT * FROM users WHERE name ILIKE '%john%'; -- with pg_trgm index
```

### 4.3 JOIN Optimization
```sql
-- Good: Join with indexed columns
SELECT o.*, u.name FROM orders o JOIN users u ON u.id = o.user_id WHERE u.active = true;

-- Good: Filter early
SELECT o.* FROM orders o WHERE o.status = 'paid' AND o.created_at > '2025-01-01'
JOIN users u ON u.id = o.user_id WHERE u.active = true;

-- Bad: Cartesian product (missing join condition)
SELECT * FROM orders, users; -- without WHERE clause
```

### 4.4 Pagination Strategies
```sql
-- Keyset (cursor) pagination — constant time, recommended
SELECT * FROM items WHERE id > :last_id ORDER BY id LIMIT 20;

-- Keyset with multiple columns
SELECT * FROM items WHERE (created_at, id) < (:last_created, :last_id) ORDER BY created_at DESC, id DESC LIMIT 20;

-- Page-based (OFFSET) — only for small offsets
SELECT * FROM items ORDER BY created_at DESC LIMIT 20 OFFSET 0;
```

### 4.5 Aggregation Optimization
```sql
-- Bad: Aggregating all rows
SELECT user_id, COUNT(*) FROM orders GROUP BY user_id;

-- Good: With WHERE clause to reduce rows
SELECT user_id, COUNT(*) FROM orders WHERE created_at > '2025-01-01' GROUP BY user_id;

-- Materialized view for expensive aggregations
CREATE MATERIALIZED VIEW order_stats AS
SELECT user_id, COUNT(*) as total_orders, SUM(total) as total_spent
FROM orders GROUP BY user_id;

REFRESH MATERIALIZED VIEW CONCURRENTLY order_stats;
```

---

## Part 5: Connection Management

### 5.1 Connection Pooling
- **PgBouncer:** Lightweight connection pooler — transaction mode for serverless
- **Application-level:** Pool in your ORM/app (Prisma, Drizzle, pg-pool)
- **Pool size:** 10-20 per app instance — don't over-provision
- **Timeout:** Set `statement_timeout` (30s) and `idle_in_transaction_session_timeout` (60s)

### 5.2 Serverless Considerations
- **Problem:** Each function invocation may create a new connection
- **Solution:** PgBouncer in transaction mode, or HTTP-based database proxy
- **Neon:** Serverless PostgreSQL with connection pooling built-in
- **Supabase:** PgBouncer Supavisor included
- **Prisma:** Data proxy or Accelerate for serverless connection pooling

### 5.3 Transaction Management
```sql
-- Good: Explicit transaction with rollback
BEGIN;
UPDATE accounts SET balance = balance - 100 WHERE id = 1;
UPDATE accounts SET balance = balance + 100 WHERE id = 2;
COMMIT; -- or ROLLBACK on error

-- Isolation levels
SET TRANSACTION ISOLATION LEVEL READ COMMITTED; -- default, good for most
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; -- strictest, for critical operations
```

---

## Part 6: Scaling Strategies

### 6.1 Vertical Scaling
- **More CPU/RAM:** Simplest scaling — upgrade database server
- **Faster disks:** NVMe SSDs for I/O-bound workloads
- **Limit:** Single machine has a ceiling

### 6.2 Read Replicas
```sql
-- Primary: writes + reads
-- Replica: reads only
-- Application: route reads to replica, writes to primary
```
- **Setup:** Streaming replication (PostgreSQL built-in)
- **Replication lag:** Monitor — reads may see stale data (milliseconds to seconds)
- **Use case:** Read-heavy workloads, reporting, analytics

### 6.3 Partitioning
```sql
-- Range partitioning by date
CREATE TABLE events (id UUID, created_at TIMESTAMPTZ, data JSONB)
PARTITION BY RANGE (created_at);

CREATE TABLE events_2025_01 PARTITION OF events
FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');

CREATE TABLE events_2025_02 PARTITION OF events
FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');
```
- **Range:** By date — most common for time-series
- **List:** By category (e.g., partition by region)
- **Hash:** Even distribution by hash
- **Benefit:** Smaller indexes per partition, easier maintenance, drop old partitions

### 6.4 Sharding
- **Horizontal partitioning:** Split data across multiple database instances
- **Shard key:** Choose carefully — even distribution, query locality
- **Application-level:** App routes queries to correct shard
- **PostgreSQL:** Use `postgres_fdw` or Citus extension
- **Trade-off:** Cross-shard queries are expensive, joins across shards are hard

### 6.5 Caching Layers
```
Request → Redis cache → Database
         (hit) → return     (miss) → query DB → cache result → return
```
- **Redis:** Cache expensive query results, session data, computed values
- **Cache invalidation:** TTL-based, event-based (invalidate on write), or write-through
- **Don't cache everything:** Cache what's expensive and frequently accessed

---

## Part 7: Full-Text Search

### 7.1 PostgreSQL Full-Text Search
```sql
-- Create tsvector column
ALTER TABLE articles ADD COLUMN search_vector tsvector
GENERATED ALWAYS AS (
  setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
  setweight(to_tsvector('english', coalesce(body, '')), 'B')
) STORED;

-- GIN index
CREATE INDEX idx_articles_search ON articles USING gin(search_vector);

-- Search
SELECT * FROM articles WHERE search_vector @@ to_tsquery('english', 'react & hooks');
SELECT * FROM articles WHERE search_vector @@ plainto_tsquery('english', 'react hooks');

-- Ranking
SELECT *, ts_rank(search_vector, query) as rank
FROM articles, to_tsquery('english', 'react & hooks') query
WHERE search_vector @@ query
ORDER BY rank DESC;
```

### 7.2 When to Use External Search
- **PostgreSQL FTS:** Good up to ~1M rows, simple search
- **Meilisearch/Typesense:** Good for typo-tolerance, instant search, faceted search
- **Elasticsearch:** Good for complex search, aggregations, large scale
- **Algolia:** Managed, fast, expensive

---

## Part 8: Backup & Recovery

### 8.1 Backup Strategy
- **pg_dump:** Logical backup — good for small databases
- **pg_basebackup:** Physical backup — good for point-in-time recovery
- **WAL archiving:** Archive WAL files for point-in-time recovery
- **Cloud:** Automated backups (RDS, Cloud SQL, Supabase, Neon)
- **Frequency:** Daily full backup + continuous WAL archiving

### 8.2 Point-in-Time Recovery (PITR)
```bash
# Configure WAL archiving
archive_mode = on
archive_command = 'aws s3 cp %p s3://backup-bucket/wal/%f'

# Restore to specific point in time
pg_basebackup -D /var/lib/postgresql/recovery
# Configure recovery_target_time = '2025-01-15 14:30:00'
```

### 8.3 Testing Backups
- **Restore regularly:** Test backup restoration — a backup you can't restore is not a backup
- **Restore to different environment:** Verify in staging
- **Automate:** Script the restore process and test it in CI

---

## Part 9: Monitoring & Maintenance

### 9.1 Key Metrics
- **Connections:** Active vs max — watch for connection exhaustion
- **Query performance:** Slow query log, `pg_stat_statements`
- **Cache hit ratio:** `shared_buffers` cache hit ratio — should be > 99%
- **Index usage:** Unused indexes waste space and slow writes
- **Table bloat:** Dead tuples — vacuum regularly
- **Replication lag:** Lag between primary and replicas

### 9.2 pg_stat_statements
```sql
-- Enable
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- Find slowest queries
SELECT query, calls, mean_exec_time, total_exec_time, rows
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 10;
```

### 9.3 Vacuum and Analyze
```sql
-- Vacuum: reclaim space from dead tuples
VACUUM (VERBOSE, ANALYZE) users;

-- Autovacuum: should be enabled by default
-- Tune autovacuum settings for large tables
ALTER TABLE large_table SET (autovacuum_vacuum_scale_factor = 0.05);
```

### 9.4 Regular Maintenance Tasks
- **Daily:** Check backups, monitor slow queries
- **Weekly:** Review index usage, check table bloat
- **Monthly:** Analyze query patterns, review partitioning needs
- **Quarterly:** Full database audit, review scaling needs

---

## Part 10: Security

### 10.1 Access Control
- **Principle of least privilege:** App user should only have INSERT, UPDATE, SELECT, DELETE on app tables
- **No DDL for app user:** Don't allow CREATE, ALTER, DROP for application user
- **Separate users:** Read-only user for analytics, read-write for app, admin for migrations
- **SSL:** Require SSL for all connections — `sslmode=require`

### 10.2 Data Encryption
- **At rest:** Enable disk encryption (LUKS, EBS encryption, Cloud SQL encryption)
- **In transit:** SSL/TLS for all connections
- **Column-level:** `pgcrypto` extension for encrypting specific columns
- **Backup encryption:** Encrypt backups at rest

### 10.3 SQL Injection Prevention
- **Parameterized queries:** Always use parameterized queries — never string concatenation
- **ORM:** Use ORM's query builder — don't write raw SQL with user input
- **Validation:** Validate input before using in queries
- **Least privilege:** Limit damage if injection occurs

---

## Execution Instructions for Cascade

When this skill is activated for database design & optimization:

1. **Read the project context** — data requirements, scale, query patterns
2. **Choose database** — PostgreSQL (default), Redis (cache), search engine (if needed)
3. **Design schema** — naming conventions, data types, constraints, relationships
4. **Define indexes** — based on expected query patterns, not guesses
5. **Implement migrations** — versioned, reversible, tested
6. **Optimize queries** — EXPLAIN ANALYZE, eliminate N+1, cursor pagination
7. **Set up connection pooling** — PgBouncer or app-level pool
8. **Implement caching** — Redis for hot data, materialized views for aggregations
9. **Plan scaling** — read replicas, partitioning, sharding strategy
10. **Set up backups** — pg_dump + WAL archiving, test restoration
11. **Configure monitoring** — pg_stat_statements, slow query log, cache hit ratio
12. **Secure the database** — least privilege, SSL, encryption, parameterized queries
13. **Document** — schema diagram, index strategy, scaling plan, backup procedures
