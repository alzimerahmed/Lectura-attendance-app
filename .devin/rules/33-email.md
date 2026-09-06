# Rule: Email & Notifications for All Projects

**ALWAYS** apply the Email & Notifications skill and workflow when implementing email or notification systems. Never send emails synchronously — always use an async queue with retries.

## Skill
`~/.codeium/windsurf/skills/email-notifications.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/email.md` — invoke with `/email`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/email-engineer.md` (parent: Feature Engineer)

## How to follow this rule:
1. When implementing email or notifications, invoke the `/email` workflow
2. Follow the workflow steps in order: Provider → React Email → Async Queue → Templates → Authentication → Push → In-App → SMS → Preferences → Test → Document
3. Always use Resend + React Email for modern apps — best DX and type-safe templates
4. Always send emails asynchronously via a queue (Inngest, BullMQ) — never in request handlers
5. Always set up email authentication — SPF, DKIM, DMARC DNS records
6. Always create notification preferences — per-user, per-channel, per-type
7. Always include unsubscribe links in marketing emails — required by CAN-SPAM and GDPR
8. Always use React Email components for templates — not raw HTML strings

## When this rule applies:
- Implementing email functionality
- Setting up push notifications or in-app notifications
- Configuring SMS notifications
- Building notification preferences
- User asks about email or notifications

## When this rule does NOT apply:
- Projects with no email or notification functionality
- User explicitly says to skip email setup
