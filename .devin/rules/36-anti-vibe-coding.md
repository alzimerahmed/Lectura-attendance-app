# Rule: Anti Vibe Coding for All Webapps and Landing Pages

**ALWAYS** apply the Anti Vibe Coding skill and workflow before shipping any webapp, landing page, or digital product. Never ship vibe-coded slop — every output must be indistinguishable from work crafted by a senior human team.

## Skill
`~/.codeium/windsurf/skills/anti-vibe-coding.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/anti-vibe-coding.md` — invoke with `/anti-vibe-coding`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/vibe-coding-auditor.md` (parent: Quality Engineer, Vibe Coding Guardian)

## Main Agent
`~/.codeium/windsurf/windsurf/agents/vibe-coding-guardian.md`

## How to follow this rule:
1. Before shipping any webapp, landing page, or digital product, invoke the `/anti-vibe-coding` workflow
2. Follow the workflow steps in order: Read Context → Design Audit → Architecture Audit → Content Audit → Code Audit → Screenshot Review → Compile Report → Fix and Iterate
3. Run the master checklist across all 4 domains (design, architecture, content, code) — every item must pass
4. If any domain fails, fix the specific issues and re-audit until all checks pass
5. Take a screenshot and run the lookalike test — does it look like it could belong to any project? If yes, revise
6. Read copy aloud — does it sound like a human or a marketing bot? If bot, rewrite
7. Ask: "If I saw this on a stranger's laptop, would I think 'AI generated this'?" If yes, revise until the answer is no
8. Use the Vibe Coding Guardian main agent to coordinate the full audit and delegate fixes to specialists

## When this rule applies:
- Before shipping any webapp, landing page, or digital product
- After building a new page or feature
- When reviewing code for quality
- After any major UI, architecture, content, or code change
- User asks to "check for vibe coding" or "check for AI slop"
- User asks "does this look AI generated?"
- As a final quality gate before deployment

## When this rule does NOT apply:
- Non-website projects (CLI tools, libraries, scripts) — though code quality checks still apply
- User explicitly says to skip the anti-vibe-coding audit
- Backend-only changes with no UI or content impact (code checks still apply)
