---
name: Git Workflow & Version Control Skill
description: Comprehensive methodology for Git version control — 2025-2026 practices with branching strategies, conventional commits, PR workflows, rebase vs merge, hooks, and monorepo patterns
version: 1.0.0
tags: [git, version-control, branching, conventional-commits, pull-requests, rebase, merge, hooks, ci-cd, monorepo]
---

# Git Workflow & Version Control Skill

## Purpose
This skill provides a comprehensive methodology for Git version control across any kind of web project. It reflects **modern 2025-2026 practices** — trunk-based development for small teams, GitHub Flow for most projects, Conventional Commits for automated changelogs, squash-and-merge for clean history, Git hooks for quality gates, and monorepo patterns with sparse checkout.

## Core Philosophy

**Commit early, commit often, commit small.** Small, focused commits are easier to review, easier to revert, and easier to understand. Each commit should tell one story — one feature, one fix, one refactor. If your commit message needs "and" in the title, it's probably two commits.

**The #1 rule:** Never commit directly to main/production. Always work on a branch, create a PR, get review, and merge. This ensures code review, CI checks, and a clean, auditable history. Direct commits to main bypass all quality gates.

---

## Part 1: Branching Strategies

### 1.1 Trunk-Based Development (Small Teams, Startups)
```
main (always deployable)
 ├── feature/login
 ├── feature/dashboard
 └── fix/header-bug
```
- **Single branch:** Everyone commits to `main` via short-lived feature branches
- **Short-lived branches:** < 24 hours, < 1 day
- **Fast feedback:** CI runs on every push, merge happens quickly
- **Best for:** Small teams (1-10), continuous deployment, fast-moving projects
- **Rebase:** Feature branches rebase on latest main before merge

### 1.2 GitHub Flow (Most Projects)
```
main (always deployable)
 ├── feature/user-auth
 ├── feature/api-endpoints
 └── fix/payment-bug
```
- **`main` is sacred:** Always deployable, always green
- **Feature branches:** `feature/description`, `fix/description`, `chore/description`
- **Pull request:** Required to merge into `main`
- **Review:** At least one approval required
- **CI:** All checks must pass before merge
- **Deploy:** From `main` after merge (or automatically)
- **Best for:** Most projects, small to medium teams (5-50)

### 1.3 Git Flow (Enterprise, Versioned Releases)
```
main (production)
develop (integration)
 ├── feature/login
 ├── feature/dashboard
release/v1.2.0
hotfix/v1.2.1
```
- **`main`:** Production-ready code, tagged with versions
- **`develop`:** Integration branch for next release
- **`feature/*`:** Branched from `develop`, merged back to `develop`
- **`release/*`:** Branched from `develop`, merged to `main` and `develop`
- **`hotfix/*`:** Branched from `main`, merged to `main` and `develop`
- **Best for:** Enterprise, versioned releases, multiple maintained versions

### 1.4 Branch Naming Conventions
```
feature/description      — New feature
fix/description          — Bug fix
chore/description        — Maintenance, deps, config
refactor/description     — Code refactoring
docs/description         — Documentation
release/v1.2.0           — Release preparation
hotfix/v1.2.1            — Production hotfix
```

---

## Part 2: Conventional Commits

### 2.1 Commit Message Format
```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

### 2.2 Commit Types
| Type | Description | Example |
|---|---|---|
| `feat` | New feature | `feat(auth): add OAuth login` |
| `fix` | Bug fix | `fix(api): handle null response` |
| `docs` | Documentation | `docs(readme): update installation steps` |
| `style` | Formatting, no code change | `style: fix indentation` |
| `refactor` | Code refactoring | `refactor(auth): extract token validation` |
| `perf` | Performance improvement | `perf(db): add index on users.email` |
| `test` | Adding/updating tests | `test(auth): add login integration tests` |
| `chore` | Maintenance, deps, config | `chore(deps): update next to 15.2` |
| `ci` | CI/CD changes | `ci: add linting to PR checks` |
| `build` | Build system changes | `build: update vite config` |
| `revert` | Revert previous commit | `revert: feat(auth) add OAuth login` |

### 2.3 Examples
```
feat(auth): add OAuth2 login with Google

Implement Google OAuth2 authentication flow using Passport.js.
Includes token refresh, session management, and error handling.

Closes #123

---

fix(api): handle null response from payment provider

The payment provider occasionally returns null for successful
payments. Add null check and default to success state.

Fixes #456

---

feat(ui)!: change button component API

BREAKING CHANGE: Button component now uses `variant` prop instead
of `type` prop. Update all usages.

---

chore(deps): bump next from 14.1.0 to 15.2.0

Update Next.js to latest version. Includes breaking changes
in App Router — see migration guide in docs/migration.md.
```

### 2.4 Breaking Changes
- **`!` after type/scope:** `feat(api)!: change response format`
- **`BREAKING CHANGE:` in footer:** Describe what breaks and how to migrate
- **Automated tooling:** Conventional Commits enable automated changelog, version bumping

---

## Part 3: Pull Request Workflow

### 3.1 PR Template
```markdown
## Description
Brief description of what this PR does and why.

