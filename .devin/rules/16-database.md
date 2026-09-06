# Rule: Database Design & Optimization for All Projects

**ALWAYS** apply the Database Design & Optimization skill and workflow when designing or optimizing databases. Schema design is the foundation — get it right early, as it's the most expensive thing to change later.

## Skill
`~/.codeium/windsurf/skills/database-design-optimization.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/database.md` — invoke with `/database`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/database-engineer.md` (parent: Data Engineer)

## How to follow this rule:
1. When designing or optimizing databases, invoke the `/database` workflow
2. Follow the workflow steps in order: Assess → Choose → Schema → Indexes → Migrations → Queries → Connections → Caching → Scaling → Backups → Monitoring → Security → Document
3. Always use PostgreSQL as default relational database — with Redis for caching
4. Always measure before optimizing — use EXPLAIN ANALYZE, not guesses
5. Always index foreign keys — they are NOT automatically indexed
6. Always use cursor (keyset) pagination — not OFFSET for large datasets
7. Always use parameterized queries — prevent SQL injection
8. Always set up backups with tested restoration and WAL archiving for PITR

## When this rule applies:
- Designing a new database schema
- Optimizing slow queries or database performance
- Planning database scaling
- Setting up database monitoring and backups
- User asks about database design or optimization

## When this rule does NOT apply:
- Projects with no database (static sites)
- User explicitly says to skip database optimization
