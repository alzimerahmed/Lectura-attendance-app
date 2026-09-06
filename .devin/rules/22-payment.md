# Rule: Payment & Commerce for All Projects

**ALWAYS** apply the Payment & Commerce skill and workflow when implementing payment processing or e-commerce functionality. Never touch card data — let the payment processor handle it.

## Skill
`~/.codeium/windsurf/skills/payment-commerce.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/payment.md` — invoke with `/payment`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/payment-integrator.md` (parent: Feature Engineer)

## How to follow this rule:
1. When implementing payments, invoke the `/payment` workflow
2. Follow the workflow steps in order: Assess → Choose Processor → Stripe → Checkout → One-Tap → Subscriptions → Webhooks → Tax → Fraud → UX → Data Model → PCI → Test → Document
3. Always use Stripe Elements or Checkout — never build custom card input forms
4. Always process payments server-side with secret key — never on client
5. Always verify webhook signatures — never trust unverified webhooks
6. Always enable Apple Pay and Google Pay for one-tap checkout
7. Always implement fraud prevention with Stripe Radar and 3DS
8. Always test in Stripe test mode with all card scenarios before going live

## When this rule applies:
- Implementing payment processing
- Building an e-commerce store
- Setting up subscriptions or recurring billing
- Implementing Apple Pay or Google Pay
- User asks about payments or commerce

## When this rule does NOT apply:
- Projects with no payment functionality
- User explicitly says to skip payment workflow
