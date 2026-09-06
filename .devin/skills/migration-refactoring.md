---
name: Migration & Refactoring Skill
description: Comprehensive methodology for migrating frameworks/libraries and refactoring codebases — 2025-2026 practices with incremental migration, strangler fig pattern, automated codemods, and safety nets
version: 1.0.0
tags: [migration, refactoring, strangler-fig, codemod, incremental, legacy, modernization, safety-net]
---

# Migration & Refactoring Skill

## Purpose
This skill provides a comprehensive methodology for migrating frameworks/libraries and refactoring codebases across any kind of web project. It reflects **modern 2025-2026 practices** — incremental migration with the strangler fig pattern, automated codemods, comprehensive test safety nets, and zero-downtime deployments. Not big-bang rewrites but safe, incremental, verifiable transformations.

## Core Philosophy

**Never do a big-bang rewrite.** Big-bang rewrites fail — they take too long, the business changes during the rewrite, and you can't ship anything until it's all done. Instead, migrate incrementally: replace one piece at a time, with the old and new systems running side by side, until the old system can be removed.

**The #1 rule:** Safety first. Every migration step must be reversible. Every refactoring must be verified by tests. If you can't test it, you can't safely change it. Build the safety net before you start.

---

## Part 1: Migration Strategy

### 1.1 Strangler Fig Pattern
```
Old System (monolith)
  → Route /api/users → New Service
  → Route /api/orders → Old System (not yet migrated)
  → Route /api/products → New Service
  → Eventually: Old System removed
```
1. **Identify boundary:** Find a clean seam in the old system
2. **Build new implementation:** Create the new version alongside the old
3. **Route incrementally:** Gradually route traffic from old to new
4. **Verify:** Compare old and new outputs (shadow mode)
5. **Cut over:** Switch traffic to new implementation
6. **Remove old:** Delete the old code once new is verified

### 1.2 Migration Approaches

| Approach | Risk | Speed | When to Use |
|---|---|---|---|
| **Big bang** | Very high | Slow | Almost never |
| **Strangler fig** | Low | Medium | Most migrations |
| **Parallel run** | Low | Slow | Critical systems |
| **Branch by abstraction** | Low | Medium | Framework migrations |
| **Expand and contract** | Low | Medium | API/schema changes |

### 1.3 Parallel Run (Shadow Mode)
```
Request → Old System → Response to user
         ↓ (shadow)
         New System → Compare output → Log differences
```
- **Both systems process:** Same input, compare outputs
- **No user impact:** Old system serves real traffic
- **Verify:** New system produces same (or better) results
- **Cut over:** When confident, switch to new system

### 1.4 Expand and Contract (API/Schema)
```
Phase 1 (Expand): Add new field/API alongside old
Phase 2 (Migrate): Update consumers to use new field/API
Phase 3 (Contract): Remove old field/API
```
- **Backward compatible:** Old and new work simultaneously
- **No downtime:** Consumers migrate at their own pace
- **Example:** Add `email` field, migrate from `emailAddress`, remove `emailAddress`

### 1.5 Branch by Abstraction
```typescript
// Step 1: Create abstraction
interface DataStore { getUser(id: string): Promise<User>; }

// Step 2: Old implementation behind abstraction
class OldDataStore implements DataStore { ... }

// Step 3: New implementation behind same abstraction
class NewDataStore implements DataStore { ... }

// Step 4: Switch implementation
const store: DataStore = new NewDataStore();

// Step 5: Remove old implementation
```

---

## Part 2: Framework Migration

### 2.1 React Class Components to Hooks
```typescript
// Before: Class component
class UserProfile extends React.Component {
  state = { user: null };
  componentDidMount() { fetchUser(this.props.id).then(u => this.setState({ user: u })); }
  componentDidUpdate(prevProps) {
    if (prevProps.id !== this.props.id) fetchUser(this.props.id).then(u => this.setState({ user: u }));
  }
  render() { return <div>{this.state.user?.name}</div>; }
}

// After: Hooks
function UserProfile({ id }: { id: string }) {
  const [user, setUser] = useState<User | null>(null);
  useEffect(() => { fetchUser(id).then(setUser); }, [id]);
  return <div>{user?.name}</div>;
}
```
- **Incremental:** Convert one component at a time
- **No mixing:** Don't mix class and hooks in the same component
- **Codemod:** Use `react-codemod` for automated conversion
- **Test:** Verify each component after conversion

