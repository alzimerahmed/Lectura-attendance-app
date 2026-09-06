# Rule: Caveman Mode for Token-Efficient Communication

**ACTIVATE** the Caveman Compression skill and workflow when the user requests compressed communication. Caveman mode cuts token usage ~75% while maintaining full technical accuracy. Every response must preserve code, URLs, file paths, numbers, and specs verbatim.

## Skill
`~/.codeium/windsurf/skills/caveman-compression.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/caveman.md` — invoke with `/caveman`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/caveman-compressor.md` (parent: Infrastructure Engineer)

## Main Agent
`~/.codeium/windsurf/windsurf/agents/infrastructure-engineer.md`

## How to follow this rule:
1. When the user activates caveman mode, invoke the `/caveman` workflow
2. Apply the requested intensity level (lite, full, ultra, wenyan variants)
3. Follow the grammar rules: drop articles, pronouns, auxiliary verbs — keep all technical terms
4. NEVER compress code snippets, URLs, file paths, numbers, specs, or error messages
5. For `/caveman-commit`: generate Conventional Commits format, subject ≤50 chars, body only when needed
6. For `/caveman-review`: one-line comments per finding — location, problem, fix
7. For `/caveman:compress`: compress memory files, save backup as `FILE.original.md`
8. Deactivate when user says "normal mode", "standard English", or "disable caveman"

## When this rule applies:
- User says `/caveman`, "caveman mode", "talk like caveman", "less tokens", "be brief"
- User says `/caveman-commit` or asks for a commit message
- User says `/caveman-review` or asks for code review comments
- User says `/caveman:compress <filepath>` or asks to compress a memory file
- User explicitly requests compressed or token-efficient communication

## When this rule does NOT apply:
- User has not requested caveman mode — use standard communication
- User says "normal mode", "standard English", or "disable caveman"
- Writing documentation, README files, or user-facing content (unless explicitly requested in caveman mode)
- Generating code — code is never compressed, only natural language descriptions
