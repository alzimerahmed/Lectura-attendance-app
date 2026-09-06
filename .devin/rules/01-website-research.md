# Rule: Website Research & Discovery Before Development

**ALWAYS** run the Website Research & Discovery workflow at the start of every new website project, before writing any code.

## Skill
`~/.codeium/windsurf/skills/website-research.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/website-research.md` — invoke with `/website-research`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/researcher.md` (parent: Project Architect)

## How to follow this rule:
1. When the user starts a new website project (new repo, new folder, or asks to build a website), invoke the `/website-research` workflow
2. Follow the workflow steps in order: Project Discovery → Competitive & Reference Research → Content Strategy → Visual Design Research → UX & User Flow Research → Technical Research → Compile & Save Research Document → Development Handoff
3. Save the compiled research document as `research.md` in the project root
4. Only begin writing code after the research document is complete and the user has confirmed the direction
5. Reference the `research.md` document throughout development to ensure consistency with the planned design, content, and architecture

## When this rule applies:
- User creates a new website project
- User asks to build, design, or create a website
- User provides a reference website to clone or draw inspiration from
- User says "new project" in the context of web development
- User asks to redesign or rebuild an existing website

## When this rule does NOT apply:
- Bug fixes or small changes to an existing project that already has a `research.md`
- Non-website projects (CLI tools, libraries, scripts, etc.)
- User explicitly says to skip research
