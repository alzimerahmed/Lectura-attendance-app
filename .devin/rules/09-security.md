# Rule: Security Audit for All Projects

**ALWAYS** apply the Security Audit skill and workflow when auditing web application security. Think like an attacker — every input is an attack vector, every endpoint is a target, trust nothing, validate everything.

## Skill
`~/.codeium/windsurf/skills/security-audit.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/security.md` — invoke with `/security`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/security-auditor.md` (parent: Quality Engineer)

## How to follow this rule:
1. When auditing security, invoke the `/security` workflow
2. Follow the workflow steps in order: Threat Model → Automated Scanning → OWASP Top 10 → Auth & Session → Authorization → Input Validation → API Security → Infrastructure → Dependencies → Manual Pentest → Remediation
3. Always cover OWASP Top 10 (2025-2026) — all 10 categories
4. Always combine automated scanning with manual penetration testing
5. Always classify findings by severity (Critical/High/Medium/Low) with CVSS scores
6. Always create issues for each vulnerability with reproduction steps and remediation
7. Always verify fixes by re-testing after remediation
8. Never rely on automated tools alone — they catch only ~30-40% of vulnerabilities

## When this rule applies:
- Before launching a new project
- After significant changes to auth, API, or data handling
- Quarterly for ongoing projects
- After a security incident
- User asks for a security audit

## When this rule does NOT apply:
- Non-web projects
- User explicitly says to skip security audit
