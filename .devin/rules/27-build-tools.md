# Rule: Build Tools & Bundlers for All Projects

**ALWAYS** apply the Build Tools & Bundlers skill and workflow when configuring build pipelines. Ship less JavaScript — code split, tree shake, lazy load, and analyze your bundle regularly.

## Skill
`~/.codeium/windsurf/skills/build-tools-bundlers.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/build-tools.md` — invoke with `/build-tools`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/build-optimizer.md` (parent: Infrastructure Engineer)

## How to follow this rule:
1. When configuring build tools, invoke the `/build-tools` workflow
2. Follow the workflow steps in order: Choose Bundler → Configure → Code Split → Tree Shake → Source Maps → Analyze → Budget → CSS → Images → Performance → Test → Document
3. Always use Vite for SPAs, Turbopack for Next.js, tsup for libraries — not Webpack
4. Always implement route-based code splitting with `React.lazy()` and `Suspense`
5. Always enable tree shaking — ESM imports, `sideEffects: false`, named imports
6. Always set a bundle budget — initial < 150KB gzipped, fail CI if exceeded
7. Always generate source maps — `hidden` in production for error tracking
8. Always analyze bundle regularly — check for large deps, duplicates, unused code

## When this rule applies:
- Setting up a build tool for a new project
- Optimizing bundle size or build performance
- Configuring code splitting or tree shaking
- Migrating from Webpack to Vite
- User asks about build tools or bundlers

## When this rule does NOT apply:
- Projects with no build step (static HTML)
- User explicitly says to skip build configuration