### 2.2 JavaScript to TypeScript
```bash
# Allow JS files in TS project
# tsconfig.json
{ "allowJs": true, "outDir": "./dist" }

# Rename files one at a time
mv component.js component.tsx

# Fix type errors
# Add types gradually
```
- **Incremental:** `allowJs: true` — mix JS and TS files
- **Strict mode:** Start with `strict: false`, gradually enable strict options
- **One file at a time:** Rename `.js` → `.ts`/`.tsx`, fix errors
- **Types first:** Add types to shared modules first (API, utils, types)
- **Codemod:** Use `ts-migrate` or custom codemods for bulk conversion

### 2.3 Create React App to Vite/Next.js
- **Vite:** Faster dev server, ESM-native, simpler config
- **Next.js:** SSR/SSG, routing, API routes, image optimization
- **Migration steps:**
  1. Set up new project alongside old
  2. Move components one route at a time
  3. Update imports and config
  4. Test each route after migration
  5. Remove CRA when all routes migrated

### 2.4 Vue 2 to Vue 3
- **Composition API:** New `setup()` function — like React hooks
- **Breaking changes:** Filters removed, `$on`/`$off` removed, teleport, fragments
- **Codemod:** `@vue/migrator` for automated migration
- **Incremental:** Use `@vue/compat` (compatibility build) during migration

### 2.5 Express to Fastify/Hono
- **Fastify:** Faster, schema validation, better TypeScript support
- **Hono:** Ultra-fast, edge-compatible, web standards
- **Migration:** Adapter pattern — abstract route handlers, swap framework

---

## Part 3: Database Migration

### 3.1 Schema Migration Principles
- **Versioned:** Every migration is numbered and recorded
- **Reversible:** Every migration has an up and down
- **Tested:** Test on staging before production
- **Small:** One change per migration — don't batch
- **Non-blocking:** Use `CONCURRENTLY` for indexes, `ALTER ... TYPE ... USING`

### 3.2 Migration Tools
- **Prisma Migrate:** Schema-first, type-safe, declarative
- **Drizzle Kit:** Lightweight, SQL-first, TypeScript
- **node-pg-migrate:** SQL-based, full control
- **Flyway:** Java ecosystem, SQL-based, widely used
- **golang-migrate:** Go-based, simple, CLI + library

### 3.3 Safe Migration Patterns
```sql
-- Add column (safe — nullable, no default)
ALTER TABLE users ADD COLUMN phone TEXT;

-- Backfill data (in batches — don't lock table)
-- Batch 1
UPDATE users SET phone = '' WHERE id IN (SELECT id FROM users WHERE phone IS NULL LIMIT 1000);
-- Repeat until done

-- Add NOT NULL constraint (after backfill)
ALTER TABLE users ALTER COLUMN phone SET NOT NULL;

-- Add index concurrently (no lock)
CREATE INDEX CONCURRENTLY idx_users_phone ON users(phone);

-- Rename column (expand and contract)
-- Phase 1: Add new column
ALTER TABLE users ADD COLUMN email_address TEXT;
-- Phase 2: Backfill + sync
UPDATE users SET email_address = email;
-- Phase 3: Update app to use new column
-- Phase 4: Drop old column
ALTER TABLE users DROP COLUMN email;
```

### 3.4 Large Table Migrations
```sql
-- Don't: ALTER TABLE large_table ADD COLUMN x BOOLEAN DEFAULT false;
-- This rewrites the entire table — blocks for hours

-- Do: Add nullable column, backfill in batches, set default
ALTER TABLE large_table ADD COLUMN x BOOLEAN;
-- Backfill in batches of 1000
UPDATE large_table SET x = false WHERE x IS NULL LIMIT 1000;
-- Set default after backfill
ALTER TABLE large_table ALTER COLUMN x SET DEFAULT false;
ALTER TABLE large_table ALTER COLUMN x SET NOT NULL;
```