## Type of Change
- [ ] Bug fix (non-breaking)
- [ ] New feature (non-breaking)
- [ ] Breaking change
- [ ] Documentation update
- [ ] Refactor
- [ ] Performance improvement

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] Tests added/updated
- [ ] All tests pass locally
- [ ] No new warnings
- [ ] CI checks pass

## Screenshots (if UI change)
Before: [screenshot]
After: [screenshot]

## Related Issues
Closes #123
Depends on #124
```

### 3.2 PR Best Practices
- **Small PRs:** < 400 lines changed — easier to review, faster to merge
- **One concern:** One feature, one fix, one refactor per PR
- **Descriptive title:** `feat(auth): add OAuth2 login with Google`
- **Description:** What, why, how — help the reviewer understand
- **Screenshots:** For UI changes — before and after
- **Self-review:** Review your own PR before requesting review
- **Respond to feedback:** Address every comment, don't dismiss
- **Update branch:** Rebase on latest main before merge

### 3.3 Code Review Guidelines
- **Review for:** Correctness, security, performance, accessibility, tests
- **Don't review for:** Style (automated linter handles this)
- **Be constructive:** Suggest improvements, don't just criticize
- **Ask questions:** "Why this approach?" not "This is wrong"
- **Approve when ready:** Don't block on nitpicks — leave as comments
- **Use suggestions:** GitHub's suggestion feature for small fixes
- **Review promptly:** Don't leave PRs waiting — blocks the team

### 3.4 Merge Strategies

| Strategy | History | Use Case |
|---|---|---|
| **Squash and merge** | Clean — one commit per PR | Default for feature branches |
| **Rebase and merge** | Linear — preserves commits | When commits are meaningful |
| **Create merge commit** | Preserves branch context | For release/feature branches |

- **Squash (recommended):** Combines all PR commits into one clean commit on main
- **Rebase:** Preserves individual commits but creates linear history
- **Merge commit:** Preserves branch topology — useful for large features

---

## Part 4: Rebase vs Merge

### 4.1 Rebase (Linear History)
```bash
# On feature branch
git rebase main

# Resolves conflicts per commit
# Result: feature commits replayed on top of main
```
- **Pros:** Linear history, clean log, easy to follow
- **Cons:** Rewrites history (force push needed), can be confusing
- **Use for:** Feature branches before merge, keeping up with main

### 4.2 Merge (Preserves History)
```bash
# On feature branch
git merge main

# Or on main
git merge feature-branch
```
- **Pros:** Preserves context, non-destructive, safe
- **Cons:** Merge commits clutter log, harder to follow
- **Use for:** Release branches, long-lived branches, shared branches

### 4.3 When to Use Each
- **Rebase:** Your private feature branch, before creating PR
- **Merge:** `main` into your branch (to stay current), or PR merge
- **Never rebase:** Shared branches, `main`, `develop` — forces others to reset

### 4.4 Interactive Rebase
```bash
# Clean up commits before merging
git rebase -i main

# Squash, reword, reorder, drop commits
pick abc1234 feat: add login form
squash def5678 fix: typo in label
squash ghi9012 fix: validation error
# Becomes one commit: "feat: add login form"
```

---

## Part 5: Git Hooks

### 5.1 Pre-commit (Quality Gate)
```bash
# .husky/pre-commit
#!/bin/sh
npx lint-staged
```

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

### 5.2 Pre-push (Run Tests)
```bash
# .husky/pre-push
#!/bin/sh
npm run test
npm run typecheck
```

### 5.3 Commit-msg (Enforce Conventional Commits)
```bash
# .husky/commit-msg
#!/bin/sh
npx --no-install commitlint --edit "$1"
```

```javascript
// commitlint.config.js
module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [2, 'always', [
      'feat', 'fix', 'docs', 'style', 'refactor',
      'perf', 'test', 'chore', 'ci', 'build', 'revert'
    ]],
    'subject-max-length': [2, 'always', 72],
  },
};
```

### 5.4 Husky Setup
```bash
# Install Husky
npm install --save-dev husky
npx husky init

# Add hooks
npx husky add .husky/pre-commit "npx lint-staged"
npx husky add .husky/pre-push "npm run test"
npx husky add .husky/commit-msg "npx commitlint --edit $1"
```

---

## Part 6: .gitignore Best Practices

### 6.1 General .gitignore
```gitignore
# Dependencies
node_modules/
.pnp/
.pnp.js

# Build outputs
dist/
build/
.next/
out/
.nuxt/

# Environment variables
.env
.env.local
.env.*.local

# IDE
.vscode/
.idea/
*.swp
*.swo

# OS
.DS_Store
Thumbs.db

# Logs
*.log
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Test coverage
coverage/

# Cache
.cache/
.parcel-cache/
.eslintcache
.turbo/

# Misc
*.tgz
.vercel
```

### 6.2 Global .gitignore
```bash
# Set global gitignore
git config --global core.excludesfile ~/.gitignore_global
```
```gitignore
# ~/.gitignore_global
.DS_Store
Thumbs.db
.vscode/
.idea/
*.swp
```

---

## Part 7: Tagging & Releases

### 7.1 Semantic Versioning
```
MAJOR.MINOR.PATCH
  1.   2.   3

