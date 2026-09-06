# Rule: Git Workflow & Version Control for All Projects

**ALWAYS** apply the Git Workflow & Version Control skill and workflow when setting up or working with Git. Never commit directly to main — always work on a branch, create a PR, get review, and merge.

## Skill
`~/.codeium/windsurf/skills/git-workflow-version-control.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/git-workflow.md` — invoke with `/git-workflow`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/git-master.md` (parent: Infrastructure Engineer)

## How to follow this rule:
1. When setting up Git workflow, invoke the `/git-workflow` workflow
2. Follow the workflow steps in order: Branching → Conventional Commits → Hooks → PR Template → Branch Protection → CI/CD → .gitignore → Versioning → PR Workflow → Document
3. Always use Conventional Commits — `type(scope): description` format with allowed types
4. Always set up Git hooks with Husky — pre-commit (lint), pre-push (test), commit-msg (validate)
5. Always create PRs with descriptive title, description, checklist, and screenshots for UI changes
6. Always squash and merge — one clean commit per PR on main
7. Always protect main branch — require PR, review, and CI checks
8. Always use Semantic Versioning with automated changelog from Conventional Commits

## When this rule applies:
- Setting up Git workflow for a new project
- Establishing branching strategy or commit conventions
- Setting up Git hooks or PR templates
- Configuring CI/CD with Git
- User asks about Git workflow or version control

## When this rule does NOT apply:
- Solo projects with no collaboration (still recommended but optional)
- User explicitly says to skip Git workflow setup