### 3.5 Zero-Downtime Migration
- **Expand and contract:** Add new schema, migrate app, remove old schema
- **Backward compatible:** Old and new app versions work with both schemas
- **Deploy order:** Deploy schema change → deploy app change → deploy schema cleanup
- **Rollback:** If app fails, old app still works with expanded schema

---

## Part 4: Refactoring Techniques

### 4.1 Extract Component
```typescript
// Before: Everything in one component
function UserCard({ user }) {
  return (
    <div>
      <img src={user.avatar} />
      <h3>{user.name}</h3>
      <p>{user.bio}</p>
      <button onClick={() => follow(user.id)}>Follow</button>
      <div>
        <span>{user.followers} followers</span>
        <span>{user.following} following</span>
      </div>
    </div>
  );
}

// After: Extract sub-components
function Avatar({ src, alt }) { return <img src={src} alt={alt} />; }
function UserStats({ followers, following }) { ... }
function FollowButton({ userId }) { ... }

function UserCard({ user }) {
  return (
    <div>
      <Avatar src={user.avatar} alt={user.name} />
      <h3>{user.name}</h3>
      <p>{user.bio}</p>
      <FollowButton userId={user.id} />
      <UserStats followers={user.followers} following={user.following} />
    </div>
  );
}
```

### 4.2 Extract Function
```typescript
// Before: Long function doing everything
function processOrder(order) {
  // validate
  if (!order.items.length) throw new Error('Empty order');
  if (order.total < 0) throw new Error('Invalid total');
  // calculate tax
  const tax = order.items.reduce((sum, item) => sum + item.price * 0.08, 0);
  // calculate shipping
  const shipping = order.total > 100 ? 0 : 10;
  // save
  order.tax = tax;
  order.shipping = shipping;
  order.grandTotal = order.total + tax + shipping;
  return db.orders.save(order);
}

// After: Extracted functions
function validateOrder(order) { ... }
function calculateTax(items) { ... }
function calculateShipping(total) { ... }

function processOrder(order) {
  validateOrder(order);
  order.tax = calculateTax(order.items);
  order.shipping = calculateShipping(order.total);
  order.grandTotal = order.total + order.tax + order.shipping;
  return db.orders.save(order);
}
```

### 4.3 Rename for Clarity
```typescript
// Bad: unclear names
const d = new Date();
const u = users.filter(x => x.a);
function proc(d) { ... }

// Good: descriptive names
const currentDate = new Date();
const activeUsers = users.filter(user => user.isActive);
function processOrder(order) { ... }
```

### 4.4 Replace Conditional with Polymorphism
```typescript
// Before: switch statement
function calculateArea(shape) {
  switch (shape.type) {
    case 'circle': return Math.PI * shape.radius ** 2;
    case 'rectangle': return shape.width * shape.height;
    case 'triangle': return 0.5 * shape.base * shape.height;
  }
}

// After: polymorphism
interface Shape { calculateArea(): number; }
class Circle implements Shape { calculateArea() { return Math.PI * this.radius ** 2; } }
class Rectangle implements Shape { calculateArea() { return this.width * this.height; } }
class Triangle implements Shape { calculateArea() { return 0.5 * this.base * this.height; } }
```

### 4.5 Replace Inheritance with Composition
```typescript
// Before: deep inheritance
class BaseUser { ... }
class PremiumUser extends BaseUser { ... }
class AdminUser extends PremiumUser { ... }

// After: composition
class User { constructor(private permissions: Permission[], private billing: Billing) {} }
const adminUser = new User([allPermissions], new PremiumBilling());
```

