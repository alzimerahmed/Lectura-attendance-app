---
name: git-workflow
description: Comprehensive Git workflow & version control workflow — branching, conventional commits, PRs, rebase, hooks, tagging, CI/CD, and monorepo patterns
---

# Git Workflow & Version Control Workflow

This workflow applies the **Git Workflow & Version Control Skill** (`~/.codeium/windsurf/skills/git-workflow-version-control.md`) to establish a clean, efficient, and safe version control process.

## When to Run
- When setting up Git workflow for a new project
- When the user says `/git-workflow` or asks about version control
- When establishing branching strategy or commit conventions
- When setting up Git hooks or PR templates
- When configuring CI/CD with Git

---

## Step 1: Choose Branching Strategy

1. Read the project context — team size, release cadence, deployment frequency
2. **Trunk-Based Development:** Small teams (1-10), continuous deployment, short-lived branches (< 24h)
3. **GitHub Flow (recommended for most):** Small to medium teams (5-50), `main` always deployable, PR required
4. **Git Flow:** Enterprise, versioned releases, multiple maintained versions
5. Document the chosen strategy in `CONTRIBUTING.md`
6. Set up branch naming conventions: `feature/`, `fix/`, `chore/`, `docs/`, `refactor/`

## Step 2: Set Up Conventional Commits

1. Install commitlint and config: `npm i -D @commitlint/cli @commitlint/config-conventional`
2. Create `commitlint.config.js` with allowed types and rules
3. Optionally install commitizen for guided commits: `npm i -D commitizen cz-conventional-changelog`
4. Document commit message format in `CONTRIBUTING.md`
5. Commit types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`, `ci`, `build`, `revert`
6. Format: `type(scope): description` — max 72 chars for subject
7. Use `!` for breaking changes: `feat(api)!: change response format`

## Step 3: Set Up Git Hooks

1. Install Husky: `npm i -D husky && npx husky init`
2. **Pre-commit:** Run lint-staged — ESLint, Prettier on staged files only
3. **Pre-push:** Run tests and typecheck — prevent pushing broken code
4. **Commit-msg:** Run commitlint — enforce Conventional Commits format
5. Configure lint-staged in `package.json` — only format/lint changed files
6. Test hooks by making a commit and pushing

## Step 4: Create PR Template

1. Create `.github/pull_request_template.md` (GitHub) or equivalent
2. Include: Description, Type of Change, Checklist, Screenshots, Related Issues
3. Checklist: style guidelines, self-review, tests, documentation, CI pass
4. Ensure template guides contributors to provide necessary context
5. Test by creating a test PR

## Step 5: Set Up Branch Protection

1. Go to GitHub/GitLab repository settings
2. Protect `main` branch:
   - Require pull request before merging
   - Require at least 1 approval
   - Require status checks to pass (CI)
   - Require branches to be up to date before merging
   - Dismiss stale reviews when new commits are pushed
3. Restrict who can push directly to `main` (no one — always via PR)
4. Enable "Allow squash merging" as default merge method
5. Disable "Allow merge commits" and "Allow rebase merging" if using squash only

## Step 6: Set Up CI/CD Pipeline

1. Create `.github/workflows/ci.yml` (GitHub Actions) or equivalent
2. **On every PR:** lint, typecheck, test, build
3. **On merge to main:** build, deploy to staging/production
4. Use `npm ci` for reproducible installs (not `npm install`)
5. Cache dependencies: `actions/setup-node` with `cache: 'npm'`
6. Run jobs in parallel where possible (lint, test, build)
7. Deploy job should depend on quality job passing
8. Set up environment secrets for deployment tokens

## Step 7: Configure .gitignore

1. Create project `.gitignore` with: node_modules, build outputs, .env files, IDE files, OS files, logs, cache
2. Set up global `.gitignore` for personal files: `git config --global core.excludesfile ~/.gitignore_global`
3. Never commit: `.env` files, `node_modules/`, build outputs, secrets, API keys
4. Use `.env.example` for documenting required environment variables (safe to commit)
5. Add `.gitkeep` to preserve empty directories

## Step 8: Set Up Versioning

1. Use Semantic Versioning: `MAJOR.MINOR.PATCH`
2. **standard-version:** Auto bump version + changelog from Conventional Commits
3. **semantic-release:** Fully automated — analyze commits, bump, publish, release
4. **Changesets:** For monorepos — manage versions across packages
5. Create git tags for releases: `git tag -a v1.0.0 -m "Release v1.0.0"`
6. Push tags: `git push origin --tags`
7. Create GitHub releases from tags with auto-generated release notes

## Step 9: Establish PR Workflow

1. **Create branch:** `git checkout -b feature/description`
2. **Commit often:** Small, focused commits with Conventional Commits format
3. **Push regularly:** Push to remote for backup and CI feedback
4. **Rebase on main:** Keep branch up to date: `git rebase main`
5. **Create PR:** Use template, fill in description, link issues
6. **Self-review:** Review your own changes before requesting review
7. **Request review:** Assign reviewers, mention in description
8. **Address feedback:** Respond to every comment, push fixes
9. **Squash and merge:** One clean commit per PR on `main`
10. **Delete branch:** Clean up after merge

## Step 10: Document Workflow

1. Create `CONTRIBUTING.md` with:
   - Branching strategy and naming conventions
   - Commit message format (Conventional Commits)
   - PR process and template
   - Code review guidelines
   - CI/CD pipeline description
   - How to run tests locally
2. Create `README.md` section on development workflow
3. Document release process — versioning, tagging, deployment
4. Share with team — ensure everyone understands and follows the workflow
5. Onboard new team members with the documentation