MAJOR: Breaking changes
MINOR: New features (backward compatible)
PATCH: Bug fixes (backward compatible)
```

### 7.2 Git Tags
```bash
# Create annotated tag
git tag -a v1.2.0 -m "Release v1.2.0: Add OAuth login"

# Push tags
git push origin v1.2.0
git push origin --tags

# List tags
git tag -l

# Checkout specific version
git checkout v1.2.0
```

### 7.3 Automated Releases with Conventional Commits
```json
// package.json
{
  "scripts": {
    "release": "standard-version"
  }
}
```
- **standard-version:** Automatically bumps version, generates changelog from Conventional Commits, creates git tag
- **semantic-release:** Fully automated — analyzes commits, bumps version, publishes to npm, creates GitHub release
- **Changesets:** For monorepos — manages versions and changelogs across packages

---

## Part 8: Common Git Operations

### 8.1 Undo Changes
```bash
# Discard unstaged changes
git checkout -- .
git restore .

# Unstage files
git reset HEAD .
git restore --staged .

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Undo last commit (discard changes)
git reset --hard HEAD~1

# Revert a commit (creates new commit)
git revert abc1234

# Undo merge
git revert -m 1 abc1234
```

### 8.2 Stash
```bash
# Stash current changes
git stash
git stash push -m "WIP: login feature"

# List stashes
git stash list

# Apply stash (keeps in list)
git stash apply
git stash apply stash@{1}

# Pop stash (removes from list)
git stash pop

# Drop stash
git stash drop stash@{0}

# Stash including untracked
git stash -u
```

### 8.3 Cherry Pick
```bash
# Apply specific commit to current branch
git cherry-pick abc1234

# Cherry pick without committing
git cherry-pick --no-commit abc1234
```

### 8.4 Bisect (Find Bug-Introducing Commit)
```bash
git bisect start
git bisect bad          # Current version is bad
git bisect good v1.1.0  # v1.1.0 was good
# Git checks out middle commit
# Test and mark:
git bisect good  # or
git bisect bad
# Git narrows down to the offending commit
git bisect reset  # Return to original branch
```

### 8.5 Reflog (Safety Net)
```bash
# View reflog — every HEAD change
git reflog

# Recover deleted branch
git checkout -b recovered-branch HEAD@{5}

# Recover after hard reset
git reset --hard HEAD@{2}
```

---

## Part 9: Monorepo Git Patterns

### 9.1 Monorepo with Turborepo
```json
// turbo.json
{
  "pipeline": {
    "build": {
      "dependsOn": ["^build"],
      "outputs": ["dist/**", ".next/**"]
    },
    "test": {},
    "lint": {}
  }
}
```

### 9.2 Sparse Checkout (Large Monorepos)
```bash
# Clone without checking out files
git clone --no-checkout https://github.com/org/monorepo.git
cd monorepo

# Enable sparse checkout
git sparse-checkout init --cone

# Only checkout specific directories
git sparse-checkout set apps/web packages/ui
git checkout main
```

### 9.3 Changesets for Monorepo Versioning
```bash
# Add changeset
npx changeset

# This creates a .changeset/xxx.md file:
# ---
# "package-name": minor
# ---
# Description of change

# Version packages
npx changeset version

# Publish
npx changeset publish
```

---

## Part 10: CI/CD with Git

### 10.1 GitHub Actions
```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

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
      - run: npm run test
      - run: npm run build

  deploy:
    needs: quality
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: npm ci
      - run: npm run build
      - run: npx vercel --prod --token ${{ secrets.VERCEL_TOKEN }}
```

### 10.2 Branch Protection Rules
- **Require PR:** No direct pushes to `main`
- **Require review:** At least 1 approval
- **Require status checks:** CI must pass before merge
- **Require branches up to date:** PR must be rebased on latest main
- **Dismiss stale reviews:** When new commits are pushed
- **Restrict who can push:** Only admins can merge to `main`

---

## Execution Instructions for Cascade

When this skill is activated for Git workflow & version control:

1. **Read the project context** — team size, release cadence, existing Git workflow
2. **Choose branching strategy** — Trunk-based (small), GitHub Flow (most), Git Flow (enterprise)
3. **Set up branch naming** — `feature/`, `fix/`, `chore/`, `docs/`, `refactor/`
4. **Set up Conventional Commits** — commitlint, commitizen, or manual enforcement
5. **Set up Git hooks** — Husky: pre-commit (lint), pre-push (test), commit-msg (validate)
6. **Create PR template** — `.github/pull_request_template.md`
7. **Set up branch protection** — require PR, review, CI checks on `main`
8. **Set up CI/CD** — GitHub Actions for lint, test, build, deploy
9. **Configure .gitignore** — project-specific + global
10. **Set up versioning** — Semantic versioning with tags, automated with standard-version or semantic-release
11. **Document workflow** — branching strategy, commit conventions, PR process
12. **Train team** — ensure everyone follows the workflow