### 4.6 Simplify Conditionals
```typescript
// Before: nested conditionals
function getDiscount(user) {
  if (user) {
    if (user.isPremium) {
      if (user.yearsActive > 5) {
        return 0.2;
      } else {
        return 0.1;
      }
    } else {
      return 0;
    }
  } else {
    return 0;
  }
}

// After: guard clauses
function getDiscount(user) {
  if (!user) return 0;
  if (!user.isPremium) return 0;
  if (user.yearsActive <= 5) return 0.1;
  return 0.2;
}
```

---

## Part 5: Automated Codemods

### 5.1 jscodeshift
```javascript
// transform.js — rename prop 'oldName' to 'newName'
module.exports = function(fileInfo, api) {
  const j = api.jscodeshift;
  return j(fileInfo.source)
    .find(j.JSXAttribute)
    .filter(path => path.node.name.name === 'oldName')
    .forEach(path => { path.node.name.name = 'newName'; })
    .toSource();
};

// Run
npx jscodeshift -t transform.js src/
```

### 5.2 ts-morph
```typescript
import { Project } from 'ts-morph';

const project = new Project();
const sourceFile = project.addSourceFileAtPath('src/component.tsx');

// Rename a function
sourceFile.getFunction('oldName')?.rename('newName');

// Change export to named
sourceFile.getExportDeclarations().forEach(d => {
  d.setIsDefaultExport(false);
});

sourceFile.saveSync();
```

### 5.3 Common Codemod Use Cases
- **Rename:** Variables, functions, components, props
- **API changes:** Update function signatures, import paths
- **Pattern migration:** Class to hooks, callbacks to async/await
- **Deprecation removal:** Remove deprecated APIs, replace with new
- **Lint fixes:** Auto-fix linting issues across codebase

### 5.4 Codemod Safety
- **Test on sample:** Run on a small subset first
- **Review diffs:** Check every change — codemods can make mistakes
- **Run tests:** Full test suite after codemod
- **Commit separately:** One commit for codemod, separate from manual changes
- **Rollback:** Easy revert if codemod broke something

---

## Part 6: Safety Nets

### 6.1 Test Coverage Before Refactoring
- **Coverage:** Ensure key paths are tested before changing them
- **Characterization tests:** If no tests exist, write tests that capture current behavior
- **Integration tests:** Verify external contracts don't break
- **Snapshot tests:** Capture current output for comparison
- **Don't refactor without tests:** If you can't test it, don't change it

### 6.2 Feature Flags for Migration
```typescript
// Use feature flag to toggle between old and new
const useNewImplementation = useFeatureFlag('new-auth-system');

if (useNewImplementation) {
  return newAuthFlow(user);
} else {
  return oldAuthFlow(user);
}
```
- **Gradual rollout:** Enable for 1% → 10% → 50% → 100%
- **Instant rollback:** Disable flag if issues detected
- **A/B test:** Compare old vs new performance and correctness
- **Kill switch:** Disable new implementation immediately

### 6.3 Monitoring During Migration
- **Error rate:** Watch for new errors after each migration step
- **Performance:** Compare old vs new response times
- **User feedback:** Monitor support tickets for new issues
- **Alerting:** Set up alerts for regression detection
- **Logging:** Log which implementation served each request

### 6.4 Rollback Strategy
- **Every step reversible:** Database migrations have down, code has git revert
- **Feature flags:** Disable new implementation instantly
- **Blue-green deployment:** Switch back to old version
- **Database:** Expand and contract — old schema still works
- **Practice:** Test rollback in staging before production

---

## Part 7: Common Migration Scenarios

### 7.1 Monolith to Microservices
1. **Identify bounded contexts:** Domain-driven design — find service boundaries
2. **Strangler fig:** Extract one service at a time
3. **API gateway:** Route requests to old monolith or new service
4. **Database:** Split database per service (or shared database with separate schemas)
5. **Communication:** Async events (Kafka, RabbitMQ) or sync API calls
6. **Data migration:** Move data to new service database incrementally

