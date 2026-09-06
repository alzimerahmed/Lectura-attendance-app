---
name: migration
description: Comprehensive migration & refactoring workflow — strategy, safety nets, incremental execution, codemods, monitoring, and cleanup
---

# Migration & Refactoring Workflow

This workflow applies the **Migration & Refactoring Skill** (`~/.codeium/windsurf/skills/migration-refactoring.md`) to safely migrate and refactor codebases.

## When to Run
- When migrating frameworks, libraries, or databases
- When the user says `/migration` or asks about refactoring
- When modernizing a legacy codebase
- When restructuring application architecture
- When doing a large-scale code transformation

---

## Step 1: Assess & Plan

1. Read the codebase — understand current architecture, dependencies, test coverage
2. Identify what needs to change — framework, library, schema, architecture, patterns
3. Assess risk — impact on users, business, other systems
4. Check test coverage — if low, plan to add tests before refactoring
5. Choose migration strategy: strangler fig (most common), parallel run, expand and contract, branch by abstraction
6. Break migration into small, reversible steps — each step independently deployable
7. Document the migration plan with timeline and milestones

## Step 2: Build Safety Nets

1. Write characterization tests if none exist — capture current behavior
2. Ensure integration tests cover external contracts (APIs, schemas)
3. Set up snapshot tests for key outputs
4. Verify CI pipeline catches regressions
5. Set up monitoring — error rate, performance, user feedback
6. Prepare rollback strategy — every step must be reversible

## Step 3: Set Up Feature Flags

1. Install feature flag system (PostHog, GrowthBook, custom)
2. Create flags for migration toggles
3. Implement dual-path code: old implementation + new implementation
4. Start with flag off (old implementation active)
5. Plan gradual rollout: 1% → 10% → 50% → 100%

## Step 4: Execute Migration Incrementally

1. **Step 1:** Build new implementation alongside old
2. **Step 2:** Route one small piece to new implementation (feature flag)
3. **Step 3:** Verify — tests pass, no errors, performance maintained
4. **Step 4:** Route more pieces incrementally
5. **Step 5:** Monitor at each step — errors, performance, user feedback
6. **Step 6:** Rollback if issues detected — disable flag, revert migration
7. **Repeat** until all pieces migrated

## Step 5: Use Codemods for Automation

1. Identify repetitive transformations (rename, API change, pattern migration)
2. Write codemod with jscodeshift or ts-morph
3. Test codemod on small subset of files
4. Review diffs carefully — codemods can make mistakes
5. Run on full codebase
6. Run full test suite
7. Commit codemod changes separately from manual changes

## Step 6: Database Migration (if applicable)

1. Use expand and contract pattern:
   - Phase 1 (Expand): Add new schema alongside old
   - Phase 2 (Migrate): Backfill data in batches
   - Phase 3 (Contract): Remove old schema
2. Use `CREATE INDEX CONCURRENTLY` for indexes (no lock)
3. Batch large data migrations — don't lock tables
4. Test on staging with production-like data volume
5. Verify rollback (down migration) works

## Step 7: Monitor During Migration

1. Watch error rate for 24-48 hours after each step
2. Compare performance: old vs new response times
3. Monitor support tickets for new user-reported issues
4. Check logs for new error patterns
5. Set up alerts for regression detection
6. Have rollback plan ready at each step

## Step 8: Verify Post-Migration

1. **Functional:** All tests pass (unit, integration, E2E)
2. **Manual:** Test critical user flows
3. **Comparison:** Compare old vs new behavior (shadow mode if possible)
4. **Performance:** Verify no performance regression
5. **Edge cases:** Test boundary conditions and error scenarios
6. **Security:** Verify security properties maintained

## Step 9: Clean Up

1. Remove old code — delete migrated-from code and files
2. Remove feature flags — clean up migration toggle flags
3. Remove old dependencies — uninstall packages no longer used
4. Remove old database schema — drop old columns/tables (after verification)
5. Update CI/CD — remove old build/deploy steps
6. Update documentation — reflect new architecture and patterns

## Step 10: Document & Learn

1. Write migration guide — what was done, why, how
2. Document new architecture — diagrams, patterns, decisions
3. Record lessons learned — what worked, what didn't, what to improve
4. Update coding standards — reflect new patterns and conventions
5. Share with team — knowledge transfer for future migrations
6. Update technical debt inventory — mark migrated items as resolved
