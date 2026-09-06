# Rule: Playwright Design Clone for URL/Screenshot Recreation

**ALWAYS** apply the Playwright Design Clone skill and workflow when the user wants a website or design screenshot cloned at high fidelity. Use Playwright MCP to measure and verify — never invent styles from memory when a live page or image is available.

## Skill
`~/.codeium/windsurf/skills/playwright-design-clone.md`  
Project-local: `.devin/skills/playwright-design-clone.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/playwright-design-clone.md` — invoke with `/playwright-design-clone` or `/design-clone`  
Project-local: `.devin/workflows/playwright-design-clone.md`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/design-cloner.md` (parent: Design Engineer)  
Project-local: `.devin/sub-agents/design-cloner.md`

## Main Agent
`~/.codeium/windsurf/windsurf/agents/design-engineer.md`  
Project-local: `.devin/agents/design-engineer.md`

## How to follow this rule:
1. When the user asks to clone/copy/recreate a site or screenshot, invoke `/playwright-design-clone`
2. Confirm Playwright MCP works (navigate + screenshot + evaluate) before coding
3. Follow order: Recon → Extract → Blueprint → Foundation → Build → Visual verify loop
4. Write section specs and tokens **before** bulk component code
5. Capture reference and clone screenshots at desktop + mobile (unless scoped otherwise)
6. Iterate until visual match; document residual gaps honestly
7. Do not bypass auth, captchas, or paywalls
8. Do not "improve" the design unless the user asks after the clone

## When this rule applies:
- User provides a URL to clone or match
- User attaches a design screenshot/mockup to recreate
- User says "pixel perfect", "100% clone", "copy this design", `/design-clone`, `/playwright-design-clone`
- Design Engineer routes a reference-based rebuild

## When this rule does NOT apply:
- Creating original design from a brief (use Claude Taste / frontend-designer)
- Pure content rewrites without visual match requirement
- Backend-only work
- User explicitly wants inspiration-only, not a clone
- Target requires unauthorized access