### 7.2 REST to GraphQL
1. **Add GraphQL alongside REST:** Both available simultaneously
2. **GraphQL resolvers call REST:** Wrap existing REST APIs
3. **Migrate clients:** Gradually switch from REST to GraphQL
4. **Optimize resolvers:** Replace REST calls with direct database queries
5. **Remove REST:** When all clients use GraphQL

### 7.3 Server Migration (e.g., Heroku to AWS)
1. **Containerize:** Dockerize the application
2. **Set up new infrastructure:** IaC (Terraform, Pulumi)
3. **Deploy to new:** Both old and new running
4. **Migrate DNS:** Gradual DNS cutover (weighted routing)
5. **Monitor:** Compare old vs new performance
6. **Decommission old:** Shut down old infrastructure

### 7.4 Authentication Migration
1. **New auth alongside old:** Both systems active
2. **New logins use new auth:** New sessions created with new system
3. **Migrate existing sessions:** Gradually migrate active sessions
4. **Dual validation:** Both old and new tokens accepted
5. **Remove old auth:** When all sessions migrated

---

## Part 8: Refactoring Legacy Code

### 8.1 Understanding Legacy Code
- **Read tests:** Tests document expected behavior
- **If no tests:** Write characterization tests before changing
- **Read code:** Understand the code before changing it
- **Talk to users:** Understand why the code behaves the way it does
- **Document:** Write down what you learn

### 8.2 Breaking Changes Safely
- **Deprecate first:** Mark old API as deprecated, add new API
- **Both work:** Old and new APIs functional simultaneously
- **Migration guide:** Document how to migrate from old to new
- **Timeline:** Set a removal date — communicate it
- **Remove:** After migration period, remove old API

### 8.3 Dealing with Untested Code
1. **Write characterization tests:** Capture current behavior
2. **Don't fix bugs yet:** Just capture behavior — even if buggy
3. **Refactor:** Improve structure with tests as safety net
4. **Fix bugs:** Now that code is tested and clean
5. **Add more tests:** Test edge cases and error conditions

### 8.4 Technical Debt Assessment
- **Inventory:** List areas of technical debt
- **Impact:** Rate by impact on development speed, bugs, performance
- **Effort:** Estimate effort to fix
- **Prioritize:** High impact + low effort first
- **Schedule:** Allocate time each sprint for debt reduction
- **Track:** Monitor debt over time — should decrease

---

## Part 9: Post-Migration Verification

### 9.1 Functional Verification
- **All tests pass:** Unit, integration, E2E
- **Manual testing:** Test critical user flows
- **Comparison:** Compare old vs new behavior (shadow mode)
- **Edge cases:** Test boundary conditions and error scenarios
- **Performance:** Verify no performance regression

### 9.2 Monitoring Post-Migration
- **Error rate:** Watch for 24-48 hours after migration
- **Performance:** Compare response times before and after
- **User feedback:** Monitor support tickets and user complaints
- **Logs:** Check for new error patterns
- **Alerts:** Set up alerts for any regression

### 9.3 Cleanup
- **Remove old code:** Delete migrated-from code
- **Remove feature flags:** Clean up migration flags
- **Remove old dependencies:** Uninstall packages no longer used
- **Update documentation:** Reflect new architecture
- **Update CI/CD:** Remove old build/deploy steps

---

## Execution Instructions for Cascade

When this skill is activated for migration & refactoring:

1. **Read the codebase** — understand current architecture, dependencies, test coverage
2. **Assess risk** — what's the impact of migration? What's the test coverage?
3. **Build safety nets** — write characterization tests if none exist
4. **Choose strategy** — strangler fig, parallel run, expand and contract, branch by abstraction
5. **Plan incrementally** — break migration into small, reversible steps
6. **Set up feature flags** — toggle between old and new implementations
7. **Execute migration** — one step at a time, verify after each step
8. **Use codemods** — automate repetitive transformations
9. **Monitor** — watch errors, performance, user feedback during migration
10. **Verify** — functional tests, comparison with old system, performance benchmarks
11. **Clean up** — remove old code, flags, dependencies
12. **Document** — migration guide, new architecture, lessons learned
